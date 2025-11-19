package unillanos.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import unillanos.servicios.BandejaService;

@Controller
public class BandejaController {

    private final BandejaService bandejaService;

    public BandejaController(BandejaService bandejaService) {
        this.bandejaService = bandejaService;
    }

    @GetMapping("/bandeja")
    public String mostrarBandeja(Model model) {

        model.addAttribute("mensaje", bandejaService.getMensaje());
        model.addAttribute("tipo", bandejaService.getTipo());

        return "bandeja";  // apunta a bandeja.html
    }
}