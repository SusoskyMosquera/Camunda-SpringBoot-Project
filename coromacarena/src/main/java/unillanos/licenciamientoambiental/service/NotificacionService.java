package unillanos.licenciamientoambiental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    public void enviarNotificacion(String destinatario, String asunto, String mensaje) {
        // Simulación de envío de notificación (podría ser email, SMS, etc.)
        logger.info("Enviando notificación a {}: Asunto: {}, Mensaje: {}", destinatario, asunto, mensaje);
        // En un entorno real, integrar con servicio de email como JavaMail o API externa
    }

    public void notificarExpiracionPlazoPago(String emailSolicitante) {
        enviarNotificacion(emailSolicitante, "Expiración del Plazo de Pago",
                "El plazo para el pago de la tarifa de evaluación ha expirado.");
    }

    public void notificarInconsistenciaFormulario(String emailSolicitante) {
        enviarNotificacion(emailSolicitante, "Inconsistencia en el Formulario de Solicitud",
                "Se encontraron inconsistencias en su formulario de solicitud. Por favor, revise y corrija.");
    }

    public void notificarRechazoPorInaccion(String emailSolicitante) {
        enviarNotificacion(emailSolicitante, "Rechazo por Inacción del Solicitante",
                "Su solicitud ha sido rechazada debido a la falta de acción dentro del plazo establecido.");
    }

    public void notificarLicenciaOtorgada(String emailSolicitante) {
        enviarNotificacion(emailSolicitante, "Licencia Ambiental Otorgada",
                "Felicitaciones, su licencia ambiental ha sido otorgada exitosamente.");
    }
}