package com.github.jdussouillez.server.service;

import com.github.jdussouillez.server.Loggers;
import com.github.jdussouillez.server.bean.Product;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class ProductService {

    private final Random random = new Random(42L);

    public Multi<Product> get(final int itemNumber) {
        return Multi.createFrom()
            .range(0, itemNumber)
            .map(this::generate)
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Sending products to the client..."))
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All products sent!"));
    }

    private Product generate(final int num) {
        var id = String.format("%08d", num);
        return new Product(id, "Product #" + id, random.nextInt(10_000));
    }
}
