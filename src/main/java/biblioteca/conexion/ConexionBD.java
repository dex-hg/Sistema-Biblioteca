package biblioteca.conexion;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private ConexionBD() {
    }

    /**
     * Obtiene una conexión a PostgreSQL. Si hay una transacción activa en el
     * hilo actual, retorna un proxy que ignora las llamadas a close().
     *
     * @return Connection activa
     * @throws SQLException error de conexión
     */
    public static Connection getConexion()
            throws SQLException {

        Connection txConn = threadLocalConnection.get();
        if (txConn != null && !txConn.isClosed()) {
            // Retornar un proxy de la conexión que ignora close()
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            // Ignorar close() durante transacciones
                            return null;
                        }
                        try {
                            return method.invoke(txConn, args);
                        } catch (java.lang.reflect.InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
            );
        }

        // Conexión normal fuera de transacción
        return DriverManager.getConnection(
                ConexionConfig.URL,
                ConexionConfig.USER,
                ConexionConfig.PASSWORD
        );
    }

    /**
     * Inicia una transacción en el hilo actual.
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = threadLocalConnection.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(
                    ConexionConfig.URL,
                    ConexionConfig.USER,
                    ConexionConfig.PASSWORD
            );
            conn.setAutoCommit(false);
            threadLocalConnection.set(conn);
        }
    }

    /**
     * Confirma la transacción en el hilo actual.
     */
    public static void commit() throws SQLException {
        Connection conn = threadLocalConnection.get();
        if (conn != null && !conn.isClosed()) {
            conn.commit();
        }
    }

    /**
     * Deshace los cambios de la transacción en el hilo actual.
     */
    public static void rollback() {
        Connection conn = threadLocalConnection.get();
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar rollback: " + e.getMessage());
        }
    }

    /**
     * Finaliza la transacción en el hilo actual, cerrando la conexión real.
     */
    public static void endTransaction() {
        Connection conn = threadLocalConnection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión transaccional: " + e.getMessage());
            } finally {
                threadLocalConnection.remove();
            }
        }
    }
}
