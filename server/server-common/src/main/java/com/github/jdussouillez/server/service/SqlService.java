package com.github.jdussouillez.server.service;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.function.Function;
import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Select;
import org.jooq.conf.ParamType;

@ApplicationScoped
public class SqlService {

    @Getter
    @Inject
    protected DSLContext dslContext;

    @Getter
    @Inject
    protected PgPool dbPool;

    public <T> Multi<T> select(final Select<?> query, final Function<Row, T> mapper) {
        return dbPool.query(getSQL(query))
            .mapping(mapper::apply)
            .execute()
            .onItem()
            .transformToMulti(RowSet::toMulti);
    }

    private static String getSQL(final Query query) {
        return query.getSQL(ParamType.INLINED);
    }
}
