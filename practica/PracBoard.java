package practica;
import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Math;

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
     * Que furgonetas han pasado por cada estacion
     */
    //private int [] nombreProvisionalAyuda;
    /*
     * Una fila por furgoneta, hasta 3 estaciones y cambio ( inicio y numero bicis, parada 1 y numero bicis, parada 2 y numero bicis ) por columna
     * Ejemplo: eA -20, eB +5, eC +15
     */
    private int [][] viajes; 
    /*
     * (Parece útil) furgonetas en esta solución actualmente
     */
    private int furgEnUso;

    /*
     * Constructora
     */
    public PracBoard(Estaciones estaciones, int maxFurgonetas){
        this.estaciones = estaciones;
        this.maxFurgonetas = maxFurgonetas;
        this.furgEnUso = 0;
        ocupacion = new int[estaciones.size()];
        viajes = new int[maxFurgonetas][6];
        creaSolucionBuena();
    }

    /* Operadores */

    /*  
     * Cambiar origen de una f
     * Cambiar destino 1 de una f
     * Cambiar destino 2 de una f
     * Swapear destino 1 y destino 2 de una f
     * 
     * Swapear destino 1 o 2 de f1 con destino 1 o 2 de f2
     * Swapear origen de f1 con origen de f2   
     *
     *  
     * Cosas que seguro que optimizan:
     * 
     * (((Quitar furgonetas cuando sobran (redistribuir y borrar))))
     * Añadir furgonetas cuando faltan (añadir y redistribuir) (generar solución inicial con más bien pocas)
     * 
     * 
     * 
     * 
     * Qué hacer con los números? Siempre el máximo? 
     *      Llevarte todas las que te puedas llevar
     *      Dejar todas las que puedas en la primera parada
     * 
     * 
     * El coste es (nb+9)/10 39/10 es 3, maximizar siempre el segundo dígito es bueno, llevar 20 eq 29
     *   Posible margen donde pilles 20 en vez de 21,22,23?..etc
     *   En general, nunca coger bicis de más y ya luego si las necesitas las puedes coger al hacer el swap (efecto "invisible" del operador)
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

    /*Intenta utilizar todas las furgonetas siempre yendo desde una estación donde "sobren" bicis a una donde falten (y a una segunda si aun quedan) para llegar a la prediccion de la hora siguiente*/
    private void creaSolucionBuena(){
        Queue<Integer> demandadas = new LinkedList<Integer>();
        Queue<Integer> noDemandadas = new LinkedList<Integer>();
        for (int i = 0; i < estaciones.size(); ++i) {
            if (estaciones.get(i).getDemanda() > estaciones.get(i).getNumBicicletasNext()) {
                demandadas.add(i);
            } else if (estaciones.get(i).getNumBicicletasNoUsadas() > 0) { //que al menos te puedas llevar una?
                noDemandadas.add(i);
            }
        }
        while (furgEnUso < maxFurgonetas && noDemandadas.peek() != null && demandadas.peek() != null) {
            int origen = noDemandadas.poll();
            //Coge bicis sin dejar la estacion por debajo de la demanda de la siguiente hora y solo cogiendo las que estan "no usadas" (disponibles) la hora actual
            int sobrantes = Math.min(estaciones.get(origen).getNumBicicletasNext()-estaciones.get(origen).getDemanda(), estaciones.get(origen).getNumBicicletasNoUsadas());
            sobrantes = Math.min(sobrantes, 30); //Una furgoneta no puede llevar mas de 30
            
            ocupacion[origen] -= sobrantes;
            viajes[furgEnUso][0] = origen;
            viajes[furgEnUso][1] = -sobrantes;

            int dest1 = demandadas.poll();
            int demanda1 = estaciones.get(dest1).getDemanda()-estaciones.get(dest1).getNumBicicletasNext();
            int anadidas1 = Math.min(demanda1, sobrantes);
            ocupacion[dest1] += anadidas1;
            sobrantes -= anadidas1;
            viajes[furgEnUso][2] = dest1;
            viajes[furgEnUso][3] = anadidas1;

            if (sobrantes > 0 && demandadas.peek() != null) {
                int dest2 = demandadas.poll();
                ocupacion[dest2] += sobrantes; //Ahora hay que dejar todas las sobrantes, no podemos hacer desaparecer bicis (o las dejamos en el origen?)
                viajes[furgEnUso][4] = dest2;
                viajes[furgEnUso][5] = sobrantes;
            }
            furgEnUso++;
        }
    }
}
