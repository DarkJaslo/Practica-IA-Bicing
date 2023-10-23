import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import IA.Bicing.Estaciones;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

public class TesterExp6
{
    private static final int NUM_SEEDS = 1000;
    private static int seeds[];

    public static void main(String args[]) throws Exception
    {
        try 
        {
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.GREEDY2;
            String modos[] = { "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd" };
            initVars(modos.length);

            String filePath = "./R/exp6.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("demanda\t");
            bufferedWriter.write("tiempo\n");
            
            //Inicialización estaciones ( en este caso, solo una vez )
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;
            int[] Demanda = {Estaciones.EQUILIBRIUM, Estaciones.RUSH_HOUR};
            
            for(int j = 0; j < 2; ++j) {
                int tipoDemanda = Demanda[j];
                for(int i = 0; i < seeds.length; ++i)
                {
                    printProgreso(i);
                    if (j == 0) bufferedWriter.write("equilibrada\t");
                    else bufferedWriter.write("horaPunta\t");
                    int seed = seeds[i];

                    Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                    //Búsqueda Hill Climbing

                    //Enum para decir que heuristico usar
                    PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                    setOperadores(successorFunction,modos[1]);

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.setRedondeo(4);
                    board.creaSolucionInicial(tipoSol);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_2));

                    Search alg = new HillClimbingSearch();

                    double startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    double endTime = System.nanoTime();

                    double tiempo = endTime - startTime;

                    bufferedWriter.write(tiempo/1000000 + "\n");
                }
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