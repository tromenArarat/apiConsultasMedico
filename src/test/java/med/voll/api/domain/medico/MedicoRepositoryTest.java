package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.MotivoCancelamiento;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.paciente.DatosRegistroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository repository;
    @Autowired
    private TestEntityManager em;
    @Test
    @DisplayName("debería retornar nulo cuando el médico se encuentre en consulta con otro paciente en ese horario")
    void seleccionarMedicoConEspecialidadEnFechaEscenario1() {
        // given
        var proximoLunes10H = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10,0);

        var medico=registrarMedico("José","jose@email.com","123456",Especialidad.OCULISTA);
        var paciente=registrarPaciente("Antonio","antonio@email.com","654321");
        registrarConsulta(medico,paciente,proximoLunes10H);

        //when
        var medicoLibre = repository.seleccionarMedicoConEspecialidadEnFecha(Especialidad.OCULISTA,proximoLunes10H);

        //then ("entonces")
        assertThat(medicoLibre).isNull();
    }
    @Test
    @DisplayName("debería retornar un médico cuando realice la consulta en la base de datos para ese horario")
    void seleccionarMedicoConEspecialidadEnFechaEscenario2() {
        // given
        var proximoLunes10H = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10,0);

        var medico=registrarMedico("José","jose@email.com","3321432",Especialidad.OCULISTA);

        //when
        var medicoLibre = repository.seleccionarMedicoConEspecialidadEnFecha(Especialidad.OCULISTA,proximoLunes10H);

        //then
        assertThat(medicoLibre).isEqualTo(medico);
    }
    private void registrarConsulta(Medico medico, Paciente paciente, LocalDateTime fecha){
        em.persist(new Consulta(null,medico,paciente,fecha,MotivoCancelamiento.MEDICO_CANCELO));
    }
    private Medico registrarMedico(String nombre, String email, String documento, Especialidad especialidad){
        var medico = new Medico(datosMedico(nombre,email,documento,especialidad));
        em.persist(medico);
        return medico;
    }
    private Paciente registrarPaciente(String nombre, String email, String documento){
        var paciente = new Paciente(datosPaciente(nombre,email,documento));
        em.persist(paciente);
        return paciente;
    }
    private DatosRegistroMedico datosMedico(String nombre, String email, String documento, Especialidad especialidad){
        return new DatosRegistroMedico(
                nombre,
                email,
                "3271832312",
                documento,
                especialidad,
                datosDireccion()
        );
    }

    private DatosRegistroPaciente datosPaciente(String nombre, String email, String documento){
        return new DatosRegistroPaciente(
                nombre,
                email,
                "3289321321",
                documento,
                datosDireccion()
        );
    }

    private DatosDireccion datosDireccion(){
        return new DatosDireccion(
                "Loca",
                "azul",
                "Acapulco",
                "México"
        );
    }

}