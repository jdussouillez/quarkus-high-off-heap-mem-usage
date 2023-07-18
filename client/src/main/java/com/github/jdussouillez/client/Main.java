package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.Product;
import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import java.time.Duration;

@QuarkusMain
public class Main implements QuarkusApplication {

    @GrpcClient("serv")
    protected ProductGrpcApiService productGrpcApiService;

    @Override
    public int run(final String... args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Usage: <delay> <src>");
            return 1;
        }
        int delay;
        try {
            delay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid number");
            return 1;
        }
        boolean fromGrpc = args[1].equals("grpc");
        return (fromGrpc ? getProductsFromServer(delay) : getProducts(delay))
            .emitOn(Infrastructure.getDefaultWorkerPool())
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Fetching products..."))
            .onFailure()
            .invoke(ex -> Loggers.MAIN.error("Error when fetching products", ex))
            .group()
            .intoLists()
            .of(10)
            .call(products -> {
                Loggers.MAIN.info("Saving {} products in db", products::size);
                return Uni.createFrom().voidItem()
                    .onItem()
                    .delayIt()
                    .by(Duration.ofMillis(100));
            })
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All fetched"))
            .collect()
            .last()
            .replaceWith(0)
            .onFailure()
            .recoverWithItem(1)
            .await()
            .indefinitely();
    }

    private Multi<Product> getProductsFromServer(final int delay) {
        return productGrpcApiService.get(ProductGetRequest.newBuilder().setDelay(delay).build());
    }

    private Multi<Product> getProducts(final int delay) {
        return Multi.createFrom().ticks()
            .every(Duration.ofMillis(delay))
            .map(i -> {
                var id = String.format("%08d", i);
                Loggers.MAIN.info("Generating product " + id);
                return Product.newBuilder()
                    .setId(String.valueOf(i))
                    .setDesignation("Product #" + String.valueOf(id))
                    .build();
            });
    }
}
