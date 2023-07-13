package com.github.jdussouillez.server.rpc.mapper;

import com.github.jdussouillez.server.bean.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductMapper implements GrpcMapper<Product, com.github.jdussouillez.api.grpc.Product> {

    @Inject
    protected ProductAttributeMapper attributeMapper;

    @Override
    public com.github.jdussouillez.api.grpc.Product toGrpc(final Product product) {
        return com.github.jdussouillez.api.grpc.Product.newBuilder()
            .setId(product.id())
            .setDesignation(product.designation())
            .setStock(product.stock())
            .addAllAttributes(
                product.attributes()
                    .stream()
                    .map(attributeMapper::toGrpc)
                    .toList()
            )
            .build();
    }
}
