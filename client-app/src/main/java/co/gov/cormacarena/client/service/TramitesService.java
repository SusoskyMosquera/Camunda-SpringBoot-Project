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

    // --- CORRECCIÓN CRÍTICA ---
    // Solo incluimos las tareas donde el "Assignee" o el carril es el SOLICITANTE.
    // Activity_03jkqxy = Diligenciar Formulario (Inicio)
    // Activity_1z0fd82 = Pagar Tarifa Evaluación
    // Activity_1ocl18k = Pagar Licencia Final
    // Activity_197ohxs = Corregir Formulario (Devolución)
    private final String TAREAS_CLIENTE_KEYS = "Activity_03jkqxy,Activity_1z0fd82,Activity_1ocl18k,Activity_197ohxs";
    // ---------------------------

    public String iniciarProceso(String processKey, Map<String, String> datosFormulario) {
        String url = ENGINE_URL + "/process-definition/key/" + processKey + "/start";
        Map<String, Object> variables = prepararVariables(datosFormulario);
        Map<String, Object> body = new HashMap<>();
        body.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, request, Map.class);
        String processInstanceId = (String) response.get("id");

        // Auto-completar la primera tarea SOLO si es nueva
        if ("licenciamientoAmbientalProcess".equals(processKey) || "SancionatorioAmbiental".equals(processKey)) {
            avanzarTareaInicial(processInstanceId);
        }
        return processInstanceId;
    }

    public void completarTarea(String taskId, Map<String, String> datosFormulario) {
        String url = ENGINE_URL + "/task/" + taskId + "/complete";
        Map<String, Object> variables = prepararVariables(datosFormulario);
        Map<String, Object> body = new HashMap<>();
        body.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForLocation(url, request);
    }

    public List<Map> obtenerTareasDelSolicitante() {
        // El filtro taskDefinitionKeyIn es vital para que no vea tareas de empleados
        String url = ENGINE_URL + "/task?taskDefinitionKeyIn=" + TAREAS_CLIENTE_KEYS + "&sortBy=created&sortOrder=desc";
        List<Map> tareas = restTemplate.getForObject(url, List.class);
        return enriquecerConVariables(tareas);
    }

    public List<Map> consultarTareasPorProceso(String processInstanceId) {
        // Aquí también aplicamos el filtro por si acaso
        String url = ENGINE_URL + "/task?processInstanceId=" + processInstanceId + "&taskDefinitionKeyIn=" + TAREAS_CLIENTE_KEYS;
        List<Map> tareas = restTemplate.getForObject(url, List.class);
        return enriquecerConVariables(tareas);
    }

    public List<Map> obtenerHistorialCliente() {
        String keys = "licenciamientoAmbientalProcess,PQRDS,SancionatorioAmbiental";
        String url = ENGINE_URL + "/history/process-instance?processDefinitionKeyIn=" + keys + "&sortBy=startTime&sortOrder=desc";
        List<Map> historial = restTemplate.getForObject(url, List.class);

        if (historial != null) {
            for (Map proc : historial) {
                String pid = (String) proc.get("id");
                try {
                    // Consultamos la variable 'estadoFinal' del historial para este proceso
                    String varUrl = ENGINE_URL + "/history/variable-instance?processInstanceId=" + pid + "&variableName=estadoFinal";
                    List<Map> vars = restTemplate.getForObject(varUrl, List.class);

                    if (vars != null && !vars.isEmpty()) {
                        String valor = (String) vars.get(0).get("value");
                        proc.put("resultadoNegocio", valor); // Guardamos como 'resultadoNegocio'
                    }
                } catch (Exception e) {
                    // Si falla o no existe variable, lo dejamos null
                }
            }
        }
        return historial;
    }

    public String consultarEstadoTramite(String processInstanceId) {
        try {
            // 1. Verificar estado técnico del proceso
            String urlProc = ENGINE_URL + "/history/process-instance?processInstanceId=" + processInstanceId;
            List<Map> instancias = restTemplate.getForObject(urlProc, List.class);

            if (instancias != null && !instancias.isEmpty()) {
                Map instancia = instancias.get(0);
                String state = (String) instancia.get("state");

                if ("COMPLETED".equals(state)) {
                    // 2. SI TERMINÓ, CONSULTAR LA VARIABLE "estadoFinal" PARA SABER SI APROBÓ O RECHAZÓ
                    String urlVar = ENGINE_URL + "/history/variable-instance?processInstanceId=" + processInstanceId + "&variableName=estadoFinal";
                    List<Map> vars = restTemplate.getForObject(urlVar, List.class);

                    if (vars != null && !vars.isEmpty()) {
                        // Retorna "APROBADO" o "RECHAZADO" según lo que puso el Worker
                        return (String) vars.get(0).get("value");
                    }
                    // Si no hay variable (procesos viejos), asumimos completado genérico
                    return "COMPLETED_UNKNOWN";

                } else if ("EXTERNALLY_TERMINATED".equals(state)) {
                    return "RECHAZADO";
                } else {
                    return "ACTIVE";
                }
            }
            return "NO_ENCONTRADO";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    private List<Map> enriquecerConVariables(List<Map> tareas) {
        if (tareas != null) {
            for (Map<String, Object> tarea : tareas) {
                String taskId = (String) tarea.get("id");
                try {
                    String varUrl = ENGINE_URL + "/task/" + taskId + "/variables";
                    Map variables = restTemplate.getForObject(varUrl, Map.class);
                    tarea.put("vars", variables);
                } catch (Exception e) { }
            }
        }
        return tareas;
    }

    private Map<String, Object> prepararVariables(Map<String, String> datos) {
        Map<String, Object> variablesCamunda = new HashMap<>();
        for (Map.Entry<String, String> entry : datos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String type = "String";
            Object typedValue = value;

            if (value != null) {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    type = "Boolean";
                    typedValue = Boolean.parseBoolean(value);
                } else if (value.matches("-?\\d+") && !key.toLowerCase().contains("telefono") && !key.toLowerCase().contains("id") && !key.toLowerCase().contains("radicado")) {
                    type = "Long";
                    typedValue = Long.parseLong(value);
                }
            }
            variablesCamunda.put(key, Map.of("value", typedValue, "type", type));
        }
        return variablesCamunda;
    }

    private void avanzarTareaInicial(String processId) {
        try {
            String taskUrl = ENGINE_URL + "/task?processInstanceId=" + processId;
            List<Map> tasks = restTemplate.getForObject(taskUrl, List.class);
            if (tasks != null && !tasks.isEmpty()) {
                String taskId = (String) tasks.get(0).get("id");
                String completeUrl = ENGINE_URL + "/task/" + taskId + "/complete";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(new HashMap<>(), headers);
                restTemplate.postForLocation(completeUrl, request);
            }
        } catch (Exception e) { }
    }
}