package alejandro.lajusticia.topic.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private String bootstrap;

    @Column
    private String groupId;

    @Column
    private Date date;

    @OneToMany(mappedBy = "topic")
    private List<Record> records;

    @Column
    private int keySerializer;

    @Column
    private int valueSerializer;

    @Column
    private String schemaRegistry;

}
