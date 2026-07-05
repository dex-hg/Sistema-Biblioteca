package biblioteca.servicios;

import biblioteca.dao.impl.LibroDAOImpl;
import biblioteca.dao.interfaces.LibroDAO;
import biblioteca.estructuras.AlgoritmosBusqueda;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.modelo.Libro;

import java.util.Comparator;
import java.util.Optional;
import biblioteca.estructuras.ListaEnlazada;

/**
 * Servicio encargado de gestionar el catálogo de libros y validar sus reglas de
 * negocio. Utiliza los algoritmos personalizados de ordenamiento y búsqueda en
 * memoria.
 */
public class LibroService {

    private final LibroDAO libroDAO;

    // Comparador reutilizable para ordenar y buscar alfabéticamente los libros.
    private static final Comparator<Libro> COMPARADOR_TITULO = (l1, l2)
            -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo());

    /**
     * Constructor por defecto. Inicializa las dependencias de persistencia.
     */
    public LibroService() {
        this.libroDAO = new LibroDAOImpl();
    }

    /**
     * Constructor para inyección de dependencias.
     *
     * @param libroDAO DAO de libros.
     */
    public LibroService(LibroDAO libroDAO) {
        this.libroDAO = libroDAO;
    }

    /**
     * Registra un nuevo libro en el sistema validando sus campos obligatorios.
     *
     * @param libro El libro a registrar.
     * @return true si fue guardado correctamente, false en caso contrario.
     */
    public boolean registrarLibro(Libro libro) {
        if (libro == null) {
            return false;
        }
        // Validaciones de negocio
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            System.out.println(
                    "Validación fallida: "
                    + "El título del libro no puede estar vacío.");
            return false;
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            System.out.println(
                    "Validación fallida: "
                    + "El autor del libro no puede estar vacío.");
            return false;
        }
        if (libro.getStock() < 0) {
            System.out.println(
                    "Validación fallida: "
                    + "El stock del libro no puede ser negativo.");
            return false;
        }

        return libroDAO.guardar(libro);
    }

    /**
     * Actualiza la información de un libro existente.
     *
     * @param libro El libro con datos actualizados.
     * @return true si fue actualizado, false en caso contrario o si falla la
     * validación.
     */
    public boolean actualizarLibro(Libro libro) {
        if (libro == null || libro.getId() <= 0) {
            return false;
        }
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return false;
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return false;
        }
        if (libro.getStock() < 0) {
            return false;
        }

        return libroDAO.actualizar(libro);
    }

    /**
     * Elimina un libro del sistema.
     *
     * @param id Identificador del libro.
     * @return true si fue eliminado, false en caso de error.
     */
    public boolean eliminarLibro(int id) {
        if (id <= 0) {
            return false;
        }
        return libroDAO.eliminar(id);
    }

    /**
     * Busca un libro por su ID único.
     *
     * @param id Identificador.
     * @return Un Optional con el libro encontrado, u vacío si no existe.
     */
    public Optional<Libro> buscarLibroPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return libroDAO.buscarPorId(id);
    }

    /**
     * Obtiene todos los libros del catálogo ordenados alfabéticamente por
     * título. Utiliza la estructura personalizada QuickSort en memoria.
     *
     * @return Lista ordenada de libros.
     */
    public ListaEnlazada<Libro> obtenerCatalogoOrdenadoPorTitulo() {
        ListaEnlazada<Libro> listaLibros = libroDAO.listarTodos();
        if (listaLibros.isEmpty()) {
            return listaLibros;
        }

        // Convertir la lista a un arreglo de Java para poder ordenar
        Libro[] arrayLibros = listaLibros.toArray(new Libro[0]);

        // Aplicar QuickSort recursivo a medida
        AlgoritmosOrdenamiento.ordenarQuickSort(
                arrayLibros,
                COMPARADOR_TITULO);

        // Retornar como una ListaEnlazada
        ListaEnlazada<Libro> listaOrdenada = new ListaEnlazada<>();
        for (Libro l : arrayLibros) {
            listaOrdenada.agregar(l);
        }
        return listaOrdenada;
    }

    /**
     * Busca un libro por su título exacto utilizando la Búsqueda Binaria
     * recursiva. Recomienda ordenar el catálogo previamente.
     *
     * @param titulo Título exacto del libro a buscar.
     * @return Un Optional con el libro encontrado, u vacío si no se encuentra.
     */
    public Optional<Libro> buscarLibroPorTituloBinario(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return Optional.empty();
        }

        // Recuperar y ordenar catálogo (requisito para búsqueda binaria)
        ListaEnlazada<Libro> catalogoOrdenado = obtenerCatalogoOrdenadoPorTitulo();
        if (catalogoOrdenado.isEmpty()) {
            return Optional.empty();
        }

        Libro[] arrayLibros = catalogoOrdenado.toArray(new Libro[0]);
        Libro libroObjetivo = new Libro();
        libroObjetivo.setTitulo(titulo);

        // Ejecutar Búsqueda Binaria recursiva a medida
        int indice = AlgoritmosBusqueda.busquedaBinaria(
                arrayLibros,
                libroObjetivo,
                COMPARADOR_TITULO);

        if (indice != -1) {
            return Optional.of(arrayLibros[indice]);
        }

        return Optional.empty();
    }

    /**
     * Busca libros cuyos títulos coincidan parcialmente con un criterio.
     * Utiliza la persistencia directa (SQL LIKE).
     *
     * @param query Criterio de búsqueda.
     * @return Lista de libros que coinciden.
     */
    public ListaEnlazada<Libro> buscarPorTitulo(String query) {
        if (query == null || query.trim().isEmpty()) {
            return libroDAO.listarTodos();
        }
        return libroDAO.buscarPorTitulo(query);
    }
}
