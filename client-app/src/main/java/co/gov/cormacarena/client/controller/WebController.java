package co.gov.cormacarena.client.controller;

import co.gov.cormacarena.client.service.TramitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class WebController {

    @Autowired
    private TramitesService tramitesService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // --- 1. LICENCIAMIENTO ---
    @GetMapping("/licencias")
    public String formLicencias() {
        return "form-licencia";
    }

    @PostMapping("/licencias")
    public String iniciarLicencia(@RequestParam Map<String, String> params, Model model) {
        return procesarTramite("LicenciamientoAmbiental", params, model);
    }

    // --- 2. PQRDS ---
    @GetMapping("/pqrds")
    public String formPqrds() {
        return "form-pqrds";
    }

    @PostMapping("/pqrds")
    public String iniciarPqrds(@RequestParam Map<String, String> params, Model model) {
        // Mapeo especial para checkbox HTML (si no está marcado no se envía)
        params.putIfAbsent("esCompetencia", "false");
        return procesarTramite("PQRDS", params, model);
    }

    // --- 3. SANCIONATORIO (DENUNCIAS) ---
    @GetMapping("/denuncias")
    public String formDenuncias() {
        return "form-denuncia"; // Asegúrate de crear este HTML si lo necesitas
    }

    @PostMapping("/denuncias")
    public String iniciarDenuncia(@RequestParam Map<String, String> params, Model model) {
        return procesarTramite("SancionatorioAmbiental", params, model);
    }

    // --- MÉTODO GENÉRICO PARA NO REPETIR CÓDIGO ---
    private String procesarTramite(String processKey, Map<String, String> params, Model model) {
        try {
            String idInstancia = tramitesService.iniciarProceso(processKey, params);
            model.addAttribute("mensaje", "¡Radicado Exitoso! Su número de seguimiento es: " + idInstancia);
            return "exito";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al radicar: " + e.getMessage()); // Reusamos exito.html para error simple
            return "exito";
        }
    }
}