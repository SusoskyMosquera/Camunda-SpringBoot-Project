package co.gov.cormacarena.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BandejaService {

    @Autowired
    private RestTemplate restTemplate;
    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    public List<Map> obtenerTareasPendientes(String taskKeys) {
        String url = ENGINE_URL + "/task?sortBy=created&sortOrder=desc";

        if (taskKeys != null && !taskKeys.isEmpty()) {
            url += "&taskDefinitionKeyIn=" + taskKeys;
        }

        List<Map> tareas = restTemplate.getForObject(url, List.class);

        // NUEVO: Enriquecer cada tarea con sus variables (Datos del formulario)
        if (tareas != null) {
            for (Map<String, Object> tarea : tareas) {
                String taskId = (String) tarea.get("id");
                try {
                    // Consultamos las variables de esa tarea específica
                    String varUrl = ENGINE_URL + "/task/" + taskId + "/variables";
                    Map variables = restTemplate.getForObject(varUrl, Map.class);

                    // Las guardamos dentro del objeto tarea bajo la llave "vars"
                    tarea.put("vars", variables);
                } catch (Exception e) {
                    System.err.println("Error cargando variables para tarea " + taskId);
                }
            }
        }

        return tareas;
    }

    public List<Map> obtenerHistorialTramites() {
        // Consultamos instancias históricas (finalizadas y activas)
        // Ordenamos por fecha de inicio descendente
        String url = ENGINE_URL + "/history/process-instance?sortBy=startTime&sortOrder=desc";
        return restTemplate.getForObject(url, List.class);
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

    public ResponseEntity<Resource> descargarArchivoVariable(String taskId, String variableName) {
        // 1. Obtener metadata de la variable (para saber el nombre del archivo y tipo)
        // Endpoint: GET /task/{id}/variables/{varName}
        String infoUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName;
        Map infoVar = restTemplate.getForObject(infoUrl, Map.class);

        if(infoVar == null || infoVar.get("value") == null) {
            throw new RuntimeException("No hay archivo adjunto");
        }

        // Camunda devuelve info del archivo en valueInfo si es tipo File
        Map valueInfo = (Map) infoVar.get("valueInfo");
        String filename = (valueInfo != null && valueInfo.get("filename") != null)
                ? (String) valueInfo.get("filename")
                : "descarga.bin";

        // 2. Descargar el contenido binario
        // Endpoint: GET /task/{id}/variables/{varName}/data
        String dataUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName + "/data";
        byte[] archivoBytes = restTemplate.getForObject(dataUrl, byte[].class);

        // 3. Retornar como recurso descargable
        ByteArrayResource resource = new ByteArrayResource(archivoBytes);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}