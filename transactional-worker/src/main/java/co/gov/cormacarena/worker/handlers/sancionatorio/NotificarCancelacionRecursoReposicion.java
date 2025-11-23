package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-cancelacion-recurso")
public class NotificarCancelacionRecursoReposicion implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        String usuario = task.getVariable("nombreInfractor");
        String mensajeCancelacion = "No se puede dar los recursos de reposición";

        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Mensajes: " + mensajeCancelacion);

        service.complete(task);
    }
}