package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        Nodo actual = cabeza;
        String s = "";
        while(actual != null) {
            s += actual.elemento.toString() + "\n";
            actual = actual.siguiente;
        }
        return s;
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        
        Nodo n = new Nodo(elemento);
        n.siguiente = cabeza;
        cabeza = n;

        if(rabo == null) {
            rabo = cabeza;
        }
    }
}
