package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.LibroDAO;
import biblioteca.modelo.Categoria;
import biblioteca.modelo.Libro;

import java.sql.*;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class LibroDAOImpl
        extends AbstractDAO
        implements LibroDAO {

    private Libro mapearLibro(ResultSet rs)
            throws SQLException {

        Libro libro = new Libro();

        libro.setId(rs.getInt("id_libro"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setStock(rs.getInt("stock"));
        libro.setEditorial(rs.getString("editorial"));
        
        int anio = rs.getInt("anio_publicacion");
        libro.setAnioPublicacion(rs.wasNull() ? null : anio);

        int idCat = rs.getInt("id_categoria");
        if (!rs.wasNull()) {
            Categoria cat = new Categoria();
            cat.setId(idCat);
            libro.setCategoria(cat);
        }

        return libro;
    }

    @Override
    public boolean guardar(Libro libro) {

        String sql = """
                     INSERT INTO libros
                     (titulo, autor, editorial, anio_publicacion, stock, id_categoria)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            if (libro.getAnioPublicacion() != null) {
                ps.setInt(4, libro.getAnioPublicacion());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, libro.getStock());
            if (libro.getCategoria() != null) {
                ps.setInt(6, libro.getCategoria().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al guardar el libro: "
                    + e.getMessage()
            );

            return false;

        }

    }

    @Override
    public Optional<Libro> buscarPorId(Integer id) {

        String sql = """
                     SELECT *
                     FROM libros
                     WHERE id_libro = ?
                     """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLibro(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar libro: "
                    + e.getMessage()
            );

        }

        return Optional.empty();

    }

    @Override
    public ListaEnlazada<Libro> listarTodos() {

        ListaEnlazada<Libro> libros = new ListaEnlazada<>();

        String sql = """
            SELECT *
            FROM libros
            ORDER BY titulo
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql); ResultSet rs
                = ps.executeQuery()) {

            while (rs.next()) {
                libros.agregar(mapearLibro(rs));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar libros: "
                    + e.getMessage()
            );
        }

        return libros;
    }

    @Override
    public boolean actualizar(Libro libro) {

        String sql = """
            UPDATE libros
            SET titulo = ?,
                autor = ?,
                editorial = ?,
                anio_publicacion = ?,
                stock = ?,
                id_categoria = ?
            WHERE id_libro = ?
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            if (libro.getAnioPublicacion() != null) {
                ps.setInt(4, libro.getAnioPublicacion());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, libro.getStock());
            if (libro.getCategoria() != null) {
                ps.setInt(6, libro.getCategoria().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, libro.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar libro: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {

        String sql = """
            DELETE FROM libros
            WHERE id_libro = ?
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar libro: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public ListaEnlazada<Libro> buscarPorTitulo(
            String titulo
    ) {

        ListaEnlazada<Libro> libros = new ListaEnlazada<>();

        String sql = """
            SELECT *
            FROM libros
            WHERE LOWER(titulo)
            LIKE LOWER(?)
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + titulo + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.agregar(mapearLibro(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar libro: "
                    + e.getMessage()
            );
        }

        return libros;
    }

    @Override
    public ListaEnlazada<Libro> buscarPorAutor(
            String autor
    ) {

        ListaEnlazada<Libro> libros = new ListaEnlazada<>();

        String sql = """
            SELECT *
            FROM libros
            WHERE LOWER(autor)
            LIKE LOWER(?)
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + autor + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.agregar(mapearLibro(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar autor: "
                    + e.getMessage()
            );
        }

        return libros;
    }

    @Override
    public boolean actualizarStock(
            int idLibro,
            int nuevoStock
    ) {

        String sql = """
            UPDATE libros
            SET stock = ?
            WHERE id_libro = ?
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(1, nuevoStock);
            ps.setInt(2, idLibro);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar stock: "
                    + e.getMessage()
            );

            return false;
        }
    }

}
