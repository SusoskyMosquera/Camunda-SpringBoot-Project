package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-infractor")
public class NotificarInfractorHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> [SANCIONATORIO] Notificando al infractor sobre el proceso.");
        externalTaskService.complete(externalTask);
    }
}