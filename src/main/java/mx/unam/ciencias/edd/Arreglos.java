package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Ordena el arreglo recibido usando QuickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador) {
        //                                                -1 ya que es inclusivo
        quickSort(arreglo, comparador, 0, arreglo.length - 1);
    }

    /**
     * Ordena el arreglo recibido usando QuickSort recursivamente.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     * @param a el indice inicial del subarreglo a ordenar
     * @param b el indice final del subarreglo a ordenar (inclusivo)
     */
    private static <T> void quickSort(T[] arr, Comparator<T> comp, int a, int b) {
        if (b <= a)
            return;

        int i = a+1;
        int j = b;
        while(i < j) {
            if(comp.compare(arr[i], arr[a]) > 0
            && comp.compare(arr[j], arr[a]) <= 0) {
                intercambia(arr, i, j);
                i++;
                j--;
            } else if(comp.compare(arr[i], arr[a]) <= 0) {
                i++;
            } else {
                j--;
            }
        }
        if(comp.compare(arr[i], arr[a]) > 0) {
            i--;
        }
        intercambia(arr, a, i);
        quickSort(arr, comp, a, i-1);
        quickSort(arr, comp, i+1, b);
    }

    /**
     * Intercambia dos elementos en un arreglo
     * @param <T> el tipo del que puede ser el arreglo
     * @param arr el arreglo con los elementos a intercambiar
     * @param i el indice de uno de los elementos a intercambiar
     * @param j el indice del otro elemento a intercambiar
     */
    private static <T> void intercambia(T[] arr, int i , int j) {
        T t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador) {
        for(int i = 0; i < arreglo.length; i++) {
            int m = i;
            for(int j = i+1; j < arreglo.length; j++) {
                if(comparador.compare(arreglo[j], arreglo[m]) < 0) {
                    m = j;
                }
            }
            intercambia(arreglo, i, m);
        }
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
        return busquedaBinaria(arreglo, elemento, comparador, 0, arreglo.length - 1);
    }

    /**
     * Hace una búsqueda binaria recursivamente del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @param a el indice inicial del subarreglo
     * @param b el indice final del subarreglo (inclusivo)
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    private static <T> int busquedaBinaria(T[] arr, T e, Comparator<T> comp, int a, int b) {
        if (b < a)
            return -1;

        int mitad = a + (b-a)/2;
        int comparacion = comp.compare(e, arr[mitad]);

        if(comparacion == 0) {
            return mitad;
        } else if(comparacion > 0) {
            return busquedaBinaria(arr, e, comp, mitad + 1, b);
        } else {
            return busquedaBinaria(arr, e, comp, a, mitad - 1);
        }
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }
}
