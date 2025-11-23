package co.gov.cormacarena.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class BandejaService {

    private static final Set<String> CAMPOS_NUMERICOS_OBLIGATORIOS = Set.of(
            "valorInfraccionBase",
            "porcentajeDisminuicion"
    );

    @Autowired
    private RestTemplate restTemplate;
    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerTareasPendientes(String taskKeys) {
        String url = ENGINE_URL + "/task?sortBy=created&sortOrder=desc";

        if (taskKeys != null && !taskKeys.isEmpty()) {
            url += "&taskDefinitionKeyIn=" + taskKeys;
        }

        List<Map<String, Object>> tareas = restTemplate.getForObject(url, List.class);

        if (tareas == null) {
            return Collections.emptyList();
        }

        for (Map<String, Object> tarea : tareas) {
                // 1. Obtener processDefinitionKey de forma robusta
                String procDefId = (String) tarea.get("processDefinitionId");
                String procDefKey = "Desconocido";

                if (procDefId != null) {
                    // Caso 1: Formato "key:version:id" → extraemos key
                    if (procDefId.contains(":")) {
                        procDefKey = procDefId.split(":")[0];
                    }
                    // Caso 2: Solo UUID → consultamos la definición del proceso
                    else {
                        try {
                            String defUrl = ENGINE_URL + "/process-definition/" + procDefId;
                            Map<String, Object> procDef = restTemplate.getForObject(defUrl, Map.class);
                            if (procDef != null) {
                                procDefKey = (String) procDef.get("key");
                                if (procDefKey == null || procDefKey.isEmpty()) {
                                    procDefKey = (String) procDef.get("id");
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("No se pudo obtener processDefinitionKey para ID: " + procDefId);
                        }
                    }
                }

                tarea.put("processDefinitionKey", procDefKey);

                // 2. Cargar variables
                String taskId = (String) tarea.get("id");
                try {
                    String varUrl = ENGINE_URL + "/task/" + taskId + "/variables";
                    Map<String, Object> variables = restTemplate.getForObject(varUrl, Map.class);
                    tarea.put("vars", variables != null ? variables : new HashMap<>());
                } catch (Exception e) {
                    System.err.println("Error variables tarea " + taskId + ": " + e.getMessage());
                    tarea.put("vars", new HashMap<>());
                }
        }

        return tareas;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerHistorialTramites() {
        // Consultamos instancias históricas (finalizadas y activas)
        // Ordenamos por fecha de inicio descendente
        String url = ENGINE_URL + "/history/process-instance?sortBy=startTime&sortOrder=desc";
        List<Map<String, Object>> historial = restTemplate.getForObject(url, List.class);
        return historial != null ? historial : Collections.emptyList();
    }

    public void completarTarea(String taskId, Map<String, String> vars) {
        Map<String, Object> variablesCamunda = new HashMap<>();

        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            variablesCamunda.put(key, crearVariableCamunda(key, value));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("variables", variablesCamunda);
        restTemplate.postForLocation(ENGINE_URL + "/task/" + taskId + "/complete", body);
    }

    private Map<String, Object> crearVariableCamunda(String key, String rawValue) {
        Map<String, Object> payload = new HashMap<>();

        if (rawValue == null) {
            payload.put("value", null);
            payload.put("type", "String");
            return payload;
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            payload.put("value", "");
            payload.put("type", "String");
            return payload;
        }

        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            payload.put("value", Boolean.parseBoolean(value));
            payload.put("type", "Boolean");
            return payload;
        }

        if (esEntero(key, value)) {
            payload.put("value", Long.parseLong(value));
            payload.put("type", "Long");
            return payload;
        }

        if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
            payload.put("value", value + ":00");
            payload.put("type", "String");
            return payload;
        }

        payload.put("value", value);
        payload.put("type", "String");
        return payload;
    }

    private boolean esEntero(String key, String value) {
        if (!value.matches("-?\\d+")) {
            return false;
        }
        if (CAMPOS_NUMERICOS_OBLIGATORIOS.contains(key)) {
            return true;
        }
        String lowerKey = key.toLowerCase(Locale.ROOT);
        return !lowerKey.contains("telefono") && !lowerKey.contains("radicado") && !lowerKey.contains("archivo");
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<Resource> descargarArchivoVariable(String taskId, String variableName) {
        // 1. Obtener metadata de la variable (para saber el nombre del archivo y tipo)
        // Endpoint: GET /task/{id}/variables/{varName}
        String infoUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName;
        Map<String, Object> infoVar = restTemplate.getForObject(infoUrl, Map.class);

        if(infoVar == null || infoVar.get("value") == null) {
            throw new RuntimeException("No hay archivo adjunto");
        }

        // Camunda devuelve info del archivo en valueInfo si es tipo File
        String filename = "descarga.bin";
        Object valueInfo = infoVar.get("valueInfo");
        if (valueInfo instanceof Map<?, ?> rawInfo) {
            Object providedName = rawInfo.get("filename");
            if (providedName instanceof String provided && !provided.isBlank()) {
                filename = provided;
            }
        }

        // 2. Descargar el contenido binario
        // Endpoint: GET /task/{id}/variables/{varName}/data
        String dataUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName + "/data";
        byte[] archivoBytes = restTemplate.getForObject(dataUrl, byte[].class);
        if (archivoBytes == null) {
            throw new IllegalStateException("No se pudo descargar el archivo solicitado");
        }

        // 3. Retornar como recurso descargable
        ByteArrayResource resource = new ByteArrayResource(archivoBytes);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}