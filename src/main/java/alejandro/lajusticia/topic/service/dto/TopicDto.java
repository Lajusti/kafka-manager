package alejandro.lajusticia.topic.service.dto;

import alejandro.lajusticia.topic.entity.Topic;
import lombok.Getter;

import java.util.Date;

@Getter
public class TopicDto {

    public TopicDto(Topic topic, long records) {
        id = topic.getId();
        name = topic.getName();
        bootstrap = topic.getBootstrap();
        date = topic.getDate();
        this.records = records;
        keySerializer = topic.getKeySerializer();
        valueSerializer = topic.getValueSerializer();
        schemaRegistry = topic.getSchemaRegistry();
    }

    private long id;

    private String name;

    private String bootstrap;

    private Date date;

    private long records;

    private int keySerializer;

    private int valueSerializer;

    private String schemaRegistry;

}
