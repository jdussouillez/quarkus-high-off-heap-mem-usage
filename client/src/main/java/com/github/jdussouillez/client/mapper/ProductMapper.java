package com.github.jdussouillez.client.mapper;

import com.github.jdussouillez.client.bean.Product;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMapper implements GrpcMapper<Product, com.github.jdussouillez.api.grpc.Product> {

    @Override
    public Product fromGrpc(final com.github.jdussouillez.api.grpc.Product product) {
        return new Product()
            .id(product.getId())
            .designation(product.getDesignation())
            .stock(product.getStock())
            .pictureUrl(product.getPictureUrl())
            .blueprintUrl(product.hasBlueprintUrl() ? product.getBlueprintUrl() : null)
            .weight(product.getWeight())
            .volume(product.getVolume())
            .obsolete(product.getObsolete());
    }
}
