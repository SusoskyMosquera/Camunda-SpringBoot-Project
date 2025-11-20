package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-inconsistencia")
public class NotificarInconsistenciaHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String obs = externalTask.getVariable("observacionDatosIniciales");
        System.out.println(">>> DEVOLUCIÓN POR INCONSISTENCIA: " + obs);
        externalTaskService.complete(externalTask);
    }
}