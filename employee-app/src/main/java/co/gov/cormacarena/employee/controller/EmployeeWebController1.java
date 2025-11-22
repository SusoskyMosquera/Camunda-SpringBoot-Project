package co.gov.cormacarena.employee.controller;

import co.gov.cormacarena.employee.service.BandejaService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller // <--- ¡Añadir esta anotación!
public class EmployeeWebController1 {

    @Autowired
    private BandejaService1 bandejaService;

    // ... (Mantener la lógica estática ROLES_TAREAS y TAREAS_CON_FORMULARIO tal como la proporcionaste) ...
    // [CÓDIGO ESTÁTICO DE FILTRADO OMITIDO POR BREVEDAD]
    // Mapeo de ROL -> IDs de Tareas del BPMN
    private static final Map<String, String> ROLES_TAREAS = new HashMap<>();
    private static final String TODAS_TAREAS_EMPLEADOS;
    static {
        // 1. LICENCIAMIENTO
        ROLES_TAREAS.put("coordinador", String.join(",",
                "Activity_04cs59r", "Activity_0mt96ax", "Activity_0vzruom"));
        ROLES_TAREAS.put("tecnico", String.join(",",
                "Activity_1dwso6r", "Activity_0qtkyf2"));
        ROLES_TAREAS.put("juridica", "Activity_1gpd4pm");
        ROLES_TAREAS.put("director", "Activity_0zhmpvm");
        // 2. OTROS PROCESOS (PQRDS / Sancionatorio) - Se omiten tareas de PQRDS puras aquí.
        ROLES_TAREAS.put("otros", String.join(",", "Activity_0aqdeas", "Activity_1hc4usc", "Activity_0v8jnti", "Activity_1xnfbt3", "Activity_0ry2plt", "Activity_0dtdoio", "Activity_0sw7kwk", "Activity_1232Cone", "Activity_0batq4k", "Activity_0l5a265", "Activity_0xrka9p", "Activity_1u8j2ux", "Activity_0j9v8ty", "Activity_10rhcun"));
        TODAS_TAREAS_EMPLEADOS = ROLES_TAREAS.values().stream().collect(Collectors.joining(","));
    }
    private final List<String> TAREAS_CON_FORMULARIO = Arrays.asList(
            "Activity_04cs59r", "Activity_0mt96ax", "Activity_1dwso6r",
            "Activity_0qtkyf2", "Activity_0vzruom", "Activity_1gpd4pm",
            "Activity_0zhmpvm", "Activity_1v8lr4p", "Activity_0aqdeas",
            "Activity_1hc4usc"
    );


    // NOTA: Usamos "/licenciamiento" para la URL principal de la V1, o mantenemos "/" si el V2 usa otro.
    @GetMapping("/")
    public String bandeja(@RequestParam(required = false, defaultValue = "todos") String rol, Model model) {
        // ... (Tu lógica original de bandeja, pero el GetMapping es diferente) ...
        try {
            String taskKeys;
            if ("todos".equals(rol)) {
                taskKeys = TODAS_TAREAS_EMPLEADOS;
            } else {
                taskKeys = ROLES_TAREAS.getOrDefault(rol, "");
            }
            // Usa BandejaService1 (V1)
            List<Map> tareas = bandejaService.obtenerTareasPendientes(taskKeys);

            for (Map<String, Object> tarea : tareas) {
                String taskKey = (String) tarea.get("taskDefinitionKey");
                tarea.put("formKey", TAREAS_CON_FORMULARIO.contains(taskKey) ? taskKey : "defaultForm");
            }
            model.addAttribute("tareas", tareas);
            model.addAttribute("rolActual", rol);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "bandeja"; // Asume que la vista sigue siendo la misma.
    }

    // Este endpoint es solo para completar tareas de LICENCIAMIENTO
    @PostMapping("/completar-v1") // <--- ¡Endpoint Modificado!
    public String completarTarea(@RequestParam Map<String, String> allParams) {
        try {
            String taskId = allParams.get("taskId");
            String rolActual = allParams.getOrDefault("rolActual", "todos");
            allParams.remove("taskId");
            allParams.remove("rolActual");

            // Usa BandejaService1 (V1)
            bandejaService.completarTarea(taskId, allParams);

            return "redirect:/licenciamiento?rol=" + rolActual; // Redirige a su propia bandeja
        } catch (Exception e) {
            return "redirect:/licenciamiento?error=" + e.getMessage();
        }
    }
}