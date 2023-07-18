package com.github.jdussouillez.server.service;

import com.github.jdussouillez.server.Loggers;
import com.github.jdussouillez.server.bean.Product;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Random;

@ApplicationScoped
public class ProductService {

    private final Random random = new Random();

    public Multi<Product> get(final int delay) {
        return Multi.createFrom().ticks()
            .every(Duration.ofMillis(delay))
            .map(this::generate)
            .invoke(p -> Loggers.MAIN.info("Generating product {}", p))
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("Done"));
    }

    private Product generate(final long num) {
        var id = String.format("%08d", num);
        return new Product(id, "Product #" + id, random.nextInt(10_000));
    }
}
