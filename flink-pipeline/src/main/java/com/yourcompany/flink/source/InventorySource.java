package com.yourcompany.flink.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import com.yourcompany.flink.config;

public class InventorySource {
    public static KafkaSource<String> create() {
        return KafkaSource.<String>builder()
                .setBootstrapServers("kafka:29092").setTopics(config.inventory_source_topic)
                .setGroupId(config.group_id)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema()).build();
    }
}
