package co.gov.cormacarena.worker.handlers.licenciamiento;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@ExternalTaskSubscription("generar-factura-evaluacion") // Debe coincidir con el 'Topic' en el BPMN
public class GenerarFacturaHandler implements ExternalTaskHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenerarFacturaHandler.class);

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        logger.info("Iniciando generación de factura para proceso: {}", externalTask.getProcessInstanceId());

        // 1. Obtener variables del proceso
        String nombreSolicitante = externalTask.getVariable("nombreSolicitante");
        Long valorProyecto = externalTask.getVariable("valorProyecto");

        // 2. Lógica de negocio (Simulada)
        logger.info("Calculando tarifa para: {}", nombreSolicitante);
        String referenciaPago = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. Retornar resultados al Engine
        Map<String, Object> variables = new HashMap<>();
        variables.put("ReferenciaPagoEvaluacion", referenciaPago);
        variables.put("costoCalculado", valorProyecto != null ? valorProyecto * 0.05 : 100000);

        logger.info("Factura generada: {}", referenciaPago);

        // 4. Completar la tarea
        externalTaskService.complete(externalTask, variables);
    }
}