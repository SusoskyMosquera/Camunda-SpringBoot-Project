package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("archivar-documentos")
public class ArchivarDocumentosHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String radicado = externalTask.getBusinessKey();
        System.out.println(">>> [ARCHIVO] Guardando expediente " + radicado + " en repositorio documental.");
        externalTaskService.complete(externalTask);
    }
}