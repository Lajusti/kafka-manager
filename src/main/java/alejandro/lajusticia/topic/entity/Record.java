package alejandro.lajusticia.topic.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String key;

    @Lob
    @Column
    private String value;

    @ManyToOne
    @JoinColumn(name = "topicId")
    private Topic topic;

    @Column
    private Date date;

    @Column
    private Long offsetValue;

    @Column
    private Integer partitionValue;

}
