package unillanos.licenciamientoambiental.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudService.class);

    public void guardarSolicitudRechazada(DelegateExecution execution) {
        String codigoSolicitud = (String) execution.getVariable("codigoSolicitud");
        logger.info("Guardando solicitud rechazada con código: {}", codigoSolicitud);
        // En un entorno real, persistir en base de datos
        execution.setVariable("estadoSolicitud", "RECHAZADA");
    }

    public void guardarSolicitudAprobada(DelegateExecution execution) {
        String codigoSolicitud = (String) execution.getVariable("codigoSolicitud");
        logger.info("Guardando solicitud aprobada con código: {}", codigoSolicitud);
        // Persistir en base de datos
        execution.setVariable("estadoSolicitud", "APROBADA");
    }

    public void generarFacturaTarifaEvaluacion(DelegateExecution execution) {
        String nombreSolicitante = (String) execution.getVariable("nombreSolicitante");
        logger.info("Generando factura de tarifa de evaluación para: {}", nombreSolicitante);
        // Lógica para generar factura, calcular costo, etc.
        execution.setVariable("facturaGenerada", true);
        execution.setVariable("montoFactura", 500000.0); // Ejemplo
    }

    public void emitirCostoLicencia(DelegateExecution execution) {
        logger.info("Emitiendo costo de licencia a pagar");
        // Calcular costo basado en variables del proceso
        Double valorProyecto = (Double) execution.getVariable("valorProyecto");
        Double costoLicencia = valorProyecto * 0.05; // Ejemplo: 5% del valor del proyecto
        execution.setVariable("costoLicencia", costoLicencia);
    }

    public void rechazarSolicitud(DelegateExecution execution) {
        String motivo = (String) execution.getVariable("motivoRechazo");
        logger.info("Rechazando solicitud por motivo: {}", motivo);
        execution.setVariable("estadoSolicitud", "RECHAZADA");
    }
}