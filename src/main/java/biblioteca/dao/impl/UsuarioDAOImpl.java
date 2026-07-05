package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.UsuarioDAO;
import biblioteca.modelo.Rol;
import biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class UsuarioDAOImpl
        extends AbstractDAO
        implements UsuarioDAO {

    /**
     * Convierte un ResultSet en un objeto Usuario.
     */
    private Usuario mapearUsuario(ResultSet rs)
            throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setId(
                rs.getInt("id_usuario")
        );

        usuario.setUsername(
                rs.getString("username")
        );

        usuario.setPassword(
                rs.getString("password")
        );

        usuario.setNombreCompleto(
                rs.getString("nombre_completo")
        );

        int idRol = rs.getInt("id_rol");
        if (!rs.wasNull()) {
            Rol rol = new Rol();
            rol.setId(idRol);
            usuario.setRol(rol);
        }

        return usuario;
    }

    @Override
    public boolean guardar(Usuario usuario) {

        String sql = """
                INSERT INTO usuarios
                (username, password, nombre_completo, id_rol)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(
                    1,
                    usuario.getUsername()
            );

            ps.setString(
                    2,
                    usuario.getPassword()
            );

            ps.setString(
                    3,
                    usuario.getNombreCompleto()
            );

            if (usuario.getRol() != null) {
                ps.setInt(4, usuario.getRol().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al guardar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {

        String sql = """
                UPDATE usuarios
                SET username = ?,
                    password = ?,
                    nombre_completo = ?,
                    id_rol = ?
                WHERE id_usuario = ?
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(
                    1,
                    usuario.getUsername()
            );

            ps.setString(
                    2,
                    usuario.getPassword()
            );

            ps.setString(
                    3,
                    usuario.getNombreCompleto()
            );

            if (usuario.getRol() != null) {
                ps.setInt(4, usuario.getRol().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(
                    5,
                    usuario.getId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {

        String sql = """
                DELETE FROM usuarios
                WHERE id_usuario = ?
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {

        String sql = """
                SELECT *
                FROM usuarios
                WHERE id_usuario = ?
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar usuario: "
                    + e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public ListaEnlazada<Usuario> listarTodos() {

        ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();

        String sql = """
                SELECT *
                FROM usuarios
                ORDER BY username
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql); ResultSet rs
                = ps.executeQuery()) {

            while (rs.next()) {

                usuarios.agregar(
                        mapearUsuario(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar usuarios: "
                    + e.getMessage()
            );
        }

        return usuarios;
    }

    @Override
    public Optional<Usuario> login(
            String username,
            String password
    ) {

        String sql = """
                SELECT *
                FROM usuarios
                WHERE username = ?
                AND password = ?
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error login: "
                    + e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorUsername(
            String username
    ) {

        String sql = """
                SELECT *
                FROM usuarios
                WHERE username = ?
                """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar username: "
                    + e.getMessage()
            );
        }

        return Optional.empty();
    }
}
