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
public class PracBoard{
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
        viajes = new int[maxFurgonetas][5];
        
        for(int i = 0; i < maxFurgonetas; ++i){
            viajes[i][ORIGEN] = -1;
            viajes[i][EST1] = -1;
            viajes[i][EST1_CANTIDAD] = 0;
            viajes[i][EST2] = -1;
            viajes[i][EST2_CANTIDAD] = 0;
        }
    }

    public static PracBoard copyOf(PracBoard b)
    {
        PracBoard aux = new PracBoard(b.getEstaciones(), b.getMaxFurgonetas());
        for(int i = 0; i < b.ocupacion.length; ++i)
        {
            aux.ocupacion[i] = b.ocupacion[i];
        }
        for(int i = 0; i < b.viajes.length; ++i)
        {
            aux.viajes[i][ORIGEN]        = b.viajes[i][ORIGEN];
            aux.viajes[i][EST1]          = b.viajes[i][EST1];
            aux.viajes[i][EST1_CANTIDAD] = b.viajes[i][EST1_CANTIDAD];
            aux.viajes[i][EST2]          = b.viajes[i][EST2];
            aux.viajes[i][EST2_CANTIDAD] = b.viajes[i][EST2_CANTIDAD];
        }
        aux.furgEnUso = b.furgEnUso;
        return aux;
    }

    /* Operadores */

    /*  
     * Hacer intercambios de estaciones entre ellas
     * Cambiar una estacion de una furgoneta
     * Añadir una furgoneta
     * 
     * La idea es que si se añade una furgoneta es porque es muy bueno hacerlo y dejamos que sea el algoritmo quien se encarga de hacer una solucion mejor
     * 
     * Requisitos para las soluciones iniciales:
     * 
     * -Que usen menos o tantas furgonetas como una "solucion optima"
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
        //Demadiadas furgonetas en uso si añadimos
        if(furgEnUso >= maxFurgonetas) return false;
        //Si se añade una furgoneta incorrecta (que repite estaciones)
        else if(origen == dest1 || origen == dest2 || dest1 == dest2) return false;
        //Estacion de origen usada ya
        else if(ocupacion[origen] < 0) return false;
        //Alguno de los destinos es origen de otra furgoneta
        else if(ocupacion[dest1] < 0 || ocupacion[dest2] < 0) return false;
        return true;
    }
    public void addVan(int origen, int dest1, int dest2)
    {
        viajes[furgEnUso][ORIGEN] = origen;
        viajes[furgEnUso][EST1] = dest1;
        viajes[furgEnUso][EST2] = dest2;

        distributeBycicles(furgEnUso, origen, demand(dest1), demand(dest2));
        ++furgEnUso;
    }

    /*  Funciones auxiliares  */


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
            swapEst(f, f, EST1, EST2);
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
                swapEst(f, f, EST1, EST2);
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
     * Devuelve las bicicletas que hacia falta traer a la estacion al principio
     */
    private int demandStart(int est)
    {
        if(est < 0) return 0;
        return (estaciones.get(est).getDemanda()-estaciones.get(est).getNumBicicletasNext());
    }

    /*
     * Devuelve la distancia Manhattan en metros del punto (x1,y1) al punto (x2,y2)
     */
    int distance(int x1, int y1, int x2, int y2)
    {
        return (Math.abs(x1-x2)+Math.abs(y1-y2));
    }

    /*
     * Funciones heurísticas
     */
    public double heuristicFunction(){
        //return 0.0;
        return -beneficioTotal(false);
    }

    

    //(Comentado hasta que esté arreglado y compile)

    /*
    public double heuristicFunction1Hector() {
        //Maximizar cobro de transporte
        double cobro_transporte = 0;
        for (int i = 0; i < estaciones.size(); ++i) {
            int ed = estaciones.get(i).getDemanda();
            int ef = estaciones.get(i).getNumBicicletasNext();
            if (ed >= ef) cobro_transporte += Math.min(ed - ef, ocupacion[i]); //Nos hacen falta más bicis para satisfacer la demanda
            else cobro_transporte += Math.min(0, ef + ocupacion[i] - ed); //Nos sobran bicis (demanda satisfecha)
        }
        return cobro_transporte;
    }

    public double heuristicFunction2Hector() {
        //Maximizar cobro de transporte
        double cobro_transporte = 0;
        for (int i = 0; i < estaciones.size(); ++i) {
            int ed = estaciones.get(i).getDemanda();
            int ef = estaciones.get(i).getNumBicicletasNext();
            if (ed >= ef) cobro_transporte += Math.min(ed - ef, ocupacion[i]); //Nos hacen falta más bicis para satisfacer la demanda
            else cobro_transporte += Math.min(0, ef + ocupacion[i] - ed); //Nos sobran bicis (demanda satisfecha)
        }

        //Minimizar coste de transporte
        double coste_transporte = 0;
        for (int i = 0; i < viajes.size(); ++i) {
            int bicis = viajes[i][EST1_CANTIDAD] + viajes[i][EST2_CANTIDAD];
            
            for (int j = 1; j < viajes[i].size(); j += 2) {
                if (viajes[i][j] > -1) { //Hemos asignado a la furgo un destino (asumimos que tiene una estación origen distinta al destino)
                    double dist = distance(estaciones.get(viajes[i][j-2]).getCoordX(), estaciones.get(viajes[i][j-2]).getCoordY(), 
                                           estaciones.get(viajes[i][j]).getCoordX(), estaciones.get(viajes[i][j]).getCoordY());
                    coste_transporte += (dist/1000.0) * ((Math.abs(bicis)+9)/10);
                    bicis -= viajes[i][j+1];
                }
            }
        }
        //Negamos "cobro_transporte" para que ambos criterios sean mínimos
        return coste_transporte - cobro_transporte;
    }
    */


    /* Getters */
    
    public int[] getOcupacion(){
        return ocupacion;
    }

    public int[][] getViajes(){
        return viajes;
    }

    public int getFurgonetasEnUso(){
        return furgEnUso;
    }

    public Estaciones getEstaciones(){
        return estaciones;
    }

    public int origen(){
        return ORIGEN;
    }

    public int destino1(){
        return EST1;
    }
    
    public int destino2(){
        return EST2;
    }

    public int getMaxFurgonetas(){
        return maxFurgonetas;
    }

    public String getNombreEstacion(int whichEst)
    {
        switch(whichEst){
            case ORIGEN:
                return "ORIGEN";
            case EST1:
                return "EST1";
            case EST2:
                return "EST2";
            default:
                return "ERR";
        }
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

    /*
     * El transporte es gratis
     */
    public int beneficioTotal(boolean print)
    {
        int beneficio = 0;

        for(int i = 0; i < estaciones.size(); ++i)
        {
            int demStart = demandStart(i);
            int demNow = demand(i);

            /*
             * Si la demanda era 8 y ahora es 4, +4
             * Si la demanda era -8 y ahora es algo > 0, -algo>0
             * Si la demanda era -30 y es -12, da igual
             * Si la demanda era 16 y ahora es -14, +16
             */

            int ganancia = 0;

            if(demStart > 0)
            {
                if(demNow <= 0)
                    ganancia = demStart;
                else 
                    ganancia= demStart-demNow;
            }
            else //if demStart <= 0
                if(demNow > 0) ganancia = -demNow;   
                
            beneficio += ganancia;

            if(print) System.out.println("Estacion " + i + ", demanda inicial: " + demStart + ", demanda final: " + demNow + ", ganancia: " + ganancia);
        }

        return beneficio;
    }
}
