package uk.offtopica.moneropool;

import lombok.Data;
import uk.offtopica.moneropool.util.ByteArrayUtils;

@Data
public class InstanceId {
    private final long value;

    public byte[] getBytes() {
        return ByteArrayUtils.longToByteArray(value);
    }
}
