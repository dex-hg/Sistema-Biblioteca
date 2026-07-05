package biblioteca.controlador;

import biblioteca.modelo.Usuario;
import biblioteca.servicios.AutenticacionService;
import java.util.Optional;

public class LoginController {

    private final AutenticacionService autenticacionService;

    public LoginController() {
        this.autenticacionService = new AutenticacionService();
    }

    /**
     * Procesa el login del usuario.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Optional con el usuario si las credenciales son correctas, vacío de lo contrario.
     */
    public Optional<Usuario> procesarLogin(String username, String password) {
        return autenticacionService.login(username, password);
    }

    /**
     * Cierra la sesión activa.
     */
    public void cerrarSesion() {
        autenticacionService.logout();
    }
}
