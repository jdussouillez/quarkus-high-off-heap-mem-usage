package com.github.jdussouillez.server.bean;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class ProductAttribute<T> {

    @Getter
    @EqualsAndHashCode.Include
    protected final String id;

    @Getter
    protected final String designation;

    @Getter
    protected final T value;
}
