package biblioteca.dao.interfaces;

import biblioteca.modelo.Libro;

import biblioteca.estructuras.ListaEnlazada;

public interface LibroDAO
        extends CrudRepository<Libro, Integer> {

    ListaEnlazada<Libro> buscarPorTitulo(String titulo);

    ListaEnlazada<Libro> buscarPorAutor(String autor);

    boolean actualizarStock(
            int idLibro,
            int nuevoStock
    );

}
