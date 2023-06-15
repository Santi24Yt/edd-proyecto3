package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase interna privada para entradas. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
        public V valor;

        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Entrada> iterador;

        /* Construye un nuevo iterador, auxiliándose de las listas del
         * diccionario. */
        public Iterador() {
            iterador = null;
            for(int i = 0; i < entradas.length; i++)
            {
                if(entradas[i] != null)
                {
                    indice = i;
                    iterador = entradas[i].iterator();
                    break;
                }
            }
        }

        /* Nos dice si hay una siguiente entrada. */
        public boolean hasNext() {
            return iterador != null;
        }

        /* Regresa la siguiente entrada. */
        public Entrada siguiente() {
            if(iterador == null)
                throw new NoSuchElementException("No hay elemento siguiente");
            
            // Todas las listas tienen al menos un elemento
            Entrada n = iterador.next();

            if(!iterador.hasNext())
            {
                iterador = null;
                for(int i = indice+1; i < entradas.length; i++)
                {
                    if(entradas[i] != null)
                    {
                        indice = i;
                        iterador = entradas[i].iterator();
                        break;
                    }
                }
            }
            
            return n;
        }
    }

    /* Clase interna privada para iteradores de llaves. */
    private class IteradorLlaves extends Iterador
        implements Iterator<K> {

        /* Regresa el siguiente elemento. */
        @Override public K next() {
            return siguiente().llave;
        }
    }

    /* Clase interna privada para iteradores de valores. */
    private class IteradorValores extends Iterador
        implements Iterator<V> {

        /* Regresa el siguiente elemento. */
        @Override public V next() {
            return siguiente().valor;
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Capacidad mínima; decidida arbitrariamente a 2^6. */
    private static final int MINIMA_CAPACIDAD = 64;

    /* Dispersor. */
    private Dispersor<K> dispersor;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores. */
    private int elementos;

    /* Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked")
    private Lista<Entrada>[] nuevoArreglo(int n) {
        return (Lista<Entrada>[])Array.newInstance(Lista.class, n);
    }

    /**
     * Construye un diccionario con una capacidad inicial y dispersor
     * predeterminados.
     */
    public Diccionario() {
        this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial definida por el
     * usuario, y un dispersor predeterminado.
     * @param capacidad la capacidad a utilizar.
     */
    public Diccionario(int capacidad) {
        this(capacidad, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial predeterminada, y un
     * dispersor definido por el usuario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(Dispersor<K> dispersor) {
        this(MINIMA_CAPACIDAD, dispersor);
    }

    /**
     * Construye un diccionario con una capacidad inicial y un método de
     * dispersor definidos por el usuario.
     * @param capacidad la capacidad inicial del diccionario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(int capacidad, Dispersor<K> dispersor) {
        if(capacidad < MINIMA_CAPACIDAD)
        {
            capacidad = 2*MINIMA_CAPACIDAD;
        }else{
            int l = log2floor(capacidad);
            if(1 << l == capacidad)
            {
                capacidad = 2 << l;
            }else{
                capacidad = 2 << l+1;
            }
        }
        entradas = nuevoArreglo(capacidad);
        this.dispersor = dispersor;
        elementos = 0;
    }

    private int log2floor(int n) {
        int r = 0;
        while(n > 1) {
            n >>= 1;
            r++;
        }
        return r;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, el
     * diccionario reemplaza ese valor con el recibido aquí.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
        if(llave == null || valor == null)
            throw new IllegalArgumentException("Ni la llave ni el valor pueden ser null");

        int i = dispersor.dispersa(llave) & entradas.length-1;
        if(entradas[i] == null)
        {
            entradas[i] = new Lista<Entrada>();
            entradas[i].agrega(new Entrada(llave, valor));
            elementos++;
        }else{
            boolean f = false;
            for(Entrada e : entradas[i])
            {
                if(e.llave.equals(llave))
                {
                    e.valor = valor;
                    f = true;
                }
            }
            if(!f)
            {
                entradas[i].agrega(new Entrada(llave, valor));
                elementos++;
            }
        }

        if(carga() >= MAXIMA_CARGA)
        {
            Lista<Entrada>[] nuev = nuevoArreglo(2*entradas.length);
            Iterador it = new Iterador();

            while(it.hasNext())
            {
                Entrada e = it.siguiente();
                i = dispersor.dispersa(e.llave) & nuev.length-1;
                if(nuev[i] == null)
                {
                    nuev[i] = new Lista<Entrada>();
                }
                nuev[i].agrega(e);
            }
            entradas = nuev;
        }
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no está en el diccionario.
     */
    public V get(K llave) {
        if(llave == null)
            throw new IllegalArgumentException("La llave no debe ser nula");

        int i = dispersor.dispersa(llave) & entradas.length-1;
        if(entradas[i] == null)
            throw new NoSuchElementException("La llave no está en el diccionario");
        
        for(Entrada e : entradas[i])
        {
            if(e.llave.equals(llave))
                return e.valor;
        }

        throw new NoSuchElementException("La llave no está en el diccionario");
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <code>true</code> si la llave está en el diccionario,
     *         <code>false</code> en otro caso.
     */
    public boolean contiene(K llave) {
        if(llave == null)
            return false;

        int i = dispersor.dispersa(llave) & entradas.length-1;
        if(entradas[i] == null)
            return false;
        
        for(Entrada e : entradas[i])
        {
            if(e.llave.equals(llave))
                return true;
        }

        return false;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException si la llave no se encuentra en
     *         el diccionario.
     */
    public void elimina(K llave) {
        if(llave == null)
            throw new IllegalArgumentException("La llave no debe ser nula");

        int i = dispersor.dispersa(llave) & entradas.length-1;
        if(entradas[i] == null)
            throw new NoSuchElementException("La llave no está en el diccionario");
        
        for(Entrada e : entradas[i])
        {
            if(e.llave.equals(llave))
            {
                entradas[i].elimina(e);
                elementos--;
                if(entradas[i].getLongitud() == 0)
                    entradas[i] = null;
                return;
            }
        }

        throw new NoSuchElementException("La llave no está en el diccionario");
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        int c = 0;
        for(int i = 0; i < entradas.length; i++)
        {
            if(entradas[i] != null)
            {
                c += entradas[i].getLongitud() - 1;
            }
        }
        return c;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos
     * en el diccionario.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int max = 0;
        for(int i = 0; i < entradas.length; i++)
        {
            if(entradas[i] != null)
            {
                int l = entradas[i].getLongitud();
                if(l > max)
                {
                    max = l;
                }
            }
        }
        return max - 1;
    }

    /**
     * Nos dice la carga del diccionario.
     * @return la carga del diccionario.
     */
    public double carga() {
        return (double)elementos/(double)entradas.length;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * @return <code>true</code> si el diccionario es vacío, <code>false</code>
     *         en otro caso.
     */
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el diccionario de elementos, dejándolo vacío.
     */
    public void limpia() {
        elementos = 0;
        entradas = nuevoArreglo(entradas.length);
    }

    /**
     * Regresa una representación en cadena del diccionario.
     * @return una representación en cadena del diccionario.
     */
    @Override public String toString() {
        String s = "{";

        Iterador it = new Iterador();
        while(it.hasNext())
        {
            Entrada e = it.siguiente();
            s += " '"+e.llave.toString()+"': '"+e.valor.toString()+"',";
        }

        if(s != "{")
            s += " ";
        s += "}";
        
        return s;
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de
     *         Diccionario, y tiene las mismas llaves asociadas a los mismos
     *         valores.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") Diccionario<K, V> d =
            (Diccionario<K, V>)o;

        if(this.elementos != d.elementos)
            return false;

        Iterador it = new Iterador();
        while(it.hasNext())
        {
            Entrada e = it.siguiente();
            if(!d.contiene(e.llave) || !d.get(e.llave).equals(e.valor))
                return false;
        }

        return true;
    }

    /**
     * Regresa un iterador para iterar las llaves del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar las llaves del diccionario.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar los valores del diccionario.
     */
    @Override public Iterator<V> iterator() {
        return new IteradorValores();
    }
}
