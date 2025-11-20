package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-nuevo-valor")
public class NotificarNuevoValorHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long valor = externalTask.getVariable("sancionTotal");
        System.out.println(">>> [SANCIONATORIO] Notificando ajuste en la multa. Nuevo valor: $" + valor);
        externalTaskService.complete(externalTask);
    }
}