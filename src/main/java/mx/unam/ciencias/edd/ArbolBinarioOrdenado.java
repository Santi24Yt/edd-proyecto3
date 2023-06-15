package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            pila = new Pila<Vertice>();
            Vertice actual = raiz;
            while(actual != null) {
                pila.mete(actual);
                actual = actual.izquierdo;
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            Vertice v = pila.saca();
            if(v.derecho != null) {
                pila.mete(v.derecho);
                Vertice actual = v.derecho;
                while(actual.izquierdo != null) {
                    pila.mete(actual.izquierdo);
                    actual = actual.izquierdo;
                }
            }
            return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if(elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        Vertice n = nuevoVertice(elemento);
        if(raiz == null) {
            raiz = n;
        }else{
            agrega(raiz, n);
        }
        ultimoAgregado = n;
        elementos++;
    }

    /**
     * Agregar un elemento de manera recursiva
     * @param actual
     * @param nuevo
     */
    private void agrega(Vertice actual, Vertice nuevo) {
        if(nuevo.elemento.compareTo(actual.elemento) <= 0) {
            if(actual.izquierdo == null) {
                actual.izquierdo = nuevo;
                nuevo.padre = actual;
            }else{
                agrega(actual.izquierdo, nuevo);
            }
        }else{
            if(actual.derecho == null) {
                actual.derecho = nuevo;
                nuevo.padre = actual;
            }else{
                agrega(actual.derecho, nuevo);
            }
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = vertice(busca(elemento));
        if(v == null)
            return;
        if(v.izquierdo != null && v.derecho != null) {
            Vertice e = intercambiaEliminable(v);
            eliminaVertice(e);
            return;
        }
        eliminaVertice(v);
    }

    /**
     * Devuelve el mayor de un subárbol
     * @param v
     * @return
     */
    private Vertice maxEnSubArb(Vertice v) {
        if(v.derecho == null)
            return v;
        return maxEnSubArb(v.derecho);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice max = maxEnSubArb(vertice.izquierdo);
        T t = max.elemento;
        max.elemento = vertice.elemento;
        vertice.elemento = t;
        return max;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        elementos--;
        Vertice t = null;
        if(vertice.izquierdo != null) {
            t = vertice.izquierdo;
        }else if(vertice.derecho != null) {
            t = vertice.derecho;
        }

        if(vertice.padre == null) {
            raiz = t;
            if(t != null)
                t.padre = null;
        }else{
            if(t != null)
                t.padre = vertice.padre;
            
            if(vertice.padre.izquierdo == vertice) {
                vertice.padre.izquierdo = t;
            }else{
                vertice.padre.derecho = t;
            }
        }
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz, elemento);
    }

    private Vertice busca(Vertice actual, T elemento) {
        if(actual == null)
            return null;
        if(actual.elemento.equals(elemento))
            return actual;
        if(actual.elemento.compareTo(elemento) > 0) {
            return busca(actual.izquierdo, elemento);
        }else{
            return busca(actual.derecho, elemento);
        }
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice q = vertice(vertice);
        if(q.izquierdo == null)
            return;
        Vertice p = q.izquierdo;
        Vertice s = p.derecho;
        if(q.padre != null) {
            p.padre = q.padre;
            if(q.padre.izquierdo == q) {
                q.padre.izquierdo = p;
            }else{
                q.padre.derecho = p;
            }
        }else{
            raiz = p;
            p.padre = null; //modificado
        }
        p.derecho = q;
        q.padre = p;
        q.izquierdo = s;
        if(s != null)
            s.padre = q;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice p = vertice(vertice);
        if(p.derecho == null)
            return;
        Vertice q = p.derecho;
        Vertice s = q.izquierdo;
        if(p.padre != null) {
            q.padre = p.padre;
            if(p.padre.izquierdo == p) {
                p.padre.izquierdo = q;
            }else{
                p.padre.derecho = q;
            }
        }else{
            raiz = q;
            q.padre = null; //modificado
        }
        q.izquierdo = p;
        p.padre = q;
        p.derecho = s;
        if(s != null)
            s.padre = p;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(raiz, accion);
    }
    
    /**
     * Dfs pre order recursivo
     * @param v un vertice
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void dfsPreOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if(v == null)
            return;
        accion.actua(v);
        dfsPreOrder(v.izquierdo, accion);
        dfsPreOrder(v.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(raiz, accion);
    }

    /**
     * Dfs in order recursivo
     * @param v un vértice
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void dfsInOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if(v == null)
            return;
        dfsInOrder(v.izquierdo, accion);
        accion.actua(v);
        dfsInOrder(v.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(raiz, accion);
    }

    /**
     * Dfs post order recursivo
     * @param v un vértice
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void dfsPostOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if(v == null)
            return;
        dfsPostOrder(v.izquierdo, accion);
        dfsPostOrder(v.derecho, accion);
        accion.actua(v);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
