# quarkus-high-off-heap-mem-usage

Project to reproduce a suspicious behavior where the off heap memory seems to go wild

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
    -m 128m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms64m -Xmx64m" \
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
    -m 128m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms64m -Xmx64m" \
    -v /tmp:/tmp \
    quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT \
    100000 \
    && docker logs client -f
```

## Cleanup

```sh
docker stop server \
    && docker stop client \
    && docker rmi quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT -f \
    && docker rmi quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT -f
```
