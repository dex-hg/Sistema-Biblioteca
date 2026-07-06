
import java.sql.*;

public class Testconexion {
    
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/biblioteca";
        Connection connection = DriverManager.getConnection(url, "postgres", "root");
        
        String query = ("SELECT * FROM usuarios");
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {                
                String username = rs.getString("username");
                String password = rs.getString("password");
                String nombre_completo = rs.getString("nombre_completo");
                int rol = rs.getInt("id_rol");
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("Nombre completo: " + nombre_completo);
                System.out.println("Rol: " + rol);
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e.toString());
        }
        
    }
    
}
