package com.yourcompany.flink.task;

import org.apache.flink.streaming.api.datastream.DataStream;

import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.stateFunctions.OrderBucketState;

public class BucketOrders {
    public static DataStream<OrderBucketEvent> bucketOrder(DataStream<ValidatedEvent> validatedStream) {
        return validatedStream.filter(r -> "Order".equals(r.getSource()))
                .keyBy(ValidatedEvent::getOrderId)
                .process(new OrderBucketState());
    }
}
