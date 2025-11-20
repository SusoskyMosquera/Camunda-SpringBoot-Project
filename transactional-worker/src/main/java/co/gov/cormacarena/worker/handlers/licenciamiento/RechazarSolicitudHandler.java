package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("rechazar-solicitud")
public class RechazarSolicitudHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> PROCESANDO RECHAZO DE SOLICITUD: " + externalTask.getProcessInstanceId());
        // Lógica de negocio: Actualizar estado en DB, enviar correo, etc.
        externalTaskService.complete(externalTask);
    }
}