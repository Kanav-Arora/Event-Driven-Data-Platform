package com.yourcompany.flink.models;

import com.yourcompany.flink.statics.OrderValidationStatus;
import java.time.Instant;

public class OrderBucketEvent {
    private String source;
    private String order_id;
    private int total_items;
    private OrderValidationStatus status;
    private String timestamp;

    public OrderBucketEvent() {
    }

    public OrderBucketEvent(ValidatedEvent event, OrderValidationStatus status) {
        this.source = event.getSource();
        this.order_id = event.getOrderId();
        this.total_items = event.getTotalItems();
        this.status = status;
        this.timestamp = Instant.now().toString();
    }

    public String getSource() {
        return source;
    }

    public String getOrderId() {
        return order_id;
    }

    public int getTotalItems() {
        return total_items;
    }

    public OrderValidationStatus getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
