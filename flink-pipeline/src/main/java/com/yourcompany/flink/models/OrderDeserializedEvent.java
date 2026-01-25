package com.yourcompany.flink.models;

public class OrderDeserializedEvent {
    public String eventType;
    public String aggregateType;
    public String payload;

    public OrderDeserializedEvent() {
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getPayload() {
        return payload;
    }

}
