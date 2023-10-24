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
    private static final int NUM_SEEDS = 50;
    private static int seeds[];

    static private void initSeeds()
    {
        seeds = new int[NUM_SEEDS];

        for(int i = 0; i < seeds.length; ++i)
        {
            seeds[i] = i*3;
        }
    }    

    public static void main(String args[])
    {
        int K[] = {1,5,10,20};
        double L[] = {0.1,0.01,0.001,0.0001,0.00001}; //{0.01,0.001,0.0005,0.0001,0.00001};
        int TEMP = 1000000; //500000;
        int iter = 1;
        initSeeds();

        try 
        {
            String filePath = "./R/exp3.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("par\tk\tlambda\tcalidad\tbeneficio\ttiempo\n");

            for(int i = 0; i < K.length; ++i)
            {
                for(int j = 0; j < L.length; ++j)
                {
                    String identif = K[i]+"|"+L[j];
                    System.out.println(identif + "\t" + K[i] + "\t" + String.format("%.9f",L[j]));
                    for(int k = 0; k < seeds.length; ++k)
                    {
                        int numEstaciones = 25;
                        int numBicis = 1250;
                        int maxFurgonetas = 5;
                        int tipoDemanda = Estaciones.EQUILIBRIUM;
                        int seed = seeds[k];

                        Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                        //Búsqueda Hill Climbing

                        //Enum para decir que heuristico usar
                        PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_2;
                        PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.GREEDY2;

                        //Búsqueda Hill Climbing

                        //Enum para decir que heuristico usar
                        PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing);
                        successorFunction.disableChange3Est();

                        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                        board.creaSolucionInicial(tipoSol,seed);

                        Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(heuristicoHC));

                        Search alg = new SimulatedAnnealingSearch(TEMP, iter, K[i], L[j]);
                        
                        double startTime = System.nanoTime();
                        SearchAgent agent = new SearchAgent(p, alg);
                        double endTime = System.nanoTime();

                        PracBoard hcBoard = (PracBoard)alg.getGoalState();

                        double calidad = hcBoard.beneficioTotal(false);
                        double beneficio = hcBoard.getBeneficioReal();
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
}
