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
public class BandejaService1 {

    @Autowired
    private RestTemplate restTemplate;
    private final String ENGINE_URL = "http://localhost:8080/engine-rest";

    public List<Map> obtenerTareasPendientes(String taskKeys) {
        String url = ENGINE_URL + "/task?sortBy=created&sortOrder=desc";

        if (taskKeys != null && !taskKeys.isEmpty()) {
            url += "&taskDefinitionKeyIn=" + taskKeys;
        }

        List<Map> tareas = restTemplate.getForObject(url, List.class);

        if (tareas != null) {
            for (Map<String, Object> tarea : tareas) {
                String taskId = (String) tarea.get("id");

                // 1. Añadir processDefinitionKey (Lógica para corregir el error de Thymeleaf)
                String procDefId = (String) tarea.get("processDefinitionId");
                String procDefKey = "Desconocido"; // Valor por defecto

                if (procDefId != null) {
                    try {
                        // Intentamos extraer la clave del proceso. Ej: "LicenciamientoAmbientalProcess:1:..." -> "LicenciamientoAmbientalProcess"
                        if (procDefId.contains(":")) {
                            procDefKey = procDefId.split(":")[0];
                        } else {
                            // Si solo es un UUID, consultamos la definición del proceso (similar a la lógica V2)
                            String defUrl = ENGINE_URL + "/process-definition/" + procDefId;
                            Map procDef = restTemplate.getForObject(defUrl, Map.class);
                            if (procDef != null && procDef.get("key") != null) {
                                procDefKey = (String) procDef.get("key");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("No se pudo obtener processDefinitionKey para ID: " + procDefId);
                    }
                }
                tarea.put("processDefinitionKey", procDefKey);

                // 2. Cargar variables (Lógica original de BandejaService1)
                try {
                    String varUrl = ENGINE_URL + "/task/" + taskId + "/variables";
                    Map variables = restTemplate.getForObject(varUrl, Map.class);
                    tarea.put("vars", variables);
                } catch (Exception e) {
                    System.err.println("Error cargando variables para tarea " + taskId);
                    tarea.put("vars", new HashMap<>()); // Asegura que 'vars' existe
                }
            }
        }

        return tareas;
    }

    // ... (restantes métodos: obtenerHistorialTramites, completarTarea, descargarArchivoVariable sin cambios) ...

    public List<Map> obtenerHistorialTramites() {
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
        String infoUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName;
        Map infoVar = restTemplate.getForObject(infoUrl, Map.class);

        if(infoVar == null || infoVar.get("value") == null) {
            throw new RuntimeException("No hay archivo adjunto");
        }

        Map valueInfo = (Map) infoVar.get("valueInfo");
        String filename = (valueInfo != null && valueInfo.get("filename") != null)
                ? (String) valueInfo.get("filename")
                : "descarga.bin";

        String dataUrl = ENGINE_URL + "/task/" + taskId + "/variables/" + variableName + "/data";
        byte[] archivoBytes = restTemplate.getForObject(dataUrl, byte[].class);

        ByteArrayResource resource = new ByteArrayResource(archivoBytes);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}