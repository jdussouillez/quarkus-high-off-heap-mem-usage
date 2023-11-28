# quarkus-high-off-heap-mem-usage

Project to reproduce a suspicious behavior where the off heap memory seems to go wild.

See https://github.com/quarkusio/quarkus/discussions/36691

## Build

1. Build the spec project

```sh
./mvnw install
```

2. Build the server and client OCI images

```sh
./mvnw package -Dquarkus.container-image.build=true
```

The OCI images are only built locally with name `quarkus-high-off-heap-mem-usage/(server or client)`.

## Run

```sh
# Server
docker run -d \
    --rm \
    --network host \
    --name server \
    -m 256m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms128m -Xmx128m" \
    -v /tmp:/tmp \
    quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT \
    && docker logs server -f
```

```sh
# Client
docker run -d \
    --rm \
    --network host \
    --name client \
    -m 512m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms64m -Xmx64m" \
    -v /tmp:/tmp \
    quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT \
    100000 \
    && docker logs client -f
```

## Cleanup

```sh
docker rm --force server \
    && docker rm --force client \
    && docker rmi quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT -f \
    && docker rmi quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT -f
```
