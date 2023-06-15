package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            this.color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        @Override public String toString() {
            if(color == Color.ROJO) {
                return "R{"+elemento.toString()+"}";
            }else if(color == Color.NEGRO){
                return "N{"+elemento.toString()+"}";
            }else{
                return elemento.toString();
            }
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            if(this.color != vertice.color)
                return false;
            return super.equals(objeto);
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        VerticeRojinegro v = (VerticeRojinegro)vertice;
        return v == null ? Color.NEGRO : v.color;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro v = (VerticeRojinegro)ultimoAgregado;
        v.color = Color.ROJO;
        rebalancearAgrega(v);
    }

    private void rebalancearAgrega(VerticeRojinegro v) {
        if(padre(v) == null) {
            v.color = Color.NEGRO;
            return;
        }
        if(getColor(padre(v)) == Color.NEGRO) {
            return;
        }
        VerticeRojinegro a = abuelo(v);
        if(getColor(tio(v)) == Color.ROJO && getColor(padre(v)) == Color.ROJO) {
            tio(v).color = Color.NEGRO;
            padre(v).color = Color.NEGRO;
            a.color = Color.ROJO;
            rebalancearAgrega(a);
            return;
        }
        VerticeRojinegro p = padre(v);
        if((esIzquierdo(p) && esDerecho(v)) || (esDerecho(p) && esIzquierdo(v))) {
            if(esIzquierdo(p)) {
                super.giraIzquierda(p);
            }else{
                super.giraDerecha(p);
            }
            VerticeRojinegro temp = p;
            p = v;
            v = temp;
        }
        p.color = Color.NEGRO;
        a.color = Color.ROJO;
        if(esIzquierdo(v)) {
            super.giraDerecha(a);
        }else{
            super.giraIzquierda(a);
        }
    }

    private VerticeRojinegro padre(VerticeRojinegro v) {
        if(v == null || v.padre == null)
            return null;
        return (VerticeRojinegro)v.padre;
    }

    private VerticeRojinegro abuelo(VerticeRojinegro v) {
        VerticeRojinegro p = padre(v);
        if(p == null || p.padre == null)
            return null;
        return (VerticeRojinegro)p.padre;
    }

    private VerticeRojinegro tio(VerticeRojinegro v) {
        VerticeRojinegro p = padre(v);
        VerticeRojinegro a = abuelo(v);
        if(p == null || a == null)
            return null;
        if(esIzquierdo(p)) {
            return (VerticeRojinegro)a.derecho;
        }else{
            return (VerticeRojinegro)a.izquierdo;
        }
    }

    private boolean esIzquierdo(Vertice v) {
        if(v == null || v.padre == null)
            return false;
        return v.padre.izquierdo == v;
    }

    private boolean esDerecho(Vertice v) {
        if(v == null || v.padre == null)
            return false;
        return v.padre.derecho == v;
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro eliminar = (VerticeRojinegro)busca(elemento);
        if(eliminar == null)
            return;
        if(eliminar.derecho != null && eliminar.izquierdo != null) {
            eliminar = (VerticeRojinegro)intercambiaEliminable(eliminar);
        }
        VerticeRojinegro f = null;
        if(eliminar.derecho == null && eliminar.izquierdo == null) {
            f = (VerticeRojinegro)nuevoVertice(null);
            f.padre = eliminar;
            eliminar.izquierdo = f;
            f.color = Color.NEGRO;
            elementos++;
        }
        VerticeRojinegro h = (VerticeRojinegro)(eliminar.derecho == null ? eliminar.izquierdo : eliminar.derecho);
        eliminaVertice(eliminar);
        if(getColor(h) == Color.ROJO && getColor(eliminar) == Color.NEGRO) {
            h.color = Color.NEGRO;
        }else if(getColor(h) == Color.NEGRO && getColor(eliminar) == Color.NEGRO) {
            rebalancearElimina(h);
        }
        if(f != null) {
            eliminaVertice(f);
        }
    }

    private void rebalancearElimina(VerticeRojinegro v) {
        VerticeRojinegro p = padre(v);
        if(p == null)
            return;
        VerticeRojinegro h = hermano(v);
        if(getColor(h) == Color.ROJO && getColor(p) == Color.NEGRO) {
            h.color = Color.NEGRO;
            p.color = Color.ROJO;
            if(esIzquierdo(v)) {
                super.giraIzquierda(p);
            }else{
                super.giraDerecha(p);
            }
            p = padre(v);
            h = hermano(v);
        }
        if(getColor(p) == Color.NEGRO && getColor(h) == Color.NEGRO
            && (getColor(h.derecho) == Color.NEGRO)
            && (getColor(h.izquierdo) == Color.NEGRO)) {
                h.color = Color.ROJO;
                rebalancearElimina(p);
                return;
            }
        if(getColor(p) == Color.ROJO && getColor(h) == Color.NEGRO
            && (getColor(h.derecho) == Color.NEGRO)
            && (getColor(h.izquierdo) == Color.NEGRO)) {
                h.color = Color.ROJO;
                p.color = Color.NEGRO;
                return;
            }
        if((esIzquierdo(v) && (getColor(h.izquierdo) == Color.ROJO)
                && (getColor(h.derecho) == Color.NEGRO))
            || (esDerecho(v) && (getColor(h.izquierdo) == Color.NEGRO)
            && (getColor(h.derecho) == Color.ROJO))) {
                h.color = Color.ROJO;
                if(esIzquierdo(v)) {
                    ((VerticeRojinegro)h.izquierdo).color = Color.NEGRO;
                    super.giraDerecha(h);
                }else{
                    ((VerticeRojinegro)h.derecho).color = Color.NEGRO;
                    super.giraIzquierda(h);
                }
                h = hermano(v);
        }
        h.color = p.color;
        p.color = Color.NEGRO;
        if(esIzquierdo(v)) {
            ((VerticeRojinegro)h.derecho).color = Color.NEGRO;
            super.giraIzquierda(p);
        }else{
            ((VerticeRojinegro)h.izquierdo).color = Color.NEGRO;
            super.giraDerecha(p);
        }
    }

    private VerticeRojinegro hermano(VerticeRojinegro v) {
        if(v == null || v.padre == null)
            return null;
        return (VerticeRojinegro)(esIzquierdo(v) ? v.padre.derecho : v.padre.izquierdo);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
