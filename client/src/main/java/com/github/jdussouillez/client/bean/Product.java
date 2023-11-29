package com.github.jdussouillez.client.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@NoArgsConstructor
@ToString
public class Product {

    @Getter
    @Setter
    protected String id;

    @Getter
    @Setter
    protected String designation;

    @Getter
    @Setter
    protected int stock;

    @Getter
    @Setter
    protected String pictureUrl;

    @Getter
    @Setter
    protected String blueprintUrl;

    @Getter
    @Setter
    protected double weight;

    @Getter
    @Setter
    protected double volume;

    @Getter
    @Setter
    protected boolean obsolete;
}
