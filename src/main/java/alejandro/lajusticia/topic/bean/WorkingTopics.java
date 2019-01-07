package alejandro.lajusticia.topic.bean;

import alejandro.lajusticia.topic.entity.Topic;

import java.util.ArrayList;
import java.util.List;

public class WorkingTopics {

    private List<Topic> topics;

    public WorkingTopics() {
        topics = new ArrayList<>();
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public void removeTopic(Topic topic) {
        topics.remove(topic);
    }

    public boolean hasTopic(Topic topic) {
        for (Topic currentTopic : topics)
            if (topic.getId() == currentTopic.getId())
                return true;
        return false;
    }

}
