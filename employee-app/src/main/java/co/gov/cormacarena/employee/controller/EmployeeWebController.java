package co.gov.cormacarena.employee.controller;

import co.gov.cormacarena.employee.service.BandejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class EmployeeWebController {

    @Autowired
    private BandejaService bandejaService;

    // Mapeo de ROL -> IDs de Tareas del BPMN
        private static final Map<String, String> ROLES_TAREAS = new HashMap<>();

        private static final List<String> TAREAS_SANCIONATORIO = List.of(
            "Activity_1kmxdrx",
            "Activity_0yrmz41",
            "Activity_0aqdeas",
            "Activity_0v8jnti",
            "Activity_1xnfbt3",
            "Activity_0dtdoio",
            "Activity_0sw7kwk",
            "Activity_1232Cone",
            "Activity_0batq4k",
            "Activity_0l5a265",
            "Activity_0xrka9p",
            "Activity_1u8j2ux",
            "Activity_10rhcun"
        );

        private static final List<String> TAREAS_PQRDS = List.of(
            "Activity_1v8lr4p",
            "Activity_0npouia",
            "Activity_0b3qdlg",
            "Activity_0k6qtqd",
            "Activity_1vjrine",
            "Activity_1v96s1f",
            "Activity_0pmfty7",
            "Activity_1tml0tf",
            "Activity_0607mqq",
            "Activity_1uzqpdw"
        );

    // Variable para agrupar TODAS las tareas internas (excluyendo al ciudadano)
    private static final String TODAS_TAREAS_EMPLEADOS;

    // Nombres de actividades que tienen formulario personalizado (más legibles y estables que taskDefinitionKey)
    private static final Set<String> TAREAS_POR_NOMBRE = Set.of(
            "Verificar que es competencia de Cormacarena",
            "Verificar asignación"
            // Puedes añadir más aquí, ej: "Asignar plazo", "Dar respuesta técnica", etc.
    );

        @SuppressWarnings("unused")
        private static final Map<String, String> NOMBRE_A_FORMULARIO_PQRDS = Map.of(
            "Verificar que es competencia de Cormacarena", "verificarCompetenciaForm",
            "Verificar asignación", "verificarAsignacionForm"
    );
    // Mapeo: nombre de tarea → nombre de fragmento
    private static final Map<String, String> NOMBRE_A_FORMULARIO = Map.of(
            "Verificar que es competencia de Cormacarena", "verificarCompetenciaForm",
            "Verificar asignación", "verificarAsignacionForm"
    );
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

        //PQRDS
        // 1. Inicial
        ROLES_TAREAS.put("Portal", String.join(",",
                "Activity_1v8lr4p", //Verificar que es competencia de Cormacarena
                "Event_1m0pzub" // Iniciar PQRDS
        ));
        ROLES_TAREAS.put("Gestor", String.join(",",
                "Activity_0npouia", //Archivar PQRDS no competencia
                "Activity_0b3qdlg" //Archivar PQRDS competencia

        ));
        ROLES_TAREAS.put("JefePQRDS", String.join(",",
                "Activity_0k6qtqd", //Verificar asignación
                "Activity_1vjrine", //Informar no competencia
                "Activity_1v96s1f",  //Asignar profesionales
                "Activity_0pmfty7",  //Asignar plazo
                "Activity_1tml0tf", //Revisar respuesta
                "Activity_0607mqq"  //Firmar respuesta

        ));
        ROLES_TAREAS.put("ProfesionalPQRDS", String.join(",",
                "Activity_1uzqpdw" //Dar respuesta

        ));


        // 2. OTROS PROCESOS (Sancionatorio + PQRDS)
        ROLES_TAREAS.put("otros",
            Stream.concat(TAREAS_SANCIONATORIO.stream(), TAREAS_PQRDS.stream())
                .collect(Collectors.joining(",")));

        // CONSTRUCCIÓN AUTOMÁTICA DE LA LISTA "VER TODO" (Solo empleados)
        TODAS_TAREAS_EMPLEADOS = ROLES_TAREAS.values().stream()
                .collect(Collectors.joining(","));
    }

    // Lista blanca de IDs que tienen formulario personalizado en forms.html
        private final List<String> TAREAS_CON_FORMULARIO = crearListaTareasConFormulario();

        private static List<String> crearListaTareasConFormulario() {
        List<String> tareas = new ArrayList<>(List.of(
            "Activity_04cs59r", "Activity_0mt96ax", "Activity_1dwso6r",
            "Activity_0qtkyf2", "Activity_0vzruom", "Activity_1gpd4pm",
            "Activity_0zhmpvm", "Activity_1v8lr4p"
        ));
        tareas.addAll(TAREAS_SANCIONATORIO);
        tareas.addAll(TAREAS_PQRDS);
        return tareas;
        }
    @SuppressWarnings("unused")
    private List<Map<String, Object>> cargarTareas(String rol, Model model) {
        String taskKeys = "todos".equals(rol)
                ? TODAS_TAREAS_EMPLEADOS
                : ROLES_TAREAS.getOrDefault(rol, "");

        List<Map<String, Object>> tareas = bandejaService.obtenerTareasPendientes(taskKeys);

        for (Map<String, Object> tarea : tareas) {
            String taskKey = (String) tarea.get("taskDefinitionKey");
            String taskName = (String) tarea.get("name");
            String formKey = "defaultForm";

            // ✅ 1. Prioridad: por NOMBRE de tarea (más estable y legible)
            if (taskName != null && TAREAS_POR_NOMBRE.contains(taskName)) {
                formKey = NOMBRE_A_FORMULARIO.getOrDefault(taskName, "defaultForm");
            }
            // ✅ 2. Fallback: por taskDefinitionKey (para los ya existentes)
            else if (taskKey != null && TAREAS_CON_FORMULARIO.contains(taskKey.trim())) {
                formKey = taskKey.trim();
            }

            tarea.put("formKey", formKey);
        }

        model.addAttribute("tareas", tareas);
        model.addAttribute("rolActual", rol);
        return tareas;
    }


    @GetMapping("/pqrds")
    public String pqrds(@RequestParam(defaultValue = "Portal") String rol, Model model) {
        try {
            String taskKeys;
            if ("todos".equals(rol)) {
                taskKeys = ROLES_TAREAS.entrySet().stream()
                        .filter(e -> e.getKey().contains("PQRDS") || "Portal".equals(e.getKey()) || "Gestor".equals(e.getKey()))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.joining(","));
            } else {
                taskKeys = ROLES_TAREAS.getOrDefault(rol, "");
            }

            List<Map<String, Object>> tareas = bandejaService.obtenerTareasPendientes(taskKeys);

            // ✅ Asignar formKey con lógica robusta (igual que en cargarTareas)
            for (Map<String, Object> tarea : tareas) {
                String taskKey = (String) tarea.get("taskDefinitionKey");
                String taskName = (String) tarea.get("name");
                String formKey = "defaultForm";

                // Prioridad: por nombre
                if (taskName != null && TAREAS_POR_NOMBRE.contains(taskName)) {
                    formKey = NOMBRE_A_FORMULARIO.getOrDefault(taskName, "defaultForm");
                }
                // Fallback: por taskKey
                else if (taskKey != null && TAREAS_CON_FORMULARIO.contains(taskKey.trim())) {
                    formKey = taskKey.trim();
                }

                // ✅ Pero: en /pqrds, todas las tareas que NO sean especiales usan 'defaultForm' (formulario simple)
                if (!TAREAS_POR_NOMBRE.contains(taskName)) {
                    formKey = "defaultForm";
                }

                tarea.put("formKey", formKey);
            }

            model.addAttribute("tareas", tareas);
            model.addAttribute("rolActual", rol);
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        return "pqrds";
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
            List<Map<String, Object>> historial = bandejaService.obtenerHistorialTramites();
            model.addAttribute("historial", historial);
            model.addAttribute("rolActual", "historial"); // Para marcar el nav activo
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando historial: " + e.getMessage());
        }
        return "historial"; // Retorna la vista historial.html
    }
}