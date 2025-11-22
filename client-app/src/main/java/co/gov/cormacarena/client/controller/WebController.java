package co.gov.cormacarena.client.controller;

import co.gov.cormacarena.client.service.TramitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    // Usamos RestTemplate directamente como tu amigo
    private final RestTemplate restTemplate = new RestTemplate();

    // URL base de Camunda (Engine Server)
    private final String camundaUrl = "http://localhost:8080/engine-rest/";

    @Autowired
    private TramitesService tramitesService; // Lo mantenemos para las otras partes (Bandeja/Licencias) si las necesitas

    @GetMapping("/")
    public String index() { return "index"; }

    // -----------------------------------------------------------
    // FLUJO PQRDS (Estilo Manual / Ventanilla)
    // -----------------------------------------------------------

    @GetMapping("/pqrds")
    public String formPqrds(Model model) {
        // No enviamos JSON ni esquemas raros.
        // Simplemente mostramos la vista HTML que construiremos a mano.
        return "form-pqrds";
    }

    @PostMapping("/pqrds")
    public String iniciarPqrds(@RequestParam Map<String, String> params, Model model) {
        try {
            Map<String, Object> variables = new HashMap<>();

            // Helper para obtener string limpio
            String tipoSolicitud = obtener(params, "tipoSolicitud", "peticion");
            String modo = obtener(params, "modo", "personal");
            String nombre = obtener(params, "nombre");
            String descripcion = obtener(params, "descripcion");
            String recibirRespuesta = obtener(params, "recibirRespuesta");
            String email = obtener(params, "email");
            String telefono = obtener(params, "telefono");
            String fechaSolicitud = obtener(params, "fechaSolicitud");
            String nombreArchivo = obtener(params, "nombreArchivo");

            // Mapeo limpio
            variables.put("tipoSolicitud", crearVariable(tipoSolicitud, "String"));
            variables.put("modo", crearVariable(modo, "String"));
            variables.put("nombre", crearVariable(nombre, "String"));
            variables.put("descripcion", crearVariable(descripcion, "String"));
            variables.put("recibirRespuesta", crearVariable(recibirRespuesta, "String"));
            variables.put("email", crearVariable(email, "String"));
            variables.put("telefono", crearVariable(telefono, "String"));
            variables.put("fechaSolicitud", crearVariable(fechaSolicitud, "String"));
            variables.put("nombreArchivo", crearVariable(nombreArchivo, "String"));

            // Checkbox
            boolean adjunta = params.containsKey("adjDocumentos");
            variables.put("adjDocumentos", crearVariable(adjunta, "Boolean"));

            // Armar request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("variables", variables);

            String url = camundaUrl + "process-definition/key/PQRDS/start";

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            String processId = (String) response.get("id");

            model.addAttribute("mensaje", "¡Radicado Exitoso! ID del proceso: " + processId);
            return "exito";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error al radicar: " + e.getMessage());
            return "exito";
        }
    }


    // -------------------------------------------
// Ayudantes seguros
// -------------------------------------------
    private String obtener(Map<String, String> params, String key) {
        return obtener(params, key, "");
    }

    private String obtener(Map<String, String> params, String key, String defaultValue) {
        String v = params.get(key);
        if (v == null || v.trim().isEmpty()) return defaultValue;
        return v.trim();
    }

    // Crear variable estilo Camunda
    private Map<String, Object> crearVariable(Object valor, String tipo) {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("value", valor);
        varMap.put("type", tipo);
        return varMap;
    }


    // -----------------------------------------------------------
    // OTROS FLUJOS (Se mantienen igual o puedes migrarlos luego)
    // -----------------------------------------------------------

    @GetMapping("/licencias")
    public String formLicencias() { return "form-licencia"; }

    @PostMapping("/licencias")
    public String iniciarLicencia(@RequestParam Map<String, String> params, Model model) {
        try {
            String id = tramitesService.iniciarProceso("licenciamientoAmbientalProcess", params);
            model.addAttribute("mensaje", "Licencia radicada ID: " + id);
            return "exito";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            return "exito";
        }
    }

    @GetMapping("/denuncias")
    public String formDenuncias() { return "form-denuncia"; }

    @PostMapping("/denuncias")
    public String iniciarDenuncia(@RequestParam Map<String, String> params, Model model) {
        try {
            String id = tramitesService.iniciarProceso("SancionatorioAmbiental", params);
            model.addAttribute("mensaje", "Denuncia radicada ID: " + id);
            return "exito";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            return "exito";
        }
    }

    // Bandeja de Tareas (Reutilizamos el servicio por ahora para no alargar el código,
    // pero podrías usar el método mostrarListaTareas de tu amigo aquí también)
    @GetMapping("/mis-tareas")
    public String verBandejaCiudadano(@RequestParam(required = false) String radicadoId, Model model) {
        // ... (Lógica existente de bandeja)
        try {
            if (radicadoId == null || radicadoId.isEmpty()) {
                model.addAttribute("historial", tramitesService.obtenerHistorialCliente());
                model.addAttribute("vista", "lista");
            } else {
                model.addAttribute("tareas", tramitesService.consultarTareasPorProceso(radicadoId));
                model.addAttribute("radicadoId", radicadoId);
                model.addAttribute("vista", "detalle");
            }
            return "mis-tareas";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @PostMapping("/completar-tarea-cliente")
    public String completarTarea(@RequestParam Map<String, String> params, Model model) {
        String taskId = params.get("taskId");
        params.remove("taskId");
        tramitesService.completarTarea(taskId, params);
        return "redirect:/mis-tareas";
    }
}