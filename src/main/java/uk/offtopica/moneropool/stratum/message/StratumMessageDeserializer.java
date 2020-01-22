package uk.offtopica.moneropool.stratum.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class StratumMessageDeserializer extends StdDeserializer<StratumMessage> {
    public StratumMessageDeserializer() {
        super(StratumMessage.class);
    }

    @Override
    public StratumMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        ObjectMapper objectMapper = (ObjectMapper) p.getCodec();
        ObjectNode object = objectMapper.readTree(p);

        if (object.has("method")) {
            return deserialize(objectMapper, StratumRequest.class, object);
        } else if (object.has("result") || object.has("error")) {
            return deserialize(objectMapper, StratumResponse.class, object);
        }

        throw new IllegalArgumentException("Unable to infer type of object");
    }

    @SuppressWarnings("unchecked")
    private StratumMessage deserialize(ObjectMapper objectMapper,
                                       Class<?> clazz,
                                       ObjectNode object) throws IOException {
        return (StratumMessage) objectMapper.treeToValue(object, clazz);
    }
}
