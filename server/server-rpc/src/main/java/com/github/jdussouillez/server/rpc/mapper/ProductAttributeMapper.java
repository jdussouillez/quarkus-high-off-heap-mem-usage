package com.github.jdussouillez.server.rpc.mapper;

import com.github.jdussouillez.server.bean.ProductAttribute;
import com.github.jdussouillez.server.bean.ProductBooleanAttribute;
import com.github.jdussouillez.server.bean.ProductNumberAttribute;
import com.github.jdussouillez.server.bean.ProductTextAttribute;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductAttributeMapper
    implements GrpcMapper<ProductAttribute<?>, com.github.jdussouillez.api.grpc.ProductAttribute> {

    @Override
    public com.github.jdussouillez.api.grpc.ProductAttribute toGrpc(final ProductAttribute<?> attribute) {
        var builder = com.github.jdussouillez.api.grpc.ProductAttribute.newBuilder()
            .setId(attribute.id())
            .setDesignation(attribute.designation());
        if (attribute instanceof ProductTextAttribute textAttr) {
            builder.setStrValue(textAttr.value());
        } else if (attribute instanceof ProductNumberAttribute numAttr) {
            builder.setDoubleValue(numAttr.value());
        } else if (attribute instanceof ProductBooleanAttribute boolAttr) {
            builder.setBoolValue(boolAttr.value());
        }
        return builder.build();
    }
}
