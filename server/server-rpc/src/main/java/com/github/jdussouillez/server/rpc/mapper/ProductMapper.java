package com.github.jdussouillez.server.rpc.mapper;

import com.github.jdussouillez.server.bean.Product;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMapper implements GrpcMapper<Product, com.github.jdussouillez.api.grpc.Product> {

    @Override
    public com.github.jdussouillez.api.grpc.Product toGrpc(final Product product) {
        var builder = com.github.jdussouillez.api.grpc.Product.newBuilder()
            .setId(product.id())
            .setDesignation(product.designation())
            .setStock(product.stock())
            .setPictureUrl(product.pictureUrl())
            .setWeight(product.weight())
            .setVolume(product.volume())
            .setObsolete(product.obsolete());
        if (product.blueprintUrl() != null) {
            builder.setBlueprintUrl(product.blueprintUrl());
        }
        return builder.build();
    }
}
