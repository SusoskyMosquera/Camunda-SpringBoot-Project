package unillanos.licenciamientoambiental.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unillanos.licenciamientoambiental.service.NotificacionService;

@Component
public class NotificarLicenciaOtorgadaDelegate implements JavaDelegate {

    @Autowired
    private NotificacionService notificacionService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String emailSolicitante = (String) execution.getVariable("email");
        notificacionService.notificarLicenciaOtorgada(emailSolicitante);
    }
}