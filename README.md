# quarkus-high-off-heap-mem-usage

Project to reproduce a suspicious behavior where the off heap memory seems to go wild

## Build

1. Build the spec project

```sh
./mvnw install
```

2. Build the server and client
    1. Java Jar

    ```sh
    ./mvnw package
    ```

    2. OCI images

    ```sh
    ./mvnw package -Dquarkus.container-image.build=true
    ```
   
    The OCI images are only built locally with name `quarkus-high-off-heap-mem-usage/(server or client)`.

## Run

### Java Jar

```sh
# Server
java -jar server-rpc/target/quarkus-app/quarkus-run.jar
```

```sh
# Client

# For local data fetching
java -XX:NativeMemoryTracking=summary -jar target/quarkus-app/quarkus-run.jar 100 local

# For gRPC data fetching
java -XX:NativeMemoryTracking=summary -jar target/quarkus-app/quarkus-run.jar 100 grpc
```

### OCI images

```sh
# Server
docker run -d \
  --network host \
  --name server \
  -m 64m \
  --cpus=2 \
  -e JAVA_OPTS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp" \
  -v /tmp:/tmp \
  quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT
```

```sh
# Client
docker run -d \
  --network host \
  --name client \
  -m 64m \
  --cpus=2 \
  -e JAVA_OPTS="-XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp" \
  -v /tmp:/tmp \
  quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT \
  100 grpc
```
