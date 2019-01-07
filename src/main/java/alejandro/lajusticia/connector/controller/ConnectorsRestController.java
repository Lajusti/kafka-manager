package alejandro.lajusticia.connector.controller;

import alejandro.lajusticia.connector.controller.data.UpdateConfigRequest;
import alejandro.lajusticia.connector.service.ConnectorsService;
import alejandro.lajusticia.connector.service.dto.ConnectorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorsRestController {

    private final ConnectorsService connectorsService;

    public ConnectorsRestController(@Autowired ConnectorsService connectorsService) {
        this.connectorsService = connectorsService;
    }

    @GetMapping
    public List<ConnectorDto> getConnectors() {
        return connectorsService.getConnectors();
    }

    @PostMapping
    public ConnectorDto addConnector(@RequestParam String name, @RequestParam String url) {
        return connectorsService.addConnector(name, url);
    }

    @GetMapping("/{id}")
    public ConnectorDto getConnector(@PathVariable int id) {
        return connectorsService.getConnector(id);
    }

    @PostMapping("/{id}")
    public ConnectorDto updateConnector(@PathVariable int id, @RequestParam String name, @RequestParam String url) {
        return connectorsService.updateConnector(id, name, url);
    }

    @DeleteMapping("/{id}")
    public int removeConnector(@PathVariable int id) {
        connectorsService.removeConnector(id);
        return 200;
    }

    @GetMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getConnectorStatus(@PathVariable int id) {
        return connectorsService.getConnectorStatus(id);
    }

    @GetMapping(value = "/{id}/config", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getConnectorConfig(@PathVariable int id) {
        return connectorsService.getConnectorConfig(id);
    }

    @PostMapping(value = "/{id}/config")
    public ConnectorDto updateConnectorConfig(@PathVariable int id, @RequestBody UpdateConfigRequest request) {
        return connectorsService.updateConnectorConfig(id, request);
    }

    @PostMapping(value = "/{id}/pause")
    public int pauseConnector(@PathVariable int id) {
        connectorsService.pauseConnector(id);
        return 200;
    }

    @PostMapping(value = "/{id}/resume")
    public int resumeConnector(@PathVariable int id) {
        connectorsService.resumeConnector(id);
        return 200;
    }

    @PostMapping(value = "/{id}/restart")
    public int restartConnector(@PathVariable int id) {
        connectorsService.restartConnector(id);
        return 200;
    }

    @PostMapping(value = "/{id}/tasks/{idTask}/restart")
    public int restartTask(@PathVariable int id, @PathVariable int idTask) {
        connectorsService.restartTask(id, idTask);
        return 200;
    }

}
