package biblioteca.dao.interfaces;

import biblioteca.modelo.Estudiante;
import java.util.Optional;

public interface EstudianteDAO extends CrudRepository<Estudiante, Integer> {
    Optional<Estudiante> buscarPorCodigo(String codigo);
}
