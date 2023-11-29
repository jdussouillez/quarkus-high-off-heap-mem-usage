package com.github.jdussouillez.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

@ApplicationScoped
public class DSLContextProvider {

    @Produces
    public DSLContext create() {
        var jooqConf = new DefaultConfiguration()
            .set(SQLDialect.POSTGRES);
        return DSL.using(jooqConf);
    }
}
