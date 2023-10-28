import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import IA.Bicing.Estaciones;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

public class TesterExp5 
{
    private static final int NUM_SEEDS = 100;
    private static final int PRUEBAS_RANDOM = 5;
    private static int seeds[];
    private static final int    SA_TEMP = 700000;
    private static final int    SA_ITER = 1;
    private static final int    SA_K = 20;
    private static final double SA_LAMBDA = 0.1;

    public static void main(String args[]) throws Exception
    {
        try 
        {
            double calidadMedia[] = {0.0,0.0,0.0,0.0};
            double beneficioMedio[] = {0.0,0.0,0.0,0.0};

            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.GREEDY2;
            String modos[] = { "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd" };
            PracHeuristicFunction heuristics[] = {
                new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1),
                new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_2)
            };
            PracHeuristicFunction.Function HEURS[] = {PracHeuristicFunction.Function.Heuristico_1, PracHeuristicFunction.Function.Heuristico_2};

            initVars(modos.length);

            String filePath = "./R/exp5.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("alg\theur\tcalidad\tbeneficio\tdistancia\ttiempo\n");
            
            //Inicialización estaciones ( en este caso, solo una vez )
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;
            int tipoDemanda = Estaciones.EQUILIBRIUM;

            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            System.out.println("Realizando busquedas con Hill Climbing:");

            //Para que la primera ejecución no tenga un tiempo mucho mayor que el resto
            cargaEnCache();

            //Busqueda Hill Climbing
            for(int i = 0; i < NUM_SEEDS; ++i) {
                printProgreso(i);

                int seed = seeds[i];
                Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                for (int j = 0; j < 2; ++j) {
                    setOperadores(successorFunction,modos[0]);

                    double calidad = 0.0;
                    double benefReal = 0.0;
                    double travelDist = 0.0;
                    double tiempo = 0.0;
                    double heuristico = 1000000.0;

                    Random random = new Random(seed);

                    for(int k = 0; k < PRUEBAS_RANDOM; ++k)
                    {
                        int solSeed = random.nextInt();

                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    
                        board.creaSolucionInicial(tipoSol,solSeed);

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), heuristics[j]);

                        Search alg = new HillClimbingSearch();

                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();
                        PracBoard hcBoard = (PracBoard)alg.getGoalState();

                        double heur = hcBoard.heuristicFunction(HEURS[j]);
                        if(heur < heuristico)
                        {
                            calidad = hcBoard.beneficioTotal(false);
                            benefReal = hcBoard.getBeneficioReal();
                            travelDist = hcBoard.getTotalTravelDist();
                            heuristico = heur;
                        }
                        tiempo += (endTime-startTime);
                    }

                    calidadMedia[j] += calidad;
                    beneficioMedio[j] += benefReal;

                    bufferedWriter.write("HC\t" + "H" + (j+1) + "\t" + calidad + "\t" + benefReal + "\t" + travelDist + "\t" + tiempo/1000000 + "\n");
                }
            }
            

            //Búsqueda Simulated Annealing
            System.out.println("Realizando busquedas con Simulated Annealing:");
            successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing);

            for(int i = 0; i < NUM_SEEDS; ++i) {
                printProgreso(i);

                int seed = seeds[i];
                Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                for (int j = 0; j < 2; ++j) {
                    setOperadores(successorFunction,modos[1]);

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    
                    board.creaSolucionInicial(tipoSol,seed);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), heuristics[j]);

                    Search alg = new SimulatedAnnealingSearch(SA_TEMP,SA_ITER,SA_K,SA_LAMBDA);

                    double startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    double endTime = System.nanoTime();
                    PracBoard hcBoard = (PracBoard)alg.getGoalState();
                    
                    double calidad = hcBoard.beneficioTotal(false);
                    double benefReal = hcBoard.getBeneficioReal();
                    double travelDist = hcBoard.getTotalTravelDist();
                    double tiempo = (endTime-startTime);

                    calidadMedia[j+2] += calidad;
                    beneficioMedio[j+2] += benefReal;

                    bufferedWriter.write("SA\t" + "H" + (j+1) + "\t" + calidad + "\t" + benefReal + "\t" + travelDist + "\t" + tiempo/1000000 + "\n");
                }
            }
            bufferedWriter.close();

            for(int i = 0; i < 4; ++i)
            {
                calidadMedia[i] /= NUM_SEEDS;
                beneficioMedio[i] /= NUM_SEEDS;
            }

            System.out.println("HC Heur 1: " + "calidad: " + calidadMedia[0] + ", benef: " + beneficioMedio[0]);
            System.out.println("HC Heur 2: " + "calidad: " + calidadMedia[1] + ", benef: " + beneficioMedio[1]);
            System.out.println("SA Heur 1: " + "calidad: " + calidadMedia[2] + ", benef: " + beneficioMedio[2]);
            System.out.println("SA Heur 2: " + "calidad: " + calidadMedia[3] + ", benef: " + beneficioMedio[3]);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    static private void initVars(int size)
    {
        seeds = new int[NUM_SEEDS];
        for(int i = 0; i < seeds.length; ++i)
            seeds[i] = i*3;
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
            board.creaSolucionInicial(PracBoard.TipoSolucion.GREEDY2,seed);

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1));

            Search alg = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(p, alg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}