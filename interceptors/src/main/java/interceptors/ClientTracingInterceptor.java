package interceptors;

import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.util.GlobalTracer;

import java.util.Iterator;
import java.util.Map;

public class ClientTracingInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        final String operationName = method.getFullMethodName();
        final Tracer tracer = GlobalTracer.get();
        final Span parentSpan = tracer.activeSpan();

        final Span span;
        if (parentSpan == null) {
            span = tracer.buildSpan(operationName).start();
        } else {
            span = tracer.buildSpan(operationName).asChildOf(parentSpan).start();
        }

        return new TraceInjectingForwardingClientCall<>(
                next.newCall(method, callOptions),
                tracer,
                span);
    }

    private static class TraceInjectingForwardingClientCall<ReqT, RespT>
            extends SimpleForwardingClientCall<ReqT, RespT> {

        private final Tracer tracer;
        private final Span activeSpan;

        TraceInjectingForwardingClientCall(ClientCall<ReqT, RespT> delegate,
                                           Tracer tracer,
                                           Span activeSpan) {
            super(delegate);
            this.tracer = tracer;
            this.activeSpan = activeSpan;
        }

        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
            tracer.inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS, new TextMap() {

                @Override
                public void put(String key, String value) {
                    headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
                }

                @Override
                public Iterator<Map.Entry<String, String>> iterator() {
                    throw new UnsupportedOperationException("should only be used with Tracer.inject()");
                }
            });

            super.start(new TraceCloserClientCallListener<>(responseListener, activeSpan), headers);
        }
    }

    private static class TraceCloserClientCallListener<RespT>
            extends ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT> {

        private final Span activeSpan;

        TraceCloserClientCallListener(ClientCall.Listener<RespT> delegate,
                                      Span activeSpan) {
            super(delegate);
            this.activeSpan = activeSpan;
        }

        @Override
        public void onClose(Status status, Metadata trailers) {
            if (activeSpan != null) {
                activeSpan.finish();
            }
            super.onClose(status, trailers);
        }
    }
}
