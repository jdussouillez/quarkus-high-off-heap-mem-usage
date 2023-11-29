package com.github.jdussouillez.client.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.conf.ParamType;

@ApplicationScoped
public class SqlService {

    @Getter
    @Inject
    protected DSLContext dslContext;

    @Getter
    @Inject
    protected PgPool dbPool;

    public <Q extends Query> Uni<Integer> executeInTransaction(final Collection<Q> queries) {
        return dbPool.withTransaction(connection -> Multi.createFrom().iterable(queries)
            .onItem()
            .transformToUniAndMerge(q -> connection.query(getSQL(q)).execute().map(RowSet::rowCount))
            .collect()
            .with(Collectors.summingInt(Integer::intValue))
        );
    }

    private static String getSQL(final Query query) {
        return query.getSQL(ParamType.INLINED);
    }
}
