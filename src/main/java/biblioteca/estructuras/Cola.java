package biblioteca.estructuras;

import java.util.NoSuchElementException;

/**
 * Estructura de datos dinámica FIFO (First In, First Out) personalizada.
 * Representa una Cola que gestiona elementos de tipo genérico. Útil para
 * gestionar la fila de espera de reservas de libros, solicitudes, etc.
 *
 * @param <T> Tipo de elementos almacenados en la Cola.
 */
public class Cola<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    /**
     * Constructor para inicializar una Cola vacía.
     */
    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }

    /**
     * Inserta (encola) un elemento al final de la Cola.
     *
     * @param elemento El elemento a insertar.
     */
    public void enqueue(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (isEmpty()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
        tamanio++;
    }

    /**
     * Remueve y retorna el elemento al inicio de la Cola.
     *
     * @return El elemento removido.
     * @throws NoSuchElementException Si la Cola está vacía.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía.");
        }
        T valor = frente.getValor();
        frente = frente.getSiguiente();
        tamanio--;
        if (frente == null) {
            fin = null; // Si se vacía la cola, el fin también es nulo
        }
        return valor;
    }

    /**
     * Retorna el elemento al inicio de la Cola sin removerlo.
     *
     * @return El primer elemento.
     * @throws NoSuchElementException Si la Cola está vacía.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("La cola está vacía.");
        }
        return frente.getValor();
    }

    /**
     * Comprueba si la Cola está vacía.
     *
     * @return true si está vacía, false en caso contrario.
     */
    public boolean isEmpty() {
        return frente == null;
    }

    /**
     * Retorna el tamaño actual de la Cola.
     *
     * @return Cantidad de elementos en la cola.
     */
    public int size() {
        return tamanio;
    }

    /**
     * Vacía completamente la Cola.
     */
    public void clear() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }
}
