
import biblioteca.estructuras.Pila;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.AlgoritmosBusqueda;
import biblioteca.modelo.DetallePrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Clase de prueba manual para verificar el comportamiento de las estructuras de
 * datos y algoritmos del curso implementados de forma personalizada.
 */
public class TestEstructuras {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  INICIANDO PRUEBAS DE ESTRUCTURAS Y ALGORITMOS   ");
        System.out.println("==================================================\n");

        // 1. Probar Pila (LIFO)
        probarPila();

        // 2. Probar Cola (FIFO)
        probarCola();

        // 3. Probar Lista Enlazada
        probarListaEnlazada();

        // 4. Probar Algoritmos de Ordenamiento (QuickSort)
        probarOrdenamiento();

        // 5. Probar Algoritmos de Búsqueda (Búsqueda Binaria y Lineal)
        probarBusqueda();

        // 6. Probar aplicaciones de dominio con Pila y Cola
        probarAplicacionesDominio();

        System.out.println("==================================================");
        System.out.println("  TODAS LAS PRUEBAS FINALIZADAS CON ÉXITO         ");
        System.out.println("==================================================");
    }

    private static void probarPila() {
        System.out.println("--- 1. PROBANDO PILA (LIFO) ---");
        Pila<String> pilaHistorial = new Pila<>();
        System.out.println("¿Está vacía?: " + pilaHistorial.isEmpty()); // true

        System.out.println("Apilando: VistaLogin, VistaPrincipal, VistaLibros...");
        pilaHistorial.push("VistaLogin");
        pilaHistorial.push("VistaPrincipal");
        pilaHistorial.push("VistaLibros");

        System.out.println("Tamaño: " + pilaHistorial.size()); // 3
        System.out.println("Tope (peek): " + pilaHistorial.peek()); // VistaLibros

        System.out.println("Desapilando: " + pilaHistorial.pop()); // VistaLibros
        System.out.println("Desapilando: " + pilaHistorial.pop()); // VistaPrincipal
        System.out.println("Tope actual: " + pilaHistorial.peek()); // VistaLogin
        System.out.println("Tamaño actual: " + pilaHistorial.size()); // 1
        System.out.println("Desapilando: " + pilaHistorial.pop()); // VistaLogin
        System.out.println("¿Está vacía al final?: " + pilaHistorial.isEmpty()); // true
        System.out.println();
    }

    private static void probarCola() {
        System.out.println("--- 2. PROBANDO COLA (FIFO) ---");
        Cola<String> colaEspera = new Cola<>();
        System.out.println("¿Está vacía?: " + colaEspera.isEmpty()); // true

        System.out.println("Encolando estudiantes: EST101, EST102, EST103...");
        colaEspera.enqueue("EST101");
        colaEspera.enqueue("EST102");
        colaEspera.enqueue("EST103");

        System.out.println("Tamaño: " + colaEspera.size()); // 3
        System.out.println("Frente (peek): " + colaEspera.peek()); // EST101

        System.out.println("Desencolando: " + colaEspera.dequeue()); // EST101
        System.out.println("Desencolando: " + colaEspera.dequeue()); // EST102
        System.out.println("Frente actual: " + colaEspera.peek()); // EST103
        System.out.println("Tamaño actual: " + colaEspera.size()); // 1
        System.out.println("Desencolando: " + colaEspera.dequeue()); // EST103
        System.out.println("¿Está vacía al final?: " + colaEspera.isEmpty()); // true
        System.out.println();
    }

    private static void probarListaEnlazada() {
        System.out.println("--- 3. PROBANDO LISTA ENLAZADA ---");
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        System.out.println("¿Está vacía?: " + numeros.isEmpty()); // true

        System.out.println("Agregando: 10, 20, 30, 40...");
        numeros.agregar(10);
        numeros.agregar(20);
        numeros.agregar(30);
        numeros.agregar(40);

        System.out.println("Tamaño: " + numeros.size()); // 4
        System.out.println("Elemento en índice 2: " + numeros.obtener(2)); // 30

        System.out.println("Removiendo índice 1 (valor 20): " + numeros.remover(1));
        System.out.println("Tamaño actual: " + numeros.size()); // 3
        System.out.println("Elemento en índice 1 ahora: " + numeros.obtener(1)); // 30
        System.out.println("¿Contiene 40?: " + numeros.contiene(40)); // true
        System.out.println("Removiendo último: " + numeros.removerUltimo()); // 40

        ListaEnlazada<Integer> mayoresAQuince = numeros.filtrar(n -> n > 15);
        System.out.print("Filtrados mayores a 15: ");
        for (Integer numero : mayoresAQuince) {
            System.out.print(numero + " ");
        }
        System.out.println();

        // Probar conversión a arreglo
        Integer[] arrayTemplate = new Integer[0];
        Integer[] array = numeros.toArray(arrayTemplate);
        System.out.print("Elementos en arreglo resultante: ");
        for (Integer num : array) {
            System.out.print(num + " ");
        }
        System.out.println("\n");
    }

    private static void probarOrdenamiento() {
        System.out.println("--- 4. PROBANDO ALGORITMOS DE ORDENAMIENTO (QUICKSORT) ---");

        // A. Ordenar Comparable (Integers)
        Integer[] numerosDesordenados = {34, 12, 5, 89, 56, 21, 2};
        System.out.print("Arreglo numérico original: ");
        imprimirArreglo(numerosDesordenados);

        AlgoritmosOrdenamiento.ordenarQuickSort(numerosDesordenados);
        System.out.print("Arreglo numérico ordenado (QuickSort): ");
        imprimirArreglo(numerosDesordenados);

        // B. Ordenar no-Comparable (Libros) usando Comparator
        Libro libro1 = new Libro();
        libro1.setTitulo("Introducción a los Algoritmos");
        Libro libro2 = new Libro();
        libro2.setTitulo("Cálculo de una Variable");
        Libro libro3 = new Libro();
        libro3.setTitulo("El amor en los tiempos del cólera");
        Libro libro4 = new Libro();
        libro4.setTitulo("Álgebra Lineal");

        Libro[] libros = {libro1, libro2, libro3, libro4};
        System.out.println("Catálogo de libros original:");
        imprimirLibros(libros);

        // Ordenar por Título alfabéticamente
        Comparator<Libro> comparadorTitulo = new Comparator<Libro>() {
            @Override
            public int compare(Libro o1, Libro o2) {
                return o1.getTitulo().compareToIgnoreCase(o2.getTitulo());
            }
        };

        AlgoritmosOrdenamiento.ordenarQuickSort(libros, comparadorTitulo);
        System.out.println("Catálogo de libros ordenado por Título:");
        imprimirLibros(libros);

        ListaEnlazada<Libro> catalogo = new ListaEnlazada<>();
        catalogo.agregar(libro2);
        catalogo.agregar(libro4);
        catalogo.agregar(libro1);
        ListaEnlazada<Libro> catalogoOrdenado = AlgoritmosOrdenamiento.ordenarQuickSort(
                catalogo,
                new Libro[0],
                comparadorTitulo);
        System.out.println("ListaEnlazada de libros ordenada con QuickSort:");
        for (Libro libro : catalogoOrdenado) {
            System.out.println("  - " + libro.getTitulo());
        }
        System.out.println();
    }

    private static void probarBusqueda() {
        System.out.println("--- 5. PROBANDO ALGORITMOS DE BÚSQUEDA ---");

        // A. Búsqueda Binaria sobre Arreglo Ordenado (Integers)
        Integer[] numerosOrdenados = {2, 5, 12, 21, 34, 56, 89};
        int buscarNum = 21;
        int indiceNum = AlgoritmosBusqueda.busquedaBinaria(numerosOrdenados, buscarNum);
        System.out.println("Búsqueda Binaria de " + buscarNum + ": índice = " + indiceNum); // 3

        int buscarNumInvalido = 99;
        int indiceNumInvalido = AlgoritmosBusqueda.busquedaBinaria(numerosOrdenados, buscarNumInvalido);
        System.out.println("Búsqueda Binaria de " + buscarNumInvalido + ": índice = " + indiceNumInvalido); // -1

        // B. Búsqueda Lineal sobre Libros (por Título)
        Libro libro1 = new Libro();
        libro1.setTitulo("Álgebra Lineal");
        Libro libro2 = new Libro();
        libro2.setTitulo("Cálculo de una Variable");
        Libro libro3 = new Libro();
        libro3.setTitulo("El amor en los tiempos del cólera");
        Libro libro4 = new Libro();
        libro4.setTitulo("Introducción a los Algoritmos");
        Libro[] libros = {libro1, libro2, libro3, libro4};

        Libro libroBuscado = new Libro();
        libroBuscado.setTitulo("Cálculo de una Variable");

        Comparator<Libro> comparadorEquivalencia = new Comparator<Libro>() {
            @Override
            public int compare(Libro o1, Libro o2) {
                return o1.getTitulo().compareToIgnoreCase(o2.getTitulo());
            }
        };

        int indiceLibro = AlgoritmosBusqueda.busquedaLineal(libros, libroBuscado, comparadorEquivalencia);
        System.out.println("Búsqueda Lineal del libro '" + libroBuscado.getTitulo() + "': índice = " + indiceLibro); // 1

        ListaEnlazada<Integer> numerosEnLista = new ListaEnlazada<>();
        numerosEnLista.agregar(2);
        numerosEnLista.agregar(5);
        numerosEnLista.agregar(12);
        numerosEnLista.agregar(21);
        int indiceLista = AlgoritmosBusqueda.busquedaBinaria(numerosEnLista, 12, new Integer[0]);
        System.out.println("Búsqueda Binaria sobre ListaEnlazada de 12: índice = " + indiceLista);
        System.out.println();
    }

    private static void probarAplicacionesDominio() {
        System.out.println("--- 6. APLICACIONES DE DOMINIO CON PILA Y COLA ---");

        ListaEnlazada<DetallePrestamo> carrito = new ListaEnlazada<>();
        Pila<DetallePrestamo> historialCarrito = new Pila<>();
        DetallePrestamo detalleAlgoritmos = detalle(libro("Algoritmos"));
        DetallePrestamo detalleJava = detalle(libro("Programación Java"));

        carrito.agregar(detalleAlgoritmos);
        historialCarrito.push(detalleAlgoritmos);
        carrito.agregar(detalleJava);
        historialCarrito.push(detalleJava);
        System.out.println("Último libro deshecho del carrito: " + historialCarrito.pop().getLibro().getTitulo());
        carrito.removerUltimo();
        System.out.println("Libros restantes en carrito: " + carrito.size());

        ListaEnlazada<Prestamo> prestamos = new ListaEnlazada<>();
        prestamos.agregar(prestamo(3, LocalDate.of(2026, 7, 5)));
        prestamos.agregar(prestamo(1, LocalDate.of(2026, 7, 1)));
        prestamos.agregar(prestamo(2, LocalDate.of(2026, 7, 3)));
        ListaEnlazada<Prestamo> ordenados = AlgoritmosOrdenamiento.ordenarQuickSort(
                prestamos,
                new Prestamo[0],
                Comparator.comparing(Prestamo::getFechaPrestamo));

        Cola<Prestamo> colaDevoluciones = new Cola<>();
        for (Prestamo prestamo : ordenados) {
            colaDevoluciones.enqueue(prestamo);
        }
        System.out.println("Orden FIFO de devolución por antigüedad:");
        while (!colaDevoluciones.isEmpty()) {
            Prestamo prestamo = colaDevoluciones.dequeue();
            System.out.println("  Préstamo #" + prestamo.getId() + " - " + prestamo.getFechaPrestamo());
        }
        System.out.println();
    }

    private static void imprimirArreglo(Integer[] arr) {
        for (Integer num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    private static void imprimirLibros(Libro[] libros) {
        for (int i = 0; i < libros.length; i++) {
            System.out.println("  [" + i + "] " + libros[i].getTitulo());
        }
    }

    private static Libro libro(String titulo) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        return libro;
    }

    private static DetallePrestamo detalle(Libro libro) {
        DetallePrestamo detalle = new DetallePrestamo();
        detalle.setLibro(libro);
        detalle.setCantidad(1);
        return detalle;
    }

    private static Prestamo prestamo(int id, LocalDate fechaPrestamo) {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setFechaPrestamo(fechaPrestamo);
        return prestamo;
    }
}
