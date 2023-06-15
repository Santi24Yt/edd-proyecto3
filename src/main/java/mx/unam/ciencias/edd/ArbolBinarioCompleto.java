package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        private Iterador() {
            if(raiz != null) {
                cola = new Cola<Vertice>();
                cola.mete(raiz);
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return cola != null && !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            if(cola == null)
                throw new NoSuchElementException("No hay elemento siguiente");
            Vertice actual = cola.saca();
            if(actual.izquierdo != null)
                cola.mete(actual.izquierdo);
            if(actual.derecho != null)
                cola.mete(actual.derecho);
            return actual.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if(elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser null");
        Vertice nuevo = new Vertice(elemento);
        if(raiz == null) {
            raiz = nuevo;
            elementos++;
            return;
        }
        int h = (altura())+1-1;
        int n = (elementos)+1-1;
        Vertice actual = raiz;
        //System.out.println("Intentando añadir elemento: "+elemento+" Altura: "+h+" Elementos: "+n+" d1: "+(((1 << h) - 1) + (1 << (h-1))));
        while(h >= 0) {
            int d = ((1 << h) - 1) + (1 << (h-1));
            if(actual.izquierdo == null) {
                nuevo.padre = actual;
                actual.izquierdo = nuevo;
                elementos++;
                break;
            }
            if(actual.derecho == null) {
                nuevo.padre = actual;
                actual.derecho = nuevo;
                elementos++;
                break;
            }
            //System.out.println("d: "+d+" n: "+n+" :: "+n+" < "+d);

            if(n == (1 << h+1)-1) {
                actual = actual.izquierdo;
            }else{
                if(n < d) {
                    //System.out.println("-izquierdo");
                    actual = actual.izquierdo;
                    n -= 1 << h-1;
                    h--;
                }else{
                    //System.out.println("-derecho");
                    actual = actual.derecho;
                    n -= 1 << h;
                    h--;
                }
            }
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = vertice(busca(elemento));
        Vertice u = ultimo();
        if(v == null || u == null)
            return;
        if(elementos == 1)
        {
            elementos--;
            raiz = null;
            return;
        }
        v.elemento = u.elemento;
        if(u.padre.derecho == u) {
            u.padre.derecho = null;
        }else{
            u.padre.izquierdo = null;
        }
        //u.padre = null;
        elementos--;
    }

    /**
     * Devuelve el último vértice por bfs
     * @return el último vértice por bfs
     */
    private Vertice ultimo() {
        if(raiz == null)
            return null;
        Cola<Vertice> c = new Cola<Vertice>();
        c.mete(raiz);
        Vertice v = null;
        while(!c.esVacia()) {
            v = c.saca();
            if(v.izquierdo != null)
                c.mete(v.izquierdo);
            if(v.derecho != null)
                c.mete(v.derecho);
        }
        return v;
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        if(raiz == null)
            return -1;
        return log2floor(elementos);
    }

    /**
     * Devuelve el piso de logaritmo base 2 de n
     * @param n
     * @return el piso del logaritmo base 2 de n
     */
    private int log2floor(int n) {
        int r = 0;
        while(n > 1) {
            n >>= 1;
            r++;
        }
        return r;
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        if(raiz == null)
            return;
        Cola<Vertice> c = new Cola<Vertice>();
        c.mete(raiz);
        while(!c.esVacia()) {
            Vertice v = c.saca();
            accion.actua(v);
            if(v.izquierdo != null)
                c.mete(v.izquierdo);
            if(v.derecho != null)
                c.mete(v.derecho);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
