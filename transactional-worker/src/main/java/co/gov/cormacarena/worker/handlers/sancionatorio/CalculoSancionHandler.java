package co.gov.cormacarena.worker.handlers.sancionatorio;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
@ExternalTaskSubscription("calcular-nuevo-valor")
public class CalculoSancionHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Long valorActual = (Long) externalTask.getVariable("sancionTotal");
        Long descuento = (Long) externalTask.getVariable("porcentajeDisminuicion"); // Ojo: variable exacta del BPMN

        if (valorActual == null) valorActual = 1000000L; // Valor default para pruebas
        if (descuento == null) descuento = 0L;

        long nuevoValor = valorActual - (valorActual * descuento / 100);
        System.out.println(">>> [FINANCIERA] Recálculo de sanción: $" + valorActual + " - " + descuento + "% = $" + nuevoValor);

        externalTaskService.complete(externalTask, Collections.singletonMap("sancionTotal", nuevoValor));
    }
}