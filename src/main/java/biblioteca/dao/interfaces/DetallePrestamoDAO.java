package biblioteca.dao.interfaces;

import biblioteca.modelo.DetallePrestamo;
import biblioteca.estructuras.ListaEnlazada;

public interface DetallePrestamoDAO
        extends CrudRepository<DetallePrestamo, Integer> {

    ListaEnlazada<DetallePrestamo> buscarPorPrestamo(int idPrestamo);

}
