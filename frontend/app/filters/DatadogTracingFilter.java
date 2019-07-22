package filters;

import com.google.inject.Inject;
import play.http.DefaultHttpFilters;

import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.regex.Pattern;

import akka.stream.Materializer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import play.api.routing.HandlerDef;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import play.routing.Router;


public class DatadogTracingFilter extends Filter {

    @Inject
    public DatadogTracingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(
            Function<Http.RequestHeader, CompletionStage<Result>> next,
            Http.RequestHeader rh) {

        Optional<HandlerDef> handlerDef = rh.attrs().getOptional(Router.Attrs.HANDLER_DEF);
        String actionMethod = handlerDef.isPresent() ? handlerDef.get().controller() + "." + handlerDef.get().method() : "no.route";

        final Tracer tracer = GlobalTracer.get();
        final Scope scope = tracer.buildSpan("play")
                .ignoreActiveSpan()
                .withTag("method", actionMethod)
                .withTag("remote", rh.remoteAddress())
                .withTag("uri", rh.uri())
                .startActive(true);


        return next.apply(rh)
                .handle((result, ex) -> {
                    if (scope != null) {
                        scope.close();
                    }
                    if (ex != null) {
                        return null;
                    }
                    return result;
                });
    }
}
