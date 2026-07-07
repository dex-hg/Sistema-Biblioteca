package biblioteca.servicios;

import biblioteca.conexion.ConexionBD;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.estructuras.ListaEnlazada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

public class ReporteService {

    private static final int LIMITE_RANKING_LIBROS = 5;

    private final PrestamoService prestamoService;
    private final MultaService multaService;

    public ReporteService() {
        this.prestamoService = new PrestamoService();
        this.multaService = new MultaService();
    }

    /**
     * Obtiene el ranking de los libros más prestados del sistema.
     * 
     * @return Una lista de arreglos de Object conteniendo: [Título, Autor, Total Prestados]
     */
    public ListaEnlazada<Object[]> obtenerLibrosMasPrestados() {
        ListaEnlazada<Object[]> reporteCompleto = new ListaEnlazada<>();
        String sql = """
                     SELECT l.titulo, l.autor, SUM(dp.cantidad) AS total_prestado
                     FROM detalle_prestamo dp
                     JOIN libros l ON dp.id_libro = l.id_libro
                     GROUP BY l.id_libro, l.titulo, l.autor
                     """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                int totalPrestado = rs.getInt("total_prestado");
                reporteCompleto.agregar(new Object[]{titulo, autor, totalPrestado});
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener libros más prestados: " + e.getMessage());
        }

        Comparator<Object[]> porTotalDesc = (r1, r2)
                -> Integer.compare((Integer) r2[2], (Integer) r1[2]);
        ListaEnlazada<Object[]> rankingOrdenado
                = AlgoritmosOrdenamiento.ordenarQuickSort(
                        reporteCompleto,
                        new Object[0][],
                        porTotalDesc);

        ListaEnlazada<Object[]> topRanking = new ListaEnlazada<>();
        int limite = Math.min(LIMITE_RANKING_LIBROS, rankingOrdenado.size());
        for (int i = 0; i < limite; i++) {
            topRanking.agregar(rankingOrdenado.obtener(i));
        }
        return topRanking;
    }

    /**
     * Obtiene todos los préstamos activos (libros no devueltos aún).
     */
    public ListaEnlazada<Prestamo> obtenerPrestamosActivos() {
        return prestamoService.listarPrestamosActivos();
    }

    /**
     * Obtiene el historial completo de préstamos del sistema.
     */
    public ListaEnlazada<Prestamo> obtenerHistorialGeneral() {
        return prestamoService.listarTodos();
    }

    /**
     * Obtiene todas las multas y completa el préstamo asociado para reportes.
     */
    public ListaEnlazada<Multa> obtenerHistorialMultas() {
        ListaEnlazada<Multa> multas = multaService.listarTodas();
        for (Multa multa : multas) {
            if (multa.getPrestamo() == null || multa.getPrestamo().getId() <= 0) {
                continue;
            }
            prestamoService.buscarPorId(multa.getPrestamo().getId())
                    .ifPresent(multa::setPrestamo);
        }
        return multas;
    }
}
