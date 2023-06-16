package mx.unam.ciencias.edd.proyecto3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Proyecto3
 */
public class Proyecto3 {

  public static void uso() {
    System.out.println("Uso:\njava -jar target/proyecto3.jar -g -s [semilla] -w <alto> -h <ancho> > <archivo>.mze\n"
                      + "java -jar target/proyecto3.jar < <archivo>.mze > solucion.svg");
    System.exit(1);
  }

  public static void main(String[] args) {
    Argumentos a = null;
    try {
      a = new Argumentos(args);
    } catch (Exception e) {
      System.err.println(e);
      uso();
    }

    byte[] maze = { 0x4D, 0x41, 0x5A, 0x45 };
    if (a.getGenerar()) {
      Laberinto l = null;
      try{
        l = new Laberinto(a.getSemilla(), a.getAncho(), a.getAlto());
      }catch(Exception e){
        System.err.println(e);
        System.exit(1);
      }
      // System.out.println("Laberinto de "+a.getAlto()+"x"+a.getAncho()+" con semilla "+a.getSemilla());
      BufferedOutputStream out = new BufferedOutputStream(System.out);
      try{
        out.write(maze);
        out.write(a.getAlto());
        out.write(a.getAncho());
        out.flush();
      }catch(Exception e){
        System.err.println(e);
        System.exit(1);
      }
      for(Byte b : l)
      {
        try{
          out.write(b.byteValue());
        }catch(Exception e){
          System.err.println(e);
          System.exit(1);
        }
      }
      try {
        out.flush();
        out.close();
      } catch (IOException e) {
        System.err.println(e);
        System.exit(1);
      }
    } else {
      BufferedInputStream in = new BufferedInputStream(System.in);
      byte[][] casillas = null;
      int w = -1;
      int h = -1;
      try{
        for(byte b : maze)
          if(in.read() != b)
            throw new IllegalArgumentException("El archivo es inválido (debe iniciar de manera correcta)");
        h = in.read();
        w = in.read();
        if (w < 2 || w > 255)
          throw new IllegalArgumentException("El archivo es inválido (el ancho es inválido)");
        
        if (h < 2 || h > 255)
          throw new IllegalArgumentException("El archivo es inválido (el alto es inválido)");
        
        casillas = new byte[h][w];
        for(int i = 0; i < h; i++)
        {
          byte[] row = in.readNBytes(w);
          if(row.length != w)
            throw new IllegalArgumentException("El archivo es inválido (las filas/columnas están incompletas)");
          
          casillas[i] = row;
        }
        in.close();
      }catch(Exception e){
        System.err.println(e);
        System.exit(1);
      }
      try{
        Laberinto l = new Laberinto(w, h, casillas);
        System.out.println(l.toSVG());
      }catch(Exception e){
        System.err.println(e);
        System.exit(1);
      }
    }
  }
  
}