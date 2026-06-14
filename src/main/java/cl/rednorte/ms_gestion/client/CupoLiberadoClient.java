package cl.rednorte.ms_gestion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.rednorte.ms_gestion.dto.CupoLiberadoEvent;

@FeignClient(name = "ms-reasignacion", url = "http://localhost:8083")
public interface CupoLiberadoClient {

    @PostMapping("/api/reasignaciones/cupo-libre")
    void notificarCupoLiberado(@RequestBody CupoLiberadoEvent event);
}
