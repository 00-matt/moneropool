package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class RpcResponseDeserializer extends StdDeserializer<RpcResponse> {
    public RpcResponseDeserializer() {
        super(RpcResponse.class);
    }

    @Override
    public RpcResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        ObjectMapper objectMapper = (ObjectMapper) p.getCodec();
        ObjectNode object = objectMapper.readTree(p);

        RpcResponse response = new RpcResponse();

        if (object.has("error")) {
            response.setError(objectMapper.treeToValue(object.get("error"), RpcError.class));
        } else if (object.has("result")) {
            JsonNode result = object.get("result");
            // TODO: Hacky. Should probably track IDs of outbound requests and use that to determine type.
            // TODO: Use a map of keys to class types?
            if (result.has("blocktemplate_blob")) {
                response.setResult(objectMapper.treeToValue(result, DaemonGetBlockTemplateResult.class));
            } else {
                response.setResult(objectMapper.treeToValue(result, RpcResult.class));
            }
        } else {
            throw new IllegalArgumentException("Unable to deserialize");
        }

        return response;
    }
}
