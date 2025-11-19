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
        // Traemos todas las tareas. En prod filtraríamos por grupo de usuario.
        String url = ENGINE_URL + "/task?sortBy=created&sortOrder=desc";
        return restTemplate.getForObject(url, List.class);
    }

    public void completarTarea(String taskId, Map<String, String> variablesFormulario) {
        String url = ENGINE_URL + "/task/" + taskId + "/complete";

        // Convertimos el Map<String, String> del formulario a Map<String, Object> de Camunda
        Map<String, Object> variablesCamunda = new HashMap<>();

        for (Map.Entry<String, String> entry : variablesFormulario.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Inferencia básica de tipos para booleanos (importante para los Gateways)
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("si") || value.equalsIgnoreCase("no")) {
                // Truco: Camunda a veces prefiere Strings para gateways tipo 'competencia=="si"',
                // pero Booleans para '${aprobado}'.
                // Aquí lo enviaremos como String si es "si/no" y Boolean si es "true/false".

                if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    variablesCamunda.put(key, Map.of("value", Boolean.parseBoolean(value), "type", "Boolean"));
                } else {
                    variablesCamunda.put(key, Map.of("value", value, "type", "String"));
                }
            } else {
                variablesCamunda.put(key, Map.of("value", value, "type", "String"));
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("variables", variablesCamunda);

        restTemplate.postForLocation(url, body);
    }
}