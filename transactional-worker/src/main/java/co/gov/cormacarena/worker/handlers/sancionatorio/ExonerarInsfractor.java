package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ExternalTaskSubscription("exonerar-infractor")
public class ExonerarInsfractor implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        String usuario = task.getVariable("nombreInfractor");
        String descripcionActoAdministrativo = task.getVariable("descripcionActoAdministrativo");
        Long valorInfraccionAntes = task.getVariable("valorInfraccion");
        Long valorInfraccionActual = 0L;

        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Acto administrativo: " + descripcionActoAdministrativo);
        System.out.println("Valor infracción anterior: " + valorInfraccionAntes);
        System.out.println("Valor infracción actual: " + valorInfraccionActual);

        service.complete(task, Map.of("valorInfraccion", valorInfraccionActual));
    }
}