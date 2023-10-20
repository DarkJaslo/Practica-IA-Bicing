package practica;
import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Math;
import java.util.Random;
import java.util.Arrays;

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
    private int REDONDEO = 0;

    //Multilica al beneficio en la función heurística
    private static final double FACTOR_HEURISTICO = 1.0;

    public static enum TipoSolucion{ VACIA, NORMAL, NORMAL_RANDOM, GREEDY }

    private class StationDemand implements Comparable<StationDemand> {
        public int id;      //Identificador de la estación
        public int demand;  //Demanda de dicha estación

        public StationDemand(int id, int demand) {
            this.id = id;
            this.demand = demand;
        }

        @Override
        public int compareTo(StationDemand other) {
            if (demand < other.demand) return -1;
            if (demand > other.demand) return 1;
            return 0;
        }
    }

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
     * Furgonetas usadas en esta solución actualmente
     */
    private int furgEnUso;

    /*
     * Constructora
     */
    public PracBoard(Estaciones estaciones, int maxFurgonetas){
        PracBoard.estaciones = estaciones;
        PracBoard.maxFurgonetas = maxFurgonetas;
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
        for(int i = 0; i < ocupacion.length; ++i)
        {
            ocupacion[i] = 0;
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
        //False si newEst se usa de origen en alguna furgoneta
        if(ocupacion[newEst] < 0) return false; 
        //False si se quiere poner en origen una estacion a la que llevamos bicicletas
        if(whichEst == ORIGEN && ocupacion[newEst] > 0) return false;
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

        if(origen >= 0) ocupacion[origen] += (viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD]);
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
        if(origen >= 0) ocupacion[origen] -= (viajes[f][EST1_CANTIDAD]+viajes[f][EST2_CANTIDAD]);
        if(est1 >= 0)   ocupacion[est1] += viajes[f][EST1_CANTIDAD];
        if(est2 >= 0)   ocupacion[est2] += viajes[f][EST2_CANTIDAD];

        
        //swapIfBad(f, origen, est1, est2);
    }

    /*
     * Swap de estaciones entre furgonetas
     */
    public boolean canSwapEst(int f1, int f2, int whichEst1, int whichEst2)
    {   
        //Si una es de origen, ambas deben ser de origen
        if((whichEst1 == ORIGEN || whichEst2 == ORIGEN) && whichEst1 != whichEst2) return false;
        //Si son la misma furgoneta y se quieren cambiar la misma estación
        else if(f1 == f2 && whichEst1 == whichEst2) return false;
        //Si se quiere poner estación nula en la posición 1 (no válido)
        else if(whichEst1 != whichEst2 && (viajes[f1][whichEst1] < 0 || viajes[f2][whichEst2] < 0)) return false;
        return true;
    }
    public void swapEst(int f1, int f2, int whichEst1, int whichEst2)
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

        if(o1 >= 0) ocupacion[o1] += (viajes[f1][EST1_CANTIDAD] + viajes[f1][EST2_CANTIDAD]);
        if(est11 >= 0) ocupacion[est11] -= viajes[f1][EST1_CANTIDAD];
        if(est12 >= 0) ocupacion[est12] -= viajes[f1][EST2_CANTIDAD];

        if(f1 != f2) //Si fueran la misma se actualizaria dos veces
        {
            if(o2 >= 0) ocupacion[o2] += (viajes[f2][EST1_CANTIDAD] + viajes[f2][EST2_CANTIDAD]);
            if(est21 >= 0) ocupacion[est21] -= viajes[f2][EST1_CANTIDAD];
            if(est22 >= 0) ocupacion[est22] -= viajes[f2][EST2_CANTIDAD];
        }
        
        //Intercambio de estaciones
        int aux = viajes[f1][whichEst1];
        viajes[f1][whichEst1] = viajes[f2][whichEst2];
        viajes[f2][whichEst2] = aux;

        //Recalcula todo por simplicidad
        o1    = viajes[f1][ORIGEN];
        o2    = viajes[f2][ORIGEN];
        est11 = viajes[f1][EST1];
        est12 = viajes[f1][EST2];
        est21 = viajes[f2][EST1];
        est22 = viajes[f2][EST2];

        
        int dem11 = demand(est11); int dem12 = demand(est12);
        distributeBycicles(f1, o1, dem11, dem12);

        if(o1 >= 0) ocupacion[o1] -= (viajes[f1][EST1_CANTIDAD] + viajes[f1][EST2_CANTIDAD]);
        if(est11 >= 0) ocupacion[est11] += viajes[f1][EST1_CANTIDAD];
        if(est12 >= 0) ocupacion[est12] += viajes[f1][EST2_CANTIDAD];

        //Swapea dest1 y dest2 de f1 si se cumplen ciertas condiciones
        //swapIfBad(f1, o1, est11, est12);

        if(f1 != f2) //Si fueran la misma se actualizaria dos veces
        {
            //Hay que esperar a que se actualicen las cosas de la otra furgoneta por si vamos a la misma estacion en algun viaje
            int dem21 = demand(est21); int dem22 = demand(est22);
            distributeBycicles(f2, o2, dem21, dem22);

            if(o2 >= 0) ocupacion[o2] -= (viajes[f2][EST1_CANTIDAD] + viajes[f2][EST2_CANTIDAD]);
            if(est21 >= 0) ocupacion[est21] += viajes[f2][EST1_CANTIDAD];
            if(est22 >= 0) ocupacion[est22] += viajes[f2][EST2_CANTIDAD];

            //Swapea dest1 y dest2 de f2 si se cumplen ciertas condiciones
            //swapIfBad(f2, o2, est21, est22);
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
        else if((dest1 >= 0 && ocupacion[dest1] < 0) || (dest2 >= 0 && ocupacion[dest2] < 0)) return false;
        return true;
    }
    public void addVan(int origen, int dest1, int dest2)
    {
        viajes[furgEnUso][ORIGEN] = origen;
        viajes[furgEnUso][EST1] = dest1;
        viajes[furgEnUso][EST2] = dest2;

        distributeBycicles(furgEnUso, origen, demand(dest1), demand(dest2));
        ocupacion[origen] -= (viajes[furgEnUso][EST1_CANTIDAD]+viajes[furgEnUso][EST2_CANTIDAD]);
        ocupacion[dest1] += viajes[furgEnUso][EST1_CANTIDAD];
        if(dest2 >= 0) ocupacion[dest2] += viajes[furgEnUso][EST2_CANTIDAD];
        ++furgEnUso;
    }

    public boolean canChange2Est(int f, int whichEst1, int whichEst2, int newEst1, int newEst2)
    {
        //No se puede cambiar la misma estacion de la furgoneta dos veces
        if(whichEst1 == whichEst2) return false;
        //No se puede asignar la misma estacion dos veces
        if(newEst1 == newEst2) return false;
        //newEst1 o 2 se usan de origen en alguna furgoneta
        if(ocupacion[newEst1] < 0) return false;
        if(ocupacion[newEst2] < 0) return false;
        //False si se quiere poner en origen una estacion a la que llevamos bicicletas
        if(whichEst1 == ORIGEN && ocupacion[newEst1] > 0) return false;
        if(whichEst2 == ORIGEN && ocupacion[newEst2] > 0) return false;

        //False si queremos cambiar una estacion por ella misma
        if(viajes[f][whichEst1] == newEst1) return false;
        if(viajes[f][whichEst1] == newEst2) return false;
        if(viajes[f][whichEst2] == newEst2) return false;
        if(viajes[f][whichEst2] == newEst1) return false;
        
        return true;
    }
    public void change2Est(int f, int whichEst1, int whichEst2, int newEst1, int newEst2)
    {
        changeEst(f, whichEst1, newEst1);
        changeEst(f, whichEst2, newEst2);
    }

    public boolean canChange3Est(int f, int newOrigen, int newDest1, int newDest2)
    {
        //No se pueden poner repetidos
        if(newOrigen == newDest1 || newOrigen == newDest2 || newDest1 == newDest2) return false;
        //Si alguna se usa de origen en alguna furgoneta
        if(ocupacion[newOrigen] < 0 || ocupacion[newDest1] < 0 || ocupacion[newDest2] < 0) return false;
        //False si se quiere poner en origen una estacion a la que llevamos bicicletas
        if(ocupacion[newOrigen] > 0) return false;

        //False si queremos cambiar una estacion por ella misma
        if(viajes[f][ORIGEN] == newOrigen) return false;
        if(viajes[f][EST1] == newDest1) return false;
        if(viajes[f][EST2] == newDest2) return false;

        //False si alguna de las estaciones esta involucrada en esta furgoneta (no verifica origen porque ya se hace con el resto)
        if(viajes[f][EST1] == newDest2) return false;
        if(viajes[f][EST2] == newDest1) return false;
        return true;
    }
    public void change3Est(int f, int newOrigen, int newDest1, int newDest2)
    {
        changeEst(f,ORIGEN,newOrigen);
        changeEst(f,EST1,newDest1);
        changeEst(f,EST2,newDest2);
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
        if(origen >= 0) 
            disponibles = getBicicletasDisponibles(origen);
        else return; //No hay origen -> no hay bicicletas para distribuir

        int cogidas = Math.min(Math.min(30, disponibles),takenBycicles(Math.max(0,dem1)+Math.max(0,dem2)));
        //No se pueden coger bicis negativas
        if(cogidas <= 0) return;

        //Intenta dejar el máximo de bicicletas en Dest1
        if(dem1 > 0)
        {
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
        else if(dem2 > 0)
        {
            viajes[f][EST1] = viajes[f][EST2];
            viajes[f][EST1_CANTIDAD] = cogidas;
            viajes[f][EST2] = -1;
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
     * Devuelve las bicicletas disponibles en una estación de origen
     */
    private int getBicicletasDisponibles(int est)
    {
        int noUsadasAhora = estaciones.get(est).getNumBicicletasNoUsadas();
        int noUsadasLuego = estaciones.get(est).getNumBicicletasNext()-estaciones.get(est).getDemanda();
        return Math.min(noUsadasAhora,  Math.max(0,noUsadasLuego));
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
     * Devuelve la distancia Manhattan en metros entre dos "Estación" cualesquiera
     */
    public int distance(Estacion e1, Estacion e2) {
        return (Math.abs(e1.getCoordX()-e2.getCoordX()) + Math.abs(e1.getCoordY()-e2.getCoordY()));
    }

    /*
     * Devuelve si el id (f) de una furgoneta existe o no
     */
    public boolean existeFurgo(int f) {
        return (f > -1 && f < maxFurgonetas);
    }

    /*
     * Devuelve si el id (est) de una estación existe o no
     */
    public boolean existeEstacion(int est) {
        return (est > -1 && est < estaciones.size());
    }

    /*
     * Devuelve la distancia recorrida por una furgoneta
     */
    public double getTravelDist(int f) {
        double dist = 0;
        if (existeFurgo(f) && existeEstacion(viajes[f][ORIGEN])) {
            //Primer viaje
            if (existeEstacion(viajes[f][EST1])) {
                dist += distance(estaciones.get(viajes[f][ORIGEN]), estaciones.get(viajes[f][EST1]));

                //Segundo viaje
                if (existeEstacion(viajes[f][EST2]))
                    dist += distance(estaciones.get(viajes[f][EST1]), estaciones.get(viajes[f][EST2]));
            }
        }
        return dist;
    }

    /*
     * Devuelve la distancia total recorrida por las furgonetas
     */
    public double getTotalTravelDist() {
        double totalDist = 0;
        for (int i = 0; i < maxFurgonetas; ++i)
            totalDist += getTravelDist(i);
        return totalDist;
    }

    /*
     * Funciones heurísticas
     */
    
    public double heuristicFunction1() 
    {
        //Maximizar cobro de transporte
        double cobro_transporte = 0;
        for (int i = 0; i < estaciones.size(); ++i) {
            int ed = estaciones.get(i).getDemanda();
            int ef = estaciones.get(i).getNumBicicletasNext();
            if (ed >= ef) cobro_transporte += Math.min(ed - ef, ocupacion[i]); //Nos hacen falta más bicis para satisfacer la demanda
            else cobro_transporte += Math.min(0, ef + ocupacion[i] - ed); //Nos sobran bicis (demanda satisfecha)
        }
        return -cobro_transporte;
    }

    public double heuristicFunction2() 
    {
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
        for (int i = 0; i < maxFurgonetas; ++i) {
            if (existeEstacion(viajes[i][ORIGEN])) {
                int bicis = viajes[i][EST1_CANTIDAD] + viajes[i][EST2_CANTIDAD];
                double dist = 0;

                //Primer viaje
                if (existeEstacion(viajes[i][EST1])) {
                    dist = distance(estaciones.get(viajes[i][ORIGEN]), estaciones.get(viajes[i][EST1]));
                    coste_transporte += (dist/1000.0) * ((Math.abs(bicis)+9)/10);
                    bicis -= viajes[i][EST1_CANTIDAD];

                    //Segundo viaje
                    if (existeEstacion(viajes[i][EST2])) {
                        dist = distance(estaciones.get(viajes[i][EST1]), estaciones.get(viajes[i][EST2]));
                        coste_transporte += (dist/1000.0) * ((Math.abs(bicis)+9)/10);
                    }
                }                
            }
        }
        //Negamos "cobro_transporte" para que ambos criterios sean mínimos
        return coste_transporte - FACTOR_HEURISTICO * cobro_transporte;
    }
    
    /*
     * Devuelve el beneficio real, es decir, beneficio por mover bicicletas de sitio menos coste de transporte
     */
    public double getBeneficioReal() 
    {
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
        for (int i = 0; i < maxFurgonetas; ++i) {
            if (existeEstacion(viajes[i][ORIGEN])) {
                int bicis = viajes[i][EST1_CANTIDAD] + viajes[i][EST2_CANTIDAD];
                double dist = 0;

                //Primer viaje
                if (existeEstacion(viajes[i][EST1])) {
                    dist = distance(estaciones.get(viajes[i][ORIGEN]), estaciones.get(viajes[i][EST1]));
                    coste_transporte += (dist/1000.0) * ((Math.abs(bicis)+9)/10);
                    bicis -= viajes[i][EST1_CANTIDAD];

                    //Segundo viaje
                    if (existeEstacion(viajes[i][EST2])) {
                        dist = distance(estaciones.get(viajes[i][EST1]), estaciones.get(viajes[i][EST2]));
                        coste_transporte += (dist/1000.0) * ((Math.abs(bicis)+9)/10);
                    }
                }                
            }
        }
        //Negamos "cobro_transporte" para que ambos criterios sean mínimos
        return cobro_transporte - coste_transporte;
    }


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
                return "origen";
            case EST1:
                return "dest1";
            case EST2:
                return "dest2";
            default:
                return "ERR";
        }
    }

    /* Setters */

    /*
     * Espera números entre 0 y 9, en particular pequeños.
     */
    public void setRedondeo(int redondeo)
    {
        REDONDEO = redondeo;
    }


    public void creaSolucionInicial(TipoSolucion tipoSolucion)
    {
        switch(tipoSolucion)
        {
            case VACIA:
                break;
            case NORMAL:
                creaSolucionBuena();
                break;
            case NORMAL_RANDOM:
                creaSolucionBuenaRandom(1234);
                break;
            case GREEDY:
                creaSolucionGreedy();
                break;
        }
    }

    public void creaSolucionInicial(TipoSolucion tipoSolucion, int seedIfRandom)
    {
        switch(tipoSolucion)
        {
            case VACIA:
                break;
            case NORMAL:
                creaSolucionBuena();
                break;
            case NORMAL_RANDOM:
                creaSolucionBuenaRandom(seedIfRandom);
                break;
            case GREEDY:
                creaSolucionGreedy2();
                break;
        }
    }

    /*Intenta utilizar todas las furgonetas siempre yendo desde una estación donde "sobren" bicis a una donde falten (y a una segunda si aun quedan) para llegar a la prediccion de la hora siguiente*/
    private void creaSolucionBuena() 
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
     * Intentaremos poner menos furgonetas de las necesarias, pero casi misma idea que creaSolucionBuena (excepto porque permite distintas opciones por ser random)
     */
    private void creaSolucionBuenaRandom(int seed)
    {
        ArrayList<Integer> estOferta = new ArrayList<Integer>();
        ArrayList<Integer> estDemanda = new ArrayList<Integer>();
        int demandaTotal = 0;
        //int ofertaTotal = 0;

        /*
         * Calcula demanda y oferta totales, asi como se guarda las estaciones que puede dar y las que piden
         */
        for(int i = 0; i < estaciones.size(); ++i)
        {
            int dem = demandStart(i);
            if(dem>0){
                demandaTotal += dem;
                estDemanda.add(i);
            }
            else if(dem<0){
                //ofertaTotal += dem;
                estOferta.add(i);
            }
        }

        //Furgonetas que usara esta solucion, seran siempre menos de las disponibles
        int nfurg = Math.min(maxFurgonetas-2,demandaTotal/30);
        Random random = new Random(seed);

        /*
         * De las estaciones con bicicletas sobrantes, escoge una random
         * Escoge dos destinos razonables (con demanda de bicicletas) de forma random tambien
         */
        for(int f = 0; f < nfurg; ++f)
        {
            int r = random.nextInt(estOferta.size());
            int orig = estOferta.get(r);
            viajes[f][ORIGEN] = orig;
            estOferta.remove(r);

            r = random.nextInt(estDemanda.size());
            int dest1 = estDemanda.get(r);
            viajes[f][EST1] = dest1;
            int dest2 = -1;
            viajes[f][EST2] = dest2;

            distributeBycicles(f, orig, demand(dest1), demand(dest2));
            ocupacion[dest1] += viajes[f][EST1_CANTIDAD];
            
            if(demand(dest1) <= 0) estDemanda.remove(r);

            if(estDemanda.size() > 0)
            {
                r = random.nextInt(estDemanda.size());
                dest2 = estDemanda.get(r);
            }
            viajes[f][EST2] = dest2;

            ocupacion[dest1] -= viajes[f][EST1_CANTIDAD];

            distributeBycicles(f, orig, demand(dest1), demand(dest2));

            if(viajes[f][EST2_CANTIDAD] == 0) 
                viajes[f][EST2] = -1;

            ocupacion[orig] -= viajes[f][EST1_CANTIDAD] + viajes[f][EST2_CANTIDAD];
            ocupacion[dest1] += viajes[f][EST1_CANTIDAD];
            if(dest2 >= 0) ocupacion[dest2] += viajes[f][EST2_CANTIDAD];

            if(dest2 >= 0 && demand(dest2) <= 0) estDemanda.remove(r);
            ++furgEnUso;
        }
    }

    /*
     * Crea la solución óptima para el primer criterio/heurístico
     */
    private void creaSolucionGreedy()
    {
        /*
         * Crear solucion buena H1 -> coger el max de bicis de la estacion con max bicis y llevarlas al sitio donde haya mas demanda posible
         */

        boolean used[] = new boolean[estaciones.size()];
        for(int i = 0; i < used.length; ++i)
            used[i] = false;
        
        /* Para cada furgoneta */
        for(int i = 0; i < maxFurgonetas; ++i)
        {
            /* Busca estacion no usada con mas bicis disponibles */
            int estMax = -1;
            int bicisMax = -1;
            for(int j = 0; j < estaciones.size(); ++j)
            {
                if(!used[j] && getBicicletasDisponibles(j) > bicisMax)
                {
                    bicisMax = getBicicletasDisponibles(j);
                    estMax = j;
                }
            }
            if(estMax == -1) break;

            used[estMax] = true;

            /* Busca estacion dest1 con mayor demanda */
            int dest1Max = -1;
            int dem1Max = 0;

            for(int j = 0; j < estaciones.size(); ++j)
            {
                if(demand(j) > dem1Max)
                {
                    dest1Max = j;
                    dem1Max = demand(j);
                }
            }
            if(dest1Max == -1) break;

            int decr =  Math.min(30,Math.min(bicisMax,dem1Max));
            ocupacion[dest1Max] += decr;

            bicisMax-= Math.min(Math.max(0,dem1Max),bicisMax);
            int dest2Max = -1;
            int dem2Max = 0;

            if(bicisMax > 0)
            {
                /* Busca otra estacion que necesite bicis */
                for(int j = 0; j < estaciones.size(); ++j)
                {
                    if(demand(j) > dem2Max)
                    {
                        dest2Max = j;
                        dem2Max = demand(j);
                    }
                }
            }

            viajes[i][ORIGEN] = estMax;
            viajes[i][EST1] = dest1Max;
            viajes[i][EST2] = dest2Max;

            ocupacion[dest1Max] -= decr;
            distributeBycicles(i, estMax, demand(dest1Max), demand(dest2Max));
            ocupacion[estMax] -= (viajes[i][EST1_CANTIDAD] + viajes[i][EST2_CANTIDAD]);
            if(dest1Max >= 0)
            {
                ocupacion[dest1Max] += viajes[i][EST1_CANTIDAD];
            } 
            if(dest2Max >= 0) ocupacion[dest2Max] += viajes[i][EST2_CANTIDAD];

            ++furgEnUso;
        }
    }    

    public void creaSolucionGreedy2() {
        ArrayList<StationDemand> stations = new ArrayList<StationDemand>();
        for (int i = 0; i < estaciones.size(); ++i) {
            stations.add(new StationDemand(i, - demand(i)));
        }

        MergeSort<StationDemand> msort = new MergeSort<StationDemand>(stations, MergeSort.SortingOrder.DECR);
        stations = msort.getResult();

        int i = 0;
        int j = stations.size() -1;
        int f = 0; //furgoneta actual

        while (i < j || i >= maxFurgonetas) {
            //Comprobamos que la situación actual sea mejorable
            //En caso contrario no nos interesa cambiarla
            if (stations.get(i).demand <= 0 || stations.get(j).demand >= 0) return; 

            int bikesOri = Math.min(30, stations.get(i).demand);
            int bikesSt1 = Math.min(- stations.get(j).demand, bikesOri);

            ocupacion[stations.get(j).id] += bikesSt1;
            
            StationDemand sd1 = stations.get(j);
            sd1.demand += bikesSt1;
            stations.set(j, sd1);

            bikesOri -= bikesSt1;

            viajes[i][ORIGEN] = stations.get(i).id;
            viajes[i][EST1]   = stations.get(j).id;
            viajes[i][EST1_CANTIDAD] += bikesSt1;

            if (stations.get(j).demand <= 0) {
                --j;
                if (bikesOri > 0) {
                    int bikesSt2 = Math.min(- stations.get(j).demand, bikesOri);

                    ocupacion[stations.get(j).id] += bikesSt2;

                    StationDemand sd2 = stations.get(j);
                    sd2.demand += bikesSt2;
                    stations.set(j, sd2);

                    bikesOri -= bikesSt2;

                    viajes[j][EST2]   = stations.get(j).id;
                    viajes[j][EST2_CANTIDAD] += bikesSt2;

                    if (stations.get(j).demand <= 0) --j;
                }
            }
            ++i;
        }
        furgEnUso = i;
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

            if(print) System.out.println("Est " + i + "\tdem ini: " + demStart + "\toferta ini: " + getBicicletasDisponibles(i) + "\tdem fin: " + demNow + "\tganancia: " + ganancia);
        }
        return beneficio;
    }

    /*
     * 
     */
    public void print()
    {
        for(int i = 0; i < furgEnUso; ++i)
        {
            System.out.println("Furgoneta " + i + ": Origen: " + viajes[i][ORIGEN] + ", dest1: " + viajes[i][EST1] + " (" + viajes[i][EST1_CANTIDAD] + ")" + ", dest2: " + viajes[i][EST2] + " (" + viajes[i][EST2_CANTIDAD] + "). Distancia recorrida: " + getTravelDist(i));
        }
        System.out.println();
    }
}
