package uk.offtopica.moneropool.stratum.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StratumError {
    private Integer code;
    private String message;
}
