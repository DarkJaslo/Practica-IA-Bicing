import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import IA.Bicing.Estaciones;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

/*
 * Clase de ejemplo para un tester.
 */
public class TesterExp2
{
    private static final int NUM_SEEDS = 100;
    private static final int PRUEBAS_RANDOM = 5;
    private static int seeds[];
    private static double mediaPorTipo[];
    private static double mediaRealPorTipo[];
    private static double distPorTipo[];

    private static final PracSuccessorFunction.SearchType algoritmo = PracSuccessorFunction.SearchType.HillClimbing;
    private static final PracHeuristicFunction.Function HEUR = PracHeuristicFunction.Function.Heuristico_1;
    private static final String RESULTADO_RANDOM = "media"; //valores: media, maximo
    public static void main(String args[]) throws Exception
    {
        try 
        {
            //IMPORTANTE! LA SOLUCION RANDOM DEBE SER LA ULTIMA DEL ARRAY
            PracBoard.TipoSolucion tiposSol[] = {PracBoard.TipoSolucion.VACIA, PracBoard.TipoSolucion.GREEDY2, PracBoard.TipoSolucion.RANDOM};
            String nombresTiposSol[] = {"Vacia", "Greedy", "Random"};
            initVars(tiposSol.length);

            String filePath = "./R/exp2.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("sol_ini\tcalidad\tbeneficio\ttiempo\n");
            
            //Para que la primera ejecución no tenga un tiempo mucho mayor que el resto
            cargaEnCache();

            for(int j = 0; j < tiposSol.length-1; ++j) //Itera tipos de solución
            {
                System.out.println(nombresTiposSol[j] + ":");
                double beneficioH1Total = 0.0;
                double beneficioH2Total = 0.0;
                double distanciaTotal = 0.0;
                for(int i = 0; i < seeds.length; ++i)
                {
                    printProgreso(i);

                    //Inicialización estaciones ( en este caso, solo una vez )
                    int numEstaciones = 25;
                    int numBicis = 1250;
                    int maxFurgonetas = 5;
                    int tipoDemanda = Estaciones.EQUILIBRIUM;
                    int seed = seeds[i];

                    Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                    //Búsqueda Hill Climbing

                    //Enum para decir que heuristico usar
                    PracBoard.TipoSolucion tipoSol = tiposSol[j];

                    //Búsqueda Hill Climbing

                    //Enum para decir que heuristico usar
                    PracSuccessorFunction successorFunction = new PracSuccessorFunction(algoritmo);
                    successorFunction.disableChange2Est();
                    successorFunction.disableChange3Est();

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.creaSolucionInicial(tipoSol,seed);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

                    Search alg = new HillClimbingSearch();
                    
                    double startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    double endTime = System.nanoTime();

                    PracBoard hcBoard = (PracBoard)alg.getGoalState();

                    double calidad = hcBoard.beneficioTotal(false);
                    double beneficio = hcBoard.getBeneficioReal();
                    double tiempo = (endTime-startTime);

                    beneficioH1Total += calidad;
                    beneficioH2Total += beneficio;
                    distanciaTotal += hcBoard.getTotalTravelDist();

                    bufferedWriter.write(nombresTiposSol[j] + "\t" + calidad + "\t" + beneficio + "\t" + tiempo/1000000 + "\n");
                }
                mediaPorTipo[j] = beneficioH1Total/seeds.length;
                mediaRealPorTipo[j] = beneficioH2Total/seeds.length;
                distPorTipo[j] = distanciaTotal/seeds.length;
            }

            for(int j = tiposSol.length-1; j < tiposSol.length; ++j)
            {
                System.out.println(nombresTiposSol[j] + ":");
                double calidadTotal = 0.0;
                double beneficioTotal = 0.0;
                double distanciaTotal = 0.0;
                
                for(int i = 0; i < seeds.length; ++i)
                {
                    int seed = seeds[i];
                    Random random = new Random(seed);

                    double calidadFinal = 0.0;
                    double beneficioFinal = 0.0;
                    double distanciaFinal = 0.0;
                    double tiempoTotal = 0.0;
                    double heurMin = 100000.0;

                    printProgreso(i);

                    //Inicialización estaciones ( en este caso, solo una vez )
                    int numEstaciones = 25;
                    int numBicis = 1250;
                    int maxFurgonetas = 5;
                    int tipoDemanda = Estaciones.EQUILIBRIUM;

                    Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                    for(int k = 0; k < PRUEBAS_RANDOM; ++k)
                    {
                        int seedSolIni = random.nextInt();

                        //Búsqueda Hill Climbing

                        //Enum para decir que heuristico usar
                        PracBoard.TipoSolucion tipoSol = tiposSol[j];

                        //Búsqueda Hill Climbing

                        //Enum para decir que heuristico usar
                        PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                        successorFunction.disableChange2Est();
                        successorFunction.disableChange3Est();

                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                        board.creaSolucionInicial(tipoSol,seedSolIni);

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

                        Search alg = new HillClimbingSearch();
                        
                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();

                        PracBoard hcBoard = (PracBoard)alg.getGoalState();

                        double calidad = hcBoard.beneficioTotal(false);
                        double beneficio = hcBoard.getBeneficioReal();
                        double tiempo = (endTime-startTime);
                        
                        if (RESULTADO_RANDOM == "maximo")
                        {
                            tiempoTotal += tiempo;
                            double heur = 0.0;
                            if(HEUR == PracHeuristicFunction.Function.Heuristico_1) 
                                heur = hcBoard.heuristicFunction1();
                            else heur = hcBoard.heuristicFunction2();

                            if(heur < heurMin)
                            {
                                heurMin = heur;
                                calidadFinal = calidad;
                                beneficioFinal = beneficio;
                                distanciaFinal = hcBoard.getTotalTravelDist();
                            }
                        }
                        else if(RESULTADO_RANDOM == "media") {
                            tiempoTotal += tiempo;
                            calidadFinal += calidad;
                            beneficioFinal += beneficio;
                            distanciaFinal += hcBoard.getTotalTravelDist();
                        }
                    }
                    if(RESULTADO_RANDOM == "media")
                    {
                        tiempoTotal /= PRUEBAS_RANDOM;
                        calidadFinal /= PRUEBAS_RANDOM;
                        beneficioFinal /= PRUEBAS_RANDOM;
                        distanciaFinal /= PRUEBAS_RANDOM;
                    }
                    calidadTotal += calidadFinal;
                    beneficioTotal += beneficioFinal;
                    distanciaTotal += distanciaFinal;

                    bufferedWriter.write(nombresTiposSol[j] + "\t" + calidadFinal + "\t" + beneficioFinal + "\t" + tiempoTotal/1000000 + "\n");
                }
                mediaPorTipo[j] = calidadTotal/seeds.length;
                mediaRealPorTipo[j] = beneficioTotal/seeds.length;
                distPorTipo[j] = distanciaTotal/seeds.length;            
            }

            System.out.println("Numero de seeds: " + NUM_SEEDS);
            System.out.println();

            for(int i = 0; i < mediaPorTipo.length; ++i)
            {
                System.out.println(nombresTiposSol[i] + ": ");
                System.out.println("Calidad media: " + mediaPorTipo[i]);
                System.out.println("Beneficio medio: " + mediaRealPorTipo[i]);
                System.out.println("Distancia media: " + distPorTipo[i]);
                System.out.println();
            }

            bufferedWriter.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    static private void initVars(int size)
    {
        seeds = new int[NUM_SEEDS];

        for(int i = 0; i < seeds.length; ++i)
        {
            seeds[i] = i*3;
        }

        mediaPorTipo = new double[size];
        mediaRealPorTipo = new double[size];
        distPorTipo = new double[size];
    }

    static private void printProgreso(int it)
    {
        int valores[] = {(2*NUM_SEEDS)/10, (4*NUM_SEEDS)/10, (6*NUM_SEEDS)/10,(8*NUM_SEEDS)/10};
        for(int i = 0; i < valores.length; ++i)
        {
            if(it == valores[i])
            {
                System.out.println("Progreso: " + (i+1)*2 + "0%");
            }
        }
    }

    static private void cargaEnCache() throws Exception {
        try
        {
            int seed = -1;
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;

            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, Estaciones.EQUILIBRIUM, seed);

            //Búsqueda Hill Climbing

            //Enum para decir que heuristico usar
            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(PracBoard.TipoSolucion.VACIA,seed);

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

            Search alg = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(p, alg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}