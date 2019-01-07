package alejandro.lajusticia.topic.repository;

import alejandro.lajusticia.topic.entity.Record;
import alejandro.lajusticia.topic.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordJpaRepository extends JpaRepository<Record, Long> {

    List<Record> findByTopicId(long id);

    List<Record> findByTopicIdOrderByDateDesc(long id, Pageable pageable);

    long countByTopic(Topic topic);

    @Query(value = "SELECT * FROM RECORD WHERE TOPIC_ID = ?1 AND (KEY LIKE %?2% OR VALUE LIKE %?2%)",
            countQuery = "SELECT COUNT(*) FROM RECORD WHERE TOPIC_ID = ?1 AND (KEY LIKE %?2% OR VALUE LIKE %?2%)",
            nativeQuery = true)
    List<Record> findWithFilter(long id, String filter, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM RECORD WHERE TOPIC_ID = ?1 AND (KEY LIKE %?2% OR VALUE LIKE %?2%)",
            nativeQuery = true)
    long countWithFilter(Topic topic, String filter);
}
