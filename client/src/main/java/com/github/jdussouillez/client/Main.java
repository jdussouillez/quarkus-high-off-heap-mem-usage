package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.Product;
import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@QuarkusMain
public class Main implements QuarkusApplication {

    @GrpcClient("serv")
    protected ProductGrpcApiService productGrpcApiService;

    private final Random random = new Random(123L);

    @Override
    public int run(final String... args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Usage: <number of items to fetch>");
            return 1;
        }
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid number");
            return 1;
        }
        var counter = new AtomicInteger();
        return getProducts(itemNumber)
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Fetching products..."))
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All products fetched!"))
            .onFailure()
            .invoke(ex -> Loggers.MAIN.error("Error when fetching products", ex))
            .group()
            .intoLists()
            .of(500)
            .call(products -> {
                Loggers.MAIN.debug("Saving {} products in db", products::size);
                return Uni.createFrom().voidItem()
                    .onItem()
                    .delayIt()
                    .by(Duration.ofMillis(1 + random.nextInt(200)));
            })
            .invoke(products -> {
                int nbProcessed = counter.addAndGet(products.size());
                if (nbProcessed % 10_000 == 0) {
                    Loggers.MAIN.info("Products processed: {} / {}", nbProcessed, itemNumber);
                }
            })
            .collect()
            .last()
            .replaceWith(0)
            .onFailure()
            .recoverWithItem(1)
            .await()
            .indefinitely();
    }

    private Multi<Product> getProducts(final int itemNumber) {
        return productGrpcApiService.get(
            ProductGetRequest.newBuilder()
                .setItemNumber(itemNumber)
                .build()
        );
    }
}
