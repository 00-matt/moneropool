# moneropool HTTP API

moneropool exposes various metrics and statistics over HTTP. Requests
to the API server should be passed through a reverse proxy and cached.

This API is not yet stable and may change in backwords-incompatible
ways without warning in the future.

## `GET /block/recent`

Get the most recent blocks.

Example response:

	[
		{
			"id": 13,
			"height": 512182,
			"paid": false,
			"orphaned": false,
			"difficulty": 183396,
			"expected_reward": 13668202361319,
			"created_at": "2020-02-07T15:20:42.211864"
		},
		{
			"id": 12,
			"height": 512181,
			"paid": false,
			"orphaned": false,
			"difficulty": 183582,
			"expected_reward": 13668228431396,
			"created_at": "2020-02-07T15:17:26.245702"
		},
		...
	]

Note that the expected reward is in atomic units.
1e12 atomic units represent 1 monero.

## `GET /block/{height}`

Get a specific block by its height.

Example response:

	{
		"id": 13,
		"height": 512182,
		"paid": false,
		"orphaned": false,
		"difficulty": 183396,
		"expected_reward": 13668202361319,
		"created_at": "2020-02-07T15:20:42.211864"
	}

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
