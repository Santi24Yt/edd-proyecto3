package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if(indice >= elementos)
                throw new NoSuchElementException("No hay siguiente elemento");
            T e = arbol[indice];
            indice++;
            return e;
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento;
            this.indice = -1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            return this.elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(128);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        arbol = nuevoArreglo(n);
        elementos = n;
        int i = 0;
        for(T e : iterable) {
            e.setIndice(i);
            arbol[i] = e;
            i++;
        }

        for(i = n/2-1; i >= 0; i--) {
            acomodaAbajo(i);
        }
    }

    private void acomodaAbajo(int i) {
        while((izquierdo(i) < elementos && arbol[i].compareTo(arbol[izquierdo(i)]) > 0) ||
                (derecho(i) < elementos && arbol[i].compareTo(arbol[derecho(i)]) > 0)) {
                    if(derecho(i) >= elementos || arbol[izquierdo(i)].compareTo(arbol[derecho(i)]) <= 0) {
                        swap(i, izquierdo(i));
                        i = izquierdo(i);
                    }else{
                        swap(i, derecho(i));
                        i = derecho(i);
                    }
                }
    }

    private int izquierdo(int i) {
        return 2*i + 1;
    }

    private int derecho(int i) {
        return 2*i + 2;
    }

    private void swap(int a, int b) {
        T t = arbol[a];
        arbol[a] = arbol[b];
        arbol[b] = t;
        arbol[a].setIndice(a);
        arbol[b].setIndice(b);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
        if(elementos == arbol.length) {
            T[] t = arbol;
            arbol = nuevoArreglo(t.length*2);
            for(int i = 0; i < t.length; i++)
                arbol[i] = t[i];
        }
        arbol[elementos] = elemento;
        elemento.setIndice(elementos);
        elementos++;
        acomodaArriba(elementos-1);
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {
        if(elementos == 0)
            throw new IllegalStateException("El montículo es vacío");
        T e = arbol[0];
        swap(0, elementos-1);
        arbol[elementos-1].setIndice(-1);
        elementos--;
        acomodaAbajo(0);
        return e;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
        int i = elemento.getIndice();
        if(i < 0 || i >= elementos)
            return;
        swap(i, elementos-1);
        arbol[elementos-1].setIndice(-1);
        elementos--;
        acomodaAbajo(i);
        acomodaArriba(i);
    }

    private void acomodaArriba(int i) {
        boolean cmpp;
        int v = i;
        do {
            int padre = padre(v);
            cmpp = padre >= 0 && arbol[padre].compareTo(arbol[v]) > 0;
            if(cmpp) {
                swap(v, padre);
                v = padre;
            }
        }while(cmpp);
    }

    private int padre(int i) {
        return (i-1)/2;
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        int i = elemento.getIndice();
        if(i < 0 || i >= elementos)
            return false;
        return arbol[i].equals(elemento);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
        arbol = nuevoArreglo(arbol.length);
        elementos = 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
        int i = elemento.getIndice();
        acomodaAbajo(i);
        acomodaArriba(i);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
        if(i < 0 || i >= elementos)
            throw new NoSuchElementException("El indice es mayor al número de elementos o menor a 0");
        return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
        String s = "";
        for(int i = 0; i < elementos; i++) {
            s += arbol[i].toString()+", ";
        }
        return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        if(this.elementos != monticulo.elementos)
            return false;
        for(int i = 0; i < elementos; i++) {
            if(!this.arbol[i].equals(monticulo.arbol[i]))
                return false;
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
        Lista<Adaptador<T>> l1 = new Lista<Adaptador<T>>();
        for(T e : coleccion) {
            l1.agrega(new Adaptador<T>(e));
        }
        Lista<T> l2 = new Lista<T>();
        MonticuloMinimo<Adaptador<T>> mm = new MonticuloMinimo<Adaptador<T>>(l1);
        while(!mm.esVacia()) {
            l2.agrega(mm.elimina().elemento);
        }
        return l2;
    }
}
