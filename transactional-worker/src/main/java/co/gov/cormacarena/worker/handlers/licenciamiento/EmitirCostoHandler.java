package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
@ExternalTaskSubscription("emitir-costo-licencia")
public class EmitirCostoHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        System.out.println(">>> CALCULANDO COSTO FINAL DE LICENCIA...");
        // Lógica compleja de cálculo
        externalTaskService.complete(externalTask, Collections.singletonMap("costoLicencia", 5000000L));
    }
}