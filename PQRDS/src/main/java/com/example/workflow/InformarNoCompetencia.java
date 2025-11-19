package com.example.workflow;

import com.example.workflow.servicios.BandejaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class InformarNoCompetencia implements JavaDelegate {

    private final BandejaService bandejaService;

    public InformarNoCompetencia(BandejaService bandejaService) {
        this.bandejaService = bandejaService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String tipoSolicitud = (String) execution.getVariable("tipoSolicitud");
        if (tipoSolicitud == null) tipoSolicitud = "Desconocido";

        String mensaje = "No es competencia de la organización.";

        String reporte =
                "📋 INFORME: SOLICITUD NO COMPETENTE\n" +
                        "----------------------------------------\n" +
                        "• Resultado: " + mensaje + "\n" +
                        "• Tipo de solicitud: " + tipoSolicitud + "\n" +
                        "• Estado: RECHAZADO\n" +
                        "----------------------------------------";

        // Guardarlo en el servicio
        bandejaService.guardar(reporte, tipoSolicitud);

        // Imprimir en consola también
        System.out.println(reporte);
    }
}