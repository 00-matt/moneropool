# moneropool Application Properties

This document covers various properties that control the behaviour of
the moneropool application.

Properties can be set by modifying the
`src/main/resources/application.properties` file, or by passing
`-Dproperty=value` on the Java command line.

## moneropool

### `blocktemplate.address`

Block rewards go to this address.

Example: `blocktemplate.address=5Ai9Y2xvA2TEvWV2...`

### `daemon.address`

The address of a Monero daemon's RPC server.

Example: `daemon.address=http://127.0.0.1:38081/json_rpc`

### `stratum.port`

Port that the Stratum server will listen on.

Example: `stratum.port=6666`

### `stratum.backlog`

SO_BACKLOG of the Stratum server.

Example: `stratum.backlog=128`

### `stratum.parentThreads`

Number of threads used to accept incoming connections. Should probably
be set to `1`.

Example: `stratum.parentThreads=1`

### `stratum.childThreads`

Number of threads used to handle miner connections. Should probably be
set to the number of cores in your system.

Example: `stratum.childThreads8`

### `globalExecutor.threads`

Number of threads used to handle background tasks, like PoW hash
verification. Should probably be set to the number of cores in your
system.

Example: `globalExecutor.threads=8`

### varDiff.shareTargetTime

The pool will attempt to adjust job difficulties to ensure that on
average, a job takes this many seconds to complete. Lower values will
place more load on the pool.

Example: `varDiff.shareTargetTime=30`

### varDiff.start

The starting difficulty for a new miner with an unknown hashrate. They
will continue to receive jobs of this difficulty until they have
submitted `varDiff.wait` shares.

Example: `varDiff.start=30000`

### varDiff.minimum

The minimum difficulty for any miner.

Example: `varDiff.minimum=20000`

### varDiff.wait

Miners will receive jobs of the starting difficulty until they have
submitted this many shares.

Example: `varDiff.wait=3`

### database.url

[JDBC connection URL to your
database](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html#db_connection_url).

The pool only supplies a driver for postgres; other databases will
require a driver to be added to the classpath before use.

Example: `database.url=jdbc:postgresql://localhost:5432/postgres`

### database.user

Username to connect to the database with.

Example: `database.user=postgres`

### database.pass

Password to connect to the database with.

Example: `database.pass=sergtsop`

### redis.host

Hostname to connect to a Redis database with. Currently only used for
block notifications.

Example: `redis.host=localhost`

### redis.port

Port to connect to a Redis database with.

Example: `redis.port=6379`

### pool.coin

The coin that this pool is mining. Currently only used to ensure that
block notifications are routed correctly.

Example: `pool.coin=monero`

### pool.network

The network that this pool is mining on. Currently only used to ensure that
block notifications are routed correctly.

Example: `pool.network=stagenet`

### payment.allowIntegrated

Allow miners to use an integrated address. Transactions with a payment
id (i.e. payments to a miner using an integrated address) cannot be
batched together, resulting in higher transaction fees.

Example: `payment.allowIntegrated=false`

## api

A Spring Boot application. See [the Spring Boot
documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)
for properties. The important ones are as follows:

### `spring.datasource.url`

JDBC connection URL to your database. Should be the same as the
moneropool application.

Example:
`spring.datasource.url=jdbc:postgresql://localhost:5432/postgres`

### `spring.datasource.username`

Username to connect to the database with. Only needs read permissions.

Example: `spring.datasource.username=postgres`

### `spring.datasource.password`

Password to connect to the database with.

Example: `spring.datsource.password=sergtsop`
