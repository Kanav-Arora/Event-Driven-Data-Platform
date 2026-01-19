package com.yourcompany.flink.dataMapper;

import org.apache.flink.util.Collector;
import org.apache.flink.api.common.functions.FlatMapFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.flink.models.OrderItemEvent;

public class OrderItemMapper implements FlatMapFunction<String, OrderItemEvent> {
    public static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void flatMap(String value, Collector<OrderItemEvent> out) throws Exception {
        JsonNode root = mapper.readTree(value);

        JsonNode payload = mapper.readTree(root.get("payload").asText());

        String orderId = payload.get("order_id").asText();
        JsonNode orderItems = payload.get("order_items");

        int totalItems = orderItems.size();

        for (JsonNode item : orderItems) {

            OrderItemEvent event = new OrderItemEvent(
                    "Order",
                    orderId,
                    item.get("inventory_id").asText(),
                    item.get("quantity").asInt(),
                    totalItems);

            out.collect(event);
        }
    }

}
