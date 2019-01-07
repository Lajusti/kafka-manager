package alejandro.lajusticia.connector.service.dto;

import alejandro.lajusticia.connector.entity.Connector;
import lombok.Getter;

@Getter
public class ConnectorDto {

    private long id;
    private String name;
    private String url;

    public ConnectorDto(Connector connector) {
        this.id = connector.getId();
        this.name = connector.getName();
        this.url = connector.getUrl();
    }

}
