package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-infractor")
public class NotifyUserDelegate implements ExternalTaskHandler {

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        String usuario = task.getVariable("nombreInfractor");
        String tipoInfraccion = task.getVariable("tipoInfraccion");
        Long valorInfracion = task.getVariable("sancionTotal");
        String descripcionConclusiones = task.getVariable("descripcionConclusiones");

        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Tipo de infraccion: " + tipoInfraccion);
        System.out.println("Valor de la infraccion: " + valorInfracion);
        System.out.println("Descripcion de la infraccion: " + descripcionConclusiones);

        service.complete(task);
    }
}
