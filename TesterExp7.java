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

public class TesterExp7 
{
    private static final int FURGO_INICIALES = 5;
    private static final int FURGO_MAXIMAS = 20;
    private static final int INC_VAL = 1;

    private static final int NUM_SEEDS = 100;
    private static final int PRUEBAS_RANDOM = 5;
    private static final PracHeuristicFunction.Function HEUR = PracHeuristicFunction.Function.Heuristico_2;
    private static int seeds[];

    public static void main(String args[]) throws Exception
    {
        try 
        {
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.RANDOM;
            String modos[] = { "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd" };
            initVars();

            String filePath = "./R/exp7.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("demanda\tfurgonetas\tcalidad\tbeneficio\n");
            
            int[] tipoDemanda = new int[] {Estaciones.EQUILIBRIUM, Estaciones.RUSH_HOUR};
            String[] demandName = new String[] {"equilibrada","horaPunta"};
            int numEstaciones = 25;
            int numBicis = 1250;

            //Enum para decir que heuristico usar
            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            setOperadores(successorFunction,modos[0]);

            for (int k = 0; k < 2; ++k) {

                System.out.println("Demanda "+ demandName[k]);

                for(int i = FURGO_INICIALES; i <= FURGO_MAXIMAS; i += INC_VAL) {
                    int maxFurgonetas = i;
                    
                    System.out.println("Numero de furgonetas: "+i);
                    for(int j = 0; j < NUM_SEEDS; ++j) {
                        printProgreso(j);
                        //Inicialización estaciones
                        int seed = seeds[j];

                        Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda[k], seed);

                        double calidad = 0.0;
                        double benefReal = 0.0;
                        double heuristico = 100000.0;
                        Random random = new Random(seed);

                        for(int l = 0; l < PRUEBAS_RANDOM; ++l)
                        {
                            int solSeed = random.nextInt();
                            //Búsqueda Hill Climbing
                            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                            board.creaSolucionInicial(tipoSol,solSeed);

                            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

                            Search alg = new HillClimbingSearch();

                            SearchAgent agent = new SearchAgent(p, alg);
                            PracBoard hcBoard = (PracBoard)alg.getGoalState();

                            double heur = hcBoard.heuristicFunction(HEUR);
                            if(heur < heuristico)
                            {
                                calidad = hcBoard.beneficioTotal(false);
                                benefReal = hcBoard.getBeneficioReal();
                                heuristico = heur;
                            }
                        }

                        bufferedWriter.write(demandName[k] + "\t" + maxFurgonetas + "\t" + calidad + "\t" + benefReal + "\n");
                    }
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
}