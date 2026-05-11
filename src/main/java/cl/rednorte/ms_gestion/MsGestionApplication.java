package cl.rednorte.ms_gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsGestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsGestionApplication.class, args);
    }
}
