package com.example.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
