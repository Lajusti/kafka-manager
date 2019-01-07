package alejandro.lajusticia.topic.service;

import alejandro.lajusticia.topic.controller.request.TopicData;
import alejandro.lajusticia.topic.service.dto.TopicDto;
import alejandro.lajusticia.topic.entity.Topic;
import alejandro.lajusticia.topic.repository.TopicJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicService {

    public final static int STRING_SERIALIZER = 0;
    public final static int AVRO_SERIALIZER = 1;

    private final TopicJpaRepository repository;

    private final ConsumersService consumersService;
    private final RecordService recordService;

    public TopicService(@Autowired TopicJpaRepository topicJpaRepository,
                        @Autowired ConsumersService consumersService,
                        @Autowired RecordService recordService
    ) {
        repository = topicJpaRepository;
        this.consumersService = consumersService;
        this.recordService = recordService;
    }

    public List<TopicDto> getAllTopics() {
        List<Topic> topics = repository.findAll();
        List<TopicDto> dtoList = new ArrayList<>();

        for (Topic topic : topics) {
            long records = recordService.countRecordsFromTopic(topic);
            dtoList.add(new TopicDto(topic, records));
        }

        return dtoList;
    }

    public TopicDto createTopic(TopicData newTopic) {
        Topic topic = new Topic();
        topic.setDate(new Date());
        topic.setGroupId(UUID.randomUUID().toString());
        setData(topic, newTopic);
        consumersService.consumeFromTopic(topic);
        return new TopicDto(topic, 0);
    }

    public TopicDto updateTopic(long idTopic, TopicData topicData) {
        Optional<Topic> optionalTopic = repository.findById(idTopic);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            setData(topic, topicData);
            long records = recordService.countRecordsFromTopic(topic);
            return new TopicDto(topic, records);
        } else
            return null;
    }

    private void setData(Topic topic, TopicData dataToSet) {
        topic.setName(dataToSet.getName());
        topic.setBootstrap(dataToSet.getBootstrap());
        topic.setKeySerializer(dataToSet.getKeySerializer());
        topic.setValueSerializer(dataToSet.getValueSerializer());
        topic.setSchemaRegistry(dataToSet.getSchemaRegistry());
        repository.save(topic);
    }

    public void removeTopic(long idTopic) {
        recordService.removeRecordsFromTopic(idTopic);
        repository.deleteById(idTopic);
    }

    public TopicDto findTopicFromId(long idTopic) {
        Optional<Topic> optionalTopic = repository.findById(idTopic);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            long records = recordService.countRecordsFromTopic(topic);
            return new TopicDto(topic, records);
        }
        return null;
    }

}
