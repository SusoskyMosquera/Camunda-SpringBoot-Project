package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-cargo-presunto")
public class NotificarCargoPresuntoInfractor implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        String usuario = task.getVariable("nombreInfractor");
        String descripcionFormulacionCargos = task.getVariable("DescripcionFormulacionCargos");

        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Descripción de los cargos: " + descripcionFormulacionCargos);

        service.complete(task);
    }
}