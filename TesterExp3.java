import java.io.BufferedWriter;
import java.io.FileWriter;

import IA.Bicing.Estaciones;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.SimulatedAnnealingSearch;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

/*
 * Tester para Simulated annealing
 */
public class TesterExp3 
{
    private static final int NUM_SEEDS = 100;
    private static int seeds[];
    private static final PracHeuristicFunction.Function HEUR = PracHeuristicFunction.Function.Heuristico_1;
    private static final PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.RANDOM;

    public static void main(String args[])
    {
        int K[] = {1,5,10,20,50};
        double L[] = {0.1,0.01,0.001,0.0001,0.00001};
        int TEMP = 700000;
        int iter = 1;
        initSeeds();

        try 
        {
            String filePath = "./R/exp3.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("par\tk\tlambda\tcalidad\tbeneficio\ttiempo\n");

            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;
            int tipoDemanda = Estaciones.EQUILIBRIUM;
            
            //Para que la primera ejecución no tenga un tiempo mucho mayor que el resto
            cargaEnCache(TEMP, iter, K[0], L[0]);

            for(int i = 0; i < K.length; ++i)
            {
                for(int j = 0; j < L.length; ++j)
                {
                    String identif = K[i]+"|"+L[j];
                    System.out.println(identif + "\t" + K[i] + "\t" + String.format("%.9f",L[j]));
                    for(int k = 0; k < seeds.length; ++k)
                    {
                        int seed = seeds[k];

                        Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                        //Búsqueda Simulated Annealing
                        
                        PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing);
                        successorFunction.disableChange2Est();
                        successorFunction.disableChange3Est();

                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                        board.creaSolucionInicial(tipoSol,seed);

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

                        Search alg = new SimulatedAnnealingSearch(TEMP, iter, K[i], L[j]);
                        
                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();

                        PracBoard saBoard = (PracBoard)alg.getGoalState();

                        double calidad = saBoard.beneficioTotal(false);
                        double beneficio = saBoard.getBeneficioReal();
                        double tiempo = (endTime-startTime);

                        bufferedWriter.write(identif + "\t" + K[i] + "\t" + String.format("%.9f",L[j]) + "\t" + calidad + "\t" + beneficio + "\t" + tiempo/1000000 + "\n");
                    }
                }
            }

            bufferedWriter.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    static private void initSeeds()
    {
        seeds = new int[NUM_SEEDS];

        for(int i = 0; i < seeds.length; ++i)
        {
            seeds[i] = i*3;
        }
    }  

    private static void cargaEnCache(int temp, int iter, int K, double L) throws Exception{
        try {
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;
            int tipoDemanda = Estaciones.EQUILIBRIUM;
            int seed = -1;

            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);


            //Búsqueda Hill Climbing

            //Enum para decir que heuristico usar
            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing);
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(tipoSol,seed);

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(HEUR));

            Search alg = new SimulatedAnnealingSearch(temp, iter, K, L);
            
            SearchAgent agent = new SearchAgent(p, alg);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
