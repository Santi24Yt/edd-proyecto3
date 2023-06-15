package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0; // 32 0's en binario
        int l = llave.length;
        for(int i = 0; i < l; i++)
        {
            r ^= bigEndian( i < l ? llave[i++] : 0,
                            i < l ? llave[i++] : 0, 
                            i < l ? llave[i++] : 0,
                            i < l ? llave[i] : 0);
        }
        return r;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a = 0x9E3779B9;
        int b = 0x9E3779B9;
        int c = 0xFFFFFFFF;
        int l = llave.length;
        for(int i = 0; i <= l; i++)
        {
            a += littleEndian( i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0);

            b += littleEndian( i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0,
                               i < l ? llave[i++] : 0);

            if(i+3 < l)
            {
                c += littleEndian(i < l ? llave[i++] : 0,
                                  i < l ? llave[i++] : 0,
                                  i < l ? llave[i++] : 0,
                                  i < l ? llave[i] : 0);
            }else{
                c += l;
                c += littleEndian((byte)0,
                                  i < l ? llave[i++] : 0,
                                  i < l ? llave[i++] : 0,
                                  i < l ? llave[i++] : 0);
            }
            
            a -= b;
            a -= c;
            a ^= (c >>> 13);

            b -= c;
            b -= a;
            b ^= (a << 8);

            c -= a;
            c -= b;
            c ^= (b >>> 13);

            a -= b;
            a -= c;
            a ^= (c >>> 12);

            b -= c;
            b -= a;
            b ^= (a << 16);

            c -= a;
            c -= b;
            c ^= (b >>> 5);

            a -= b;
            a -= c;
            a ^= (c >>> 3);

            b -= c;
            b -= a;
            b ^= (a << 10);

            c -= a;
            c -= b;
            c ^= (b >>> 15);
        }

        return c;
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;

        int l = llave.length;
        for(int i = 0; i < l; i++)
        {
            h += (h << 5) + (i < l ? (llave[i] & 0xFF) : 0);
        }

        return h;
    }

    private static int bigEndian(byte a, byte b, byte c, byte d)
    {
        return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8) | (d & 0xFF);
    }

    private static int littleEndian(byte a, byte b, byte c, byte d)
    {
        return ((d & 0xFF) << 24) | ((c & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF);
    }
}
