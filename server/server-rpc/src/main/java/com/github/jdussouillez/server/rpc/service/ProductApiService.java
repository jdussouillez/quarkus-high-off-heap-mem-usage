package com.github.jdussouillez.server.rpc.service;

import com.github.jdussouillez.api.grpc.Product;
import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import com.github.jdussouillez.server.Loggers;
import com.github.jdussouillez.server.rpc.interceptor.EnableGrpcErrorManagement;
import com.github.jdussouillez.server.rpc.mapper.ProductMapper;
import com.github.jdussouillez.server.service.ProductService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
@EnableGrpcErrorManagement
public class ProductApiService implements ProductGrpcApiService {

    @Inject
    protected ProductMapper productMapper;

    @Inject
    protected ProductService productService;

    @Override
    public Multi<Product> getAll(final ProductGetRequest request) {
        var limit = request.hasLimit() ? request.getLimit() : null;
        var counter = new AtomicInteger();
        return productService.getAll(limit)
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Sending {} products...", () -> limit != null ? limit : "all"))
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All products sent"))
            .map(productMapper::toGrpc)
            .invoke(() -> {
                var nbSent = counter.incrementAndGet();
                if (nbSent % 10_000 == 0) {
                    Loggers.MAIN.info("Products sent: {}", nbSent);
                }
            });
    }
}
