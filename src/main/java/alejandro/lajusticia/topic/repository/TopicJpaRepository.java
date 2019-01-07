package alejandro.lajusticia.topic.repository;

import alejandro.lajusticia.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicJpaRepository extends JpaRepository<Topic, Long> {

}
