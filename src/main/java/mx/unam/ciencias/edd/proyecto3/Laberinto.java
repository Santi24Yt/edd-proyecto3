package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.IteradorLista;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.VerticeGrafica;

import java.util.Iterator;
import java.util.Random;

public class Laberinto implements Iterable<Byte> {
  
  private Random r;
  private byte width;
  private byte height;
  private byte[][] casillas;
  private Lista<Integer> visitadas = new Lista<Integer>();
  private int[] visitadasRandom;
  private int visitadasRandomElementos = 0;
  private int entrada = -1;
  private int salida = -1;

  private class Iterador implements Iterator<Byte> {
    private int i = 0;
    private int j = 0;

    @Override public boolean hasNext() {
      return i < toUnsignedByte(height) && j < toUnsignedByte(width);
    }

    @Override public Byte next() {
      byte e = casillas[i][j++];
      if (j >= toUnsignedByte(width)) {
        j = 0;
        i++;
      }
      return Byte.valueOf(e);
    }
  }

  public Laberinto(long semilla, int w, int h) {
    if (w < 2 || w > 255)
      throw new IllegalArgumentException("El ancho debe ser mayor a 2 y menor a 255");
    
    if (h < 2 || h > 255)
      throw new IllegalArgumentException("El alto debe ser mayor a 2 y menor a 255");

    r = new Random(semilla);
    width = toByte(w);
    height = toByte(h);
    // System.out.println("w: "+toUnsignedByte(width)+" h: "+toUnsignedByte(height));
    casillas = new byte[toUnsignedByte(height)][toUnsignedByte(width)];
    for(int i = 0; i < toUnsignedByte(height); i++)
    {
      for(int j = 0; j < toUnsignedByte(width); j++)
      {
        casillas[i][j] = (byte)0xFF;
      }
    }
    generar();
  }

  public Laberinto(int w, int h, byte[][] c) {
    if (w < 2 || w > 255)
      throw new IllegalArgumentException("El ancho debe ser mayor a 2 y menor a 255");
    
    if (h < 2 || h > 255)
      throw new IllegalArgumentException("El alto debe ser mayor a 2 y menor a 255");

    width = toByte(w);
    height = toByte(h);
    casillas = c;
  }

  public void generar() {
    visitadasRandom = new int[toUnsignedByte(width)*toUnsignedByte(height)+1];
    int inicial = par(randX(), randY());
    // System.out.println("Par: "+inicial);
    // System.out.println("Casilla inicial x: "+toUnsignedByte(getX(inicial))+" y: "+toUnsignedByte(getY(inicial)));
    visitadas.agregaInicio(inicial);
    setValor(getX(inicial), getY(inicial), toByte(0));
    while (!visitadas.esVacia()) {
      int actual = siguiente();
      // System.out.println("Actual: x: "+getX(actual)+" y: "+getY(actual));
      int[] vecinos = new int[4];
      Lista<Integer> direccionesValidas = new Lista<Integer>();
      for(int i = 0; i < 4; i++)
      {
        int vecino = getVecino(actual, i);
        vecinos[i] = vecino;
        if(vecino != -1 && valor(getCasilla(getX(vecino), getY(vecino))) == 15) // Es una casilla válida y no ha sido visitada
          direccionesValidas.agrega(i);
      }
      // System.out.println("Direcciones válidas: "+direccionesValidas.toString());
      if(direccionesValidas.esVacia())
      {
        // System.out.println("No hay vecinos válidos");
        visitadas.elimina(actual);
        eliminarVisitadasRandom(actual);
        continue;
      }
      int i = r.nextInt(direccionesValidas.getLongitud());
      int d = direccionesValidas.get(i);
      int vecinorandom = vecinos[d];
      tiraMuro(getX(actual), getY(actual), d);
      tiraMuro(getX(vecinorandom), getY(vecinorandom), direccionOpuesta(d));
      visitadas.agregaInicio(vecinorandom);
      // System.out.println("Vecino random x: "+getX(vecinorandom)+" y: "+getY(vecinorandom));
      setValor(getX(vecinorandom), getY(vecinorandom), toByte(0));
      setValor(getX(actual), getY(actual), toByte(0));
      visitadasRandom[visitadasRandomElementos++] = vecinorandom;
    }

    for(int i = 0; i < toUnsignedByte(height); i++)
    {
      for(int j = 0; j < toUnsignedByte(width); j++)
      {
        setValor((byte)j, (byte)i, (byte)r.nextInt(16));
      }
    }

    int entrada = r.nextInt(toUnsignedByte(height));
    int salida = r.nextInt(toUnsignedByte(height));

    tiraMuro((byte)0, (byte)entrada, 2);
    tiraMuro((byte)(width-1), (byte)salida, 0);

    entrada = par((byte)0, (byte)entrada);
    salida = par((byte)(width-1), (byte)salida);
  }
  
  private int valor(byte c) {
    // System.out.println("valor de: "+toUnsignedByte(c)+" v: "+((int)(toUnsignedByte(c) >> 4)));
    return (int)((int)toUnsignedByte(c) >> 4);
  }

  private byte toByte(int n) {
    return (byte)(n & 0xFF);
  }

  private short toUnsignedByte(byte b) {
    return (short)(((short)b) & 0xFF); 
  }

  private int par(byte x, byte y)
  {
    short ux = toUnsignedByte(x);
    short uy = toUnsignedByte(y);
    // System.out.println("par: "+((ux << 8) | uy));
    return (int)((ux << 8) | uy);
  }

  private byte randX() {
    return toByte(r.nextInt(toUnsignedByte(width)));
  }

  private byte randY() {
    return toByte(r.nextInt(toUnsignedByte(height)));
  }

  private byte getX(int par) {
    // System.out.println("getx >> 8: "+(par >> 8));
    // System.out.println("getx tobyte: "+toByte(par >> 8));
    return toByte(par >> 8);
  }

  private byte getY(int par) {
    return toByte(par & 0xFF);
  }

  private byte getCasilla(byte x, byte y) {
    return casillas[toUnsignedByte(y)][toUnsignedByte(x)];
  }

  private int siguiente() {
    int c = r.nextInt(100);
    if (c < 25) // Random
    {
      // System.out.println("Escogiendo casilla al azar");
      if(visitadasRandomElementos <= 0)
        return visitadas.getUltimo();
      
      int i = r.nextInt(visitadasRandomElementos);
      // System.out.println("l: "+visitadasRandomElementos+" i: "+i);
      return visitadasRandom[i];
    } else { // Más reciente
      // System.out.println("Escogiendo casilla más reciente");
      return visitadas.getPrimero();
    }
  }

  private void eliminarVisitadasRandom(int e) {
    for(int i = 0; i < visitadasRandomElementos; i++)
    {
      if(visitadasRandom[i] == e)
      {
        for(int j = i+1; j < visitadasRandomElementos; j++)
        {
          visitadasRandom[j-1] = visitadasRandom[j];
        }
        visitadasRandomElementos--;
        break;
      }
    }
  }

  private int getVecino(int par, int d) {
    if (d == 0) { // E
      if(getX(par) == width-1)
        return -1;
      return par(toByte(getX(par)+1), getY(par));
    } else if (d == 1) { // N
      if(getY(par) == 0)
        return -1;
      return par(getX(par), toByte(getY(par)-1));
    } else if (d == 2) { // O
      if(getX(par) == 0)
        return -1;
      return par(toByte(getX(par)-1), getY(par));
    } else if (d == 3) { // S
      if(getY(par) == height-1)
        return -1;
      return par(getX(par), toByte(getY(par)+1));
    }
    return -1;
  }

  private void setValor(byte x, byte y, byte v) {
    casillas[toUnsignedByte(y)][toUnsignedByte(x)] = toByte(((v & 0xF) << 4) | (casillas[toUnsignedByte(y)][toUnsignedByte(x)] & 0xF));
  }

  private void tiraMuro(byte x, byte y, int d) {
    if (d == 0) {
      casillas[toUnsignedByte(y)][toUnsignedByte(x)] &= 0xFE; // casilla & 1111 1110
    } else if (d == 1) {
      casillas[toUnsignedByte(y)][toUnsignedByte(x)] &= 0xFD; // casilla & 1111 1101
    } else if (d == 2) {
      casillas[toUnsignedByte(y)][toUnsignedByte(x)] &= 0xFB; // casilla & 1111 1011
    } else if (d == 3) {
      casillas[toUnsignedByte(y)][toUnsignedByte(x)] &= 0xF7; // casilla & 1111 0111
    }
  }

  private int direccionOpuesta(int d) {
    return (d+2) % 4;
  }

  public Iterator<Byte> iterator() {
    return new Iterador();
  }

  private Grafica<Integer> grafica() {
    Grafica<Integer> g = new Grafica<Integer>();
    for(int i = 0; i < toUnsignedByte(height); i++)
    {
      for(int j = 0; j < toUnsignedByte(width); j++)
      {
        g.agrega(par((byte)j, (byte)i));
      }
    }
    for(int i = 0; i < toUnsignedByte(height); i++)
    {
      for(int j = 0; j < toUnsignedByte(width); j++)
      {
        byte c1 = getCasilla((byte)j, (byte)i);
        if((c1 & 0b0001) == 0b0000)
        {
          if(j < width-1)
          {
            byte c2 = getCasilla((byte)(j+1), (byte)i);
            try{
              g.conecta(par((byte)j, (byte)i), par((byte)(j+1), (byte)i), valor(c1)+valor(c2)+1);
            }catch(Exception e){

            }
          }else{
            salida = par((byte)j, (byte)i);
          }
        }
        if((c1 & 0b0010) == 0b0000 && i > 0)
        {
          byte c2 = getCasilla((byte)j, (byte)(i-1));
          try{
            g.conecta(par((byte)j, (byte)i), par((byte)j, (byte)(i-1)), valor(c1)+valor(c2)+1);
          }catch(Exception e){

          }
        }
        if((c1 & 0b0100) == 0b0000)
        {
          if(j > 0)
          {
            byte c2 = getCasilla((byte)(j-1), (byte)i);
            try{
              g.conecta(par((byte)j, (byte)i), par((byte)(j-1), (byte)i), valor(c1)+valor(c2)+1);
            }catch(Exception e){

            }
          }else{
            entrada = par((byte)j, (byte)i);
          }
        }
        if((c1 & 0b1000) == 0b0000 && i < height-1)
        {
          byte c2 = getCasilla((byte)j, (byte)(i+1));
          try{
            g.conecta(par((byte)j, (byte)i), par((byte)j, (byte)(i+1)), valor(c1)+valor(c2)+1);
          }catch(Exception e){

          }
        }
      }
    }
    return g;
  }

  public Lista<VerticeGrafica<Integer>> resuelve() {
    Grafica<Integer> g = grafica();
    return g.dijkstra(entrada, salida);
  }

  public String toSVG() {
    StringBuilder s = new StringBuilder();
    s.append("<svg version='1.1' width='"+(toUnsignedByte(width)*10 + 4)+"' height='"+(toUnsignedByte(height)*10 + 4)+"'>\n");
    s.append("\t<rect x='2' y='2' width='"+(toUnsignedByte(width)*10)+"' height='"+(toUnsignedByte(height)*10)+"' fill='white' />\n");
    s.append("\t<!-- Lineas horizontales -->\n");
    for(int i = 0; i < toUnsignedByte(height); i++)
    {
      int ini = 0;
      int fin;
      for(int j = 0; j <= toUnsignedByte(width); j++)
      {
        if(j == toUnsignedByte(width) || (casillas[i][j] & 0b0010) == 0b0000)
        {
          fin = j;
          if(ini != j)
            s.append("\t<line x1='"+(2 + ini*10)+"' y1='"+(2 + i*10)+"' x2='"+(2 + fin*10)+"' y2='"+(2 + i*10)+"' stroke='black' />\n");
          ini = j+1;
        }
      }
    }
    int i = toUnsignedByte(height)-1;
    int ini = 0;
    int fin;
    for(int j = 0; j <= toUnsignedByte(width); j++)
    {
      if(j == toUnsignedByte(width) || (casillas[i][j] & 0b1000) == 0b0000)
      {
        fin = j;
        if(ini != j)
          s.append("\t<line x1='"+(2 + ini*10)+"' y1='"+(2 + (i+1)*10)+"' x2='"+(2 + fin*10)+"' y2='"+(2 + (i+1)*10)+"' stroke='black' />\n");
        ini = j+1;
      }
    }
    s.append("\t<!-- Lineas verticales -->\n");
    for(int j = 0; j < toUnsignedByte(width); j++)
    {
      ini = 0;
      fin = -1;
      for(i = 0; i <= toUnsignedByte(height); i++)
      {
        if(i == toUnsignedByte(height) || (casillas[i][j] & 0b0100) == 0b0000 )
        {
          fin = i;
          if(ini != i)
            s.append("\t<line x1='"+(2 + j*10)+"' y1='"+(2 + ini*10)+"' x2='"+(2 + j*10)+"' y2='"+(2 + fin*10)+"' stroke='black' />\n");
          ini = i+1;
        }
      }
    }
    int j = toUnsignedByte(width)-1;
    ini = 0;
    fin = -1;
    for(i = 0; i <= toUnsignedByte(height); i++)
    {
      if(i == toUnsignedByte(height) || (casillas[i][j] & 0b0001) == 0b0000 )
      {
        fin = i;
        if(ini != i)
          s.append("\t<line x1='"+(2 + (j+1)*10)+"' y1='"+(2 + ini*10)+"' x2='"+(2 + (j+1)*10)+"' y2='"+(2 + fin*10)+"' stroke='black' />\n");
        ini = i+1;
      }
    }
    s.append("\t<!-- Solución -->\n");
    Lista<VerticeGrafica<Integer>> p = resuelve();
    IteradorLista<VerticeGrafica<Integer>> it = p.iteradorLista();
    i = 0;
    int[] sol = new int[p.getElementos()];
    while(it.hasNext())
      sol[i++] = it.next().get();
    if(sol.length > 0)
      s.append("\t<line x1='"+(2)+"' y1='"+(toUnsignedByte(getY(sol[0]))*10 + 5 + 2)+"' x2='"+(toUnsignedByte(getX(sol[0]))*10 + 5 + 2)+"' y2='"+(toUnsignedByte(getY(sol[0]))*10 + 5 + 2)+"'  stroke='green' />\n");
    for(i = 0; i < sol.length; i++)
    {
      int n = sol[i];
      if(i < sol.length-1)
      {
        int n2 = sol[i+1];
       s.append("\t<line x1='"+(toUnsignedByte(getX(n))*10 + 5 + 2)+"' y1='"+(toUnsignedByte(getY(n))*10 + 5 + 2)+"' x2='"+(toUnsignedByte(getX(n2))*10 + 5 + 2)+"' y2='"+(toUnsignedByte(getY(n2))*10 + 5 + 2)+"'  stroke='green' />\n");
      }
    }
    if(sol.length > 0)
      s.append("\t<line x1='"+(toUnsignedByte(getX(sol[sol.length-1]))*10 + 5 + 2)+"' y1='"+(toUnsignedByte(getY(sol[sol.length-1]))*10 + 5 + 2)+"' x2='"+(toUnsignedByte(getX(sol[sol.length-1]))*10 + 10 + 2)+"' y2='"+(toUnsignedByte(getY(sol[sol.length-1]))*10 + 5 + 2)+"'  stroke='green' />\n");
    s.append("</svg>");
    return s.toString();
  }
}