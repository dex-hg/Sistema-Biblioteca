package biblioteca;

import biblioteca.vista.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Inicializar el tema FlatLaf moderno
        try {
            FlatLightLaf.setup();
            
            // Personalizaciones adicionales de UI
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            System.err.println("Fallo al inicializar FlatLaf: " + e.getMessage());
        }

        // Ejecutar en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
        
        // Credenciales de prueba
        // user: admin, password: root, rol: Administrador
        // user: biblio1, password: password123, rol: Bibliotecario
        // user: estudiante1, password: estudiante123, rol: Estudiante
        // ID alumno: EST2026101 para pruebas 
        
    }
}
