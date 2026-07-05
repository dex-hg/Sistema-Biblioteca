package biblioteca.servicios;

import biblioteca.dao.impl.MultaDAOImpl;
import biblioteca.dao.impl.PrestamoDAOImpl;
import biblioteca.dao.interfaces.MultaDAO;
import biblioteca.dao.interfaces.PrestamoDAO;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;

import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

/**
 * Servicio encargado de gestionar las multas de los estudiantes, calcular
 * montos y registrar pagos de penalizaciones por devoluciones tardías.
 */
public class MultaService {

    private final MultaDAO multaDAO;
    private final PrestamoDAO prestamoDAO;

    /**
     * Constructor por defecto. Inicializa las dependencias.
     */
    public MultaService() {
        this.multaDAO = new MultaDAOImpl();
        this.prestamoDAO = new PrestamoDAOImpl();
    }

    /**
     * Constructor para inyección de dependencias (pruebas).
     *
     * @param multaDAO DAO de multas.
     * @param prestamoDAO DAO de préstamos.
     */
    public MultaService(MultaDAO multaDAO, PrestamoDAO prestamoDAO) {
        this.multaDAO = multaDAO;
        this.prestamoDAO = prestamoDAO;
    }

    /**
     * Registra una nueva multa en el sistema.
     *
     * @param multa La multa a registrar.
     * @return true si se guardó con éxito.
     */
    public boolean registrarMulta(Multa multa) {
        if (multa == null
                || multa.getPrestamo() == null
                || multa.getMonto() <= 0) {
            return false;
        }
        return multaDAO.guardar(multa);
    }

    /**
     * Obtiene la lista de multas con estado 'PENDIENTE' para un estudiante en
     * particular.
     *
     * @param idEstudiante Identificador del estudiante.
     * @return Lista de multas pendientes de pago.
     */
    public ListaEnlazada<Multa> obtenerMultasPendientes(int idEstudiante) {
        ListaEnlazada<Multa> multasPendientes = new ListaEnlazada<>();
        if (idEstudiante <= 0) {
            return multasPendientes;
        }

        // Obtener todos los préstamos asociados al estudiante
        ListaEnlazada<Prestamo> prestamosEstudiante
                = prestamoDAO.listarPrestamoPorEstudiante(idEstudiante);
        ListaEnlazada<Integer> prestamoIds = new ListaEnlazada<>();
        for (int i = 0; i < prestamosEstudiante.size(); i++) {
            prestamoIds.agregar(prestamosEstudiante.obtener(i).getId());
        }

        // Obtener todas las multas del sistema y filtrar
        ListaEnlazada<Multa> todasMultas = multaDAO.listarTodos();
        for (int i = 0; i < todasMultas.size(); i++) {
            Multa m = todasMultas.obtener(i);
            
            boolean containsId = false;
            for (int k = 0; k < prestamoIds.size(); k++) {
                if (prestamoIds.obtener(k).equals(m.getPrestamo().getId())) {
                    containsId = true;
                    break;
                }
            }

            if ("PENDIENTE".equalsIgnoreCase(m.getEstado()) && containsId) {
                // Enriquecer la relación con los detalles del préstamo
                for (int j = 0; j < prestamosEstudiante.size(); j++) {
                    Prestamo p = prestamosEstudiante.obtener(j);
                    if (p.getId() == m.getPrestamo().getId()) {
                        m.setPrestamo(p);
                        break;
                    }
                }
                multasPendientes.agregar(m);
            }
        }

        return multasPendientes;
    }

    /**
     * Registra el pago de una multa cambiando su estado a 'PAGADA'.
     *
     * @param idMulta Identificador único de la multa.
     * @return true si el pago se procesó y actualizó con éxito, false en caso
     * contrario.
     */
    public boolean pagarMulta(int idMulta) {
        if (idMulta <= 0) {
            return false;
        }

        Optional<Multa> multaOpt = multaDAO.buscarPorId(idMulta);
        if (multaOpt.isPresent()) {
            Multa multa = multaOpt.get();
            if ("PAGADA".equalsIgnoreCase(multa.getEstado())) {
                System.out.println("La multa ya se encuentra pagada.");
                return true; // Ya está pagada
            }

            multa.setEstado("PAGADA");
            return multaDAO.actualizar(multa);
        }

        System.out.println("Error: No se encontró la multa especificada.");
        return false;
    }

    /**
     * Lista todas las multas registradas en la biblioteca.
     *
     * @return Lista general de multas.
     */
    public ListaEnlazada<Multa> listarTodas() {
        return multaDAO.listarTodos();
    }
}
