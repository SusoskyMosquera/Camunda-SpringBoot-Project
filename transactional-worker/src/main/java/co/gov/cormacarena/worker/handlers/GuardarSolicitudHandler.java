package co.gov.cormacarena.worker.handlers;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("guardar-solicitud-aprobada")
public class GuardarSolicitudHandler implements ExternalTaskHandler {

    private static final Logger logger = LoggerFactory.getLogger(GuardarSolicitudHandler.class);

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String idSolicitante = externalTask.getVariable("idSolicitante");

        logger.info("Persistiendo solicitud aprobada para ID: {} en base de datos externa...", idSolicitante);

        // Aquí iría tu código de repositorio JPA para guardar en PostgreSQL/MySQL

        externalTaskService.complete(externalTask);
    }
}