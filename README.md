# moneropool

A Monero mining pool.

## Features

* Scalable pool architecture
* Utilises monerod's `--block-notify` feature to fetch new work
  quickly.
* Validates hashes with RandomX fast/full-memory mode

## Installation and Configuration

For developers: see
[docs/developer-installation.md](docs/developer-installation.md) and
[docs/application-properties.md](docs/application-properties.md).

## Web API

The pool has various API endpoints that can be accessed over HTTP, for
custom integrations with alerts and monitoring dashboards.

The documentation for this can be found at [docs/api.md](docs/api.md).

## Project Structure

* **api** - HTTP API for miners to view statistics like hashrate.
* **docs** - Documentation for setting up and configuring the pool.
* **moneropool** - The server that mining software connects to, to get
  jobs and submit work.
* **support** - Contains things like database schemas, deployment
  scripts, etc.

## Donations

Feel free to send some Moneroj to the following address:

    42wML3xoP4SPn6jYPqgLAVhBJ7yhL386bNfeTjGYk4usMwcKCoAvFwEUoNj1zqxziSVkyRgsgxgdQGZ3eYaDj8jzMHHrzEP

## License

Released under the terms of the MIT license.
See [LICENSE](LICENSE) for more details.
