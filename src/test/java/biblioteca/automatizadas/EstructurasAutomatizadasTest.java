package biblioteca.automatizadas;

import biblioteca.estructuras.AlgoritmosBusqueda;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.estructuras.Pila;
import biblioteca.modelo.Libro;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstructurasAutomatizadasTest {

    @Test
    void pilaDebeRespetarOrdenLifo() {
        Pila<String> pila = new Pila<>();

        assertTrue(pila.isEmpty());
        pila.push("VistaLogin");
        pila.push("VistaPrincipal");
        pila.push("VistaLibros");

        assertEquals(3, pila.size());
        assertEquals("VistaLibros", pila.peek());
        assertEquals("VistaLibros", pila.pop());
        assertEquals("VistaPrincipal", pila.pop());
        assertEquals("VistaLogin", pila.pop());
        assertTrue(pila.isEmpty());
        assertThrows(EmptyStackException.class, pila::pop);
    }

    @Test
    void colaDebeRespetarOrdenFifo() {
        Cola<String> cola = new Cola<>();

        assertTrue(cola.isEmpty());
        cola.enqueue("EST101");
        cola.enqueue("EST102");
        cola.enqueue("EST103");

        assertEquals(3, cola.size());
        assertEquals("EST101", cola.peek());
        assertEquals("EST101", cola.dequeue());
        assertEquals("EST102", cola.dequeue());
        assertEquals("EST103", cola.dequeue());
        assertTrue(cola.isEmpty());
        assertThrows(NoSuchElementException.class, cola::dequeue);
    }

    @Test
    void listaEnlazadaDebeAgregarObtenerRemoverYConvertirAArreglo() {
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();

        numeros.agregar(10);
        numeros.agregar(20);
        numeros.agregar(30);
        numeros.agregar(40);

        assertEquals(4, numeros.size());
        assertEquals(30, numeros.obtener(2));
        assertEquals(20, numeros.remover(1));
        assertEquals(3, numeros.size());
        assertEquals(30, numeros.obtener(1));
        assertArrayEquals(new Integer[]{10, 30, 40}, numeros.toArray(new Integer[0]));
    }

    @Test
    void quickSortDebeOrdenarNumerosYLibrosPorTitulo() {
        Integer[] numeros = {34, 12, 5, 89, 56, 21, 2};
        AlgoritmosOrdenamiento.ordenarQuickSort(numeros);
        assertArrayEquals(new Integer[]{2, 5, 12, 21, 34, 56, 89}, numeros);

        Libro[] libros = {
            libro("Introducción a los Algoritmos"),
            libro("Cálculo de una Variable"),
            libro("El amor en los tiempos del cólera"),
            libro("Algebra Lineal")
        };
        Comparator<Libro> porTitulo = (l1, l2) -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo());

        AlgoritmosOrdenamiento.ordenarQuickSort(libros, porTitulo);

        assertEquals("Algebra Lineal", libros[0].getTitulo());
        assertEquals("Cálculo de una Variable", libros[1].getTitulo());
        assertEquals("El amor en los tiempos del cólera", libros[2].getTitulo());
        assertEquals("Introducción a los Algoritmos", libros[3].getTitulo());
    }

    @Test
    void busquedasDebenRetornarIndiceEncontradoOMenosUno() {
        Integer[] numerosOrdenados = {2, 5, 12, 21, 34, 56, 89};

        assertEquals(3, AlgoritmosBusqueda.busquedaBinaria(numerosOrdenados, 21));
        assertEquals(-1, AlgoritmosBusqueda.busquedaBinaria(numerosOrdenados, 99));

        Libro[] libros = {
            libro("Algebra Lineal"),
            libro("Cálculo de una Variable"),
            libro("El amor en los tiempos del cólera")
        };
        Libro buscado = libro("Cálculo de una Variable");
        Comparator<Libro> porTitulo = (l1, l2) -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo());

        assertEquals(1, AlgoritmosBusqueda.busquedaLineal(libros, buscado, porTitulo));
    }

    private Libro libro(String titulo) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        return libro;
    }
}
