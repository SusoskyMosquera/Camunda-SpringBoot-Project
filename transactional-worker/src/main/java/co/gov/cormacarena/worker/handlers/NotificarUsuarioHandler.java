package co.gov.cormacarena.worker.handlers;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("notificar-licencia-otorgada")
public class NotificarUsuarioHandler implements ExternalTaskHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificarUsuarioHandler.class);

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String email = externalTask.getVariable("email");
        String radicado = externalTask.getVariable("RadicadoLicencia");

        logger.info("ENVIANDO CORREO a: {} - Su licencia {} ha sido otorgada.", email, radicado);

        // Simulación de envío exitoso
        externalTaskService.complete(externalTask);
    }
}