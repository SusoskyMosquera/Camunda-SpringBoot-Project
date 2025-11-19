package unillanos.servicios;

import org.springframework.stereotype.Service;

@Service
public class BandejaService {
    private volatile String mensaje = "";
    private volatile String tipo = "";

    public void guardar(String msg, String t) {
        this.mensaje = msg;
        this.tipo = t;
    }

    public String getMensaje() { return mensaje; }
    public String getTipo() { return tipo; }
}