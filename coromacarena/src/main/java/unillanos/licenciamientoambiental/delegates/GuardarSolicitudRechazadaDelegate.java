package unillanos.licenciamientoambiental.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unillanos.licenciamientoambiental.service.SolicitudService;

@Component
public class GuardarSolicitudRechazadaDelegate implements JavaDelegate {

    @Autowired
    private SolicitudService solicitudService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        solicitudService.guardarSolicitudRechazada(execution);
    }
}