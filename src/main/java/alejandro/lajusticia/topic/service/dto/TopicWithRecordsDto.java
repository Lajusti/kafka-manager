package alejandro.lajusticia.topic.service.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TopicWithRecordsDto {

    private TopicDto topic;
    private List<RecordDto> records;
    private long numRecords;
    private int page;
    private long maxPages;
    private boolean hasMoreRecords;
    private boolean loadingRecords;

    public TopicWithRecordsDto(TopicDto topicDto) {
        this.topic = topicDto;
        records = new ArrayList<>();
    }

    public void addRecord(RecordDto record) {
        records.add(record);
    }

    public void setLoadingRecords(boolean loadingRecords) {
        this.loadingRecords = loadingRecords;
    }

    public void setPageInfo(long numRecords, int page, int recordsPerPage) {
        this.numRecords = numRecords;
        this.page = page;
        this.maxPages = numRecords / recordsPerPage;
        if (this.maxPages * recordsPerPage < numRecords)
            this.maxPages++;
    }

    public void setHasMoreRecords(boolean hasMoreRecords) {
        this.hasMoreRecords = hasMoreRecords;
    }

}
