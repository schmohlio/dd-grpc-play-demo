package controllers;

import play.*;
import play.mvc.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import play.libs.concurrent.HttpExecutionContext;
import javax.inject.*;
import datadog.examples.*;
import helpers.FutureConverter;

import java.util.concurrent.CompletableFuture;

@Singleton
public class AdditionController extends Controller {
    private final HttpExecutionContext context;
    private final BackendClient backend;

    @Inject
    public AdditionController(HttpExecutionContext context, BackendClient backend) {
        this.context = context;
        this.backend = backend;
    }

    public CompletableFuture<Result> add3(Integer value) {
        return toCompletableFuture(backend.addOne(value))
                .thenCompose(result1 -> toCompletableFuture(backend.addTwo(result1)))
                .thenApply(result2 -> Results.ok("got " + result2));
    }

    private <V> CompletableFuture<V> toCompletableFuture(final ListenableFuture<V> listenable) {
        return new FutureConverter<>(context.current(), listenable).getCompletableFuture();
    }
}
