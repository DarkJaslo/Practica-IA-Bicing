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

    //Ejemplo: si hay que coger 20+REDONDEO o menos, coge 20, pero con 20+REDONDEO+1 ya coge esas
    private static final int REDONDEO = 2;

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
        //False si queremos cambiar una estacion por ella misma
        if(viajes[f][whichEst] == newEst) return false;
        return true;
    }
    public void changeEst(int f, int whichEst, int newEst)
    {
        //Cambia la estación whichEst (whichEst == ORIGEN, whichEst == EST1, whichEst == EST2) a newEst

        int origen = viajes[f][ORIGEN];
        int est1 = viajes[f][EST1];
        int est2 = viajes[f][EST2];

        if(origen >= 0) ocupacion[origen] += viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD];
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
        if(origen >= 0) ocupacion[origen] -= viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD];
        if(est1 >= 0)   ocupacion[est1] += viajes[f][EST1_CANTIDAD];
        if(est2 >= 0)   ocupacion[est2] += viajes[f][EST2_CANTIDAD];

        
        swapIfBad(f, origen, est1, est2);
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
         * Si son de origen, cambialas y ajusta numeros en origen y destinos
         */
        /*
         * Si son de destino, cambialas y ajusta. Se permiten swaps en una misma furgoneta pero no de exactamente la misma estación
         */

        int o1 = viajes[f1][ORIGEN];
        int o2 = viajes[f2][ORIGEN];

        int est11 = viajes[f1][EST1];
        int est12 = viajes[f1][EST2];
        int est21 = viajes[f2][EST1];
        int est22 = viajes[f2][EST2];

        if(o1 > 0) ocupacion[o1] += (viajes[f1][EST1_CANTIDAD] + viajes[f1][EST2_CANTIDAD]);
        if(est11 > 0) ocupacion[est11] -= viajes[f1][EST1_CANTIDAD];
        if(est12 > 0) ocupacion[est12] -= viajes[f1][EST2_CANTIDAD];

        if(f1 != f2) //Si fueran la misma se actualizaria dos veces
        {
            if(o2 > 0) ocupacion[o2] += (viajes[f2][EST1_CANTIDAD] + viajes[f2][EST2_CANTIDAD]);
            if(est21 > 0) ocupacion[est21] -= viajes[f2][EST1_CANTIDAD];
            if(est22 > 0) ocupacion[est22] -= viajes[f2][EST2_CANTIDAD];
        }
        
        //Intercambio de estaciones
        int aux = viajes[f1][est1];
        viajes[f1][est1] = viajes[f2][est2];
        viajes[f2][est2] = aux;

        //Recalcula todo por simplicidad
        o1    = viajes[f1][ORIGEN];
        o2    = viajes[f2][ORIGEN];
        est11 = viajes[f1][EST1];
        est12 = viajes[f1][EST2];
        est21 = viajes[f2][EST1];
        est22 = viajes[f2][EST2];

        
        int dem11 = demand(est11); int dem12 = demand(est12);
        distributeBycicles(f1, o1, dem11, dem12);

        if(o1 > 0) ocupacion[o1] -= (viajes[f1][EST1_CANTIDAD] + viajes[f1][EST2_CANTIDAD]);
        if(est11 > 0) ocupacion[est11] += viajes[f1][EST1_CANTIDAD];
        if(est12 > 0) ocupacion[est12] += viajes[f1][EST2_CANTIDAD];

        //Swapea dest1 y dest2 de f1 si se cumplen ciertas condiciones
        swapIfBad(f1, o1, est11, est12);

        if(f1 != f2) //Si fueran la misma se actualizaria dos veces
        {
            //Hay que esperar a que se actualicen las cosas de la otra furgoneta por si vamos a la misma estacion en algun viaje
            int dem21 = demand(est21); int dem22 = demand(est22);
            distributeBycicles(f2, o2, dem21, dem22);

            if(o2 > 0) ocupacion[o2] += (viajes[f2][EST1_CANTIDAD] + viajes[f2][EST2_CANTIDAD]);
            if(est21 > 0) ocupacion[est21] -= viajes[f2][EST1_CANTIDAD];
            if(est22 > 0) ocupacion[est22] -= viajes[f2][EST2_CANTIDAD];

            //Swapea dest1 y dest2 de f2 si se cumplen ciertas condiciones
            swapIfBad(f2, o2, est21, est22);
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
        if(intended%10 <= REDONDEO) return intended-intended%10;
        return intended;
    }

    /*
     * Se lleva un número entre el máximo de bicicletas posibles y takenBycicles() y las asigna de forma que la primera estación se llena todo lo posible primero
     * 
     * Motivos: 
     * 1. A priori parece arbitrario
     * 2. Se viaja mas rato con menos bicicletas
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
     * Hace swap de est1 y est2 de una furgoneta si est2 esta mas cerca de origen que est1 o si est1 es -1
     */
    private void swapIfBad(int f, int origen, int est1, int est2)
    {
        if(est1 < 0 && est2 > 0)
        {
            swapEst(f, f, est1, est2);
        }
        else if(origen > 0 && est1 > 0 && est2 > 0)
        {
            int x1 = estaciones.get(est1).getCoordX();
            int y1 = estaciones.get(est1).getCoordY();
            int x2 = estaciones.get(est2).getCoordX();
            int y2 = estaciones.get(est2).getCoordY();

            int x0 = estaciones.get(origen).getCoordX();
            int y0 = estaciones.get(origen).getCoordY();

            if(distance(x0, y0, x1, y1) > distance(x0, y0, x2, y2)){
                swapEst(f, f, est1, est2);
            }
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
     * Devuelve la distancia en metros
     */
    int distance(int x1, int y1, int x2, int y2)
    {
        return (Math.abs(x1-x2)+Math.abs(y1-y2));
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
