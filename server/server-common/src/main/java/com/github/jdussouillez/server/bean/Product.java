package com.github.jdussouillez.server.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
@ToString
public class Product {

    @Getter
    protected final String id;

    @Getter
    protected final String designation;

    @Getter
    protected final int stock;
}
