package co.gov.cormacarena.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BandejaService {

    @Autowired
    private RestTemplate restTemplate;
    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    public List<Map> obtenerTareasPendientes() {
        return restTemplate.getForObject(ENGINE_URL + "/task?sortBy=created&sortOrder=desc", List.class);
    }

    public void completarTarea(String taskId, Map<String, String> vars) {
        Map<String, Object> variablesCamunda = new HashMap<>();

        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                variablesCamunda.put(key, Map.of("value", Boolean.parseBoolean(value), "type", "Boolean"));
            } else {
                variablesCamunda.put(key, Map.of("value", value, "type", "String"));
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("variables", variablesCamunda);
        restTemplate.postForLocation(ENGINE_URL + "/task/" + taskId + "/complete", body);
    }
}