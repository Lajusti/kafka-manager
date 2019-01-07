package alejandro.lajusticia.topic.service;

import alejandro.lajusticia.topic.bean.WorkingTopics;
import alejandro.lajusticia.topic.entity.Record;
import alejandro.lajusticia.topic.entity.Topic;
import alejandro.lajusticia.topic.repository.RecordJpaRepository;
import alejandro.lajusticia.topic.repository.TopicJpaRepository;
import alejandro.lajusticia.topic.service.dto.RecordDto;
import alejandro.lajusticia.topic.service.dto.TopicDto;
import alejandro.lajusticia.topic.service.dto.TopicWithRecordsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private final RecordJpaRepository repository;
    private final TopicJpaRepository topicRepository;

    private final ConsumersService consumersService;

    private final WorkingTopics workingTopics;

    public RecordService(@Autowired RecordJpaRepository recordJpaRepository,
                         @Autowired TopicJpaRepository topicJpaRepository,
                         @Autowired ConsumersService consumersService,
                         @Autowired WorkingTopics workingTopics
    ) {
        repository = recordJpaRepository;
        topicRepository = topicJpaRepository;
        this.consumersService = consumersService;
        this.workingTopics = workingTopics;
    }

    public TopicWithRecordsDto getRecordsFromTopic(long idTopic, Pageable pageable, String filter) {
        Optional<Topic> optionalTopic = topicRepository.findById(idTopic);
        return optionalTopic.map(topic -> buildDtoFromTopic(topic, pageable, filter)).orElse(null);
    }

    long countRecordsFromTopic(Topic topic) {
        return repository.countByTopic(topic);
    }

    void removeRecordsFromTopic(long idTopic) {
        List<Record> records = repository.findByTopicId(idTopic);
        repository.deleteAll(records);
    }

    public void updateRecordsAndGet(long idTopic) {
        Optional<Topic> optionalTopic = topicRepository.findById(idTopic);
        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();
            consumersService.consumeFromTopic(topic);
        }
    }

    private TopicWithRecordsDto buildDtoFromTopic(Topic topic, Pageable pageable, String filter) {
        List<Record> records;
        long count = 0;
        if (filter != null && !filter.isEmpty()) {
            records = repository.findWithFilter(topic.getId(), filter, pageable);
            count = repository.countWithFilter(topic, filter);
        } else {
            records = repository.findByTopicIdOrderByDateDesc(topic.getId(), pageable);
            count = repository.countByTopic(topic);
        }
        TopicWithRecordsDto dto = new TopicWithRecordsDto(new TopicDto(topic, records.size()));
        dto.setPageInfo(count, pageable.getPageNumber(), pageable.getPageSize());
        dto.setHasMoreRecords(count > pageable.getPageNumber() * pageable.getPageSize() + records.size());
        for (Record record : records)
            dto.addRecord(new RecordDto(record));

        dto.setLoadingRecords(workingTopics.hasTopic(topic));

        return dto;
    }




}
