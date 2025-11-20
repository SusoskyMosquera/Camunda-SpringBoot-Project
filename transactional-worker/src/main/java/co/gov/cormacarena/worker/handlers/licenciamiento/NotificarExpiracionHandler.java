package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-expiracion")
public class NotificarExpiracionHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> NOTIFICACIÓN: Su plazo de pago ha expirado.");
        externalTaskService.complete(externalTask);
    }
}