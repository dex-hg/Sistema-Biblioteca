package biblioteca.servicios;

import biblioteca.dao.impl.DetallePrestamoDAOImpl;
import biblioteca.dao.impl.LibroDAOImpl;
import biblioteca.dao.impl.PrestamoDAOImpl;
import biblioteca.dao.interfaces.DetallePrestamoDAO;
import biblioteca.dao.interfaces.LibroDAO;
import biblioteca.dao.interfaces.PrestamoDAO;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.estructuras.ListaEnlazada;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Servicio central del negocio. Gestiona las solicitudes de préstamos y
 * devoluciones de libros, validando reglas de disponibilidad, atrasos de
 * estudiantes y cálculo de multas automáticas.
 */
public class PrestamoService {

    public static final int MAX_DIAS_PRESTAMO = 7;
    public static final double COSTO_MULTA_DIARIA = 2.0;

    private final PrestamoDAO prestamoDAO;
    private final DetallePrestamoDAO detallePrestamoDAO;
    private final LibroDAO libroDAO;
    private final MultaService multaService;

    /**
     * Constructor por defecto. Inicializa las dependencias de persistencia y
     * servicios relacionados.
     */
    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAOImpl();
        this.detallePrestamoDAO = new DetallePrestamoDAOImpl();
        this.libroDAO = new LibroDAOImpl();
        this.multaService = new MultaService();
    }

    /**
     * Constructor para inyección de dependencias (útil para pruebas unitarias y
     * mocks).
     */
    public PrestamoService(
            PrestamoDAO prestamoDAO,
            DetallePrestamoDAO detalleDAO,
            LibroDAO libroDAO,
            MultaService multaService) {
        this.prestamoDAO = prestamoDAO;
        this.detallePrestamoDAO = detalleDAO;
        this.libroDAO = libroDAO;
        this.multaService = multaService;
    }

    /**
     * Registra un nuevo préstamo y sus detalles asociados en la base de datos.
     * Decrementa el stock disponible de los libros prestados. Aplica
     * validaciones estrictas sobre el estudiante y la disponibilidad de los
     * libros.
     *
     * @param prestamo Cabeza del préstamo (fecha, estudiante, bibliotecario).
     * @param detalles Libros y cantidades solicitadas.
     * @return true si el préstamo se registró correctamente, false en caso contrario.
     */
    public boolean registrarPrestamo(
            Prestamo prestamo,
            ListaEnlazada<DetallePrestamo> detalles) {

        if (prestamo == null || detalles == null || detalles.isEmpty()) {
            System.out.println("Error: Datos del préstamo incompletos.");
            return false;
        }
        if (prestamo.getEstudiante() == null || prestamo.getBibliotecario() == null) {
            System.out.println("Error: El estudiante y el bibliotecario registrador son obligatorios.");
            return false;
        }

        int idEstudiante = prestamo.getEstudiante().getId();

        // Regla 1: Validar que el estudiante no tenga multas pendientes de pago
        ListaEnlazada<Multa> multasPendientes = multaService.obtenerMultasPendientes(idEstudiante);
        if (!multasPendientes.isEmpty()) {
            System.out.println("Validación fallida: El estudiante tiene multas pendientes de pago.");
            return false;
        }

        // Regla 2: Validar que el estudiante no tenga préstamos activos vencidos
        ListaEnlazada<Prestamo> prestamosEstudiante = prestamoDAO.listarPrestamoPorEstudiante(idEstudiante);
        for (int i = 0; i < prestamosEstudiante.size(); i++) {
            Prestamo p = prestamosEstudiante.obtener(i);
            if (p.getEstado() == EstadoPrestamo.ACTIVO || p.getEstado() == EstadoPrestamo.ATRASADO) {
                long diasActivo = ChronoUnit.DAYS.between(p.getFechaPrestamo(), LocalDate.now());
                if (diasActivo > MAX_DIAS_PRESTAMO) {
                    System.out.println("Validación fallida: El estudiante tiene préstamos activos vencidos (atrasados).");
                    return false;
                }
            }
        }

        // Regla 3: Validar stock suficiente de todos los libros antes de guardar
        for (int i = 0; i < detalles.size(); i++) {
            DetallePrestamo dp = detalles.obtener(i);
            if (dp.getLibro() == null || dp.getCantidad() <= 0) {
                System.out.println("Error: Detalle del libro inválido.");
                return false;
            }
            Optional<Libro> libroOpt = libroDAO.buscarPorId(dp.getLibro().getId());
            if (libroOpt.isEmpty()) {
                System.out.println("Error: No se encontró el libro ID: " + dp.getLibro().getId());
                return false;
            }
            Libro libro = libroOpt.get();
            if (libro.getStock() < dp.getCantidad()) {
                System.out.println("Validación fallida: Stock insuficiente para el libro '"
                        + libro.getTitulo() + "' (Stock actual: " + libro.getStock()
                        + ", Solicitado: " + dp.getCantidad() + ").");
                return false;
            }
        }

        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        try {
            biblioteca.conexion.ConexionBD.beginTransaction();

            boolean guardadoPrestamo = prestamoDAO.guardar(prestamo);
            if (!guardadoPrestamo) {
                throw new java.sql.SQLException("No se pudo registrar la cabecera del préstamo.");
            }

            for (int i = 0; i < detalles.size(); i++) {
                DetallePrestamo dp = detalles.obtener(i);
                dp.setPrestamo(prestamo);
                boolean guardadoDetalle = detallePrestamoDAO.guardar(dp);
                if (!guardadoDetalle) {
                    throw new java.sql.SQLException("No se pudo guardar el detalle del libro ID " + dp.getLibro().getId());
                }

                // Decrementar stock dentro de la misma transacción
                Optional<Libro> libroOpt = libroDAO.buscarPorId(dp.getLibro().getId());
                if (libroOpt.isPresent()) {
                    Libro libro = libroOpt.get();
                    int nuevoStock = libro.getStock() - dp.getCantidad();
                    boolean stockActualizado = libroDAO.actualizarStock(libro.getId(), nuevoStock);
                    if (!stockActualizado) {
                        throw new java.sql.SQLException("No se pudo actualizar el stock del libro ID " + libro.getId());
                    }
                } else {
                    throw new java.sql.SQLException("No se encontró el libro ID " + dp.getLibro().getId());
                }
            }

            biblioteca.conexion.ConexionBD.commit();
            return true;

        } catch (java.sql.SQLException e) {
            System.out.println("Error en transacción registrarPrestamo: " + e.getMessage());
            biblioteca.conexion.ConexionBD.rollback();
            return false;
        } finally {
            biblioteca.conexion.ConexionBD.endTransaction();
        }
    }

    /**
     * Registra la devolución de un préstamo. Incrementa el stock de los libros
     * devueltos y calcula multas de forma automática si se supera el periodo de
     * 7 días.
     *
     * @param idPrestamo Identificador del préstamo.
     * @return true si la devolución se completó con éxito.
     */
    public boolean registrarDevolucion(int idPrestamo) {
        if (idPrestamo <= 0) {
            return false;
        }

        try {
            biblioteca.conexion.ConexionBD.beginTransaction();

            Optional<Prestamo> prestamoOpt = prestamoDAO.buscarPorId(idPrestamo);
            if (prestamoOpt.isEmpty()) {
                System.out.println("Error: No se encontró el préstamo con ID: " + idPrestamo);
                return false;
            }

            Prestamo prestamo = prestamoOpt.get();
            if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
                System.out.println("El préstamo ya fue devuelto con anterioridad.");
                return true;
            }

            LocalDate fechaRetorno = LocalDate.now();
            long diasTranscurridos = ChronoUnit.DAYS.between(prestamo.getFechaPrestamo(), fechaRetorno);

            if (diasTranscurridos > MAX_DIAS_PRESTAMO) {
                long diasRetraso = diasTranscurridos - MAX_DIAS_PRESTAMO;
                double montoMulta = diasRetraso * COSTO_MULTA_DIARIA;

                Multa multa = new Multa();
                multa.setPrestamo(prestamo);
                multa.setMonto(montoMulta);
                multa.setMotivo("Retraso en devolución por " + diasRetraso + " días.");
                multa.setEstado("PENDIENTE");

                boolean multaCreada = multaService.registrarMulta(multa);
                if (multaCreada) {
                    System.out.println("Alerta: Se ha generado una multa de S/. " + montoMulta);
                } else {
                    throw new java.sql.SQLException("No se pudo registrar la multa por retraso.");
                }
            }

            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            prestamo.setFechaDevolucion(fechaRetorno);
            boolean actualizado = prestamoDAO.actualizar(prestamo);
            if (!actualizado) {
                throw new java.sql.SQLException("No se pudo actualizar el estado del préstamo.");
            }

            ListaEnlazada<DetallePrestamo> detalles = detallePrestamoDAO.buscarPorPrestamo(idPrestamo);
            for (int i = 0; i < detalles.size(); i++) {
                DetallePrestamo dp = detalles.obtener(i);
                Optional<Libro> libroOpt = libroDAO.buscarPorId(dp.getLibro().getId());
                if (libroOpt.isPresent()) {
                    Libro libro = libroOpt.get();
                    int nuevoStock = libro.getStock() + dp.getCantidad();
                    boolean stockActualizado = libroDAO.actualizarStock(libro.getId(), nuevoStock);
                    if (!stockActualizado) {
                        throw new java.sql.SQLException("No se pudo actualizar el stock del libro ID " + libro.getId());
                    }
                } else {
                    throw new java.sql.SQLException("No se encontró el libro ID " + dp.getLibro().getId());
                }
            }

            biblioteca.conexion.ConexionBD.commit();
            System.out.println("Devolución procesada correctamente. Stocks actualizados.");
            return true;

        } catch (java.sql.SQLException e) {
            System.out.println("Error en transacción registrarDevolucion: " + e.getMessage());
            biblioteca.conexion.ConexionBD.rollback();
            return false;
        } finally {
            biblioteca.conexion.ConexionBD.endTransaction();
        }
    }

    /**
     * Busca un préstamo por su ID único.
     *
     * @param idPrestamo Identificador del préstamo.
     * @return Un Optional con el préstamo si se encuentra.
     */
    public Optional<Prestamo> buscarPorId(int idPrestamo) {
        if (idPrestamo <= 0) {
            return Optional.empty();
        }
        return prestamoDAO.buscarPorId(idPrestamo);
    }

    /**
     * Lista todos los préstamos activos (libros no devueltos aún).
     *
     * @return Lista de préstamos activos.
     */
    public ListaEnlazada<Prestamo> listarPrestamosActivos() {
        return prestamoDAO.listarPrestamosActivos();
    }

    /**
     * Lista todos los préstamos realizados en el sistema.
     *
     * @return Lista general de historial.
     */
    public ListaEnlazada<Prestamo> listarTodos() {
        return prestamoDAO.listarTodos();
    }
}
