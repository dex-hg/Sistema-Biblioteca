package biblioteca.dao.interfaces;

import biblioteca.modelo.Prestamo;

import biblioteca.estructuras.ListaEnlazada;

public interface PrestamoDAO
        extends CrudRepository<Prestamo, Integer> {

    ListaEnlazada<Prestamo> listarPrestamosActivos();

    ListaEnlazada<Prestamo> listarPrestamoPorEstudiante(
            int idEstudiante
    );

}
