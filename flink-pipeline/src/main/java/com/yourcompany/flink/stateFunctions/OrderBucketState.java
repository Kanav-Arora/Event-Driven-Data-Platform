package com.yourcompany.flink.stateFunctions;

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

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> itemCountDesc = new ValueStateDescriptor<>("item_count", Integer.class);
        ValueStateDescriptor<Boolean> hasRejectedDesc = new ValueStateDescriptor<>("has_rejected", Boolean.class);
        itemCount = getRuntimeContext().getState(itemCountDesc);
        hasRejected = getRuntimeContext().getState(hasRejectedDesc);
    }

    @Override
    public void processElement(ValidatedEvent event, Context ctx, Collector<OrderBucketEvent> out) throws Exception {
        int count = (itemCount.value() == null ? 0 : itemCount.value()) + 1;
        if (event.getTotalItems() == count) {
            OrderValidationStatus status;
            if (hasRejected.value() == null)
                hasRejected.update(false);
            if (hasRejected.value())
                status = OrderValidationStatus.REJECTED;
            else
                status = OrderValidationStatus.ACCEPTED;
            out.collect(new OrderBucketEvent(event, status));
            itemCount.clear();
            hasRejected.clear();
        } else {
            if (event.getStatus() == OrderValidationStatus.REJECTED)
                hasRejected.update(true);
            itemCount.update(count);
        }
    }

}
