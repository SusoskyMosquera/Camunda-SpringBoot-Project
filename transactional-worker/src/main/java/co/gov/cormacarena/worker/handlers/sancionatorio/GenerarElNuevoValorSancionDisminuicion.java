package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ExternalTaskSubscription("calcular-nuevo-valor-sancion")
public class GenerarElNuevoValorSancionDisminuicion implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        String usuario = task.getVariable("nombreInfractor");
        Long valorInfraccionAntes = task.getVariable("valorInfraccion");
        Long porcentajeDisminuicion = task.getVariable("porcentajeDisminuicion");

        long valorInfraccionActual = valorInfraccionAntes - (valorInfraccionAntes * porcentajeDisminuicion / 100);

        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Valor infracción anterior: " + valorInfraccionAntes);
        System.out.println("Valor infracción actual: " + valorInfraccionActual);

        service.complete(task, Map.of("valorInfraccion", valorInfraccionActual));
    }
}