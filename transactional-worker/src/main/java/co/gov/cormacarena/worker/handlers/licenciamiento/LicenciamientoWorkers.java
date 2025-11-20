package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.UUID;

@Component
public class LicenciamientoWorkers {

    private static final Logger logger = LoggerFactory.getLogger(LicenciamientoWorkers.class);

    // 1. Generar Factura
    @Component
    @ExternalTaskSubscription("generar-factura-evaluacion")
    public static class GenerarFactura implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String ref = "FACT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            logger.info(">>> Generando factura evaluación: {}", ref);
            service.complete(task, Collections.singletonMap("ReferenciaPagoEvaluacion", ref));
        }
    }

    // 2. Notificar Inconsistencia
    @Component
    @ExternalTaskSubscription("notificar-inconsistencia")
    public static class NotificarInconsistencia implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String obs = (String) task.getVariable("observacionDatosIniciales");
            logger.info(">>> [CORREO] Devolviendo solicitud. Motivo: {}", obs);
            // Al completar esta tarea, el proceso avanza a "Diligenciar Formulario nuevamente" (Cliente)
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("rechazar-solicitud")
    public static class RechazarSolicitud implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Solicitud RECHAZADA definitivamente.");
            // MARCAMOS EL PROCESO COMO RECHAZADO
            service.complete(task, Collections.singletonMap("estadoFinal", "RECHAZADO"));
        }
    }

    // --- MODIFICADO: APROBAR (Añade variable estadoFinal) ---
    @Component
    @ExternalTaskSubscription("guardar-aprobada")
    public static class GuardarAprobada implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Guardando Licencia APROBADA.");
            // MARCAMOS EL PROCESO COMO APROBADO
            service.complete(task, Collections.singletonMap("estadoFinal", "APROBADO"));
        }
    }

    // 4. Notificar Expiración
    @Component
    @ExternalTaskSubscription("notificar-expiracion")
    public static class NotificarExpiracion implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Notificando expiración de plazo de pago.");
            service.complete(task);
        }
    }

    // 5. Notificar Rechazo por Inacción
    @Component
    @ExternalTaskSubscription("notificar-rechazo-inaccion")
    public static class NotificarInaccion implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Notificando rechazo por inacción del usuario.");
            service.complete(task);
        }
    }

    // 6. Guardar Solicitud Rechazada
    @Component
    @ExternalTaskSubscription("guardar-rechazo")
    public static class GuardarRechazo implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Guardando expediente rechazado en historial.");
            service.complete(task);
        }
    }

    // 7. Emitir Costo Licencia
    @Component
    @ExternalTaskSubscription("emitir-costo-licencia")
    public static class EmitirCosto implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String ref = "LIC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            logger.info(">>> Costo de licencia calculado. Ref: {}", ref);
            service.complete(task, Collections.singletonMap("ReferenciaPagoLicencia", ref));
        }
    }

    @Component
    @ExternalTaskSubscription("notificar-licencia-otorgada")
    public static class NotificarOtorgada implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            logger.info(">>> Enviando Licencia oficial al correo del solicitante.");
            service.complete(task);
        }
    }
}