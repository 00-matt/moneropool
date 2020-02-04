# moneropool HTTP API

moneropool exposes various metrics and statistics over HTTP. Requests
to the API server should be passed through a reverse proxy and cached.

This API is not yet stable and may change in backwords-incompatible
ways without warning in the future.

## `GET /stats/pool`

Get global pool statistics.

Example response:

	{
		"estimated_hashrate": 2038,
		"total_miner_count": 2,
		"total_block_count": 8
	}

## `GET /stats/miner/{wallet_address}`

Get stats on a specific miner. Returns 404 if the wallet address has
never been used on the pool before.

Example response:

	{
		"first_seen": "2020-02-04T12:25:30.058973",
		"estimated_hashrate": 1186,
		"valid_shares": 18
	}
