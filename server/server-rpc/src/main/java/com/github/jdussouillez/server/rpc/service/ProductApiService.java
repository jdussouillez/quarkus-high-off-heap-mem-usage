package com.github.jdussouillez.server.rpc.service;

import com.github.jdussouillez.api.grpc.Product;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import com.github.jdussouillez.server.Loggers;
import com.github.jdussouillez.server.rpc.interceptor.EnableGrpcErrorManagement;
import com.github.jdussouillez.server.rpc.mapper.ProductMapper;
import com.github.jdussouillez.server.service.ProductService;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;

@GrpcService
@EnableGrpcErrorManagement
public class ProductApiService implements ProductGrpcApiService {

    @Inject
    protected ProductMapper productMapper;

    @Inject
    protected ProductService productService;

    @Override
    public Multi<Product> getAll(final Empty request) {
        return productService.getAll()
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            .onSubscription()
            .invoke(() -> Loggers.MAIN.info("Sending products..."))
            .onCompletion()
            .invoke(() -> Loggers.MAIN.info("All products sent"))
            .map(productMapper::toGrpc);
    }
}
