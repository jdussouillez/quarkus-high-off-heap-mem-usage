package com.github.jdussouillez.client;

import io.quarkus.reactive.pg.client.PgPoolCreator;
import io.vertx.pgclient.PgPool;
import jakarta.inject.Singleton;

@Singleton
public class CustomPgPoolCreator implements PgPoolCreator {

    @Override
    public PgPool create(final Input input) {
        var connectOptions = input.pgConnectOptionsList();
        connectOptions.forEach(options -> options.getProperties().put("application_name", "client"));
        var poolOptions = input.poolOptions()
            .setName("client")
            .setMaxSize(16);
        return PgPool.pool(input.vertx(), connectOptions, poolOptions);
    }
}
