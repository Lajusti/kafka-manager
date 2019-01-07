package alejandro.lajusticia.connector.service;

import alejandro.lajusticia.connector.controller.data.ConfigParam;
import alejandro.lajusticia.connector.controller.data.UpdateConfigRequest;
import alejandro.lajusticia.connector.entity.Connector;
import alejandro.lajusticia.connector.repository.ConnectorJpaRepository;
import alejandro.lajusticia.connector.service.dto.ConnectorDto;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectorsService {

    private final ConnectorJpaRepository repository;

    public ConnectorsService(@Autowired ConnectorJpaRepository connectorJpaRepository) {
        repository = connectorJpaRepository;
    }

    public List<ConnectorDto> getConnectors() {
        List<Connector> connectors = repository.findAll();
        List<ConnectorDto> dtoList = new ArrayList<>();

        for (Connector connector : connectors)
            dtoList.add(new ConnectorDto(connector));

        return dtoList;
    }

    public ConnectorDto addConnector(String name, String url) {
        Connector connector = new Connector();
        connector.setName(name);
        connector.setUrl(url);
        repository.save(connector);
        return new ConnectorDto(connector);
    }

    public ConnectorDto getConnector(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        return optionalConnector.map(ConnectorDto::new).orElse(null);
    }

    public void removeConnector(long id) {
        repository.deleteById(id);
    }

    public ConnectorDto updateConnector(long id, String name, String url) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            connector.setName(name);
            connector.setUrl(url);
            repository.save(connector);
            return new ConnectorDto(connector);
        }
        return null;
    }

    public String getConnectorStatus(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            try {
                Connector connector = optionalConnector.get();
                return getGetResponseFromUrl(connector.getUrl() + "/status");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public String getConnectorConfig(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            try {
                Connector connector = optionalConnector.get();
                return getGetResponseFromUrl(connector.getUrl());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private String getGetResponseFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Reader reader = new InputStreamReader(con.getInputStream());
        String response = IOUtils.toString(reader);
        con.disconnect();
        return cleanString(response);
    }

    private String cleanString(String response) {
        return response.replace("\\", "\\\\");
    }

    public ConnectorDto updateConnectorConfig(long id, UpdateConfigRequest request) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            String content = "{";
            for (ConfigParam param : request.getConfig()) {
                if (!"{".equals(content))
                    content += ",";
                content += "\"" + param.getName() + "\":\"" + param.getValue() + "\"";
            }
            content += "}";

            try {
                URL url = new URL(connector.getUrl() + "/config");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("PUT");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(content);
                wr.close();
                con.getResponseCode();
                con.disconnect();
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
            return new ConnectorDto(connector);
        }
        return null;
    }

    public void pauseConnector(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            try {
                doCall(connector.getUrl() + "/pause", "PUT");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void resumeConnector(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            try {
                doCall(connector.getUrl() + "/resume", "PUT");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void restartConnector(long id) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            try {
                doCall(connector.getUrl() + "/restart", "POST");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void doCall(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.getResponseCode();
        con.disconnect();
    }

    public void restartTask(long id, int idTask) {
        Optional<Connector> optionalConnector = repository.findById(id);
        if (optionalConnector.isPresent()) {
            Connector connector = optionalConnector.get();
            try {
                doCall(connector.getUrl() + "/tasks/" + idTask + "/restart", "POST");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
