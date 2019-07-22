package interceptors;

import io.grpc.*;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.util.GlobalTracer;

import java.util.HashMap;
import java.util.Map;

public class ServerTracingInterceptor implements ServerInterceptor {

    private final Tracer tracer = GlobalTracer.get();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        Map<String, String> headerMap = new HashMap<>();
        for (String key : headers.keys()) {
            if (!key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
                String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
                headerMap.put(key, value);
            }
        }

        final String operationName = call.getMethodDescriptor().getFullMethodName();
        final Span span = getSpanFromHeaders(headerMap, operationName);

//        Context contextWithSpan = Context.current().withValue(OpenTracingContextKey.getKey(), span);
        Context contextWithSpan = Context.current();
        ServerCall.Listener<ReqT> listenerWithContext =
                Contexts.interceptCall(contextWithSpan, call, headers, next);

        return new SimpleForwardingServerCallListener<ReqT>(listenerWithContext) {
            @Override
            public void onCancel() {
                span.finish();
                super.onCancel();
            }

            @Override
            public void onComplete() {
                span.finish();
                super.onComplete();
            }
        };
    }

    private Span getSpanFromHeaders(Map<String, String> headers, String operationName) {
        Span span;
        try {
            SpanContext parentSpanCtx = tracer.extract(
                    Format.Builtin.HTTP_HEADERS,
                    new TextMapExtractAdapter(headers));
            if (parentSpanCtx == null) {
                span = tracer.buildSpan(operationName).start();
            } else {
                span = tracer.buildSpan(operationName).asChildOf(parentSpanCtx).start();
            }
        } catch (Exception e){
            span = tracer.buildSpan(operationName)
                    .withTag("Error", "extract failure")
                    .start();
        }
        return span;
    }
}
