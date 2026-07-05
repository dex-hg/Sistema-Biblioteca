package biblioteca.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDAO {

    /**
     * Obtiene un Integer del ResultSet.
     */
    protected Integer getInteger(
            ResultSet rs,
            String columna
    ) throws SQLException {

        int valor = rs.getInt(columna);

        return rs.wasNull()
                ? null
                : valor;
    }

    /**
     * Obtiene un String seguro.
     */
    protected String getString(
            ResultSet rs,
            String columna
    ) throws SQLException {

        return rs.getString(columna);
    }
}
