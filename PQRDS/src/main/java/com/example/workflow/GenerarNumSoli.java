package com.example.workflow;

import com.example.workflow.servicios.BandejaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class GenerarNumSoli implements JavaDelegate {

    private final BandejaService bandejaService;

    public GenerarNumSoli(BandejaService bandejaService) {
        this.bandejaService = bandejaService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // 1. Construir un número único
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String aleatorio = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        String numeroSolicitud = "PQR-" + fecha + "-" + aleatorio;

        // 2. Guardarlo como variable del proceso
        execution.setVariable("numeroSolicitud", numeroSolicitud);

        // 3. Enviar a bandeja (opcional)
        String reporte =
                "📄 NÚMERO DE SOLICITUD GENERADO\n" +
                        "--------------------------------------\n" +
                        "• Número: " + numeroSolicitud + "\n" +
                        "• Estado: PENDIENTE DE RADICAR\n" +
                        "--------------------------------------";

        bandejaService.guardar(reporte, "Generación de Número");

        // 4. Mostrar en consola
        System.out.println(reporte);
    }
}
