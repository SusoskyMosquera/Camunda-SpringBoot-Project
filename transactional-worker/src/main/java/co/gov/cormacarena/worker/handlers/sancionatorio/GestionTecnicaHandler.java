package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
public class GestionTecnicaHandler {

    @Component
    @ExternalTaskSubscription("programar-visita")
    public static class ProgramarVisita implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [TECNICA] Visita técnica programada en agenda.");
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("registrar-concepto-tecnico")
    public static class RegistrarConcepto implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [TECNICA] Concepto técnico registrado en el sistema.");
            service.complete(task);
        }
    }
}