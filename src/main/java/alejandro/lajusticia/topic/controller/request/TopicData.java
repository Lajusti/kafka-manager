package alejandro.lajusticia.topic.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicData {

    private String name;

    private String bootstrap;

    private int keySerializer;

    private int valueSerializer;

    private String schemaRegistry;

}
