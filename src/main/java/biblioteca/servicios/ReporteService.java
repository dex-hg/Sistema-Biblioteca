package biblioteca.servicios;

import biblioteca.conexion.ConexionBD;
import biblioteca.modelo.Prestamo;
import biblioteca.estructuras.ListaEnlazada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteService {

    private final PrestamoService prestamoService;

    public ReporteService() {
        this.prestamoService = new PrestamoService();
    }

    /**
     * Obtiene el ranking de los libros más prestados del sistema.
     * 
     * @return Una lista de arreglos de Object conteniendo: [Título, Autor, Total Prestados]
     */
    public ListaEnlazada<Object[]> obtenerLibrosMasPrestados() {
        ListaEnlazada<Object[]> reporte = new ListaEnlazada<>();
        String sql = """
                     SELECT l.titulo, l.autor, SUM(dp.cantidad) AS total_prestado
                     FROM detalle_prestamo dp
                     JOIN libros l ON dp.id_libro = l.id_libro
                     GROUP BY l.id_libro, l.titulo, l.autor
                     ORDER BY total_prestado DESC
                     LIMIT 5
                     """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                int totalPrestado = rs.getInt("total_prestado");
                reporte.agregar(new Object[]{titulo, autor, totalPrestado});
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener libros más prestados: " + e.getMessage());
        }
        return reporte;
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
}
