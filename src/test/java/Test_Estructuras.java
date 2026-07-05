
import biblioteca.estructuras.Pila;
import biblioteca.estructuras.Cola;
import biblioteca.estructuras.ListaEnlazada;
import biblioteca.estructuras.AlgoritmosOrdenamiento;
import biblioteca.estructuras.AlgoritmosBusqueda;
import biblioteca.modelo.Libro;

import java.util.Comparator;

/**
 * Clase de prueba manual para verificar el comportamiento de las estructuras de
 * datos y algoritmos del curso implementados de forma personalizada.
 */
public class Test_Estructuras {

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
}
