package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import java.time.Duration;

@QuarkusMain
public class Main implements QuarkusApplication {

    @GrpcClient("serv")
    protected ProductGrpcApiService productGrpcApiService;

    @Override
    public int run(final String... args) throws InterruptedException {
        int count;
        try {
            count = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid number");
            return 1;
        }
        productGrpcApiService.get(ProductGetRequest.newBuilder().setCount(count).build())
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Fetching products..."))
            .onFailure()
            .invoke(ex -> Loggers.MAIN.error("Error when fetching products", ex))
            .group()
            .intoLists()
            .of(500)
            .call(products -> {
                Loggers.MAIN.info("Saving {} products in db", products::size);
                return Uni.createFrom().voidItem()
                    .onItem()
                    .delayIt()
                    .by(Duration.ofMillis(150));
            })
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All fetched"))
            .subscribe()
            .with(
                sub -> {},
                ex -> {}
            );
        Thread.sleep(30_000L);
        return 0;
    }
}
