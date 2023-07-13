package com.github.jdussouillez.server.bean;

import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
public class Product {

    @Getter
    protected final String id;

    @Getter
    protected final String designation;

    @Getter
    protected final int stock;

    @Getter
    protected final Set<ProductAttribute<?>> attributes;
}
