package com.yourcompany.flink.dataMapper;

import org.apache.flink.api.common.functions.MapFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.flink.models.InventoryEvent;

public class InventoryMapper implements MapFunction<String, InventoryEvent> {
    public static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public InventoryEvent map(String value) throws Exception {
        JsonNode root = mapper.readTree(value);
        JsonNode after = root.get("after");
        JsonNode source = root.get("source");

        return new InventoryEvent("Inventory", after.get("inventory_id").asText(),
                after.get("quantity").asInt(),
                after.get("updated_at").asText(), source.get("snapshot").asText());
    }
}
