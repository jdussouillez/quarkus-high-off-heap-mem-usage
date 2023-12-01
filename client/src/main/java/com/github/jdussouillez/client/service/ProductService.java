package com.github.jdussouillez.client.service;

import com.github.jdussouillez.api.grpc.ProductGetRequest;
import com.github.jdussouillez.api.grpc.ProductGrpcApiService;
import com.github.jdussouillez.client.Loggers;
import com.github.jdussouillez.client.bean.Product;
import static com.github.jdussouillez.client.jooq.Product.PRODUCT;
import com.github.jdussouillez.client.jooq.tables.records.ProductsRecord;
import com.github.jdussouillez.client.mapper.ProductMapper;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.jooq.DSLContext;

@ApplicationScoped
public class ProductService {

    @GrpcClient("serv")
    protected ProductGrpcApiService productGrpcApiService;

    @Inject
    protected ProductMapper mapper;

    @Inject
    protected SqlService sqlService;

    @Inject
    protected DSLContext dslContext;

    public Multi<Product> fetch(final Integer limit, final String overflowMode) {
        var reqBuilder = ProductGetRequest.newBuilder();
        if (limit != null) {
            reqBuilder.setLimit(limit);
        }
        var fetch = productGrpcApiService
            .getAll(reqBuilder.build())
            .map(mapper::fromGrpc);
        if ("drop".equals(overflowMode)) {
            var dropCounter = new AtomicInteger();
            fetch = fetch.onOverflow()
                .invoke(() -> dropCounter.incrementAndGet())
                .drop()
                .onCompletion()
                .invoke(() -> Loggers.MAIN.info("Number of products dropped: {}", dropCounter::get));
        } else if ("buffer".equals(overflowMode)) {
            var bufferCounter = new AtomicInteger();
            fetch = fetch.onOverflow()
                .invoke(() -> bufferCounter.incrementAndGet())
                .bufferUnconditionally()
                .onCompletion()
                .invoke(() -> Loggers.MAIN.info("Number of products buffered: {}", bufferCounter::get));
        }
        return fetch;
    }

    public Uni<Integer> persist(final Collection<Product> products) {
        return sqlService
            .executeInTransaction(
                products
                    .stream()
                    .map(p -> {
                        var rec = new ProductsRecord();
                        rec.setId(p.id());
                        rec.setDesignation(p.designation());
                        rec.setStock(p.stock());
                        rec.setPictureUrl(p.pictureUrl());
                        rec.setBlueprintUrl(p.blueprintUrl());
                        rec.setWeight(p.weight());
                        rec.setVolume(p.volume());
                        rec.setObsolete(p.obsolete());
                        return dslContext.insertInto(PRODUCT.PRODUCTS)
                            .set(rec)
                            .onDuplicateKeyUpdate()
                            .set(rec);
                    })
                    .toList()
            )
            .invoke(nbInserted -> Loggers.MAIN.trace("Saved {} products in db...", nbInserted));
    }
}
