package co.gov.cormacarena.employee.controller;

import co.gov.cormacarena.employee.service.BandejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeWebController {

    @Autowired
    private BandejaService bandejaService;

    // Lista de IDs de tareas que TIENEN un formulario personalizado en forms.html
    private final List<String> TAREAS_CON_FORMULARIO = Arrays.asList(
            "Activity_04cs59r", // Verificar Validez (Licencia)
            "Activity_1dwso6r", // Visita Técnica (Licencia)
            "Activity_0vzruom", // Verificar Pago (Licencia)
            "Activity_1v8lr4p", // Verificar Competencia (PQRDS)
            "Activity_0aqdeas", // Determinar Valor (Sancionatorio)
            "Activity_1hc4usc"  // Determinar Infracción (Sancionatorio)
    );

    @GetMapping("/")
    public String bandeja(Model model) {
        try {
            List<Map> tareas = bandejaService.obtenerTareasPendientes();

            // Lógica de seguridad para evitar el error 500
            for (Map<String, Object> tarea : tareas) {
                String taskKey = (String) tarea.get("taskDefinitionKey");

                // Si la tarea tiene form especial, usamos su Key. Si no, usamos "defaultForm".
                if (TAREAS_CON_FORMULARIO.contains(taskKey)) {
                    tarea.put("formKey", taskKey);
                } else {
                    tarea.put("formKey", "defaultForm");
                }
            }

            model.addAttribute("tareas", tareas);
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando tareas: " + e.getMessage());
        }
        return "bandeja";
    }

    @PostMapping("/completar")
    public String completarTarea(@RequestParam Map<String, String> allParams, Model model) {
        try {
            String taskId = allParams.get("taskId");
            if (taskId == null || taskId.isEmpty()) throw new RuntimeException("ID de tarea no recibido.");
            allParams.remove("taskId");

            bandejaService.completarTarea(taskId, allParams);
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error completando tarea: " + e.getMessage());
            // Recargar tareas para mostrar la tabla nuevamente
            try {
                List<Map> tareas = bandejaService.obtenerTareasPendientes();
                // Aplicar la misma lógica de formKey aquí también
                for (Map<String, Object> t : tareas) {
                    String k = (String) t.get("taskDefinitionKey");
                    t.put("formKey", TAREAS_CON_FORMULARIO.contains(k) ? k : "defaultForm");
                }
                model.addAttribute("tareas", tareas);
            } catch (Exception ex) {}
            return "bandeja";
        }
    }
}