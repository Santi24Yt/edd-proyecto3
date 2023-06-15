package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
        Nodo actual = cabeza;
        String s = "";
        if(actual == null)
            return s;
        while(actual.siguiente != null) {
            s += actual.elemento.toString() + ",";
            actual = actual.siguiente;
        }
        s += actual.elemento.toString() + ",";
        return s;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        
        Nodo n = new Nodo(elemento);
        if(rabo == null) {
            rabo = cabeza = n;
        }else{
            rabo.siguiente = n;
            rabo = n;
        }
    }
}
