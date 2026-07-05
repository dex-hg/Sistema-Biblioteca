package biblioteca.controlador;

import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Usuario;
import biblioteca.servicios.EstudianteService;
import biblioteca.servicios.LibroService;
import biblioteca.servicios.PrestamoService;
import biblioteca.estructuras.ListaEnlazada;
import java.util.Optional;

public class PrestamoController {

    private final PrestamoService prestamoService;
    private final EstudianteService estudianteService;
    private final LibroService libroService;

    public PrestamoController() {
        this.prestamoService = new PrestamoService();
        this.estudianteService = new EstudianteService();
        this.libroService = new LibroService();
    }

    /**
     * Busca un estudiante por su código universitario.
     */
    public Optional<Estudiante> buscarEstudiantePorCodigo(String codigo) {
        return estudianteService.buscarEstudiantePorCodigo(codigo);
    }

    /**
     * Busca un libro por su ID único.
     */
    public Optional<Libro> buscarLibroPorId(int id) {
        return libroService.buscarLibroPorId(id);
    }

    /**
     * Registra un nuevo préstamo para un estudiante con una lista de detalles.
     */
    public boolean registrarPrestamo(Estudiante estudiante, Usuario bibliotecario, ListaEnlazada<DetallePrestamo> detalles) {
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
     * Obtiene un préstamo por su ID.
     */
    public Optional<Prestamo> buscarPrestamoPorId(int idPrestamo) {
        return prestamoService.buscarPorId(idPrestamo);
    }
}
