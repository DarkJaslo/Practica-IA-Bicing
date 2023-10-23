import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    private static final int NUM_SEEDS = 1000;
    private static int seeds[];
    private static final int    SA_TEMP = 1000000;
    private static final int    SA_ITER = 1;
    private static final int    SA_K = 1;
    private static final double SA_LAMBDA = 0.001;

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

            initVars(modos.length);

            String filePath = "./R/exp5.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("alg\theur\tcalidad\tbeneficio\tdistancia\ttiempo\n");  //RECORDAR CAMBIAR CABECERA
            
            //Inicialización estaciones ( en este caso, solo una vez )
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;
            int tipoDemanda = Estaciones.EQUILIBRIUM;

            //double results[][][] = new double[NUM_SEEDS*2][2][4];  //Para cada ejecución guardamos calidad, beneficio, distancia y tiempo
            PracSuccessorFunction successorFunction;


            //Búsqueda Hill Climbing
            successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            System.out.println("Realizando busquedas con Hill Climbing:");

            for(int i = 0; i < NUM_SEEDS; ++i) {
                printProgreso(i);

                int seed = seeds[i];
                Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                for (int j = 0; j < 2; ++j) {
                    setOperadores(successorFunction,modos[1]);

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.setRedondeo(0);
                    board.creaSolucionInicial(tipoSol);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), heuristics[j]);

                    Search alg = new HillClimbingSearch();

                    double startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    double endTime = System.nanoTime();
                    PracBoard hcBoard = (PracBoard)alg.getGoalState();
                    
                    double calidad = hcBoard.beneficioTotal(false);
                    double benefReal = hcBoard.getBeneficioReal();
                    double travelDist = hcBoard.getTotalTravelDist();
                    double tiempo = (endTime-startTime);

                    calidadMedia[j] += calidad;
                    beneficioMedio[j] += benefReal;

                    bufferedWriter.write("HC\t" + "H" + (j+1) + "\t" + calidad + "\t" + benefReal + "\t" + travelDist + "\t" + tiempo/1000000 + "\n");
                }
            }
            

            //Búsqueda Simulated Annealing
            System.out.println("Realizando busquedas con Simulated Annealing:");
            int TEMP = 1000000;
            int iter = 1;
            successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing);

            for(int i = 0; i < NUM_SEEDS; ++i) {
                printProgreso(i);

                int seed = seeds[i];
                Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                for (int j = 0; j < 2; ++j) {
                    setOperadores(successorFunction,modos[1]);

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.setRedondeo(0);
                    board.creaSolucionInicial(tipoSol);

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

                    bufferedWriter.write("HC\t" + "H" + j+1 + "\t" + calidad + "\t" + benefReal + "\t" + travelDist + "\t" + tiempo/1000000 + "\n");
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
            successorFunction.disableChangeEst();
            successorFunction.disableSwapEst();
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
}