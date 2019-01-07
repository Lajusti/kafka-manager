package alejandro.lajusticia.topic.service.dto;

import alejandro.lajusticia.topic.entity.Record;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RecordDto {

    private long id;
    private String key;
    private String value;
    private Date date;
    private long offset;
    private long partition;

    public RecordDto(Record record) {
        id = record.getId();
        key = record.getKey();
        value = record.getValue();
        date = record.getDate();
        offset = record.getOffsetValue();
        partition = record.getPartitionValue();
    }

}
