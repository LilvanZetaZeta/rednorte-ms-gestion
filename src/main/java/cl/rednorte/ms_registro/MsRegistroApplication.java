package cl.rednorte.ms_registro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

// Quitamos el exclude, vuelve a ser una aplicación normal
@SpringBootApplication
public class MsRegistroApplication {

    public static void main(String[] args) {
        // Carga de variables de entorno (opcional, útil para desarrollo local)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // Arranque de la aplicación
        SpringApplication.run(MsRegistroApplication.class, args);
    }
}