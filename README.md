# quarkus-high-off-heap-mem-usage

Simple project to reproduce a suspicious behavior where the off heap memory seems to go wild.

See https://github.com/quarkusio/quarkus/discussions/36691

## Architecture

- `spec` - gRPC specifications (used by both the server and client)
- `server` - Server project
- `client` - Client project

```mermaid
flowchart LR
    subgraph Server
        serverdb((Server DB))
        server[Server]
    end
    subgraph Client
        clientdb((Client DB))
        client[Client]
    end
    serverdb -.-> |jOOQ & Vert.x reactive SQL client| server
    client -.-> |jOOQ & Vert.x reactive SQL client| clientdb
    server --> |gRPC| client
```

Note:
- There are some duplicated code between server and client because this project is a throwaway.
- In this test project, both server and client databases are on the same PostgreSQL server (localhost:5432)

## Build

1. Start the database

```sh
docker run -d \
    --rm \
    --name db \
    -p 5432:5432 \
    --network host \
    -e POSTGRES_USER=foo \
    -e POSTGRES_PASSWORD=bar \
    postgres:15-bullseye
```

Create the databases and insert data (in the server db only):

```sh
PGPASSWORD=bar \
    && psql -h localhost -p 5432 -U foo -f db/init.sql \
    && psql -h localhost -p 5432 -U foo -d client -f db/client-init.sql \
    && psql -h localhost -p 5432 -U foo -d server -f db/server-init.sql \
    && unzip db/server-data.sql.zip -d db/ \
    && psql -h localhost -p 5432 -U foo -d server -f db/server-data.sql -q -1 \
    && rm -v db/server-data.sql
```

**Optional**: To generate another set of data, use:

```sh
./db/generate-server-data.mjs \
    && PGPASSWORD=bar psql -h localhost -p 5432 -U foo -d server -f db/server-data.sql -q -1
```

2. Build the spec project

```sh
cd spec && ./mvnw clean install && cd ..
```

3. Build the server and client OCI images

```sh
cd server \
    && ./mvnw clean package -Dquarkus.container-image.build=true \
    && cd ../client \
    && ./mvnw clean package -Dquarkus.container-image.build=true \
    && cd ..
```

The OCI images are only built locally with name `quarkus-high-off-heap-mem-usage/(server or client)`.

## Run

```sh
# Server
docker run \
    --rm \
    --network host \
    --name server \
    -m 2048m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms1024m -Xmx1024m" \
    -v /tmp:/tmp \
    quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT
```

```sh
# Client
docker run \
    --rm \
    --network host \
    --name client \
    -m 1024m \
    --cpus=2 \
    -e JAVA_OPTS="-Xms256m -Xmx256m" \
    -v /tmp:/tmp \
    quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT

# Usage: client [limit] [overflowMode]
- limit: Optional, max number of products to fetch from server. -1 for no limit
- overflowMode: Optional, "drop" or "buffer"
```

## Cleanup

```sh
docker rm --force server \
    && docker rm --force client \
    && docker rm --force db \
    && docker rmi quarkus-high-off-heap-mem-usage/server:1.0.0-SNAPSHOT -f \
    && docker rmi quarkus-high-off-heap-mem-usage/client:1.0.0-SNAPSHOT -f \
    && docker rmi postgres:15-bullseye
```


## Analysis

Go in the [charts](./charts) folder to get some charts about memory usage on the client side and number of products "received" (sent by the server) and "processed" (grouped in chunks and insert into target database).

The problem is related to back-pressure: the producer (server) is faster than the consumer (client) and the messages not processed by the client yet are stored in the off-heap memory (I guess? because the heap memory usage doesn't change that much).

The server sends all the products to the client and the client process them while receiving more products. But once the server is done sending products, the client still process products for a few seconds/minutes.
What is weird is:
- The client container memory (heap+off-heap) is slowly growing up continuously when the server is sending data, even if the heap memory is not changed (the heap does not grow up, thanks to both `Xms` and `Xmx` JVM options set to the same value)
- Once the server is done sending data, there is a little "bump" in the client off-heap memory that increases just when the server send the last data.

What bothers me is that "bump": if the client cannot handle all messages, it should be buffering them continuously so it should look like a straight line since the client started, right? Not a bump just when the server is done!

When using `onOverflow().drop()`, ~80% of messages are dropped but we can still see a "bump" in the client container used memory. Why?

### Memory charts

- Using a 1GB memory heap, in a 4GB client container

![1GB heap - 4GB container](./charts/1gb-4gb.png)

- Using a 1GB memory heap, in a 4GB client container, explicitly using `onOverflow().bufferUnconditionally()` (very similar to the first one. Is it the default strategy? I can't find it written in the docs)

![1GB heap - 4GB container (buffer)](./charts/1gb-4gb-overflow-buffer.png)

- Using a 1GB memory heap, in a 4GB client container, drop messages on overflow

![1GB heap - 4GB container (drop)](./charts/1gb-4gb-overflow-drop.png)

*See more in the [charts](./charts) folder.*