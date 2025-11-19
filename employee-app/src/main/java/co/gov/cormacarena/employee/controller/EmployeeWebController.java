package co.gov.cormacarena.employee.controller;

import co.gov.cormacarena.employee.service.BandejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class EmployeeWebController {

    @Autowired
    private BandejaService bandejaService;

    @GetMapping("/")
    public String bandeja(Model model) {
        List<Map> tareas = bandejaService.obtenerTareasPendientes();
        model.addAttribute("tareas", tareas);
        return "bandeja";
    }

    @PostMapping("/completar")
    public String completarTarea(@RequestParam Map<String, String> allParams) {
        // Extraemos el ID de la tarea y removemos para que no sea una variable del proceso
        String taskId = allParams.get("taskId");
        allParams.remove("taskId");

        // El resto de parámetros (aprobado, competencia, observaciones) se van al Engine
        bandejaService.completarTarea(taskId, allParams);

        return "redirect:/";
    }
}