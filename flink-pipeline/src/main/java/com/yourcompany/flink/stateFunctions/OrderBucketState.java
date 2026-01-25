package com.yourcompany.flink.stateFunctions;

import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.statics.OrderValidationStatus;

public class OrderBucketState extends KeyedProcessFunction<String, ValidatedEvent, OrderBucketEvent> {
    private ValueState<Integer> itemCount;
    private ValueState<Boolean> hasRejected;
    private MapState<String, Integer> reservedState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> itemCountDesc = new ValueStateDescriptor<>("item_count", Integer.class);
        ValueStateDescriptor<Boolean> hasRejectedDesc = new ValueStateDescriptor<>("has_rejected", Boolean.class);
        MapStateDescriptor<String, Integer> reservedStateDesc = new MapStateDescriptor<>("reserved_state", String.class,
                Integer.class);
        itemCount = getRuntimeContext().getState(itemCountDesc);
        hasRejected = getRuntimeContext().getState(hasRejectedDesc);
        reservedState = getRuntimeContext().getMapState(reservedStateDesc);
    }

    @Override
    public void processElement(ValidatedEvent event, Context ctx, Collector<OrderBucketEvent> out) throws Exception {
        int count = (itemCount.value() == null ? 0 : itemCount.value()) + 1;
        reservedState.put(event.getInventoryId(), event.getQuantity());
        if (event.getTotalItems() == count) {
            OrderValidationStatus status;
            if (hasRejected.value() == null)
                hasRejected.update(false);
            if (hasRejected.value())
                status = OrderValidationStatus.REJECTED;
            else
                status = OrderValidationStatus.ACCEPTED;

            Map<String, Integer> orderItems = new HashMap<>();
            for (Map.Entry<String, Integer> entry : reservedState.entries()) {
                orderItems.put(entry.getKey(), entry.getValue());
            }

            out.collect(new OrderBucketEvent(event, status, orderItems));
            itemCount.clear();
            hasRejected.clear();
            reservedState.clear();
        } else {
            if (event.getStatus() == OrderValidationStatus.REJECTED)
                hasRejected.update(true);
            itemCount.update(count);
        }
    }

}
