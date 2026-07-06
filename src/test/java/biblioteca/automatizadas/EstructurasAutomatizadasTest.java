package biblioteca.automatizadas;

import biblioteca.estructuras.AlgoritmosBusqueda;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.estructuras.Pila;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void listaEnlazadaDebeFiltrarContenerRemoverUltimoEIterar() {
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        numeros.agregar(5);
        numeros.agregar(12);
        numeros.agregar(20);
        numeros.agregar(7);

        assertTrue(numeros.contiene(12));
        assertFalse(numeros.contiene(99));

        ListaEnlazada<Integer> mayoresADiez = numeros.filtrar(n -> n > 10);
        assertArrayEquals(new Integer[]{12, 20}, mayoresADiez.toArray(new Integer[0]));

        int suma = 0;
        for (Integer numero : numeros) {
            suma += numero;
        }
        assertEquals(44, suma);
        assertEquals(7, numeros.removerUltimo());
        assertArrayEquals(new Integer[]{5, 12, 20}, numeros.toArray(new Integer[0]));
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

    @Test
    void quickSortYBusquedasDebenOperarSobreListaEnlazada() {
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        numeros.agregar(34);
        numeros.agregar(12);
        numeros.agregar(5);
        numeros.agregar(89);

        ListaEnlazada<Integer> ordenados
                = AlgoritmosOrdenamiento.ordenarQuickSort(numeros, new Integer[0]);

        assertArrayEquals(new Integer[]{5, 12, 34, 89}, ordenados.toArray(new Integer[0]));
        assertEquals(2, AlgoritmosBusqueda.busquedaBinaria(ordenados, 34, new Integer[0]));
        assertEquals(-1, AlgoritmosBusqueda.busquedaLineal(ordenados, 100));
    }

    @Test
    void pilaDebePermitirDeshacerUltimoLibroDelCarrito() {
        ListaEnlazada<DetallePrestamo> carrito = new ListaEnlazada<>();
        Pila<DetallePrestamo> historial = new Pila<>();
        Pila<DetallePrestamo> rehacer = new Pila<>();

        DetallePrestamo primero = detalle(libro("Algoritmos"));
        DetallePrestamo segundo = detalle(libro("Base de Datos"));
        carrito.agregar(primero);
        historial.push(primero);
        carrito.agregar(segundo);
        historial.push(segundo);

        assertEquals("Base de Datos", historial.pop().getLibro().getTitulo());
        DetallePrestamo deshecho = carrito.removerUltimo();
        rehacer.push(deshecho);
        assertEquals("Base de Datos", deshecho.getLibro().getTitulo());
        assertEquals(1, carrito.size());
        assertEquals("Algoritmos", carrito.obtener(0).getLibro().getTitulo());

        DetallePrestamo rehecho = rehacer.pop();
        carrito.agregar(rehecho);
        historial.push(rehecho);
        assertEquals(2, carrito.size());
        assertEquals("Base de Datos", carrito.obtener(1).getLibro().getTitulo());
    }

    @Test
    void colaDebePriorizarPrestamosActivosPorAntiguedad() {
        ListaEnlazada<Prestamo> prestamos = new ListaEnlazada<>();
        prestamos.agregar(prestamo(3, LocalDate.of(2026, 7, 5)));
        prestamos.agregar(prestamo(1, LocalDate.of(2026, 7, 1)));
        prestamos.agregar(prestamo(2, LocalDate.of(2026, 7, 3)));

        Comparator<Prestamo> porFecha = Comparator.comparing(Prestamo::getFechaPrestamo);
        ListaEnlazada<Prestamo> ordenados = AlgoritmosOrdenamiento.ordenarQuickSort(
                prestamos,
                new Prestamo[0],
                porFecha);

        Cola<Prestamo> cola = new Cola<>();
        for (Prestamo prestamo : ordenados) {
            cola.enqueue(prestamo);
        }

        assertEquals(1, cola.dequeue().getId());
        assertEquals(2, cola.dequeue().getId());
        assertEquals(3, cola.dequeue().getId());
        assertTrue(cola.isEmpty());
    }

    private Libro libro(String titulo) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        return libro;
    }

    private DetallePrestamo detalle(Libro libro) {
        DetallePrestamo detalle = new DetallePrestamo();
        detalle.setLibro(libro);
        detalle.setCantidad(1);
        return detalle;
    }

    private Prestamo prestamo(int id, LocalDate fechaPrestamo) {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setFechaPrestamo(fechaPrestamo);
        return prestamo;
    }
}
