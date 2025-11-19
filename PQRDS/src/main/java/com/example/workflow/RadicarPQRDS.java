package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class RadicarPQRDS implements JavaDelegate {
    private final PqrdsRepository repository;

    public RadicarPQRDS(PqrdsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // 1. Obtener variables del proceso
        String numeroSolicitud = (String) execution.getVariable("numeroSolicitud");
        String tipoSolicitud      = (String) execution.getVariable("tipoSolicitud");
        String modo               = (String) execution.getVariable("modo");
        String nombre             = (String) execution.getVariable("nombre");
        String descripcion        = (String) execution.getVariable("descripcion");
        String recibirRespuesta   = (String) execution.getVariable("recibirRespuesta");
        String email              = (String) execution.getVariable("email");
        String telefono           = (String) execution.getVariable("telefono");
        String fechaSolicitud     = (String) execution.getVariable("fechaSolicitud");
        Boolean adjDocumentos     = (Boolean) execution.getVariable("adjDocumentos");
        String nombreArchivo      = (String) execution.getVariable("nombreArchivo");

        // 2. Crear entidad
        Pqrds p = new Pqrds();
        p.setNumeroSolicitud(numeroSolicitud);
        p.setTipoSolicitud(tipoSolicitud);
        p.setModo(modo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setMedioRespuesta(recibirRespuesta);
        p.setEmail(email);
        p.setTelefono(telefono);
        p.setFechaSolicitud(fechaSolicitud);
        p.setAdjuntaDocumento(adjDocumentos != null && adjDocumentos);
        p.setNombreArchivo(nombreArchivo);

        // 3. Guardar en BD
        repository.save(p);

        // 4. Mensaje en consola
        System.out.println("====================================");
        System.out.println("📌 PQRDS RADICADA EXITOSAMENTE");
        System.out.println("====================================");
        System.out.println("• ID Radicado: " + p.getId());
        System.out.println("• Número de Solicitud: " + numeroSolicitud);
        System.out.println("• Tipo de Solicitud: " + tipoSolicitud);
        System.out.println("• Modo Registro: " + modo);
        System.out.println("• Nombre: " + nombre);
        System.out.println("• Descripción: " + descripcion);
        System.out.println("• Medio Respuesta: " + recibirRespuesta);
        System.out.println("• Email: " + email);
        System.out.println("• Teléfono: " + telefono);
        System.out.println("• Fecha Solicitud: " + fechaSolicitud);
        System.out.println("• Adjunta Documentos: " + adjDocumentos);
        System.out.println("• Nombre Archivo: " + nombreArchivo);
        System.out.println("====================================");

        // 5. Enviar ID al proceso
        execution.setVariable("idRadicado", p.getId());
    }
}
