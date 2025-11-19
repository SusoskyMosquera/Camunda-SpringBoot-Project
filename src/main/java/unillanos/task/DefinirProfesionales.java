package unillanos.task;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class DefinirProfesionales implements JavaDelegate {


    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // Lista como JSON string
        String listaProfesionalesJson = "["
                + "{\"id\":\"juan\",\"name\":\"Juan Pérez\"},"
                + "{\"id\":\"maria\",\"name\":\"María Gómez\"},"
                + "{\"id\":\"carlos\",\"name\":\"Carlos Ruiz\"}"
                + "]";

        execution.setVariable("listaProfesionales", listaProfesionalesJson);
    }
}
