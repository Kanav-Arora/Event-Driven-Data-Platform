package com.yourcompany.flink.task;

import org.apache.flink.streaming.api.datastream.DataStream;

import com.yourcompany.flink.models.InventoryEvent;
import com.yourcompany.flink.models.OrderItemEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.stateFunctions.InventoryOrderValidationState;

public class ValidateStream {
    public static DataStream<ValidatedEvent> validateStream(DataStream<InventoryEvent> inventoryStream,
            DataStream<OrderItemEvent> orderItemStream) {
        return inventoryStream.keyBy(InventoryEvent::getInventoryId)
                .connect(orderItemStream
                        .filter(r -> !"reject-order".equals(r.getEventType()))
                        .keyBy(OrderItemEvent::getInventoryId))
                .process(new InventoryOrderValidationState());
    }
}
