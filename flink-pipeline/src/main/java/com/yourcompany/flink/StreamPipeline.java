package com.yourcompany.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import com.yourcompany.flink.dataMapper.InventoryMapper;
import com.yourcompany.flink.dataMapper.OrderItemMapper;
import com.yourcompany.flink.models.InventoryEvent;
import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.OrderItemEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.stateFunctions.InventoryOrderValidationState;
import com.yourcompany.flink.stateFunctions.OrderBucketState;
import com.yourcompany.flink.utils.JsonMapper;

import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

class KafkaConfig {
        public static final String inventory_source_topic = "inventory.events";
        public static final String orders_source_topic = "orders.events";
        public static final String orders_sink_topic = "validated.orders.events";
        public static final String group_id = "";
}

public class StreamPipeline {

        public static void main(String[] args) throws Exception {

                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

                KafkaSource<String> inventorySource = KafkaSource.<String>builder()
                                .setBootstrapServers("kafka:29092").setTopics(KafkaConfig.inventory_source_topic)
                                .setGroupId(KafkaConfig.group_id)
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new SimpleStringSchema()).build();

                KafkaSource<String> ordersSource = KafkaSource.<String>builder()
                                .setBootstrapServers("kafka:29092").setTopics(KafkaConfig.orders_source_topic)
                                .setGroupId(KafkaConfig.group_id).setStartingOffsets(
                                                OffsetsInitializer.committedOffsets(
                                                                OffsetResetStrategy.EARLIEST))
                                .setValueOnlyDeserializer(new SimpleStringSchema()).build();

                DataStream<InventoryEvent> inventoryStream = env.fromSource(inventorySource,
                                WatermarkStrategy.noWatermarks(), "Inventory Source").map(new InventoryMapper())
                                .name("Inventory Event Mapper");

                DataStream<OrderItemEvent> orderItemStream = env
                                .fromSource(ordersSource, WatermarkStrategy.noWatermarks(), "Order Source")
                                .flatMap(new OrderItemMapper()).name("Order Item Event Mapper");

                DataStream<ValidatedEvent> validatedStream = inventoryStream.keyBy(InventoryEvent::getInventoryId)
                                .connect(orderItemStream.keyBy(OrderItemEvent::getInventoryId))
                                .process(new InventoryOrderValidationState());
                // DataStream<ValidatedEvent> inventoryProcessed = validatedStream
                // .filter(r -> "Inventory".equals(r.getSource()));
                DataStream<ValidatedEvent> validatedOrders = validatedStream.filter(r -> "Order".equals(r.getSource()));
                DataStream<OrderBucketEvent> bucketedOrders = validatedOrders.keyBy(ValidatedEvent::getOrderId)
                                .process(new OrderBucketState());
                KafkaSink<String> ordersSink = KafkaSink.<String>builder()
                                .setBootstrapServers("kafka:29092")
                                .setRecordSerializer(
                                                KafkaRecordSerializationSchema.builder()
                                                                .setTopic(KafkaConfig.orders_sink_topic)
                                                                .setValueSerializationSchema(new SimpleStringSchema())
                                                                .build())
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE).build();
                bucketedOrders.map(event -> JsonMapper.toJson(event))
                                .sinkTo(ordersSink);

                env.execute("Flink Streaming Pipeline");
        }
}