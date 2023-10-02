package practica;
import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;
import java.util.ArrayList;

/*
 * Propuesta actual, seguro que se puede mejorar
 */
public class PracBoard {
    /*
     * Constantes
     */
    private static Estaciones estaciones;
    private static int maxFurgonetas;

    /*
     * Cambios en la ocupación de las estaciones (e1: +2, e2: -30, e3: +12, etc)
     */
    private int [] ocupacion;
    /*
     * Una fila por furgoneta, hasta 3 estaciones y cambio ( inicio y numero bicis, parada 1 y numero bicis, parada 2 y numero bicis ) por columna
     * Ejemplo: eA -20, eB +5, eC +15
     */
    private int [][] viajes; 

    /*
     * Constructora
     */
    public PracBoard(Estaciones est, int maxFurg){
        estaciones = est;
        maxFurgonetas = maxFurg;
    }
}
