package com.github.jdussouillez.server.rpc.service;

import com.github.jdussouillez.api.grpc.Product;
import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import com.github.jdussouillez.server.rpc.interceptor.EnableGrpcErrorManagement;
import com.github.jdussouillez.server.rpc.mapper.ProductMapper;
import com.github.jdussouillez.server.service.ProductService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;

@GrpcService
@EnableGrpcErrorManagement
public class ProductApiService implements ProductGrpcApiService {

    @Inject
    protected ProductMapper productMapper;

    @Inject
    protected ProductService productService;

    @Override
    public Multi<Product> get(final ProductGetRequest request) {
        return productService.get(request.getItemNumber())
            .map(productMapper::toGrpc);
    }
}
