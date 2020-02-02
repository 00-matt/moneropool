package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DaemonGetBlockTemplateRequest extends RpcRequestParams {
    @JsonProperty("wallet_address")
    private String address;

    @JsonProperty("reserve_size")
    private Integer reserveSize;
}