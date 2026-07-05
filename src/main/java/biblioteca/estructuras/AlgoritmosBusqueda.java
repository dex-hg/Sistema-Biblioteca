package biblioteca.estructuras;

import java.util.Comparator;

/**
 * Clase utilitaria que provee métodos de búsqueda en arreglos de datos.
 * Implementa la Búsqueda Binaria recursiva para arreglos ordenados y la
 * Búsqueda Lineal para colecciones de datos no ordenadas.
 */
public class AlgoritmosBusqueda {

    private AlgoritmosBusqueda() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Realiza una búsqueda binaria recursiva en un arreglo ordenado de
     * elementos Comparable. Complejidad: O(log N)
     *
     * @param <T> Tipo del objeto (debe implementar Comparable).
     * @param arreglo Arreglo ordenado donde buscar.
     * @param objetivo Elemento buscado.
     * @return El índice del elemento si se encuentra, o -1 si no está presente.
     */
    public static <T extends Comparable<T>> int busquedaBinaria(
            T[] arreglo,
            T objetivo) {
        if (arreglo == null || arreglo.length == 0 || objetivo == null) {
            return -1;
        }
        return busquedaBinariaRecursiva(
                arreglo,
                objetivo,
                0,
                arreglo.length - 1);
    }

    /**
     * Realiza una búsqueda binaria recursiva en un arreglo ordenado usando un
     * Comparador. Complejidad: O(log N)
     *
     * @param <T> Tipo del objeto.
     * @param arreglo Arreglo ordenado donde buscar.
     * @param objetivo Elemento buscado.
     * @param comparador Comparador para establecer el criterio de igualdad y
     * orden.
     * @return El índice del elemento si se encuentra, o -1 si no está presente.
     */
    public static <T> int busquedaBinaria(
            T[] arreglo,
            T objetivo,
            Comparator<T> comparador) {
        if (arreglo == null
                || arreglo.length == 0
                || objetivo == null
                || comparador == null) {
            return -1;
        }
        return busquedaBinariaRecursiva(
                arreglo,
                objetivo,
                0,
                arreglo.length - 1,
                comparador);
    }

    /**
     * Método interno recursivo para Búsqueda Binaria (Comparable).
     *
     * @param arreglo Arreglo ordenado.
     * @param objetivo Elemento a buscar.
     * @param izquierda Índice inferior de búsqueda.
     * @param derecha Índice superior de búsqueda.
     * @return Índice del elemento o -1.
     */
    private static <T extends Comparable<T>> int busquedaBinariaRecursiva(
            T[] arreglo,
            T objetivo,
            int izquierda,
            int derecha) {
        if (izquierda > derecha) {
            return -1; // Rango de búsqueda inválido, elemento no encontrado
        }

        int medio = izquierda + (derecha - izquierda) / 2;
        int comparacion = objetivo.compareTo(arreglo[medio]);

        if (comparacion == 0) {
            return medio; // Elemento encontrado
        } else if (comparacion < 0) {
            // Buscar en la mitad izquierda
            return busquedaBinariaRecursiva(
                    arreglo,
                    objetivo,
                    izquierda,
                    medio - 1);
        } else {
            // Buscar en la mitad derecha
            return busquedaBinariaRecursiva(
                    arreglo,
                    objetivo,
                    medio + 1,
                    derecha);
        }
    }

    /**
     * Método interno recursivo para Búsqueda Binaria (Comparator).
     *
     * @param arreglo Arreglo ordenado.
     * @param objetivo Elemento a buscar.
     * @param izquierda Índice inferior de búsqueda.
     * @param derecha Índice superior de búsqueda.
     * @param comparador Comparador personalizado.
     * @return Índice del elemento o -1.
     */
    private static <T> int busquedaBinariaRecursiva(
            T[] arreglo,
            T objetivo,
            int izquierda,
            int derecha,
            Comparator<T> comparador) {
        if (izquierda > derecha) {
            return -1;
        }

        int medio = izquierda + (derecha - izquierda) / 2;
        int comparacion = comparador.compare(objetivo, arreglo[medio]);

        if (comparacion == 0) {
            return medio;
        } else if (comparacion < 0) {
            return busquedaBinariaRecursiva(
                    arreglo,
                    objetivo,
                    izquierda,
                    medio - 1,
                    comparador);
        } else {
            return busquedaBinariaRecursiva(
                    arreglo,
                    objetivo,
                    medio + 1,
                    derecha,
                    comparador);
        }
    }

    /**
     * Realiza una búsqueda lineal en un arreglo. Útil cuando los datos no están
     * ordenados o se busca por un criterio secundario. Complejidad: O(N)
     *
     * @param <T> Tipo del objeto.
     * @param arreglo Arreglo donde buscar.
     * @param objetivo Elemento buscado.
     * @return El índice del elemento si se encuentra, o -1 si no está presente.
     */
    public static <T> int busquedaLineal(T[] arreglo, T objetivo) {
        if (arreglo == null || objetivo == null) {
            return -1;
        }
        for (int i = 0; i < arreglo.length; i++) {
            if (objetivo.equals(arreglo[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Realiza una búsqueda lineal utilizando un comparador de igualdad
     * personalizado. Complejidad: O(N)
     *
     * @param <T> Tipo del objeto.
     * @param arreglo Arreglo donde buscar.
     * @param objetivo Elemento buscado.
     * @param comparador Comparador que determina si dos elementos coinciden
     * (debe retornar 0 si coinciden).
     * @return El índice del elemento si se encuentra, o -1 si no está presente.
     */
    public static <T> int busquedaLineal(
            T[] arreglo,
            T objetivo,
            Comparator<T> comparador) {
        if (arreglo == null || objetivo == null || comparador == null) {
            return -1;
        }
        for (int i = 0; i < arreglo.length; i++) {
            if (comparador.compare(arreglo[i], objetivo) == 0) {
                return i;
            }
        }
        return -1;
    }
}
