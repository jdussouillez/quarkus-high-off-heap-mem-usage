package com.github.jdussouillez.client.mapper;

public interface GrpcMapper<J, G> {

    default G toGrpc(J javaObj) {
        throw new UnsupportedOperationException();
    }

    default J fromGrpc(G grpcObj) {
        throw new UnsupportedOperationException();
    }
}
