package alejandro.lajusticia.topic.service;

import alejandro.lajusticia.topic.bean.WorkingTopics;
import alejandro.lajusticia.topic.entity.Record;
import alejandro.lajusticia.topic.entity.Topic;
import alejandro.lajusticia.topic.repository.RecordJpaRepository;
import alejandro.lajusticia.topic.repository.TopicJpaRepository;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class ConsumersService {

    private final TopicJpaRepository topicRepository;
    private final RecordJpaRepository recordRepository;

    private final WorkingTopics workingTopics;

    private final int GIVE_UP = 6;
    private final int MAX_ITERATIONS = 15;

    //TODO: finalizar con un máximo para no bloquear

    public ConsumersService(@Autowired TopicJpaRepository topicJpaRepository,
                            @Autowired RecordJpaRepository recordJpaRepository,
                            @Autowired WorkingTopics workingTopics
    ) {
        topicRepository = topicJpaRepository;
        recordRepository = recordJpaRepository;
        this.workingTopics = workingTopics;
        consumeAllTopics();
    }

    void consumeFromTopic(Topic topic) {
        workingTopics.addTopic(topic);
        Consumer consumer = getConsumerFromTopic(topic);
        int tryWithZero = 0;
        int iteration = 0;
        Duration duration = Duration.ofSeconds(1);
        while(tryWithZero < GIVE_UP && iteration < MAX_ITERATIONS) {
            ConsumerRecords records = consumer.poll(duration);
            if (records.count() == 0)
                tryWithZero++;
            else
                saveRecords(records, topic);
            consumer.commitAsync();
            iteration++;
        }
        consumer.close();
        workingTopics.removeTopic(topic);
    }

    private void consumeAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        for (Topic topic : topics) {
            topic.setGroupId(UUID.randomUUID().toString());
            topicRepository.save(topic);
            consumeFromTopic(topic);
        }
    }


    private void saveRecords(ConsumerRecords records, Topic topic) {
        if (topic.getKeySerializer() == TopicService.STRING_SERIALIZER
                && topic.getValueSerializer() == TopicService.STRING_SERIALIZER) {
            ConsumerRecords<String, String> castedRecords = records;
            for (ConsumerRecord<String, String> record : castedRecords) {
                Date timestamp = new Date(record.timestamp());
                saveRecord(topic, record.key(), record.value(), record.offset(), record.partition(), timestamp);
            }
        } else if (topic.getKeySerializer() == TopicService.STRING_SERIALIZER
                && topic.getValueSerializer() == TopicService.AVRO_SERIALIZER) {
            ConsumerRecords<String, GenericRecord> castedRecords = records;
            for (ConsumerRecord<String, GenericRecord> record : castedRecords) {
                Date timestamp = new Date(record.timestamp());
                String value = record.value().toString();
                saveRecord(topic, record.key(), value, record.offset(), record.partition(), timestamp);
            }
        } else if (topic.getKeySerializer() == TopicService.AVRO_SERIALIZER
                && topic.getValueSerializer() == TopicService.STRING_SERIALIZER) {
            ConsumerRecords<GenericRecord, String> castedRecords = records;
            for (ConsumerRecord<GenericRecord, String> record : castedRecords) {
                Date timestamp = new Date(record.timestamp());
                String key = record.key().toString();
                saveRecord(topic, key, record.value(), record.offset(), record.partition(), timestamp);
            }
        } else {
            ConsumerRecords<GenericRecord, GenericRecord> castedRecords = records;
            for (ConsumerRecord<GenericRecord, GenericRecord> record : castedRecords) {
                Date timestamp = new Date(record.timestamp());
                String key = record.key().toString();
                String value = record.value().toString();
                saveRecord(topic, key, value, record.offset(), record.partition(), timestamp);
            }
        }


    }

    private void saveRecord(Topic topic, String key, String value, long offset, int partition, Date timestamp) {
        Record entityRecord = new Record();
        entityRecord.setDate(timestamp);
        entityRecord.setKey(key);
        entityRecord.setValue(value);
        entityRecord.setTopic(topic);
        entityRecord.setOffsetValue(offset);
        entityRecord.setPartitionValue(partition);
        recordRepository.save(entityRecord);
    }

    private Consumer getConsumerFromTopic(Topic topic) {
        Consumer consumer;
        if (topic.getKeySerializer() == TopicService.STRING_SERIALIZER
                && topic.getValueSerializer() == TopicService.STRING_SERIALIZER)
            consumer = new KafkaConsumer<String, String>(buildPropertiesFromTopic(topic));
        else if (topic.getKeySerializer() == TopicService.STRING_SERIALIZER
                && topic.getValueSerializer() == TopicService.AVRO_SERIALIZER)
            consumer = new KafkaConsumer<String, GenericRecord>(buildPropertiesFromTopic(topic));
        else if (topic.getKeySerializer() == TopicService.AVRO_SERIALIZER
                && topic.getValueSerializer() == TopicService.STRING_SERIALIZER)
            consumer = new KafkaConsumer<GenericRecord, String>(buildPropertiesFromTopic(topic));
        else
            consumer = new KafkaConsumer<GenericRecord, GenericRecord>(buildPropertiesFromTopic(topic));
        consumer.subscribe(Collections.singleton(topic.getName()));
        return consumer;
    }

    private Properties buildPropertiesFromTopic(Topic topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, topic.getBootstrap());
        if (topic.getKeySerializer() == TopicService.STRING_SERIALIZER)
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        else
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        if (topic.getValueSerializer() == TopicService.STRING_SERIALIZER)
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        else
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, topic.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "lajus_record_ui");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        if (topic.getSchemaRegistry() != null && !topic.getSchemaRegistry().isEmpty())
            properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, topic.getSchemaRegistry());
        return properties;
    }

}
