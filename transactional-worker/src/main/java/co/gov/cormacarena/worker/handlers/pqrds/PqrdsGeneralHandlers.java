package co.gov.cormacarena.worker.handlers.pqrds;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PqrdsGeneralHandlers {

    // ✅ Usa una clase local como fallback para el logger (no depende de LicenciamientoWorkers)
    private static final Logger logger = LoggerFactory.getLogger(PqrdsGeneralHandlers.class);

    @Component
    @ExternalTaskSubscription("generar-num-solicitud")
    public static class GenerarConsecutivo implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String consecutivo = "PQR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            System.out.println(">>> [PQRDS] Consecutivo generado: " + consecutivo);
            service.complete(task, Collections.singletonMap("numeroRadicado", consecutivo));
        }
    }

    @Component
    @ExternalTaskSubscription("informar-no-competencia")
    public static class InformarNoCompetencia implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String tipoSolicitud = (String) task.getVariable("tipoSolicitud");
            if (tipoSolicitud == null) tipoSolicitud = "Desconocido";

            logger.info(">>> [PQRDS] Rechazando solicitud por NO COMPETENCIA. Tipo: {}", tipoSolicitud);

            String mensaje = "La solicitud de tipo " + tipoSolicitud + " no es competencia de la entidad.";
            service.complete(task, Collections.singletonMap("mensajeRechazo", mensaje));
        }
    }

    @Component
    @ExternalTaskSubscription("radicar-pqrds")
    public static class RadicarPQRDS implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String numeroSolicitud = (String) task.getVariable("numeroSolicitud");
            String nombre = (String) task.getVariable("nombre");
            String tipo = (String) task.getVariable("tipoSolicitud");

            long fakeId = ThreadLocalRandom.current().nextLong(10000, 99999);

            logger.info(">>> [PQRDS] RADICANDO. Solicitante: {}, Tipo: {}, Num: {}", nombre, tipo, numeroSolicitud);
            logger.info(">>> [PQRDS] ID Radicado generado exitosamente: {}", fakeId);

            service.complete(task, Collections.singletonMap("idRadicado", fakeId));
        }
    }

    // ✅ CORREGIDO: Usa String.valueOf() para evitar ClassCastException

    /* ============================================================
       GENERAR INFORME FINAL
    ============================================================ */
    @Component
    @ExternalTaskSubscription("generar-informe-final")
    public static class InformeFinal implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {

            // ⚠️ safe() evita el NullPointerException
            String idRadicado     = safe(task.getVariable("idRadicado"));
            String numeroSolicitud = safe(task.getVariable("numeroSolicitud"));
            String nombre         = safe(task.getVariable("nombre"));
            String email          = safe(task.getVariable("email"));
            String descripcion    = safe(task.getVariable("descripcion"));
            String firma          = safe(task.getVariable("firma"));
            String fechaSolicitud          = safe(task.getVariable("fechaSolicitud"));
            String tipoSolicitud          = safe(task.getVariable("tipoSolicitud"));
            String profesional    = safe(task.getVariable("profesional"));
            String respuestaSolicitud    = safe(task.getVariable("respuestaSolicitud"));
            String recibirRespuesta          = safe(task.getVariable("recibirRespuesta"));
            String telefono          = safe(task.getVariable("telefono"));

            logger.info(">>> [PQRDS] Generando INFORME FINAL para Radicado: {}", idRadicado);
            logger.info(">>> [PQRDS] Detalle Solicitud {} creada por {}. Asunto: {}", numeroSolicitud, nombre, descripcion);

            System.out.println("===================================================================");
            System.out.println(">>> [CORREO SIMULADO] Enviando Respuesta Final PQRDS");
            System.out.println("DESTINO: " + (recibirRespuesta.isBlank() ? "No registrado" : recibirRespuesta));
            System.out.println("ASUNTO: Respuesta final Solicitud " + numeroSolicitud);
            System.out.println("Fecha solicitud: " + (fechaSolicitud.isBlank() ? "No registrado" : fechaSolicitud));
            System.out.println("Nombre: " + (nombre.isBlank() ? "No registrado" : nombre));
            System.out.println("Tipo solicitud: " + (tipoSolicitud.isBlank() ? "No registrado" : tipoSolicitud));
            System.out.println("Respondido por: " + (profesional.isBlank() ? "No registrado" : profesional));
            System.out.println("Respuesta dada: " + (respuestaSolicitud.isBlank() ? "No registrado" : respuestaSolicitud));

            System.out.println("CORREO: " + (email.isBlank() ? "No registrado" : email));
            System.out.println("TELEFONO: " + (telefono.isBlank() ? "No registrado" : recibirRespuesta));

            System.out.println("-------------------------------------------------------------------");
            System.out.println("DESCRIPCIÓN: " + (descripcion.isBlank() ? "Sin descripción" : descripcion));
            System.out.println("FIRMA: " + (firma.isBlank() ? "No firmada" : firma));
            System.out.println("===================================================================");

            String mensajeFinal = "Informe final generado correctamente para el radicado " + idRadicado;
            service.complete(task, Collections.singletonMap("mensajeFinal", mensajeFinal));
        }
    }


    @Component
    @ExternalTaskSubscription("clasificar-dependencia")
    public static class Clasificar implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String asunto = (String) task.getVariable("asunto");
            String dep = (asunto != null && asunto.contains("denuncia")) ? "Juridica" : "AtencionUsuario";
            System.out.println(">>> [PQRDS] Clasificado a: " + dep);
            service.complete(task, Collections.singletonMap("dependenciaAsignada", dep));
        }
    }

    // Nota: 'radicar-pqrdss' parece typo — ¿debería ser 'radicar-pqrds'?
    @Component
    @ExternalTaskSubscription("radicar-pqrdss")
    public static class Radicar implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [PQRDS] Radicación completada en sistema central.");
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("enviar-respuesta")
    public static class EnviarRespuesta implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [PQRDS] Respuesta final enviada al ciudadano.");
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("notificar-usuario-pqrds")
    public static class NotificarUsuario implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [PQRDS] Acuse de recibo enviado.");
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("definir-profesionales")
    public static class DefinirProfesionales implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [PQRDS] Lista de profesionales cargada.");
            service.complete(task, Collections.singletonMap("listaProfesionales", "Ing. Ambiental, Abogado"));
        }
    }

    private static String safe(Object o) {
        return (o == null) ? "" : o.toString();
    }
}