package com.github.jdussouillez.server.rpc.interceptor;

import com.github.jdussouillez.server.Loggers;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Priority(2000)
@EnableGrpcErrorManagement
public class GrpcErrorManager {

    @AroundInvoke
    protected Object addOnFailureOnMutiny(final InvocationContext context) throws Exception {
        var obj = context.proceed();
        if (Multi.class.isAssignableFrom(obj.getClass())) {
            return ((Multi<?>) obj).onFailure().invoke(this::logThrowable).onFailure().transform(this::mapThrowable);
        }
        if (Uni.class.isAssignableFrom(obj.getClass())) {
            return ((Uni<?>) obj).onFailure().invoke(this::logThrowable).onFailure().transform(this::mapThrowable);
        }
        return obj;
    }

    protected void logThrowable(final Throwable throwable) {
        Loggers.MAIN.error(throwable);
    }

    protected StatusException mapThrowable(final Throwable throwable) {
        var meta = new Metadata();
        meta.put(Metadata.Key.of("ex", Metadata.ASCII_STRING_MARSHALLER), throwable.getMessage());
        return new StatusException(Status.INTERNAL, meta);
    }
}
