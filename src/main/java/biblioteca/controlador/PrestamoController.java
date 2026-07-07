package biblioteca.controlador;

import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Usuario;
import biblioteca.dao.impl.DetallePrestamoDAOImpl;
import biblioteca.dao.interfaces.DetallePrestamoDAO;
import biblioteca.servicios.EstudianteService;
import biblioteca.servicios.LibroService;
import biblioteca.servicios.PrestamoService;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import java.util.Optional;

public class PrestamoController {

    private final PrestamoService prestamoService;
    private final EstudianteService estudianteService;
    private final LibroService libroService;
    private final DetallePrestamoDAO detallePrestamoDAO;

    public PrestamoController() {
        this.prestamoService = new PrestamoService();
        this.estudianteService = new EstudianteService();
        this.libroService = new LibroService();
        this.detallePrestamoDAO = new DetallePrestamoDAOImpl();
    }

    /**
     * Busca un estudiante por su código universitario.
     */
    public Optional<Estudiante> buscarEstudiantePorCodigo(String codigo) {
        return estudianteService.buscarEstudiantePorCodigo(codigo);
    }

    /**
     * Lista estudiantes para consultas rápidas de la interfaz.
     */
    public ListaEnlazada<Estudiante> obtenerEstudiantes() {
        return estudianteService.listarTodos();
    }

    /**
     * Filtra estudiantes por coincidencia parcial en el nombre completo.
     */
    public ListaEnlazada<Estudiante> buscarEstudiantesPorNombre(String nombre) {
        ListaEnlazada<Estudiante> estudiantes = estudianteService.listarTodos();
        if (nombre == null || nombre.trim().isEmpty()) {
            return estudiantes;
        }
        String criterio = nombre.trim().toLowerCase();
        return estudiantes.filtrar(estudiante
                -> estudiante.getNombreCompleto() != null
                && estudiante.getNombreCompleto().toLowerCase().contains(criterio));
    }

    /**
     * Busca un libro por su ID único.
     */
    public Optional<Libro> buscarLibroPorId(int id) {
        return libroService.buscarLibroPorId(id);
    }

    /**
     * Lista libros para consultas rápidas de la interfaz.
     */
    public ListaEnlazada<Libro> obtenerLibros() {
        return libroService.buscarPorTitulo("");
    }

    /**
     * Filtra libros por coincidencia parcial en el título.
     */
    public ListaEnlazada<Libro> buscarLibrosPorTitulo(String titulo) {
        return libroService.buscarPorTitulo(titulo);
    }

    /**
     * Registra un nuevo préstamo para un estudiante con una lista de detalles.
     */
    public boolean registrarPrestamo(
            Estudiante estudiante,
            Usuario bibliotecario,
            ListaEnlazada<DetallePrestamo> detalles
    ) {
        Prestamo prestamo = new Prestamo();
        prestamo.setEstudiante(estudiante);
        if (bibliotecario != null) {
            biblioteca.modelo.Bibliotecario bib = new biblioteca.modelo.Bibliotecario();
            bib.setId(bibliotecario.getId());
            bib.setUsername(bibliotecario.getUsername());
            bib.setPassword(bibliotecario.getPassword());
            bib.setNombreCompleto(bibliotecario.getNombreCompleto());
            bib.setRol(bibliotecario.getRol());
            prestamo.setBibliotecario(bib);
        }
        return prestamoService.registrarPrestamo(prestamo, detalles);
    }

    /**
     * Registra la devolución de un préstamo por su ID.
     */
    public boolean registrarDevolucion(int idPrestamo) {
        return prestamoService.registrarDevolucion(idPrestamo);
    }

    /**
     * Obtiene todos los préstamos registrados.
     */
    public ListaEnlazada<Prestamo> obtenerTodos() {
        return prestamoService.listarTodos();
    }

    /**
     * Obtiene préstamos que todavía no han sido devueltos.
     */
    public ListaEnlazada<Prestamo> obtenerPrestamosActivos() {
        return prestamoService.listarPrestamosActivos();
    }

    /**
     * Obtiene préstamos activos priorizando los más antiguos.
     */
    public ListaEnlazada<Prestamo> obtenerPrestamosActivosOrdenadosPorAntiguedad() {
        return prestamoService.listarPrestamosActivosOrdenadosPorAntiguedad();
    }

    /**
     * Construye la cola FIFO de devoluciones pendientes.
     */
    public Cola<Prestamo> obtenerColaDevolucionesPendientes() {
        return prestamoService.crearColaDevolucionesPendientes();
    }

    /**
     * Obtiene los préstamos de un estudiante.
     */
    public ListaEnlazada<Prestamo> obtenerPrestamosPorEstudiante(int idEstudiante) {
        return prestamoService.listarPrestamosPorEstudiante(idEstudiante);
    }

    /**
     * Obtiene un préstamo por su ID.
     */
    public Optional<Prestamo> buscarPrestamoPorId(int idPrestamo) {
        return prestamoService.buscarPorId(idPrestamo);
    }

    /**
     * Obtiene los libros asociados a un préstamo.
     */
    public ListaEnlazada<DetallePrestamo> obtenerDetallesPrestamo(int idPrestamo) {
        if (idPrestamo <= 0) {
            return new ListaEnlazada<>();
        }
        return detallePrestamoDAO.buscarPorPrestamo(idPrestamo);
    }
}
