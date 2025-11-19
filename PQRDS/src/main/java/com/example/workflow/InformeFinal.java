package com.example.workflow;

import com.example.workflow.servicios.BandejaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class InformeFinal implements JavaDelegate {
    private final BandejaService bandejaService;

    public InformeFinal(BandejaService bandejaService) {
        this.bandejaService = bandejaService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // Obtener variables del proceso
        String idRadicado        = String.valueOf(execution.getVariable("idRadicado"));
        String numeroSolicitud   = (String) execution.getVariable("numeroSolicitud");
        String tipoSolicitud     = (String) execution.getVariable("tipoSolicitud");
        String modo              = (String) execution.getVariable("modo");
        String nombre            = (String) execution.getVariable("nombre");
        String descripcion       = (String) execution.getVariable("descripcion");
        String recibirRespuesta  = (String) execution.getVariable("recibirRespuesta");
        String email             = (String) execution.getVariable("email");
        String telefono          = (String) execution.getVariable("telefono");
        String fechaSolicitud    = (String) execution.getVariable("fechaSolicitud");
        Boolean adjDocumentos    = (Boolean) execution.getVariable("adjDocumentos");
        String nombreArchivo     = (String) execution.getVariable("nombreArchivo");

        if (tipoSolicitud == null) tipoSolicitud = "Desconocido";

        // Construir el mensaje como un solo string
        String mensaje =
                "📌 INFORME FINAL PQRDS\n" +
                        "====================================\n" +
                        "• ID Radicado: " + idRadicado + "\n" +
                        "• Número de Solicitud: " + numeroSolicitud + "\n" +
                        "• Tipo de Solicitud: " + tipoSolicitud + "\n" +
                        "• Modo Registro: " + modo + "\n" +
                        "• Nombre: " + nombre + "\n" +
                        "• Descripción: " + descripcion + "\n" +
                        "• Medio Respuesta: " + recibirRespuesta + "\n" +
                        "• Email: " + email + "\n" +
                        "• Teléfono: " + telefono + "\n" +
                        "• Fecha Solicitud: " + fechaSolicitud + "\n" +
                        "• Adjunta Documentos: " + (adjDocumentos != null && adjDocumentos) + "\n" +
                        "• Nombre Archivo: " + nombreArchivo + "\n" +
                        "====================================";

        // Guardar el mensaje en el servicio (opcional)
        bandejaService.guardar(mensaje, "Informe Final");

        // También dejarlo como variable de proceso para usarlo en formularios
        execution.setVariable("mensajeFinal", mensaje);

        // Imprimir en consola
        System.out.println(mensaje);
    }
}
