package uk.offtopica.moneropool.stratum.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StratumRequest implements StratumMessage {
    private Object id;
    private String method;
    private Map<String, Object> params;

    @Transient
    public boolean isNotification() {
        return id == null;
    }
}
