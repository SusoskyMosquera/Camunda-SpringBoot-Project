package co.gov.cormacarena.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TramitesService {

    @Autowired
    private RestTemplate restTemplate;

    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    public String iniciarProceso(String processKey, Map<String, String> datosFormulario) {
        String url = ENGINE_URL + "/process-definition/key/" + processKey + "/start";

        // 1. Convertir datos planos (String) a estructura Camunda (Value/Type)
        Map<String, Object> variablesCamunda = new HashMap<>();

        for (Map.Entry<String, String> entry : datosFormulario.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Detección simple de tipos (puedes mejorar esto)
            String type = "String";
            Object typedValue = value;

            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                type = "Boolean";
                typedValue = Boolean.parseBoolean(value);
            } else if (value.matches("-?\\d+") && !key.contains("telefono") && !key.contains("id")) {
                // Si es número y no es teléfono ni ID, lo tratamos como Long
                type = "Long";
                typedValue = Long.parseLong(value);
            }

            variablesCamunda.put(key, Map.of("value", typedValue, "type", type));
        }

        // 2. Preparar Request
        Map<String, Object> body = new HashMap<>();
        body.put("variables", variablesCamunda);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 3. Enviar y obtener respuesta
        Map response = restTemplate.postForObject(url, request, Map.class);
        return (String) response.get("id");
    }

    // Mantener método antiguo por compatibilidad si es necesario, redirigiendo al nuevo
    public String iniciarLicenciamiento(String nombre, Long valor) {
        return iniciarProceso("LicenciamientoAmbiental", Map.of(
                "nombreSolicitante", nombre,
                "valorProyecto", String.valueOf(valor)
        ));
    }
}