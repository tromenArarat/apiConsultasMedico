package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.consulta.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {
    @Autowired
    private AgendaConsultaService agendaConsultaService;
    @Autowired
    private ConsultaRepository repository;
    @PostMapping
    @Transactional
    public ResponseEntity agendar(@RequestBody @Valid DatosAgendarConsulta datos){
        var response = agendaConsultaService.agendar(datos);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Transactional
    @Operation(
            summary = "cancela una consulta de la agenda",
            description = "requiere motivo",
            tags = {"consulta","delete"}
    )
    public ResponseEntity eliminar(@RequestBody @Valid DatosCancelamientoConsulta datos) {
        agendaConsultaService.cancelar(datos);
        var consulta = repository.getReferenceById(datos.idConsulta());
        consulta.cancelar(datos.motivo());
        return ResponseEntity.noContent().build();
    }

}
