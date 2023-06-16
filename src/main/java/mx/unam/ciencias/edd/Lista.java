package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        private T elemento;
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nodo con un elemento. */
        private Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        private Nodo anterior;
        /* El nodo siguiente. */
        private Nodo siguiente;

        /* Construye un nuevo iterador. */
        private Iterador() {
            anterior = null;
            siguiente = cabeza;
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            Nodo actual = siguiente;
            if (actual == null)
                throw new NoSuchElementException("No hay siguiente elemento");
            siguiente = siguiente.siguiente;
            anterior = actual;
            return actual.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            Nodo actual = anterior;
            if (actual == null)
                throw new NoSuchElementException("No hay elemento previo");
            anterior = anterior.anterior;
            siguiente = actual;
            return actual.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            anterior = null;
            siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            anterior = rabo;
            siguiente = null;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud = 0;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return getLongitud();
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return longitud == 0;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        if (cabeza == null) {
            cabeza = rabo = new Nodo(elemento);
        } else {
            Nodo t = rabo;
            rabo = new Nodo(elemento);
            t.siguiente = rabo;
            rabo.anterior = t;
        }
        longitud++;
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        agrega(elemento);
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        if (cabeza == null) {
            cabeza = rabo = new Nodo(elemento);
        } else {
            Nodo t = cabeza;
            cabeza = new Nodo(elemento);
            cabeza.siguiente = t;
            t.anterior = cabeza;
        }
        longitud++;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no puede ser nulo");
        if (i <= 0) {
            agregaInicio(elemento);
        } else if (i >= longitud) {
            agregaFinal(elemento);
        } else {
            Nodo anterior = cabeza;
            for (int k = 0; k < i-1; k++) {
                anterior = anterior.siguiente;
            }
            Nodo siguiente = anterior.siguiente;
            Nodo nuevo = new Nodo(elemento);
            anterior.siguiente = nuevo;
            nuevo.anterior = anterior;
            siguiente.anterior = nuevo;
            nuevo.siguiente = siguiente;
            longitud++;
        }
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Nodo actual = cabeza;
        if (actual != null) {
            while(actual.siguiente != null) {
                if (actual.elemento.equals(elemento))
                    break;
                actual = actual.siguiente;
            }
            if (actual.anterior == null) {
                cabeza = actual.siguiente;
            } else {
                actual.anterior.siguiente = actual.siguiente;
            }
            if (actual.siguiente == null) {
                rabo = actual.anterior;
            } else {
                actual.siguiente.anterior = actual.anterior;
            }
            longitud--;
        }
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if (esVacia())
            throw new NoSuchElementException("La lista es vacía");
        Nodo primero = cabeza;
        if (cabeza.siguiente != null) {
            cabeza.siguiente.anterior = null;
            cabeza = cabeza.siguiente;
        }
        if (longitud == 1)
            rabo = cabeza = null;
        longitud--;
        return primero.elemento;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if (esVacia())
            throw new NoSuchElementException("La lista es vacía");
        Nodo ultimo = rabo;
        if (rabo.anterior != null) {
            rabo.anterior.siguiente = null;
            rabo = rabo.anterior;
        }
        if (longitud == 1)
            rabo = cabeza = null;
        longitud--;
        return ultimo.elemento;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return indiceDe(elemento) >= 0;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> nuevaLista = new Lista<T>();
        Nodo actual = rabo;
        if (actual != null) {
            while (actual.anterior != null) {
                nuevaLista.agrega(actual.elemento);
                actual = actual.anterior;
            }
            nuevaLista.agrega(actual.elemento);
        }
        return nuevaLista;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Lista<T> nuevaLista = new Lista<T>();
        Nodo actual = cabeza;
        if (actual != null) {
            while (actual.siguiente != null) {
                nuevaLista.agrega(actual.elemento);
                actual = actual.siguiente;
            }
            nuevaLista.agrega(actual.elemento);
        }
        return nuevaLista;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
        while (!esVacia()) {
            eliminaUltimo();
        }
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if (esVacia())
            throw new NoSuchElementException("La lista es vacía");
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if (esVacia())
            throw new NoSuchElementException("La lista es vacía");
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if (i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido("El indice debe ser menor a la longitud de la lista");
        if(i <= longitud/2)
        {
            Nodo actual = cabeza;
            for (int k = 0; k < i; k++) {
                actual = actual.siguiente;
            }
            return actual.elemento;
        }else{
            Nodo actual = rabo;
            for(int k = 0; k < longitud-i-1; k++) {
                actual = actual.anterior;
            }
            return actual.elemento;
        }
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        Nodo actual = cabeza;
        int i = 0;
        if (actual == null)
            return -1;
        while (actual.siguiente != null) {
            if (actual.elemento.equals(elemento))
                break;
            i++;
            actual = actual.siguiente;
        }
        return actual.elemento.equals(elemento) ? i : -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        String s = "[";
        Nodo actual = cabeza;
        for (int i = 0; i < longitud; i++) {
            s += actual.elemento.toString();
            if (i != longitud - 1) {
                s += ", ";
            }
            actual = actual.siguiente;
        }
        s += "]";
        return s;
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
        if (lista.getLongitud() != longitud)
            return false;
        boolean r = true;
        for (int i = 0; i < longitud; i++) {
            if (!get(i).equals(lista.get(i))) {
                r = false;
                break;
            }
        }
        return r;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
        // Escape de la recursión, cualquier lista de 1 elemento (o 0 por vacuidad) está ordenada
        if (this.longitud <= 1)
            return this.copia();
        
        Lista<T> l1 = this.sublista(0, this.longitud/2);
        Lista<T> l2 = this.sublista(this.longitud/2, longitud);

        Lista<T> ordenada = mezcla(l1.mergeSort(comparador), l2.mergeSort(comparador), comparador);

        return ordenada;
    }

    /**
     * Crea una sublista con los elementos [a, b) (no incluye a b)
     * @param a el indice inicial
     * @param b el indice final (no inclusivo)
     * @return
     */
    private Lista<T> sublista(int a, int b) {
        Lista<T> r = new Lista<T>();
        Nodo nodo = nodo(a);
        for(int i = a; i < b; i++) {
            r.agrega(nodo.elemento);
            nodo = nodo.siguiente;
        }
        return r;
    }

    /**
     * Regresa una lista ordenada con todos los elementos de las listas que recibe
     * @param l1 una lista ordenada
     * @param l2 una lista ordenada
     * @return una lista ordenada con todos los elementos de las listas que recibe
     */
    private Lista<T> mezcla(Lista<T> l1, Lista<T> l2, Comparator<T> comparador) {
        Lista<T> res = new Lista<T>();
        Nodo n1 = l1.nodo(0);
        Nodo n2 = l2.nodo(0);
        while(n1 != null && n2 != null) {
            if(comparador.compare(n1.elemento, n2.elemento) <= 0) {
                res.agrega(n1.elemento);
                n1 = n1.siguiente;
            } else {
                res.agrega(n2.elemento);
                n2 = n2.siguiente;
            }
        }
        while(n1 != null) {
            res.agrega(n1.elemento);
            n1 = n1.siguiente;
        }
        while(n2 != null) {
            res.agrega(n2.elemento);
            n2 = n2.siguiente;
        }
        return res;
    }

    /**
     * Devuelve el nodo i (como get pero devuelve el nodo en vez del elemento)
     * @param i el indice del nodo a devolver
     * @return el nodo con indice i
     */
    private Nodo nodo(int i) {
        if (i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido("El indice debe ser menor a la longitud de la lista");
        Nodo actual = cabeza;
        for (int k = 0; k < i; k++) {
            actual = actual.siguiente;
        }
        return actual;
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz {@link
     * Comparable}.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        Nodo actual = cabeza;
        while(actual != null) {
            if(comparador.compare(actual.elemento, elemento) > 0)
                return false;
            if(comparador.compare(actual.elemento, elemento) == 0)
                return true;
            if(actual.siguiente == null)
                break;
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que
     * contener nada más elementos que implementan la interfaz {@link
     * Comparable}, y se da por hecho que está ordenada.
     * @param <T> tipo del que puede ser la lista.
     * @param lista la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
