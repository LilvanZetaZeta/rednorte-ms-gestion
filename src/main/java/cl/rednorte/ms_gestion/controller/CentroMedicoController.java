package cl.rednorte.ms_gestion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rednorte.ms_gestion.entity.CentroMedico;
import cl.rednorte.ms_gestion.repository.CentroMedicoRepository;

@RestController
@RequestMapping("/api/centros-medicos")
public class CentroMedicoController {

    @Autowired private CentroMedicoRepository centroMedicoRepository;

    @GetMapping
    public List<CentroMedico> listar() {
        return centroMedicoRepository.findAll();
    }

    @PostMapping
    public CentroMedico crear(@RequestBody Map<String, String> body) {
        CentroMedico centro = new CentroMedico();
        centro.setNombreSucursal(body.get("nombreSucursal"));
        return centroMedicoRepository.save(centro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CentroMedico> getById(@PathVariable Long id) {
        return centroMedicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
