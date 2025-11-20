package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
public class JuridicaHandler {

    @Component
    @ExternalTaskSubscription("imponer-condena")
    public static class ImponerCondena implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [JURIDICA] Sentencia impuesta formalmente.");
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("eliminar-sancion")
    public static class EliminarSancion implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [JURIDICA] Sanción revocada/eliminada por exoneración.");
            service.complete(task);
        }
    }
}