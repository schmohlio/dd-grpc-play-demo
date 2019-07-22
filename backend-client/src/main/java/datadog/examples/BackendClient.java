package datadog.examples;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import datadog.examples.services.AdditionReply;
import datadog.examples.services.AdditionRequest;
import datadog.examples.services.BackendGrpc;
import interceptors.ClientTracingInterceptor;
import io.grpc.ManagedChannelBuilder;

public class BackendClient {
    private final BackendGrpc.BackendFutureStub stub;

    public BackendClient() {
        stub = BackendGrpc.newFutureStub(
                ManagedChannelBuilder.forAddress("localhost", 50051)
                    .usePlaintext()
                    .build())
            .withInterceptors(new ClientTracingInterceptor());
    }

    public ListenableFuture<Integer> addOne(int complement) {
        ListenableFuture<AdditionReply> reply = stub.addOne(
            AdditionRequest.newBuilder()
                .setComplement(complement)
                .build());
        return Futures.transform(reply, AdditionReply::getResult, MoreExecutors.directExecutor());
    }

    public ListenableFuture<Integer> addTwo(int complement) {
        ListenableFuture<AdditionReply> reply = stub.addTwo(
            AdditionRequest.newBuilder()
                .setComplement(complement)
                .build());
        return Futures.transform(reply, AdditionReply::getResult, MoreExecutors.directExecutor());
    }
}
