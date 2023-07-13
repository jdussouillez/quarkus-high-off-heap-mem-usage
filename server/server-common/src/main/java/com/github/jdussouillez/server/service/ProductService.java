package com.github.jdussouillez.server.service;

import com.github.jdussouillez.server.Loggers;
import com.github.jdussouillez.server.bean.Product;
import com.github.jdussouillez.server.bean.ProductAttribute;
import com.github.jdussouillez.server.bean.ProductBooleanAttribute;
import com.github.jdussouillez.server.bean.ProductNumberAttribute;
import com.github.jdussouillez.server.bean.ProductTextAttribute;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;

@ApplicationScoped
public class ProductService {

    private final Random random = new Random();

    public Multi<Product> get(final int maxCount) {
        return Multi.createFrom().range(1, maxCount + 1)
            .map(this::generate);
    }

    private Product generate(final int num) {
        var id = String.format("%08d", num);
        if (num % 10_000 == 0) {
            Loggers.MAIN.info("Generating product " + id);
        }
        return new Product(id, "Product #" + id, random.nextInt(10_000), generateAttributes());
    }

    private Set<ProductAttribute<?>> generateAttributes() {
        Set<ProductAttribute<?>> attributes = new HashSet<>();
        attributes.add(new ProductNumberAttribute("weight", "Weight (in g)", (double) random.nextInt(1, 30_000)));
        attributes.add(new ProductNumberAttribute("volume", "Volume (in mm3)", (double) random.nextInt(1, 50_000)));
        attributes.addAll(
            IntStream.rangeClosed(0, random.nextInt(5))
                .boxed()
                .map(i -> generateRandomAttribute())
                .toList()
        );
        return attributes;
    }

    private ProductAttribute<?> generateRandomAttribute() {
        var id = RandomStringUtils.randomAlphabetic(8);
        var designation = "Attribute #" + id;
        return switch (random.nextInt(3)) {
            case 0 -> new ProductTextAttribute(id, designation, RandomStringUtils.randomAlphanumeric(1, 20));
            case 1 -> new ProductNumberAttribute(id, designation, random.nextDouble(5_000));
            case 2 -> new ProductBooleanAttribute(id, designation, random.nextBoolean());
            default -> throw new IllegalStateException("Generated int is out of range (should not happen)");
        };
    }
}
