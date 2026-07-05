package biblioteca.servicios;

import biblioteca.dao.impl.UsuarioDAOImpl;
import biblioteca.dao.interfaces.UsuarioDAO;
import biblioteca.modelo.Usuario;

import java.util.Optional;

/**
 * Servicio encargado de gestionar los procesos de seguridad y sesión del
 * sistema. Permite el inicio de sesión validando roles y contraseñas.
 */
public class AutenticacionService {

    private final UsuarioDAO usuarioDAO;
    private static Usuario usuarioLogueado;

    /**
     * Constructor por defecto. Inicializa las dependencias de persistencia.
     */
    public AutenticacionService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Constructor para inyección de dependencias (útil para pruebas).
     *
     * @param usuarioDAO DAO de usuarios.
     */
    public AutenticacionService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Realiza la autenticación del usuario en el sistema.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña en texto plano.
     * @return Un Optional con el Usuario si el login fue exitoso, u vacío si
     * falló.
     */
    public Optional<Usuario> login(String username, String password) {
        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparamos contraseñas. En sistemas reales se usaría hash.
            if (usuario.getPassword().equals(password)) {
                usuarioLogueado = usuario;
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }

    /**
     * Cierra la sesión activa en el sistema.
     */
    public void logout() {
        usuarioLogueado = null;
    }

    /**
     * Obtiene el usuario que se encuentra actualmente logueado en la
     * aplicación.
     *
     * @return El usuario con sesión activa, o null si no hay sesión.
     */
    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Verifica si existe un usuario logueado en la aplicación.
     *
     * @return true si hay sesión activa, false en caso contrario.
     */
    public static boolean isSessionActiva() {
        return usuarioLogueado != null;
    }
}
