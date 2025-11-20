package co.gov.cormacarena.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TramitesService {

    @Autowired
    private RestTemplate restTemplate;

    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    public String iniciarProceso(String processKey, Map<String, String> datosFormulario) {
        String url = ENGINE_URL + "/process-definition/key/" + processKey + "/start";
        return enviarAEngine(url, datosFormulario);
    }

    public void completarTarea(String taskId, Map<String, String> datosFormulario) {
        String url = ENGINE_URL + "/task/" + taskId + "/complete";
        enviarAEngine(url, datosFormulario);
    }

    public List<Map> consultarTareasPorProceso(String processInstanceId) {
        String url = ENGINE_URL + "/task?processInstanceId=" + processInstanceId;
        return restTemplate.getForObject(url, List.class);
    }

    // Método auxiliar privado para reutilizar lógica de conversión y envío
    private String enviarAEngine(String url, Map<String, String> datos) {
        Map<String, Object> variablesCamunda = new HashMap<>();

        for (Map.Entry<String, String> entry : datos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String type = "String";
            Object typedValue = value;

            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                type = "Boolean";
                typedValue = Boolean.parseBoolean(value);
            } else if (value.matches("-?\\d+") && !key.toLowerCase().contains("telefono") && !key.toLowerCase().contains("id")) {
                type = "Long";
                typedValue = Long.parseLong(value);
            }
            variablesCamunda.put(key, Map.of("value", typedValue, "type", type));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("variables", variablesCamunda);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, request, Map.class);
        return response != null ? (String) response.get("id") : null;
    }
}