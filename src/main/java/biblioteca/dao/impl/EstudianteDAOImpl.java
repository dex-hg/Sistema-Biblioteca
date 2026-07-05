package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.EstudianteDAO;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Rol;

import java.sql.*;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class EstudianteDAOImpl extends AbstractDAO implements EstudianteDAO {

    @Override
    public boolean guardar(Estudiante estudiante) {
        String sqlUsuario = """
            INSERT INTO usuarios (username, password, nombre_completo, id_rol)
            VALUES (?, ?, ?, 3)
            """;
        
        String sqlEstudiante = """
            INSERT INTO estudiantes (id_estudiante, codigo, nombres, apellidos, carrera, telefono, correo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = ConexionBD.getConexion()) {
            // 1. Guardar en la tabla base (usuarios)
            int idUsuarioGenerated = -1;
            try (PreparedStatement psUser = cn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, estudiante.getUsername());
                psUser.setString(2, estudiante.getPassword());
                // El nombre completo se compone de nombres + apellidos
                String nombreComp = estudiante.getNombres() + " " + estudiante.getApellidos();
                psUser.setString(3, nombreComp);

                int affectedUserRows = psUser.executeUpdate();
                if (affectedUserRows == 0) {
                    throw new SQLException("No se pudo insertar el registro del estudiante en usuarios.");
                }

                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idUsuarioGenerated = generatedKeys.getInt(1);
                        estudiante.setId(idUsuarioGenerated);
                    } else {
                        throw new SQLException("No se pudo obtener el ID autogenerado del usuario.");
                    }
                }
            }

            // 2. Guardar en la tabla especializada (estudiantes)
            try (PreparedStatement psEst = cn.prepareStatement(sqlEstudiante)) {
                psEst.setInt(1, idUsuarioGenerated);
                psEst.setString(2, estudiante.getCodigo());
                psEst.setString(3, estudiante.getNombres());
                psEst.setString(4, estudiante.getApellidos());
                psEst.setString(5, estudiante.getCarrera());
                psEst.setString(6, estudiante.getTelefono());
                psEst.setString(7, estudiante.getCorreo());

                psEst.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar estudiante: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Estudiante estudiante) {
        String sqlUsuario = """
            UPDATE usuarios
            SET username = ?,
                password = ?,
                nombre_completo = ?
            WHERE id_usuario = ?
            """;
        
        String sqlEstudiante = """
            UPDATE estudiantes
            SET codigo = ?,
                nombres = ?,
                apellidos = ?,
                carrera = ?,
                telefono = ?,
                correo = ?
            WHERE id_estudiante = ?
            """;

        try (Connection cn = ConexionBD.getConexion()) {
            // 1. Actualizar tabla base (usuarios)
            try (PreparedStatement psUser = cn.prepareStatement(sqlUsuario)) {
                psUser.setString(1, estudiante.getUsername());
                psUser.setString(2, estudiante.getPassword());
                String nombreComp = estudiante.getNombres() + " " + estudiante.getApellidos();
                psUser.setString(3, nombreComp);
                psUser.setInt(4, estudiante.getId());
                psUser.executeUpdate();
            }

            // 2. Actualizar tabla especializada (estudiantes)
            try (PreparedStatement psEst = cn.prepareStatement(sqlEstudiante)) {
                psEst.setString(1, estudiante.getCodigo());
                psEst.setString(2, estudiante.getNombres());
                psEst.setString(3, estudiante.getApellidos());
                psEst.setString(4, estudiante.getCarrera());
                psEst.setString(5, estudiante.getTelefono());
                psEst.setString(6, estudiante.getCorreo());
                psEst.setInt(7, estudiante.getId());
                psEst.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        // Al haber ON DELETE CASCADE en la FK fk_estudiante_usuario,
        // eliminar el usuario eliminará automáticamente al estudiante.
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        
        // Mapear campos heredados de Usuario
        estudiante.setId(rs.getInt("id_usuario"));
        estudiante.setUsername(rs.getString("username"));
        estudiante.setPassword(rs.getString("password"));
        estudiante.setNombreCompleto(rs.getString("nombre_completo"));
        
        Rol rol = new Rol();
        rol.setId(rs.getInt("id_rol"));
        estudiante.setRol(rol);
        
        // Mapear campos específicos de Estudiante
        estudiante.setCodigo(rs.getString("codigo"));
        estudiante.setNombres(rs.getString("nombres"));
        estudiante.setApellidos(rs.getString("apellidos"));
        estudiante.setCarrera(rs.getString("carrera"));
        estudiante.setTelefono(rs.getString("telefono"));
        estudiante.setCorreo(rs.getString("correo"));
        
        return estudiante;
    }

    @Override
    public Optional<Estudiante> buscarPorId(Integer id) {
        String sql = """
            SELECT u.id_usuario, u.username, u.password, u.nombre_completo, u.id_rol,
                   e.codigo, e.nombres, e.apellidos, e.carrera, e.telefono, e.correo
            FROM estudiantes e
            JOIN usuarios u ON e.id_estudiante = u.id_usuario
            WHERE e.id_estudiante = ?
            """;
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public ListaEnlazada<Estudiante> listarTodos() {
        ListaEnlazada<Estudiante> estudiantes = new ListaEnlazada<>();
        String sql = """
            SELECT u.id_usuario, u.username, u.password, u.nombre_completo, u.id_rol,
                   e.codigo, e.nombres, e.apellidos, e.carrera, e.telefono, e.correo
            FROM estudiantes e
            JOIN usuarios u ON e.id_estudiante = u.id_usuario
            ORDER BY e.apellidos, e.nombres
            """;
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                estudiantes.agregar(mapearEstudiante(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar estudiantes: " + e.getMessage());
        }
        return estudiantes;
    }

    @Override
    public Optional<Estudiante> buscarPorCodigo(String codigo) {
        String sql = """
            SELECT u.id_usuario, u.username, u.password, u.nombre_completo, u.id_rol,
                   e.codigo, e.nombres, e.apellidos, e.carrera, e.telefono, e.correo
            FROM estudiantes e
            JOIN usuarios u ON e.id_estudiante = u.id_usuario
            WHERE e.codigo = ?
            """;
        try (Connection cn = ConexionBD.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por código: " + e.getMessage());
        }
        return Optional.empty();
    }
}
