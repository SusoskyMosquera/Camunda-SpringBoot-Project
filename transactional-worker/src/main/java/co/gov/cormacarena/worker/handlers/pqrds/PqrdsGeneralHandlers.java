package co.gov.cormacarena.worker.handlers.pqrds;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.UUID;

@Component
public class PqrdsGeneralHandlers {

    @Component
    @ExternalTaskSubscription("generar-consecutivo")
    public static class GenerarConsecutivo implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String consecutivo = "PQR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            System.out.println(">>> [PQRDS] Consecutivo generado: " + consecutivo);
            service.complete(task, Collections.singletonMap("numeroRadicado", consecutivo));
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

    @Component
    @ExternalTaskSubscription("radicar-pqrds")
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
}