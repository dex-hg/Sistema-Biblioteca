package biblioteca.estructuras;

import java.util.EmptyStackException;

/**
 * Estructura de datos dinámica LIFO (Last In, First Out) personalizada.
 * Representa una Pila que gestiona elementos de tipo genérico. Ideal para
 * control de navegación, historiales y acciones de deshacer/rehacer.
 *
 * @param <T> Tipo de elementos almacenados en la Pila.
 */
public class Pila<T> {

    private Nodo<T> tope;
    private int tamanio;

    /**
     * Constructor para inicializar una Pila vacía.
     */
    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    /**
     * Inserta (apila) un elemento en la parte superior de la Pila.
     *
     * @param elemento El elemento a insertar.
     */
    public void push(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.setSiguiente(tope);
        tope = nuevoNodo;
        tamanio++;
    }

    /**
     * Remueve y retorna el elemento en la parte superior de la Pila.
     *
     * @return El elemento desapilado.
     * @throws EmptyStackException Si la Pila está vacía.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T valor = tope.getValor();
        tope = tope.getSiguiente();
        tamanio--;
        return valor;
    }

    /**
     * Retorna el elemento en la parte superior de la Pila sin removerlo.
     *
     * @return El elemento superior.
     * @throws EmptyStackException Si la Pila está vacía.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return tope.getValor();
    }

    /**
     * Comprueba si la Pila no contiene elementos. Complejidad: O(1)
     *
     * @return true si la pila está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return tope == null;
    }

    /**
     * Retorna la cantidad de elementos en la Pila. Complejidad: O(1)
     *
     * @return Cantidad de elementos.
     */
    public int size() {
        return tamanio;
    }

    /**
     * Limpia la Pila removiendo todas las referencias. Complejidad: O(1)
     */
    public void clear() {
        this.tope = null;
        this.tamanio = 0;
    }
}
