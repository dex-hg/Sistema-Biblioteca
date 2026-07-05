package biblioteca.dao.interfaces;

import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

public interface CrudRepository<T, ID> {

    boolean guardar(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(ID id);

    Optional<T> buscarPorId(ID id);

    ListaEnlazada<T> listarTodos();

}
