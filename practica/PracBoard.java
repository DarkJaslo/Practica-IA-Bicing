package practica;
import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;

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
     * Parece útil
     */
    private int furgEnUso;

    /*
     * Constructora
     */
    public PracBoard(Estaciones estaciones, int maxFurgonetas){
        this.estaciones = estaciones;
        this.maxFurgonetas = maxFurgonetas;
        this.furgEnUso = 0;
    }

    /* Operadores */

    /*  
     * Cambiar origen de una f
     * Cambiar destino 1 de una f
     * Cambiar destino 2 de una f
     * Swapear destino 1 y destino 2 de una f
     * 
     * Swapear destino 1,2 de f1 con destino 1,2 de f2
     * Swapear origen de f1 con origen de f2   
     *
     *  
     * Cosas que seguro que optimizan:
     * 
     * Quitar furgonetas cuando sobran (redistribuir y borrar)
     * Añadir furgonetas cuando faltan (añadir y redistribuir)
     * 
     * Borrar una furgoneta siempre que se pueda
     * 
     * 
     * Qué hacer con los números? Siempre el máximo? 
     *      Llevarte todas las que te puedas llevar
     *      Dejar todas las que puedas? y la siguiente parada?
     * 
     * 
     * En hill climbing vale la pena intentar redistribuir más?
     * El coste es (nb+9)/10! 39/10 es 3, maximizar siempre el segundo dígito es bueno, llevar 20 eq 29
    */




    /*
     * Función heurística
     */
    public double heuristicFunction(){
        return 0.0;
    }



    /* Getters */
    
    public int[] getOcupacion(){
        return ocupacion;
    }

    public int[][] getViajes(){
        return viajes;
    }
}
