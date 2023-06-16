package mx.unam.ciencias.edd.proyecto3;

/**
 * Clase para extraer banderas
 */
public class Argumentos {
  
  /* Es verdadero si se usó la bandera -g */
  private boolean generar = false;
  /* La semilla */
  private long semilla = System.currentTimeMillis();
  /* Es verdadero si se usó la bandera -w */
  private boolean wflag = false;
  /* El ancho */
  private int w = -1;
  /* Es verdadero si se usó la bandera -h */
  private boolean hflag = false;
  /* El alto */
  private int h = -1;


  /**
   * Constructor, extrae propiedades de los argumentos
   * @param args los argumentos
   * @throws IllegalArgumentException si una de las banderas es usada incorrectamente
   */
  public Argumentos(String[] args) throws IllegalArgumentException {
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-g")) {
        generar = true;
      }else if(args[i].equals("-w")) {
        wflag = true;
        if(i+1 >= args.length)
          throw new IllegalArgumentException("Se debe especificar el ancho del laberinto -w <ancho>");
        
        try{
          w = Integer.parseInt(args[i+1]);
        }catch(NumberFormatException nfe){
          throw new IllegalArgumentException("El ancho debe ser un número válido");
        }
        i++;
      }else if(args[i].equals("-h")) {
        hflag = true;
        if(i+1 >= args.length)
          throw new IllegalArgumentException("Se debe especificar el alto del laberinto -h <alto>");
        
        try{
          h = Integer.parseInt(args[i+1]);
        }catch(NumberFormatException nfe){
          throw new IllegalArgumentException("El alto debe ser un número válido");
        }
        i++;
      }else if(args[i].equals("-s")) {
        if(i+1 >= args.length)
          throw new IllegalArgumentException("Se debe especificar la semilla -s [semilla]");
        
        try{
          semilla = Integer.parseInt(args[i+1]);
        }catch(NumberFormatException nfe){
          throw new IllegalArgumentException("La semilla debe ser un número válido");
        }
        i++;
      }
    }
    if(generar) {
      if(!wflag || !hflag)
        throw new IllegalArgumentException("Se debe especificar el ancho -w <ancho> y alto -h <alto> del laberinto");
      
      if(w < 2 || h < 2)
        throw new IllegalArgumentException("El alto y el ancho deben ser mayores o iguales a 2");
    }
  }

  /**
   * Devuelve true si la bandera -g se usó
   * @return true si la bandera -g se usó
   */
  public boolean getGenerar() {
    return generar;
  }

  /**
   * Devuelve el ancho
   * @return el ancho
   */
  public int getAncho() {
    return w;
  }

  /**
   * Devuelve el alto
   * @return el alto
   */
  public int getAlto() {
    return h;
  }

  /**
   * Devuelve la semilla
   * @return la semilla
   */
  public long getSemilla() {
    return semilla;
  }

}
