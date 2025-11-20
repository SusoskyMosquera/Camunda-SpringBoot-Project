package co.gov.cormacarena.client.controller;

import co.gov.cormacarena.client.service.TramitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    @Autowired
    private TramitesService tramitesService;

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/licencias")
    public String formLicencias() { return "form-licencia"; }

    @PostMapping("/licencias")
    public String iniciarLicencia(@RequestParam Map<String, String> params, Model model) {
        // Llamada simple sin archivo
        return procesarTramite("licenciamientoAmbientalProcess", params, model);
    }

    @GetMapping("/pqrds")
    public String formPqrds() { return "form-pqrds"; }

    @PostMapping("/pqrds")
    public String iniciarPqrds(@RequestParam Map<String, String> params, Model model) {
        params.putIfAbsent("esCompetencia", "false");
        return procesarTramite("PQRDS", params, model);
    }

    @GetMapping("/denuncias")
    public String formDenuncias() { return "form-denuncia"; }

    @PostMapping("/denuncias")
    public String iniciarDenuncia(@RequestParam Map<String, String> params, Model model) {
        return procesarTramite("SancionatorioAmbiental", params, model);
    }

    @GetMapping("/mis-tareas")
    public String verBandejaCiudadano(@RequestParam(required = false) String radicadoId, Model model) {
        try {
            // MODO HISTORIAL (LISTA)
            if (radicadoId == null || radicadoId.isEmpty()) {
                List<Map> historial = tramitesService.obtenerHistorialCliente();
                model.addAttribute("historial", historial);
                model.addAttribute("vista", "lista");
                return "mis-tareas";
            }

            // MODO DETALLE
            List<Map> tareas = tramitesService.consultarTareasPorProceso(radicadoId);

            if (tareas.isEmpty()) {
                String estado = tramitesService.consultarEstadoTramite(radicadoId);

                if ("APROBADO".equals(estado)) {
                    model.addAttribute("mensajeFinal", "¡Felicitaciones! Su trámite ha sido APROBADO y finalizado.");
                    model.addAttribute("tipoFinal", "success");
                } else if ("RECHAZADO".equals(estado)) {
                    model.addAttribute("mensajeFinal", "Su solicitud ha sido RECHAZADA y el proceso ha finalizado.");
                    model.addAttribute("tipoFinal", "danger");
                }
            }
            // ...
            model.addAttribute("radicadoId", radicadoId);
            model.addAttribute("tareas", tareas);
            model.addAttribute("vista", "detalle");
            return "mis-tareas";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "index";
        }
    }
    @PostMapping("/completar-tarea-cliente")
    public String completarTareaCliente(@RequestParam Map<String, String> params, Model model) {
        try {
            String taskId = params.get("taskId");
            params.remove("taskId");
            tramitesService.completarTarea(taskId, params);
            model.addAttribute("mensaje", "Tarea enviada correctamente.");
            List<Map> tareas = tramitesService.obtenerTareasDelSolicitante();
            model.addAttribute("tareas", tareas);
            return "mis-tareas";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "mis-tareas";
        }
    }

    private String procesarTramite(String processKey, Map<String, String> params, Model model) {
        try {
            String idInstancia = tramitesService.iniciarProceso(processKey, params);
            model.addAttribute("mensaje", "¡Radicado Exitoso! ID: " + idInstancia);
            return "exito";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            return "exito";
        }
    }
}