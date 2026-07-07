package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.DetallePrestamoDAO;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;

import java.sql.*;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class DetallePrestamoDAOImpl
        extends AbstractDAO
        implements DetallePrestamoDAO {

    @Override
    public boolean guardar(
            DetallePrestamo detalle
    ) {

        String sql = """
            INSERT INTO detalle_prestamo
            (id_prestamo,
             id_libro,
             cantidad)
            VALUES (?, ?, ?)
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    detalle.getPrestamo()
                            .getId()
            );

            ps.setInt(
                    2,
                    detalle.getLibro()
                            .getId()
            );

            ps.setInt(
                    3,
                    detalle.getCantidad()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error detalle préstamo: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean actualizar(DetallePrestamo detalle) {
        String sql = """
            UPDATE detalle_prestamo
            SET id_prestamo = ?,
                id_libro = ?,
                cantidad = ?
            WHERE id_detalle = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getPrestamo().getId());
            ps.setInt(2, detalle.getLibro().getId());
            ps.setInt(3, detalle.getCantidad());
            ps.setInt(4, detalle.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar detalle préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = """
            DELETE FROM detalle_prestamo
            WHERE id_detalle = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<DetallePrestamo> buscarPorId(Integer id) {
        String sql = """
            SELECT dp.id_detalle, dp.id_prestamo, dp.id_libro, dp.cantidad,
                   l.titulo AS libro_titulo
            FROM detalle_prestamo dp
            LEFT JOIN libros l ON dp.id_libro = l.id_libro
            WHERE dp.id_detalle = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetallePrestamo detalle = new DetallePrestamo();
                    detalle.setId(rs.getInt("id_detalle"));
                    detalle.setCantidad(rs.getInt("cantidad"));

                    Prestamo prestamo = new Prestamo();
                    prestamo.setId(rs.getInt("id_prestamo"));
                    detalle.setPrestamo(prestamo);

                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("libro_titulo"));
                    detalle.setLibro(libro);

                    return Optional.of(detalle);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar detalle préstamo: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public ListaEnlazada<DetallePrestamo> listarTodos() {
        ListaEnlazada<DetallePrestamo> detalles = new ListaEnlazada<>();
        String sql = """
            SELECT dp.id_detalle, dp.id_prestamo, dp.id_libro, dp.cantidad,
                   l.titulo AS libro_titulo
            FROM detalle_prestamo dp
            LEFT JOIN libros l ON dp.id_libro = l.id_libro
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DetallePrestamo detalle = new DetallePrestamo();
                detalle.setId(rs.getInt("id_detalle"));
                detalle.setCantidad(rs.getInt("cantidad"));

                Prestamo prestamo = new Prestamo();
                prestamo.setId(rs.getInt("id_prestamo"));
                detalle.setPrestamo(prestamo);

                Libro libro = new Libro();
                libro.setId(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("libro_titulo"));
                detalle.setLibro(libro);

                detalles.agregar(detalle);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar detalles préstamo: " + e.getMessage());
        }

        return detalles;
    }

    @Override
    public ListaEnlazada<DetallePrestamo> buscarPorPrestamo(int idPrestamo) {
        ListaEnlazada<DetallePrestamo> detalles = new ListaEnlazada<>();
        String sql = """
            SELECT dp.id_detalle, dp.id_prestamo, dp.id_libro, dp.cantidad,
                   l.titulo AS libro_titulo
            FROM detalle_prestamo dp
            LEFT JOIN libros l ON dp.id_libro = l.id_libro
            WHERE dp.id_prestamo = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePrestamo detalle = new DetallePrestamo();
                    detalle.setId(rs.getInt("id_detalle"));
                    detalle.setCantidad(rs.getInt("cantidad"));

                    Prestamo prestamo = new Prestamo();
                    prestamo.setId(rs.getInt("id_prestamo"));
                    detalle.setPrestamo(prestamo);

                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("libro_titulo"));
                    detalle.setLibro(libro);

                    detalles.agregar(detalle);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar detalles por préstamo: " + e.getMessage());
        }

        return detalles;
    }

}
