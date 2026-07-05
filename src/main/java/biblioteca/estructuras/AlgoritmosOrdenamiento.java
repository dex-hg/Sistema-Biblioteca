package biblioteca.estructuras;

import java.util.Comparator;

/**
 * Clase utilitaria que provee métodos de ordenamiento dinámicos y
 * personalizados. Aplica recursividad mediante el algoritmo de QuickSort
 * (Divide y Vencerás). Diseñado para ordenar colecciones representadas en
 * arreglos.
 */
public class AlgoritmosOrdenamiento {

    private AlgoritmosOrdenamiento() {
        // Constructor privado para evitar instanciación (clase utilitaria)
    }

    /**
     * Ordena un arreglo de elementos que implementan la interfaz Comparable.
     * Complejidad: O(N log N) promedio, O(N^2) en el peor caso.
     *
     * @param <T> Tipo del objeto que debe ser Comparable.
     * @param arreglo El arreglo de elementos a ordenar.
     */
    public static <T extends Comparable<T>> void ordenarQuickSort(T[] arreglo) {
        if (arreglo == null || arreglo.length <= 1) {
            return;
        }
        quickSort(arreglo, 0, arreglo.length - 1);
    }

    /**
     * Ordena un arreglo de elementos utilizando un Comparador personalizado.
     * Complejidad: O(N log N) promedio, O(N^2) en el peor caso.
     *
     * @param <T> Tipo del objeto a ordenar.
     * @param arreglo El arreglo de elementos.
     * @param comparador Criterio de comparación personalizado.
     */
    public static <T> void ordenarQuickSort(
            T[] arreglo,
            Comparator<T> comparador) {
        if (arreglo == null || arreglo.length <= 1 || comparador == null) {
            return;
        }
        quickSort(arreglo, 0, arreglo.length - 1, comparador);
    }

    /**
     * Método recursivo interno de QuickSort para elementos Comparable.
     *
     * @param arreglo Arreglo a ordenar.
     * @param bajo Índice inferior.
     * @param alto Índice superior.
     */
    private static <T extends Comparable<T>> void quickSort(
            T[] arreglo,
            int bajo,
            int alto) {
        if (bajo < alto) {
            int indiceParticion = particion(arreglo, bajo, alto);
            // Llamadas recursivas para ordenar sub-arreglos izquierdo y derecho
            quickSort(arreglo, bajo, indiceParticion - 1);
            quickSort(arreglo, indiceParticion + 1, alto);
        }
    }

    /**
     * Realiza la partición del arreglo para QuickSort (Comparable). Toma el
     * último elemento como pivote, lo coloca en su posición correcta y
     * posiciona los menores a la izquierda y los mayores a la derecha.
     *
     * @param arreglo Arreglo a particionar.
     * @param bajo Índice inferior.
     * @param alto Índice superior.
     * @return Índice del pivote después de la partición.
     */
    private static <T extends Comparable<T>> int particion(
            T[] arreglo,
            int bajo,
            int alto) {
        T pivote = arreglo[alto];
        int i = bajo - 1;

        for (int j = bajo; j < alto; j++) {
            // Comparación usando compareTo de la interfaz Comparable
            if (arreglo[j].compareTo(pivote) <= 0) {
                i++;
                intercambiar(arreglo, i, j);
            }
        }
        intercambiar(arreglo, i + 1, alto);
        return i + 1;
    }

    /**
     * Método recursivo interno de QuickSort utilizando Comparator.
     *
     * @param arreglo Arreglo a ordenar.
     * @param bajo Índice inferior.
     * @param alto Índice superior.
     * @param comparador Comparador personalizado.
     */
    private static <T> void quickSort(
            T[] arreglo,
            int bajo,
            int alto,
            Comparator<T> comparador) {
        if (bajo < alto) {
            int indiceParticion = particion(arreglo, bajo, alto, comparador);
            // Ordenar recursivamente las sub-partes
            quickSort(arreglo, bajo, indiceParticion - 1, comparador);
            quickSort(arreglo, indiceParticion + 1, alto, comparador);
        }
    }

    /**
     * Realiza la partición del arreglo para QuickSort (Comparator).
     *
     * @param arreglo Arreglo a particionar.
     * @param bajo Índice inferior.
     * @param alto Índice superior.
     * @param comparador Comparador personalizado.
     * @return Índice del pivote después de la partición.
     */
    private static <T> int particion(
            T[] arreglo,
            int bajo,
            int alto,
            Comparator<T> comparador) {
        T pivote = arreglo[alto];
        int i = bajo - 1;

        for (int j = bajo; j < alto; j++) {
            // Comparación usando la regla del comparador
            if (comparador.compare(arreglo[j], pivote) <= 0) {
                i++;
                intercambiar(arreglo, i, j);
            }
        }
        intercambiar(arreglo, i + 1, alto);
        return i + 1;
    }

    /**
     * Intercambia dos elementos dentro de un arreglo.
     *
     * @param arreglo El arreglo.
     * @param i Índice del primer elemento.
     * @param j Índice del segundo elemento.
     */
    private static <T> void intercambiar(T[] arreglo, int i, int j) {
        T temp = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = temp;
    }
}
