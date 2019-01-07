package alejandro.lajusticia.topic.controller;

import alejandro.lajusticia.topic.controller.request.TopicData;
import alejandro.lajusticia.topic.service.RecordService;
import alejandro.lajusticia.topic.service.TopicService;
import alejandro.lajusticia.topic.service.dto.TopicDto;

import alejandro.lajusticia.topic.service.dto.TopicWithRecordsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/topics")
public class TopicRestController {

    private final TopicService topicService;
    private final RecordService recordService;

    public TopicRestController(@Autowired TopicService topicService,
                               @Autowired RecordService recordService
    ) {
        this.topicService = topicService;
        this.recordService = recordService;
    }

    @GetMapping
    public List<TopicDto> getTopics() {
        return topicService.getAllTopics();
    }

    @PostMapping
    public TopicDto newTopic(@RequestBody TopicData newTopic) {
        return topicService.createTopic(newTopic);
    }

    @PostMapping(value = "/{idTopic}")
    public TopicDto updateTopic(@RequestBody TopicData topicData, @PathVariable long idTopic) {
        return topicService.updateTopic(idTopic,topicData);
    }

    @DeleteMapping(value = "/{idTopic}")
    public void deleteTopic(@PathVariable long idTopic) {
        topicService.removeTopic(idTopic);
    }

    @GetMapping(value = "/{idTopic}/records")
    public TopicWithRecordsDto getRecordsFromTopic(@PathVariable long idTopic, Pageable pageable, String filter) {
        return recordService.getRecordsFromTopic(idTopic, pageable, filter);
    }

    @PutMapping(value = "/{idTopic}/records")
    public TopicWithRecordsDto updateRecords(@PathVariable long idTopic, Pageable pageable) {
        recordService.updateRecordsAndGet(idTopic);
        return recordService.getRecordsFromTopic(idTopic, pageable, null);
    }
}
