package biblioteca.controlador;

import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.modelo.Libro;
import biblioteca.servicios.LibroService;
import java.util.Comparator;
import java.util.Optional;

public class LibroController {

    private final LibroService libroService;

    public LibroController() {
        this.libroService = new LibroService();
    }

    /**
     * Obtiene todos los libros.
     */
    public ListaEnlazada<Libro> obtenerTodos() {
        return libroService.buscarPorTitulo("");
    }

    /**
     * Registra un nuevo libro.
     */
    public boolean registrarLibro(String titulo, String autor, int stock) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setStock(stock);
        return libroService.registrarLibro(libro);
    }

    /**
     * Actualiza los datos de un libro existente.
     */
    public boolean actualizarLibro(int id, String titulo, String autor, int stock) {
        Libro libro = new Libro();
        libro.setId(id);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setStock(stock);
        return libroService.actualizarLibro(libro);
    }

    /**
     * Elimina un libro por su ID.
     */
    public boolean eliminarLibro(int id) {
        return libroService.eliminarLibro(id);
    }

    /**
     * Busca libros por coincidencia en el título.
     */
    public ListaEnlazada<Libro> buscarPorTitulo(String query) {
        return libroService.buscarPorTitulo(query);
    }

    /**
     * Realiza búsqueda binaria exacta por título.
     */
    public Optional<Libro> buscarPorTituloBinario(String titulo) {
        return libroService.buscarLibroPorTituloBinario(titulo);
    }

    /**
     * Ordena una lista de libros utilizando el algoritmo QuickSort personalizado.
     * 
     * @param libros Lista a ordenar.
     * @param criterio "Título" o "Stock".
     * @return Lista ordenada.
     */
    public ListaEnlazada<Libro> ordenarLibros(ListaEnlazada<Libro> libros, String criterio) {
        if (libros == null || libros.isEmpty()) {
            return libros;
        }

        Libro[] array = libros.toArray(new Libro[0]);

        Comparator<Libro> comparator;
        if ("Stock".equalsIgnoreCase(criterio)) {
            comparator = Comparator.comparingInt(Libro::getStock);
        } else {
            // Por defecto por título
            comparator = (l1, l2) -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo());
        }

        AlgoritmosOrdenamiento.ordenarQuickSort(array, comparator);
        
        ListaEnlazada<Libro> ordenada = new ListaEnlazada<>();
        for (Libro l : array) {
            ordenada.agregar(l);
        }
        return ordenada;
    }
}
