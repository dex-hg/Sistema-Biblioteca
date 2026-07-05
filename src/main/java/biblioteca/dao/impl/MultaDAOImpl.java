package biblioteca.dao.impl;

import biblioteca.conexion.ConexionBD;
import biblioteca.dao.interfaces.MultaDAO;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public class MultaDAOImpl
        extends AbstractDAO
        implements MultaDAO {

    @Override
    public boolean guardar(
            Multa multa
    ) {

        String sql = """
            INSERT INTO multas
            (id_prestamo,
             monto,
             motivo,
             estado,
             fecha_creacion)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (
                Connection cn
                = ConexionBD.getConexion(); PreparedStatement ps
                = cn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    multa.getPrestamo()
                            .getId()
            );

            ps.setDouble(
                    2,
                    multa.getMonto()
            );

            ps.setString(
                    3,
                    multa.getMotivo()
            );

            ps.setString(
                    4,
                    multa.getEstado() != null ? multa.getEstado() : "PENDIENTE"
            );

            ps.setDate(
                    5,
                    Date.valueOf(multa.getFechaCreacion() != null ? multa.getFechaCreacion() : LocalDate.now())
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error multa: "
                    + e.getMessage()
            );

            return false;
        }
    }

    @Override
    public boolean actualizar(Multa multa) {
        String sql = """
            UPDATE multas
            SET id_prestamo = ?,
                monto = ?,
                motivo = ?,
                estado = ?,
                fecha_creacion = ?
            WHERE id_multa = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, multa.getPrestamo().getId());
            ps.setDouble(2, multa.getMonto());
            ps.setString(3, multa.getMotivo());
            ps.setString(4, multa.getEstado() != null ? multa.getEstado() : "PENDIENTE");
            ps.setDate(5, Date.valueOf(multa.getFechaCreacion() != null ? multa.getFechaCreacion() : LocalDate.now()));
            ps.setInt(6, multa.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar multa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = """
            DELETE FROM multas
            WHERE id_multa = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar multa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Multa> buscarPorId(Integer id) {
        String sql = """
            SELECT id_multa, id_prestamo, monto, motivo, estado, fecha_creacion
            FROM multas
            WHERE id_multa = ?
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Multa multa = new Multa();
                    multa.setId(rs.getInt("id_multa"));
                    multa.setMonto(rs.getDouble("monto"));
                    multa.setMotivo(rs.getString("motivo"));
                    multa.setEstado(rs.getString("estado"));

                    java.sql.Date fCreacion = rs.getDate("fecha_creacion");
                    multa.setFechaCreacion(fCreacion != null ? fCreacion.toLocalDate() : null);

                    Prestamo prestamo = new Prestamo();
                    prestamo.setId(rs.getInt("id_prestamo"));
                    multa.setPrestamo(prestamo);

                    return Optional.of(multa);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar multa: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public ListaEnlazada<Multa> listarTodos() {
        ListaEnlazada<Multa> multas = new ListaEnlazada<>();
        String sql = """
            SELECT id_multa, id_prestamo, monto, motivo, estado, fecha_creacion
            FROM multas
            """;

        try (
                Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Multa multa = new Multa();
                multa.setId(rs.getInt("id_multa"));
                multa.setMonto(rs.getDouble("monto"));
                multa.setMotivo(rs.getString("motivo"));
                multa.setEstado(rs.getString("estado"));

                java.sql.Date fCreacion = rs.getDate("fecha_creacion");
                multa.setFechaCreacion(fCreacion != null ? fCreacion.toLocalDate() : null);

                Prestamo prestamo = new Prestamo();
                prestamo.setId(rs.getInt("id_prestamo"));
                multa.setPrestamo(prestamo);

                multas.agregar(multa);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar multas: " + e.getMessage());
        }

        return multas;
    }

}
