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

public class TesterExp1 
{
    private static final int NUM_SEEDS = 100;
    private static final int PRUEBAS_RANDOM = 5;
    private static int seeds[];
    private static double mediaPorTipo[];
    private static double mediaRealPorTipo[];
    private static double distPorTipo[];
    private static double tiempoPorTipo[];

    public static void main(String args[]) throws Exception
    {
        try 
        {
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.RANDOM;
            String modos[] = { "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd" };
            initVars(modos.length);

            String filePath = "./R/exp1.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("operadores\tcalidad\tbeneficio\ttiempo\n");

            //Para que la primera ejecución no tenga un tiempo mucho mayor que el resto
            cargaEnCache();
            
            for(int j = 0; j < modos.length; ++j) //Itera tipos de solución
            {
                System.out.println(modos[j] + ":");
                double beneficioH1Total = 0.0;
                double beneficioH2Total = 0.0;
                double distanciaTotal = 0.0;
                double tiempoTotal = 0.0;
                for(int i = 0; i < seeds.length; ++i)
                {
                    printProgreso(i);
                    int seed = seeds[i];
                    Random random = new Random(seed);

                    //Inicialización estaciones ( en este caso, solo una vez )
                    int numEstaciones = 25;
                    int numBicis = 1250;
                    int maxFurgonetas = 5;
                    int tipoDemanda = Estaciones.EQUILIBRIUM;
                    Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                    double tiempoSeed = 0.0;
                    double heurMin = 100000.0;
                    double calidadMax = 0.0;
                    double beneficioMax = 0.0;
                    double distanciaMax = 0.0;

                    for(int k = 0; k < PRUEBAS_RANDOM; ++k)
                    {
                        //Búsqueda Hill Climbing

                        //Enum para decir que heuristico usar
                        PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                        setOperadores(successorFunction,modos[j]);

                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);

                        board.creaSolucionInicial(tipoSol,random.nextInt());

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_2));

                        Search alg = new HillClimbingSearch();

                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();
                        PracBoard hcBoard = (PracBoard)alg.getGoalState();

                        /*
                        System.out.println("Combinacion operadores: " + modos[j]);
                        System.out.println("Seed: " + seeds[i]);
                        System.out.println("Beneficio H1: " + hcBoard.beneficioTotal(false));
                        System.out.println("Beneficio H2: " + hcBoard.getBeneficioReal());
                        System.out.println("Distancia: " + hcBoard.getTotalTravelDist());
                        System.out.println();
                        */
                        double calidad = hcBoard.beneficioTotal(false);
                        double benefReal = hcBoard.getBeneficioReal();
                        double travelDist = hcBoard.getTotalTravelDist();
                        double tiempo = (endTime-startTime);
                        double heur = hcBoard.heuristicFunction1();
                        tiempoSeed += tiempo;

                        if(heur < heurMin)
                        {
                            beneficioMax = benefReal;
                            calidadMax = calidad;
                            distanciaMax = travelDist;
                            heurMin = heur;
                        }
                    }
                    beneficioH1Total += calidadMax;
                    beneficioH2Total += beneficioMax;
                    distanciaTotal += distanciaMax;
                    tiempoTotal += tiempoSeed;

                    bufferedWriter.write(modos[j] + "\t" + calidadMax + "\t" + beneficioMax + "\t" + tiempoSeed/1000000 + "\n");
                }
                mediaPorTipo[j] = beneficioH1Total/seeds.length;
                mediaRealPorTipo[j] = beneficioH2Total/seeds.length;
                distPorTipo[j] = distanciaTotal/seeds.length;
                tiempoPorTipo[j] = (tiempoTotal/seeds.length)/1000000.0; //A ms
            }

            System.out.println("Numero de seeds: " + NUM_SEEDS);
            System.out.println();

            for(int i = 0; i < mediaPorTipo.length; ++i)
            {
                System.out.println(modos[i] + ": ");
                System.out.println("Beneficio medio H1: " + mediaPorTipo[i]);
                System.out.println("Beneficio medio real: " + mediaRealPorTipo[i]);
                System.out.println("Distancia media: " + distPorTipo[i]);
                System.out.println("Tiempo medio: " + tiempoPorTipo[i] + "ms");
                System.out.println();
            }

            bufferedWriter.close();
        } 
        catch (IOException e) {
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
        tiempoPorTipo = new double[size];
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

    static private void setOperadores(PracSuccessorFunction successorFunction, String ops)
    {
        //"Change", "ChangeSwap", "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange3SwapAdd", "ChangeChange2Change3SwapAdd", "ChangeChange2Change3Swap"
        successorFunction.enableAllOperators();
        if(ops == "Change")
        {
            successorFunction.disableAddVan();
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();
            successorFunction.disableSwapEst();
        }
        else if(ops == "ChangeSwap")
        {
            successorFunction.disableAddVan();
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();
        }
        else if(ops == "ChangeSwapAdd")
        {
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();
        }
        else if(ops == "ChangeChange2SwapAdd")
        {
            successorFunction.disableChange3Est();
        }
        else if(ops == "ChangeChange3SwapAdd")
        {
            successorFunction.disableChange2Est();
        }
        else if(ops == "ChangeChange2Change3SwapAdd")
        {

        }
        else if(ops == "ChangeChange2Change3Swap")
        {
            successorFunction.disableAddVan();
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
            setOperadores(successorFunction,"ChangeSwapAdd");

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(PracBoard.TipoSolucion.RANDOM,seed);

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1));

            Search alg = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(p, alg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}