package servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tt1.trabajo.utilidades.ApiClient;
import com.tt1.trabajo.utilidades.ApiException;
import com.tt1.trabajo.utilidades.api.ResultadosApi;
import com.tt1.trabajo.utilidades.api.SolicitudApi;
import com.tt1.trabajo.utilidades.model.ResultsResponse;
import com.tt1.trabajo.utilidades.model.Solicitud;
import com.tt1.trabajo.utilidades.model.SolicitudResponse;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;

@Service
public class ContactoSimService implements InterfazContactoSim {

    private static final Logger logger = LoggerFactory.getLogger(ContactoSimService.class);
    private static final String NOMBRE_USUARIO = "usuarioConstante";

    private final SolicitudApi solicitudApi;
    private final ResultadosApi resultadosApi;

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

    public ContactoSimService() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8080");
        this.solicitudApi = new SolicitudApi(apiClient);
        this.resultadosApi = new ResultadosApi(apiClient);
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        if (sol == null) {
            logger.warn("Solicitud inválida o vacía");
            return -1;
        }
        try {
            Solicitud solicitud = new Solicitud();
            List<Integer> cantidades = new ArrayList<>();
            List<String> nombres = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : sol.getNums().entrySet()) {
                entidadesDisponibles.stream()
                    .filter(e -> e.getId() == entry.getKey())
                    .findFirst()
                    .ifPresent(e -> {
                        nombres.add(e.getName());
                        cantidades.add(entry.getValue());
                    });
            }

            solicitud.setNombreEntidades(nombres);
            solicitud.setCantidadesIniciales(cantidades);

            // *** AJUSTA ESTE NOMBRE DE MÉTODO si difiere en tu SolicitudApi.java ***
            SolicitudResponse response = solicitudApi.solicitudSolicitarPost(
                NOMBRE_USUARIO, solicitud
            );

            if (response != null && response.getDone()) {
                int token = response.getTokenSolicitud();
                logger.info("Solicitud aceptada con token {}", token);
                return token;
            } else {
                logger.error("Servicio rechazó la solicitud: {}",
                    response != null ? response.getErrorMessage() : "respuesta nula");
                return -1;
            }
        } catch (ApiException e) {
            logger.error("Error al contactar con el servicio: {}", e.getMessage());
            return -1;
        }
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        try {
            // *** AJUSTA ESTE NOMBRE DE MÉTODO si difiere en tu ResultadosApi.java ***
            ResultsResponse response = resultadosApi.resultadosPost(
                NOMBRE_USUARIO, ticket
            );

            if (response == null || !response.getDone()) {
                logger.error("Error al descargar datos: {}",
                    response != null ? response.getErrorMessage() : "respuesta nula");
                return null;
            }

            return parsearDatos(response.getData());

        } catch (ApiException e) {
            logger.error("Error al descargar datos: {}", e.getMessage());
            return null;
        }
    }

    private DatosSimulation parsearDatos(String data) {
        DatosSimulation ds = new DatosSimulation();
        Map<Integer, List<Punto>> puntos = new HashMap<>();

        String[] lineas = data.split("\n");

        // Primera línea: ancho del tablero
        ds.setAnchoTablero(Integer.parseInt(lineas[0].trim()));

        int maxTiempo = 0;

        // Resto de líneas: tiempo,x,y,color
        for (int i = 1; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            int tiempo = Integer.parseInt(partes[0]);
            int x      = Integer.parseInt(partes[1]);
            int y      = Integer.parseInt(partes[2]);
            String color = partes[3];

            Punto p = new Punto();
            p.setX(x);
            p.setY(y);
            p.setColor(color);

            puntos.computeIfAbsent(tiempo, k -> new ArrayList<>()).add(p);
            if (tiempo > maxTiempo) maxTiempo = tiempo;
        }

        ds.setMaxSegundos(maxTiempo + 1);
        ds.setPuntos(puntos);
        return ds;
    }

    @Override
    public List<Entidad> getEntities() {
        return entidadesDisponibles;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return entidadesDisponibles.stream().anyMatch(e -> e.getId() == id);
    }
}