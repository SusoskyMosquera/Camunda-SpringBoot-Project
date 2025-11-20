package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
public class RegistroRuiaHandler {

    @Component
    @ExternalTaskSubscription("registrar-ruia")
    public static class RegistrarRuia implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            String infractor = task.getVariable("nombreInfractor");
            System.out.println(">>> [RUIA] Registrando antecedente para: " + infractor);
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("registrar-sancion-ruia")
    public static class RegistrarSancion implements ExternalTaskHandler {
        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            System.out.println(">>> [RUIA] Sanción ejecutoriada registrada exitosamente.");
            service.complete(task);
        }
    }
}