package helpers;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FutureConverter<V> {

    private final Executor delegate;
    private final ListenableFuture<V> listenableFuture;

    public FutureConverter(Executor delegate, ListenableFuture<V> listenableFuture) {
        this.delegate = delegate;
        this.listenableFuture = listenableFuture;
    }

    public CompletableFuture<V> getCompletableFuture() {

        final CompletableFuture<V> f = new CompletableFuture<V>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        Futures.addCallback(listenableFuture, new FutureCallback<V>() {
            @Override
            public void onSuccess(@Nullable V result) {
                f.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                f.completeExceptionally(t);
            }
        }, delegate);

        return f;
    }

}
