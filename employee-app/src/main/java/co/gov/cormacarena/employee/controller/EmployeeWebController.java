package co.gov.cormacarena.employee.controller;

import co.gov.cormacarena.employee.service.BandejaService;
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

@Controller
public class EmployeeWebController {

    @Autowired
    private BandejaService bandejaService;

    // Mapeo de ROL -> IDs de Tareas del BPMN
    private static final Map<String, String> ROLES_TAREAS = new HashMap<>();

    // Variable para agrupar TODAS las tareas internas (excluyendo al ciudadano)
    private static final String TODAS_TAREAS_EMPLEADOS;

    static {
        // 1. LICENCIAMIENTO
        ROLES_TAREAS.put("coordinador", String.join(",",
                "Activity_04cs59r", // Verificar validez
                "Activity_0mt96ax", // Asignar profesional
                "Activity_0vzruom"  // Verificar pago
        ));

        ROLES_TAREAS.put("tecnico", String.join(",",
                "Activity_1dwso6r", // Visita técnica
                "Activity_0qtkyf2"  // Concepto técnico
        ));

        ROLES_TAREAS.put("juridica", "Activity_1gpd4pm"); // Resolución
        ROLES_TAREAS.put("director", "Activity_0zhmpvm"); // Firmar

        // 2. OTROS PROCESOS (PQRDS / Sancionatorio)
        ROLES_TAREAS.put("otros", String.join(",",
                "Activity_1v8lr4p", // PQRDS: Verificar competencia
                "Activity_0k6qtqd", // PQRDS: Verificar asignación
                "Activity_1vjrine", // PQRDS: Informar no competencia
                "Activity_0npouia", // PQRDS: Archivar
                "Activity_1v96s1f", // PQRDS: Asignar profesionales
                "Activity_0pmfty7", // PQRDS: Asignar plazo
                "Activity_1uzqpdw", // PQRDS: Dar respuesta
                "Activity_1tml0tf", // PQRDS: Revisar respuesta
                "Activity_0607mqq", // PQRDS: Firmar respuesta
                "Activity_0b3qdlg", // PQRDS: Archivar final

                "Activity_0aqdeas", // SANC: Valor infracción
                "Activity_1hc4usc", // SANC: Determinar infracción
                "Activity_0v8jnti", // SANC: Radicar denuncia
                "Activity_1xnfbt3", // SANC: Formular cargos
                "Activity_0ry2plt", // SANC: Recibir recursos
                "Activity_0dtdoio", // SANC: Informe tecnico
                "Activity_0sw7kwk", // SANC: Verificar hechos
                "Activity_1232Cone",// SANC: Elaborar concepto
                "Activity_0batq4k", // SANC: Registrar flagrancia
                "Activity_0l5a265", // SANC: Emitir concepto pruebas
                "Activity_0xrka9p", // SANC: Determinar recursos
                "Activity_1u8j2ux", // SANC: Determinar condena
                "Activity_0j9v8ty", // SANC: Recepción descargos
                "Activity_10rhcun"  // SANC: Recibir respuesta infractor
        ));

        // CONSTRUCCIÓN AUTOMÁTICA DE LA LISTA "VER TODO" (Solo empleados)
        TODAS_TAREAS_EMPLEADOS = ROLES_TAREAS.values().stream()
                .collect(Collectors.joining(","));
    }

    // Lista blanca de IDs que tienen formulario personalizado en forms.html
    private final List<String> TAREAS_CON_FORMULARIO = Arrays.asList(
            "Activity_04cs59r", "Activity_0mt96ax", "Activity_1dwso6r",
            "Activity_0qtkyf2", "Activity_0vzruom", "Activity_1gpd4pm",
            "Activity_0zhmpvm", "Activity_1v8lr4p", "Activity_0aqdeas",
            "Activity_1hc4usc"
    );

    @GetMapping("/")
    public String bandeja(@RequestParam(required = false, defaultValue = "todos") String rol, Model model) {
        try {
            String taskKeys;

            // Lógica de filtrado
            if ("todos".equals(rol)) {
                taskKeys = TODAS_TAREAS_EMPLEADOS; // AHORA FILTRA, NO TRAE TODO
            } else {
                taskKeys = ROLES_TAREAS.getOrDefault(rol, "");
            }

            List<Map> tareas = bandejaService.obtenerTareasPendientes(taskKeys);

            // Asignar formularios
            for (Map<String, Object> tarea : tareas) {
                String taskKey = (String) tarea.get("taskDefinitionKey");
                tarea.put("formKey", TAREAS_CON_FORMULARIO.contains(taskKey) ? taskKey : "defaultForm");
            }

            model.addAttribute("tareas", tareas);
            model.addAttribute("rolActual", rol);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "bandeja";
    }

    @PostMapping("/completar")
    public String completarTarea(@RequestParam Map<String, String> allParams) {
        try {
            String taskId = allParams.get("taskId");
            String rolActual = allParams.getOrDefault("rolActual", "todos");
            allParams.remove("taskId");
            allParams.remove("rolActual");

            bandejaService.completarTarea(taskId, allParams);

            return "redirect:/?rol=" + rolActual;
        } catch (Exception e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        try {
            List<Map> historial = bandejaService.obtenerHistorialTramites();
            model.addAttribute("historial", historial);
            model.addAttribute("rolActual", "historial"); // Para marcar el nav activo
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando historial: " + e.getMessage());
        }
        return "historial"; // Retorna la vista historial.html
    }
}