import java.io.BufferedWriter;
import java.io.FileWriter;

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
public class TesterExp4
{
    private static final int NUM_EST_INI = 25;

    private static final int ITERS = 4;
    private static final int SEEDS_PER_ITER = 50;

    private static final int NUM_SEEDS = SEEDS_PER_ITER * ITERS;
    private static int seeds[];

    public static void main(String args[]) throws Exception
    {   
        try 
        {
            initVars();
            String modos[] = { "Change", "ChangeSwap", "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange3SwapAdd", "ChangeChange2Change3SwapAdd", "ChangeChange2Change3Swap"};

            int tipoDemanda = Estaciones.EQUILIBRIUM;

            System.out.println("Iniciando test...");

            String filePath = "./R/exp4.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("estaciones\ttiempo\n");

            for(int i = 0; i < ITERS; ++i) {

                //Inicialización estaciones ( en este caso, solo una vez )
                int numEstaciones = NUM_EST_INI*(i+1);
                int numBicis = numEstaciones*50;
                int maxFurgonetas = numEstaciones/5;
                
                for (int j = 0; j < SEEDS_PER_ITER; ++j) {
                    printProgreso(i*SEEDS_PER_ITER+j);

                    int seed = seeds[i];
                    Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                    //Búsqueda Hill Climbing
                    PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                    setOperadores(successorFunction,modos[3]);

                    PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.GREEDY2;
                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.setRedondeo(0);
                    board.creaSolucionInicial(tipoSol);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1));

                    Search alg = new HillClimbingSearch();

                    long startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    long endTime = System.nanoTime();
                    PracBoard hcBoard = (PracBoard)alg.getGoalState();

                    bufferedWriter.write(numEstaciones + "\t" + (endTime-startTime)/1000000 + "\n");
                }
            }

            bufferedWriter.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


        
    }

    static private void initVars()
    {
        seeds = new int[NUM_SEEDS];

        for(int i = 0; i < seeds.length; ++i)
        {
            seeds[i] = i*3;
        }
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