package alejandro.lajusticia.topic.config;

import alejandro.lajusticia.topic.bean.WorkingTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanDeclaration {


    @Bean
    public WorkingTopics buildWorkingTopics() {
        return new WorkingTopics();
    }

}
