package com.yourcompany.flink.deserializer;

import java.nio.charset.StandardCharsets;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.yourcompany.flink.models.OrderDeserializedEvent;

public class OrderRecordDeserializer implements KafkaRecordDeserializationSchema<OrderDeserializedEvent> {
    @Override
    public void deserialize(
            ConsumerRecord<byte[], byte[]> record,
            Collector<OrderDeserializedEvent> out) {

        OrderDeserializedEvent event = new OrderDeserializedEvent();

        if (record.headers() != null) {
            var h = record.headers();
            if (h.lastHeader("event_type") != null) {
                event.eventType = new String(
                        h.lastHeader("event_type").value(),
                        StandardCharsets.UTF_8);
            }
            if (h.lastHeader("aggregate_type") != null) {
                event.aggregateType = new String(
                        h.lastHeader("aggregate_type").value(),
                        StandardCharsets.UTF_8);
            }
        }
        event.payload = new String(record.value(), StandardCharsets.UTF_8);
        out.collect(event);
    }

    @Override
    public TypeInformation<OrderDeserializedEvent> getProducedType() {
        return TypeInformation.of(OrderDeserializedEvent.class);
    }
}
