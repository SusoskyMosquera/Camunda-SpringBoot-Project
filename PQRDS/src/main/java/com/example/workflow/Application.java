package com.example.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
@EnableProcessApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);

    // Abrir navegador en Windows usando Runtime.exec()
    new Thread(() -> {
      try {
        // Esperar 3 segundos para asegurarse que el servidor esté listo
        Thread.sleep(3000);

        String url = "http://localhost:8080/bandeja";
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
          // Comando para abrir navegador en Windows
          Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
        } else if (os.contains("mac")) {
          Runtime.getRuntime().exec(new String[]{"open", url});
        } else if (os.contains("nix") || os.contains("nux")) {
          Runtime.getRuntime().exec(new String[]{"xdg-open", url});
        } else {
          System.out.println("Sistema operativo no soportado para abrir navegador automáticamente.");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }
}