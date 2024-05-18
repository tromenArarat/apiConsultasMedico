package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// component es la clase rey de la que derivan las otras
@Component
public class PacienteSinConsulta implements ValidadorDeConsultas{
    @Autowired
    private ConsultaRepository repository;
    public void validar(DatosAgendarConsulta datos){
        var primerHorario = datos.fecha().withHour(7);
        var ultimoHorario = datos.fecha().withHour(18);

        var pacienteConConsulta = repository.existsByPacienteIdAndDataBetween(datos.idPaciente(),primerHorario,ultimoHorario);
        if(pacienteConConsulta){
            throw new ValidationException("El paciente ya tiene una consulta para ese día");
        }
    }
}
