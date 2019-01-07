package alejandro.lajusticia.controller;

import alejandro.lajusticia.topic.service.TopicService;
import alejandro.lajusticia.topic.service.dto.TopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AppController {

    private final TopicService topicService;

    public AppController(@Autowired TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping(value = "/")
    public String getIndex() {
        return "index";
    }

    @GetMapping(value ="/topics/{idTopic}")
    public String getTopic(@PathVariable long idTopic) {
        TopicDto topic = topicService.findTopicFromId(idTopic);
        if (topic == null)
            return "redirect:/";
        return "records";
    }

    @GetMapping(value = "/connectors")
    public String getConnectors() {
        return "connectors";
    }

}
