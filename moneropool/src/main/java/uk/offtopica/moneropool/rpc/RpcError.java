package uk.offtopica.moneropool.rpc;

import lombok.Data;

@Data
public class RpcError {
    private Integer code;
    private String message;
}
