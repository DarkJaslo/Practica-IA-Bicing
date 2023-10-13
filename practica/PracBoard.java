package practica;
import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;
import aima.basic.MockAgent;

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

    private static final int ORIGEN = 0;
    private static final int EST1 = 1;
    private static final int EST1_CANTIDAD = 2;
    private static final int EST2 = 3;
    private static final int EST2_CANTIDAD = 4;

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
     * (Parece útil) furgonetas usadas en esta solución actualmente
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
        
        for(int i = 0; i < maxFurgonetas; ++i){
            viajes[i][ORIGEN] = -1;
            viajes[i][EST1] = -1;
            viajes[i][EST1_CANTIDAD] = 0;
            viajes[i][EST2] = -1;
            viajes[i][EST2_CANTIDAD] = 0;
        }
    }

    /* Operadores */

    /*  
     * Cambiar origen de una f
     * Cambiar destino 1 de una f
     * Cambiar destino 2 de una f
     * Swapear destino 1 y destino 2 de una f (ok)
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
     * Cambia estacion de furgoneta
     */
    public boolean canChangeEst(int f, int whichEst, int newEst)
    {
        //False si newEst se usa de origen en otra furgoneta
        if(ocupacion[newEst] < 0) return false; 
        //False si queremos cambiar una estacion por si misma
        if(viajes[f][whichEst] == newEst) return false;
        return true;
    }
    public void changeEst(int f, int whichEst, int newEst)
    {
        //Cambia la estación whichEst (whichEst == ORIGEN, whichEst == EST1, whichEst == EST2) a newEst

        int origen = viajes[f][ORIGEN];
        int est1 = viajes[f][EST1];
        int est2 = viajes[f][EST2];

        ocupacion[origen] += viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD];
        if(est1 >= 0) ocupacion[est1] -= viajes[f][EST1_CANTIDAD];
        if(est2 >= 0) ocupacion[est2] -= viajes[f][EST2_CANTIDAD];

        int dem1 = demand(est1);
        int dem2 = demand(est2);
        
        if(whichEst == ORIGEN)
        {
            distributeBycicles(f, newEst, dem1, dem2);
            origen = newEst;
        }
        else //Cambiando alguno de los destinos
        {            
            int demandaNewEst = demand(newEst);

            if(whichEst == EST1){
                distributeBycicles(f, origen, demandaNewEst, dem2);
                est1 = newEst;
            }
            else{ //EST2
                distributeBycicles(f, origen, dem1, demandaNewEst);
                est2 = newEst;
            }
        }

        viajes[f][whichEst] = newEst;
        ocupacion[origen] -= viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD];
        if(est1 > 0) ocupacion[est1] += viajes[f][EST1_CANTIDAD];
        if(est2 > 0) ocupacion[est2] += viajes[f][EST2_CANTIDAD];
    }

    /*
     * Swap de estaciones entre furgonetas
     */
    public boolean canSwapEst(int f1, int f2, int est1, int est2)
    {   
        //Si una es de origen, ambas deben ser de origen
        if((est1 == ORIGEN || est2 == ORIGEN) && est1 != est2) return false;
        //Si son la misma furgoneta y se quieren cambiar la misma estación
        else if(f1 == f2 && est1 == est2) return false;
        return true;
    }
    public void swapEst(int f1, int f2, int est1, int est2)
    {
        /*
         * Si son de origen, cambialas y ajusta origen y destinos como en changeEst
         */
        /*
         * Si son de destino, cambialas y coge/deja bicis del origen si se puede y si es necesario, siguiendo
         * el criterio del 1,2 de changeEst
         */
        if(est1 == ORIGEN && est2 == ORIGEN)
        {
            int e1 = viajes[f1][est1];
            int e2 = viajes[f2][est2];

            if(e1 > 0) ocupacion[e1] -= (viajes[f1][EST1_CANTIDAD] + viajes[f1][EST2_CANTIDAD]);
            if(e2 > 0) ocupacion[e2] -= (viajes[f2][EST1_CANTIDAD] + viajes[f2][EST2_CANTIDAD]);
        }

        
    }

    /*
     * Añade una furgoneta nueva
     */
    public boolean canAddVan(int origen, int dest1, int dest2)
    {
        if(furgEnUso >= maxFurgonetas) return false;
        else if(origen == dest1 || origen == dest2 || dest1 == dest2) return false;
        else if(ocupacion[origen] < 0) return false; //Estacion de origen usada ya
        return true;
    }
    public void addVan(int origen, int dest1, int dest2)
    {
        viajes[furgEnUso][ORIGEN] = origen;
        viajes[furgEnUso][EST1] = dest1;
        viajes[furgEnUso][EST2] = dest2;

        /*
         * To do: Reparte números entre origen y dest1,dest2, prioriza llenar dest1
         */

        ++furgEnUso;
    }

    /*
     * Funciones auxiliares
     */

    /*
     * Pretende redondear hacia abajo las bicicletas que una furgoneta se lleva de una estación de origen con tal de aprovechar la fórmula de coste
     */
    private int takenBycicles(int intended)
    {
        if(intended%10 <= 3) return intended-intended%10;
        return intended;
    }

    /*
     * Se lleva un número entre el máximo de bicicletas posibles y takenBycicles() y las asigna de forma que la primera estación se llena todo lo posible primero
     */
    private void distributeBycicles(int f, int origen, int dem1, int dem2)
    {
        viajes[f][EST1_CANTIDAD] = 0;
        viajes[f][EST2_CANTIDAD] = 0;

        int disponibles = 0;
        if(origen > 0) disponibles = estaciones.get(origen).getNumBicicletasNoUsadas();
        else return; //No hay origen -> no hay bicicletas para distribuir

        int cogidas = Math.min(Math.min(30, disponibles),takenBycicles(dem1+dem2));

        //Intenta dejar el máximo de bicicletas en Dest1
        if(dem1 < cogidas)
        {
            viajes[f][EST1_CANTIDAD] = dem1;
            cogidas -= dem1;
            //Las que sobran se llevan a la estación 2
            viajes[f][EST2_CANTIDAD] = cogidas;
        }
        else{
            viajes[f][EST1_CANTIDAD] = cogidas;
        }
    }
    /*
     * Devuelve las bicicletas que hace falta traer a una estación
     */
    private int demand(int est)
    {
        if(est < 0) return 0;
        return (estaciones.get(est).getDemanda()-estaciones.get(est).getNumBicicletasNext()-ocupacion[est]);
    }



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
    public void creaSolucionBuena()
    {
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
            viajes[furgEnUso][ORIGEN] = origen;
            //viajes[furgEnUso][ORIGEN_CANTIDAD] = -sobrantes;

            int dest1 = demandadas.poll();
            int demanda1 = estaciones.get(dest1).getDemanda()-estaciones.get(dest1).getNumBicicletasNext();
            int anadidas1 = Math.min(demanda1, sobrantes);
            ocupacion[dest1] += anadidas1;
            sobrantes -= anadidas1;
            viajes[furgEnUso][EST1] = dest1;
            viajes[furgEnUso][EST1_CANTIDAD] = anadidas1;

            if (sobrantes > 0 && demandadas.peek() != null) {
                int dest2 = demandadas.poll();
                ocupacion[dest2] += sobrantes; //Ahora hay que dejar todas las sobrantes, no podemos hacer desaparecer bicis (o las dejamos en el origen?)
                viajes[furgEnUso][EST2] = dest2;
                viajes[furgEnUso][EST2_CANTIDAD] = sobrantes;
            }
            furgEnUso++;
        }
    }
}
