package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaConsultaService {
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    //Esto inyecta todos los validadores en una lista
    @Autowired
    List<ValidadorDeConsultas> validadores;

    public DatosDetalleConsulta agendar(DatosAgendarConsulta datos){
        if(!pacienteRepository.findById(datos.idPaciente()).isPresent()){
            throw new ValidacionDeIntegridad("Este id de paciente no fue encontrado");
        }
        if(datos.idMedico()!=null && !medicoRepository.existsById(datos.idMedico())){
            throw new ValidacionDeIntegridad("Este id de médico no fue encontrado");
        }

        // Validaciones con design pattern principios SOLID
        // Adapta una interfaz a múltiples finalidades

        // principio de singleResponsability
        // una única responsabilidad por cada clase

        /*
        Principio Abierto-Cerrado (Open-Closed Principle)
        Principio de Sustitución de Liskov (Liskov Substitution Principle)
        Principio de Segregación de Interfaces (Interface Segregation Principle)
        Robert Martin, conocido como Uncle Bob, en su artículo Design Principles and Design Patterns.
         */

        // principio de inversión de dependencia
        // las clases de alto nivel tienen que depender de abstracciones o interfaces y no de
        // clases de bajo nivel. Las de alto son las que se encuentran relacionadas con las reglas de negocio
        // buscar consultas, guardar, asignar son de alto nivel
        // de bajo nivel son las que se encargan de hacer conexiones

        validadores.forEach(v->v.validar(datos));


        var paciente = pacienteRepository.findById(datos.idPaciente()).get();
        var medico = seleccionarMedico(datos);

        if(medico==null){
            throw new ValidacionDeIntegridad("No existen médicos disponibles para este horario y especialidad");
        }

        var consulta = new Consulta(
                null,
                medico,
                paciente,
                datos.fecha()
        );
        consultaRepository.save(consulta);
        return new DatosDetalleConsulta(consulta);
    }

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {
        if(datos.idMedico()!=null){
            //a diferencia del findById get reference no devuelve un optional
            return medicoRepository.getReferenceById(datos.idMedico());
        }
        if(datos.especialidad()==null){
            throw new ValidacionDeIntegridad("Debe seleccionar una especialidad");
        }

        return medicoRepository.seleccionarMedicoConEspecialidadEnFecha(datos.especialidad(),datos.fecha());
    }
}
