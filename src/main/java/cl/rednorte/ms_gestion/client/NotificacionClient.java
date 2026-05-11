package cl.rednorte.ms_gestion.client;

import cl.rednorte.ms_gestion.dto.NotificacionReservaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificaciones", url = "http://localhost:8085")
public interface NotificacionClient {

    @PostMapping("/api/notificaciones/reserva")
    void notificarReserva(@RequestBody NotificacionReservaRequest request);
}