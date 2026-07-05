package biblioteca.estructuras;

/**
 * Representa un nodo genérico para estructuras de datos lineales (Pila, Cola,
 * Lista Enlazada).
 *
 * @param <T> Tipo de dato almacenado en el nodo.
 */
public class Nodo<T> {

    private T valor;
    private Nodo<T> siguiente;

    /**
     * Constructor por defecto del nodo.
     */
    public Nodo() {
        this.valor = null;
        this.siguiente = null;
    }

    /**
     * Constructor con valor inicial.
     *
     * @param valor El dato a almacenar en el nodo.
     */
    public Nodo(T valor) {
        this.valor = valor;
        this.siguiente = null;
    }

    /**
     * Constructor con valor inicial y referencia al siguiente nodo.
     *
     * @param valor El dato a almacenar en el nodo.
     * @param siguiente El siguiente nodo en la secuencia.
     */
    public Nodo(T valor, Nodo<T> siguiente) {
        this.valor = valor;
        this.siguiente = siguiente;
    }

    /**
     * Obtiene el valor almacenado en el nodo.
     *
     * @return El valor del nodo.
     */
    public T getValor() {
        return valor;
    }

    /**
     * Establece el valor almacenado en el nodo.
     *
     * @param valor El nuevo valor a almacenar.
     */
    public void setValor(T valor) {
        this.valor = valor;
    }

    /**
     * Obtiene la referencia al siguiente nodo.
     *
     * @return El nodo siguiente.
     */
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece la referencia al siguiente nodo.
     *
     * @param siguiente El nuevo nodo siguiente.
     */
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
