package biblioteca.estructuras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Estructura de datos de Lista Enlazada Simple genérica y dinámica. Permite
 * almacenar una colección secuencial de elementos. Es la base para realizar
 * ordenamientos y búsquedas en memoria de catálogos y registros.
 *
 * @param <T> Tipo de elementos almacenados en la Lista.
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> cabeza;
    private int tamanio;

    /**
     * Constructor para inicializar una Lista Enlazada vacía.
     */
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    /**
     * Agrega un nuevo elemento al final de la Lista Enlazada.
     *
     * @param elemento El elemento a agregar.
     */
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> aux = cabeza;
            while (aux.getSiguiente() != null) {
                aux = aux.getSiguiente();
            }
            aux.setSiguiente(nuevoNodo);
        }
        tamanio++;
    }

    /**
     * Obtiene el elemento ubicado en un índice específico.
     *
     * @param indice Posición del elemento (0-indexed).
     * @return El elemento en la posición especificada.
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango
     * válido (0 a tamaño - 1).
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: "
                    + indice);
        }
        Nodo<T> aux = cabeza;
        for (int i = 0; i < indice; i++) {
            aux = aux.getSiguiente();
        }
        return aux.getValor();
    }

    /**
     * Elimina el elemento en un índice específico.
     *
     * @param indice Posición del elemento a remover (0-indexed).
     * @return El elemento que fue eliminado.
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango
     * válido.
     */
    public T remover(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: "
                    + indice);
        }
        T valorEliminado;
        if (indice == 0) {
            valorEliminado = cabeza.getValor();
            cabeza = cabeza.getSiguiente();
        } else {
            Nodo<T> aux = cabeza;
            for (int i = 0; i < indice - 1; i++) {
                aux = aux.getSiguiente();
            }
            Nodo<T> nodoEliminar = aux.getSiguiente();
            valorEliminado = nodoEliminar.getValor();
            aux.setSiguiente(nodoEliminar.getSiguiente());
        }
        tamanio--;
        return valorEliminado;
    }

    /**
     * Remueve y retorna el último elemento de la lista.
     *
     * @return Elemento removido al final de la lista.
     * @throws NoSuchElementException Si la lista está vacía.
     */
    public T removerUltimo() {
        if (isEmpty()) {
            throw new NoSuchElementException("La lista está vacía.");
        }
        return remover(tamanio - 1);
    }

    /**
     * Verifica si la lista contiene un elemento usando igualdad estándar.
     *
     * @param elemento Elemento buscado.
     * @return true si el elemento existe en la lista.
     */
    public boolean contiene(T elemento) {
        Nodo<T> aux = cabeza;
        while (aux != null) {
            if (Objects.equals(aux.getValor(), elemento)) {
                return true;
            }
            aux = aux.getSiguiente();
        }
        return false;
    }

    /**
     * Crea una nueva ListaEnlazada con los elementos que cumplen el predicado.
     *
     * @param predicado Regla de filtrado.
     * @return Lista filtrada sin modificar la lista original.
     */
    public ListaEnlazada<T> filtrar(Predicate<T> predicado) {
        ListaEnlazada<T> filtrada = new ListaEnlazada<>();
        if (predicado == null) {
            return filtrada;
        }
        Nodo<T> aux = cabeza;
        while (aux != null) {
            T valor = aux.getValor();
            if (predicado.test(valor)) {
                filtrada.agregar(valor);
            }
            aux = aux.getSiguiente();
        }
        return filtrada;
    }

    /**
     * Comprueba si la Lista Enlazada no contiene elementos.
     *
     * @return true si la lista está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return cabeza == null;
    }

    /**
     * Retorna el tamaño actual de la Lista Enlazada.
     *
     * @return Cantidad de elementos en la lista.
     */
    public int size() {
        return tamanio;
    }

    /**
     * Vacía la Lista Enlazada por completo.
     */
    public void clear() {
        cabeza = null;
        tamanio = 0;
    }

    /**
     * Convierte la Lista Enlazada a un arreglo genérico estándar de Java. Es
     * fundamental para poder aplicar algoritmos de búsqueda binaria que
     * requieren acceso indexado.
     *
     * @param array Arreglo donde se almacenarán los datos o del cual se tomará
     * el tipo.
     * @return Arreglo relleno con los elementos de la lista.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        if (array.length < tamanio) {
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(),
                    tamanio);
        }
        Nodo<T> aux = cabeza;
        int i = 0;
        while (aux != null) {
            array[i++] = aux.getValor();
            aux = aux.getSiguiente();
        }
        if (array.length > tamanio) {
            array[tamanio] = null;
        }
        return array;
    }

    /**
     * Convierte la Lista Enlazada personalizada a una lista estándar
     * java.util.List de Java. Facilita la integración y el llenado de
     * componentes Swing como JTables.
     *
     * @return Instancia de java.util.List conteniendo los mismos elementos.
     */
    public List<T> toList() {
        List<T> lista = new ArrayList<>();
        Nodo<T> aux = cabeza;
        while (aux != null) {
            lista.add(aux.getValor());
            aux = aux.getSiguiente();
        }
        return lista;
    }

    /**
     * Crea una Lista Enlazada a partir de una java.util.List estándar de Java.
     *
     * @param <E> Tipo de dato de la lista.
     * @param list La lista estándar de origen.
     * @return Nueva instancia de ListaEnlazada con los elementos copiados.
     */
    public static <E> ListaEnlazada<E> fromList(List<E> list) {
        ListaEnlazada<E> nuevaLista = new ListaEnlazada<>();
        for (E elemento : list) {
            nuevaLista.agregar(elemento);
        }
        return nuevaLista;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T valor = actual.getValor();
                actual = actual.getSiguiente();
                return valor;
            }
        };
    }
}
