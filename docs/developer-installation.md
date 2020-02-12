# moneropool Installation Guide For Developers

Following this guide will allow you to quickly get started with
hacking on moneropool. It will not lead to a production-ready pool.

## 1 Requirements

- A modern Linux system (other systems may require minor persuasion)
- Docker
- >=Java 11
- Maven

## 2 Databases

### 2.1 Redis

Start a Redis instance in Docker, and publish the port:

```bash
docker run --name redis --rm -d -p 6379:6379 redis
```

### 2.2 Postgres

Start a Postgres database in Docker, publish the port, and set the
default password:

```bash
docker run --rm -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
```

Open an SQL shell and paste in the contents of `support/schema.sql` to
initialise the database:

```bash
docker run -it --rm postgres psql -h 192.168.1.2 -U postgres
```

This shell is also useful for inspecting the state of the
application. You can exit it by pressing Ctrl+D or entering `\q`.

## 3 Monero Daemon

It is recommended to build the Monero daemon from source so that you
can build the shared libraries used by moneropool:

```bash
# Firstly, install Monero's non-optional dependencies
sudo apt install build-essential cmake pkg-config \
  libboost-all-dev libssl-dev libzmq3-dev libpgm-dev \
  libunbound-dev libsodium-dev git

# Download the Monero source code
git clone https://github.com/monero-project/monero.git
cd monero
git checkout v0.15.0.1

# Build Monero
mkdir build
cd $_
cmake .. -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=ON
make -j$(nproc)

# Install the binaries and shared libraries
sudo cp -v bin/* /usr/local/bin/
find . -name '*.so' -exec sudo cp -v {} /usr/local/lib/ \;

# Update shared library cache
sudo ldconfig
```

You can launch the Monero daemon with the supervisor of your choice
(e.g. systemd or supervisord), or just launch it in a terminal:

```
monerod --stagenet \
  --block-notify '/path/to/moneropool/support/block-notify.sh stagenet %s'
```

Ensure that the path to block-notify.sh is correct, otherwise the pool
will never get new jobs.

## 4 Pool Daemons

### 4.1 moneropool Daemon

moneropool requires some native libraries, build and install those
first:

```bash
# Ensure JAVA_HOME is set. If not, see troubleshooting section.
echo $JAVA_HOME

cd /path/to/moneropool/moneropool
make
sudo cp libmoneropool.so /usr/local/lib/
sudo ldconfig
```

You can then launch moneropool by running the main method from your
IDE, or on the command-line with the following commands:

```bash
# The version 0.1.0-SNAPSHOT might be different. Check it with ls.

mvn package
java -jar target/moneropool-0.1.0-SNAPSHOT.jar
```

If you are following this guide exactly, you will not have to change
any options. See
[application-properties.md](application-properties.md) for options,
such as the daemon address or database password.

## 4.2 API

You can launch the API server the same way via your IDE, or you can
run it on the command line with the following commands:

```bash
cd /path/to/moneropool/api
mvn spring-boot:run
```

## 4.3 PPLNS Payments

Normally the payment runner would be launched by a scheduler like
cron, but while developing you can run it from your IDE, or in your
shell with the following commands:

```
cd /path/to/moneropool/pplns
mvn package
java -jar target/pplns-0.1.0-SNAPSHOT.jar
```

## 5 Testing

Use a miner such as XMRig:

```bash
./xmrig -o localhost:6666 -u YOUR_WALLET_ADDRESS
```

After it has submitted a few shares, query the API:

```bash
curl localhost:8080/stats/miner/YOUR_WALLET_ADDRESS | jq
```

```json
{
	"first_seen": "2020-02-04T12:25:30.058973",
	"estimated_hashrate": 1186,
	"valid_shares": 18
}
```

## 6 Troubleshooting

### 6.1 `JAVA_HOME` not set

`JAVA_HOME` must be set so that the C++ compiler can find the Java
headers for the native library. This environment variable should be
set by your distribution's Java package. If it is unset, consult
Google.

Debian administrators should use the `update-java-alternatives` tool.


### 6.2 `java.lang.UnsatisfiedLinkError: ...`

This error indicates that the native library (`libmoneropool`) could
not be found. You can move the libraries to another directory, or you
can expand Java's search path by setting `LD_LIBRARY_PATH`, e.g.:

```bash
export LD_LIBRARY_PATH=/usr/local/lib64
# (continue as normal)
java -jar ...
```
