package uk.offtopica.moneropool.stratum.message;

import lombok.Data;

import java.util.Map;

@Data
public class StratumResponse implements StratumMessage {
    private Object id;
    private Map<String, Object> result;
    private StratumError error;
}
