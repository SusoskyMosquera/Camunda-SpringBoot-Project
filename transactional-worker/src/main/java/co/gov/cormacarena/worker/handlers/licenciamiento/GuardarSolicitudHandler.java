package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
public class GuardarSolicitudHandler {

    @ExternalTaskSubscription("guardar-aprobada")
    public void guardarAprobada(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> GUARDANDO SOLICITUD APROBADA EN BASE DE DATOS.");
        externalTaskService.complete(externalTask);
    }

    @ExternalTaskSubscription("guardar-rechazo")
    public void guardarRechazada(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> GUARDANDO SOLICITUD RECHAZADA EN BASE DE DATOS.");
        externalTaskService.complete(externalTask);
    }
}