package uk.offtopica.moneropool.rpc;

import lombok.Data;

@Data
public class RpcResponse {
    private RpcError error;
    private RpcResult result;
}
