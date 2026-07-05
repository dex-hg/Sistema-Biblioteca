package biblioteca.dao.interfaces;

import biblioteca.modelo.Usuario;

import java.util.Optional;

public interface UsuarioDAO
        extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> login(
            String username,
            String password
    );

    Optional<Usuario> buscarPorUsername(
            String username
    );

}
