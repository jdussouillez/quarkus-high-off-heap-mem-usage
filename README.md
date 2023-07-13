# quarkus-high-off-heap-mem-usage

Project to reproduce a suspicious behavior where the off heap memory seems to go wild

## Build

1. Build the spec project

```sh
./mvnw install
```

2. Build the server and client

```sh
./mvnw package
```

## Run

```
# Server
java -jar server-rpc/target/quarkus-app/quarkus-run.jar
```

```
# Client
java -jar target/quarkus-app/quarkus-run.jar
```
