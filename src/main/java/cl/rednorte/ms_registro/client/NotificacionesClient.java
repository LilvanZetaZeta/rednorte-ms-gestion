package cl.rednorte.ms_registro.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cl.rednorte.ms_registro.config.FeignConfig;

@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url}", configuration = FeignConfig.class)
public interface NotificacionesClient {

    @PostMapping("/api/notificaciones/enviar")
    void enviarCorreo(@RequestBody Map<String, Object> request);
}