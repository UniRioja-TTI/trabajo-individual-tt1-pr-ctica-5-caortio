package servicios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;

@Service
public class ContactoSimService implements InterfazContactoSim{

	private static final Logger logger = LoggerFactory.getLogger(ContactoSimService.class);
	private final Random random = new Random();
    private final Map<Integer, DatosSolicitud> solicitudes = new HashMap<>();
	private final List<Entidad> entidadesDisponibles = List.of(
			new Entidad(1, "Movistar", "España"),
            new Entidad(2, "Vodafone", "España"),
            new Entidad(3, "Orange", "España"),
            new Entidad(4, "Yoigo", "España"),
            new Entidad(5, "Pepephone", "España"),
            new Entidad(6, "Lowii", "España"),
            new Entidad(7, "Simyo", "España"),
            new Entidad(8, "O2", "España")
	    );
	
	@Override
	public int solicitarSimulation(DatosSolicitud sol) {
		if (sol == null) {
            logger.warn("Solicitud inválida o vacía");
            return -1;
        }

        int token = random.nextInt(100000);
        solicitudes.put(token, sol);
        logger.info("Solicitud almacenada con token {}", token);

		return token;
	}

	@Override
	public DatosSimulation descargarDatos(int ticket) {
		logger.debug("Consulta de datos para ticket {}", ticket);
        return null;
    }

	@Override
	public List<Entidad> getEntities() {
		return entidadesDisponibles;
	}

	@Override
	public boolean isValidEntityId() {
		logger.warn("Implementación incompleta");
        return true;	
    }

}