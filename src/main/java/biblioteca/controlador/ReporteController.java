package biblioteca.controlador;

import biblioteca.modelo.Prestamo;
import biblioteca.servicios.ReporteService;
import biblioteca.estructuras.ListaEnlazada;

public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController() {
        this.reporteService = new ReporteService();
    }

    /**
     * Obtiene el listado de los libros más prestados.
     */
    public ListaEnlazada<Object[]> obtenerLibrosMasPrestados() {
        return reporteService.obtenerLibrosMasPrestados();
    }

    /**
     * Obtiene el listado de préstamos que siguen activos (no devueltos).
     */
    public ListaEnlazada<Prestamo> obtenerPrestamosActivos() {
        return reporteService.obtenerPrestamosActivos();
    }

    /**
     * Obtiene el historial general de todos los préstamos.
     */
    public ListaEnlazada<Prestamo> obtenerHistorialGeneral() {
        return reporteService.obtenerHistorialGeneral();
    }
}
