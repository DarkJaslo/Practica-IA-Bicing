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

public class TesterExp7 
{
    private static final int FURGO_INICIALES = 5;
    private static final int NUM_ITERS = 50;
    private static final int INC_VAL = 1;

    private static final int NUM_SEEDS = 1000;
    private static int seeds[];

    public static void main(String args[]) throws Exception
    {
        try 
        {
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.GREEDY2;
            String modos[] = { "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd" };
            initVars();

            String filePath = "./R/exp7.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("#Problema\tEscenario\tfurgonetas\tcalidad\tbeneficio\n");
            
            int[] tipoDemanda = new int[] {Estaciones.EQUILIBRIUM, Estaciones.RUSH_HOUR};
            String[] demandName = new String[] {"Equilibrado","HoraPunta"};
            int numEstaciones = 25;
            int numBicis = 1250;

            //Enum para decir que heuristico usar
            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            setOperadores(successorFunction,modos[1]);

            for(int i = 0; i < NUM_SEEDS; ++i) {
                int maxFurgonetas = FURGO_INICIALES;
                printProgreso(i);

                for(int j = 0; j <= NUM_ITERS - FURGO_INICIALES; ++j) {
                    for (int k = 0; k < 2; ++k) {
                        //Inicialización estaciones ( en este caso, solo una vez )
                        int seed = seeds[i];

                        Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda[k], seed);

                        //Búsqueda Hill Climbing
                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                        board.setRedondeo(0);
                        board.creaSolucionInicial(tipoSol);

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1));

                        Search alg = new HillClimbingSearch();

                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();
                        PracBoard hcBoard = (PracBoard)alg.getGoalState();

                        double calidad = hcBoard.beneficioTotal(false);
                        double benefReal = hcBoard.getBeneficioReal();

                        bufferedWriter.write(i + "\t" + demandName[k] + "\t" + maxFurgonetas + "\t" + calidad + "\t" + benefReal + "\n");
                    }
                    maxFurgonetas += INC_VAL;
                }
            }
            bufferedWriter.close();
        } 
        catch (IOException e) {
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