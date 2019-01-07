package alejandro.lajusticia.connector.controller.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateConfigRequest {

    private List<ConfigParam> config;

}
