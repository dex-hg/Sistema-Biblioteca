package biblioteca.servicios;

import biblioteca.dao.impl.EstudianteDAOImpl;
import biblioteca.dao.interfaces.EstudianteDAO;
import biblioteca.modelo.Estudiante;

import java.util.Optional;
import biblioteca.conexion.ConexionBD;
import java.sql.SQLException;
import biblioteca.estructuras.ListaEnlazada;

/**
 * Servicio encargado de gestionar las operaciones operativas de los
 * estudiantes. Realiza validaciones del correo, teléfono y campos obligatorios
 * antes de interactuar con la base de datos.
 */
public class EstudianteService {

    private final EstudianteDAO estudianteDAO;

    /**
     * Constructor por defecto. Inicializa las dependencias.
     */
    public EstudianteService() {
        this.estudianteDAO = new EstudianteDAOImpl();
    }

    /**
     * Constructor para inyección de dependencias.
     *
     * @param estudianteDAO DAO de estudiantes.
     */
    public EstudianteService(EstudianteDAO estudianteDAO) {
        this.estudianteDAO = estudianteDAO;
    }

    /**
     * Registra un nuevo estudiante en el sistema validando sus datos. El
     * estudiante cuenta con una cuenta de usuario asociada automáticamente.
     *
     * @param estudiante Estudiante con datos de perfil y de usuario
     * (username/password).
     * @return true si se guardó exitosamente, false en caso contrario.
     */
    public boolean registrarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            return false;
        }

        // 1. Validaciones básicas de campos obligatorios
        if (estudiante.getCodigo() == null
                || estudiante.getCodigo().trim().isEmpty()) {
            System.out.println(
                    "Validación fallida: "
                    + "El código del estudiante es obligatorio.");
            return false;
        }
        if (estudiante.getNombres() == null
                || estudiante.getNombres().trim().isEmpty()) {
            return false;
        }
        if (estudiante.getApellidos() == null
                || estudiante.getApellidos().trim().isEmpty()) {
            return false;
        }

        // 2. Validación de usuario para login
        if (estudiante.getUsername() == null
                || estudiante.getUsername().trim().isEmpty()) {
            System.out.println(
                    "Validación fallida: "
                    + "El username de login es obligatorio.");
            return false;
        }
        if (estudiante.getPassword() == null
                || estudiante.getPassword().trim().isEmpty()) {
            System.out.println(
                    "Validación fallida: "
                    + "La password de login es obligatoria.");
            return false;
        }

        // 3. Validación de correo electrónico
        if (estudiante.getCorreo()
                != null
                && !estudiante.getCorreo().trim().isEmpty()) {
            if (!estudiante.getCorreo().contains("@")) {
                System.out.println(
                        "Validación fallida: "
                        + "El formato de correo es incorrecto (Falta '@').");
                return false;
            }
        }

        // 4. Validación de teléfono (solo números)
        if (estudiante.getTelefono()
                != null
                && !estudiante.getTelefono().trim().isEmpty()) {
            String tel = estudiante.getTelefono().trim();
            for (int i = 0; i < tel.length(); i++) {
                if (!Character.isDigit(tel.charAt(i))
                        && tel.charAt(i) != ' '
                        && tel.charAt(i) != '+') {
                    System.out.println(
                            "Validación fallida: "
                            + "El teléfono debe contener solo caracteres"
                            + " numéricos.");
                    return false;
                }
            }
        }

        // 5. Validar que el código no esté duplicado
        if (estudianteDAO.buscarPorCodigo(
                estudiante.getCodigo()).isPresent()) {
            System.out.println(
                    "Validación fallida: "
                    + "El código de estudiante '"
                    + estudiante.getCodigo()
                    + "' ya existe.");
            return false;
        }

        try {
            ConexionBD.beginTransaction();
            boolean res = estudianteDAO.guardar(estudiante);
            if (res) {
                ConexionBD.commit();
            } else {
                ConexionBD.rollback();
            }
            return res;
        } catch (SQLException e) {
            System.out.println("Error en transacción registrarEstudiante: " + e.getMessage());
            ConexionBD.rollback();
            return false;
        } finally {
            ConexionBD.endTransaction();
        }
    }

    /**
     * Actualiza la información de un estudiante existente.
     *
     * @param estudiante El estudiante con los nuevos datos.
     * @return true si se actualizó, false en caso contrario.
     */
    public boolean actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getId() <= 0) {
            return false;
        }
        if (estudiante.getCodigo() == null
                || estudiante.getCodigo().trim().isEmpty()) {
            return false;
        }
        if (estudiante.getNombres() == null
                || estudiante.getNombres().trim().isEmpty()) {
            return false;
        }
        if (estudiante.getApellidos() == null
                || estudiante.getApellidos().trim().isEmpty()) {
            return false;
        }

        // Validar correo
        if (estudiante.getCorreo() != null
                && !estudiante.getCorreo().trim().isEmpty()
                && !estudiante.getCorreo().contains("@")) {
            return false;
        }

        try {
            ConexionBD.beginTransaction();
            boolean res = estudianteDAO.actualizar(estudiante);
            if (res) {
                ConexionBD.commit();
            } else {
                ConexionBD.rollback();
            }
            return res;
        } catch (SQLException e) {
            System.out.println("Error en transacción actualizarEstudiante: " + e.getMessage());
            ConexionBD.rollback();
            return false;
        } finally {
            ConexionBD.endTransaction();
        }
    }

    /**
     * Elimina un estudiante y su usuario asociado.
     *
     * @param id Identificador único del estudiante.
     * @return true si fue eliminado, false en caso contrario.
     */
    public boolean eliminarEstudiante(int id) {
        if (id <= 0) {
            return false;
        }
        return estudianteDAO.eliminar(id);
    }

    /**
     * Busca un estudiante por su ID de usuario.
     *
     * @param id Identificador.
     * @return Un Optional con el estudiante si existe.
     */
    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return estudianteDAO.buscarPorId(id);
    }

    /**
     * Busca un estudiante por su código universitario.
     *
     * @param codigo Código del estudiante (e.g. EST2026101).
     * @return Un Optional con el estudiante si existe.
     */
    public Optional<Estudiante> buscarEstudiantePorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return estudianteDAO.buscarPorCodigo(codigo.trim());
    }

    /**
     * Lista todos los estudiantes matriculados en la biblioteca.
     *
     * @return Lista de estudiantes.
     */
    public ListaEnlazada<Estudiante> listarTodos() {
        return estudianteDAO.listarTodos();
    }
}
