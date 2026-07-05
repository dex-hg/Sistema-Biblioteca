package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.PrestamoDAO;
import biblioteca.modelo.Bibliotecario;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Prestamo;

import java.sql.*;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class PrestamoDAOImpl
        extends AbstractDAO
        implements PrestamoDAO {

    private Prestamo mapearPrestamo(ResultSet rs)
            throws SQLException {

        Prestamo prestamo = new Prestamo();

        prestamo.setId(rs.getInt("id_prestamo"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());

        java.sql.Date fDev = rs.getDate("fecha_devolucion");
        prestamo.setFechaDevolucion(fDev != null ? fDev.toLocalDate() : null);

        prestamo.setEstado(EstadoPrestamo.fromString(rs.getString("estado")));

        int idEstudiante = rs.getInt("id_estudiante");
        if (!rs.wasNull()) {
            Estudiante estudiante = new Estudiante();
            estudiante.setId(idEstudiante);
            try {
                estudiante.setNombres(rs.getString("estudiante_nombres"));
                estudiante.setApellidos(rs.getString("estudiante_apellidos"));
                estudiante.setNombreCompleto(rs.getString("estudiante_nombre_completo"));
            } catch (SQLException e) {
                // columnas no presentes en esta consulta, se ignoran
            }
            prestamo.setEstudiante(estudiante);
        }

        int idUsuario = rs.getInt("id_usuario");
        if (!rs.wasNull()) {
            Bibliotecario bibliotecario = new Bibliotecario();
            bibliotecario.setId(idUsuario);
            try {
                bibliotecario.setNombreCompleto(rs.getString("bibliotecario_nombre_completo"));
            } catch (SQLException e) {
                // columna no presente en esta consulta, se ignora
            }
            prestamo.setBibliotecario(bibliotecario);
        }

        return prestamo;
    }

    @Override
    public boolean guardar(Prestamo prestamo) {
        String sql = """
            INSERT INTO prestamos
            (id_estudiante,
             id_usuario,
             fecha_prestamo,
             fecha_devolucion,
             estado)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (prestamo.getEstudiante() != null) {
                ps.setInt(1, prestamo.getEstudiante().getId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            if (prestamo.getBibliotecario() != null) {
                ps.setInt(2, prestamo.getBibliotecario().getId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));

            if (prestamo.getFechaDevolucion() != null) {
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            EstadoPrestamo estado = prestamo.getEstado() != null
                    ? prestamo.getEstado()
                    : EstadoPrestamo.ACTIVO;
            ps.setString(5, estado.name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Error al guardar préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ListaEnlazada<Prestamo> listarPrestamosActivos() {

        ListaEnlazada<Prestamo> prestamos = new ListaEnlazada<>();

        // Filtra por estado en lugar de fecha_devolucion IS NULL para incluir
        // tanto ACTIVO como ATRASADO (préstamos no devueltos).
        String sql = """
            SELECT p.id_prestamo, p.id_estudiante, p.id_usuario, p.fecha_prestamo, p.fecha_devolucion, p.estado,
                   e.nombres AS estudiante_nombres, e.apellidos AS estudiante_apellidos,
                   u.nombre_completo AS estudiante_nombre_completo,
                   b.nombre_completo AS bibliotecario_nombre_completo
            FROM prestamos p
            LEFT JOIN estudiantes e ON p.id_estudiante = e.id_estudiante
            LEFT JOIN usuarios u ON e.id_estudiante = u.id_usuario
            LEFT JOIN usuarios b ON p.id_usuario = b.id_usuario
            WHERE p.estado IN ('ACTIVO', 'ATRASADO')
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.agregar(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos activos: " + e.getMessage());
        }

        return prestamos;
    }

    @Override
    public ListaEnlazada<Prestamo> listarPrestamoPorEstudiante(int idEstudiante) {
        ListaEnlazada<Prestamo> prestamos = new ListaEnlazada<>();

        String sql = """
            SELECT p.id_prestamo, p.id_estudiante, p.id_usuario, p.fecha_prestamo, p.fecha_devolucion, p.estado,
                   e.nombres AS estudiante_nombres, e.apellidos AS estudiante_apellidos,
                   u.nombre_completo AS estudiante_nombre_completo,
                   b.nombre_completo AS bibliotecario_nombre_completo
            FROM prestamos p
            LEFT JOIN estudiantes e ON p.id_estudiante = e.id_estudiante
            LEFT JOIN usuarios u ON e.id_estudiante = u.id_usuario
            LEFT JOIN usuarios b ON p.id_usuario = b.id_usuario
            WHERE p.id_estudiante = ?
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.agregar(mapearPrestamo(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos por estudiante: " + e.getMessage());
        }

        return prestamos;
    }

    @Override
    public boolean actualizar(Prestamo prestamo) {
        String sql = """
            UPDATE prestamos
            SET id_estudiante = ?,
                id_usuario = ?,
                fecha_prestamo = ?,
                fecha_devolucion = ?,
                estado = ?
            WHERE id_prestamo = ?
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (prestamo.getEstudiante() != null) {
                ps.setInt(1, prestamo.getEstudiante().getId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            if (prestamo.getBibliotecario() != null) {
                ps.setInt(2, prestamo.getBibliotecario().getId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));

            if (prestamo.getFechaDevolucion() != null) {
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            EstadoPrestamo estado = prestamo.getEstado() != null
                    ? prestamo.getEstado()
                    : EstadoPrestamo.ACTIVO;
            ps.setString(5, estado.name());
            ps.setInt(6, prestamo.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = """
            DELETE FROM prestamos
            WHERE id_prestamo = ?
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Prestamo> buscarPorId(Integer id) {
        String sql = """
            SELECT p.id_prestamo, p.id_estudiante, p.id_usuario, p.fecha_prestamo, p.fecha_devolucion, p.estado,
                   e.nombres AS estudiante_nombres, e.apellidos AS estudiante_apellidos,
                   u.nombre_completo AS estudiante_nombre_completo,
                   b.nombre_completo AS bibliotecario_nombre_completo
            FROM prestamos p
            LEFT JOIN estudiantes e ON p.id_estudiante = e.id_estudiante
            LEFT JOIN usuarios u ON e.id_estudiante = u.id_usuario
            LEFT JOIN usuarios b ON p.id_usuario = b.id_usuario
            WHERE p.id_prestamo = ?
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPrestamo(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar préstamo: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public ListaEnlazada<Prestamo> listarTodos() {
        ListaEnlazada<Prestamo> prestamos = new ListaEnlazada<>();
        String sql = """
            SELECT p.id_prestamo, p.id_estudiante, p.id_usuario, p.fecha_prestamo, p.fecha_devolucion, p.estado,
                   e.nombres AS estudiante_nombres, e.apellidos AS estudiante_apellidos,
                   u.nombre_completo AS estudiante_nombre_completo,
                   b.nombre_completo AS bibliotecario_nombre_completo
            FROM prestamos p
            LEFT JOIN estudiantes e ON p.id_estudiante = e.id_estudiante
            LEFT JOIN usuarios u ON e.id_estudiante = u.id_usuario
            LEFT JOIN usuarios b ON p.id_usuario = b.id_usuario
            ORDER BY p.fecha_prestamo DESC
            """;

        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.agregar(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar todos los préstamos: " + e.getMessage());
        }

        return prestamos;
    }

}
