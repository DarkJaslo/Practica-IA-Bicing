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
import practica.PracSearch;
import practica.PracSuccessorFunction;

/*
 * Clase de ejemplo para un tester.
 */
public class TesterExp2
{
    private static final int NUM_SEEDS = 1000;
    private static int seeds[];
    private static double mediaPorTipo[];
    private static double mediaRealPorTipo[];
    private static double distPorTipo[];

    public static void main(String args[]) throws Exception
    {
        try 
        {
            PracBoard.TipoSolucion tiposSol[] = {PracBoard.TipoSolucion.VACIA, PracBoard.TipoSolucion.NORMAL};
            String nombresTiposSol[] = {"Vacia", "Normal"};
            initVars(tiposSol.length);

            String filePath = "./R/exp2.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("sol_ini\tbeneficio\ttiempo\n");
            
            for(int j = 0; j < tiposSol.length; ++j) //Itera tipos de solución
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
                    PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_1;
                    PracBoard.TipoSolucion tipoSol = tiposSol[j];

                    //Búsqueda Hill Climbing

                    //Enum para decir que heuristico usar
                    PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                    successorFunction.disableChange3Est();

                    PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                    board.setRedondeo(0);
                    board.creaSolucionInicial(tipoSol);

                    Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(heuristicoHC));

                    Search alg = new HillClimbingSearch();
                    
                    double startTime = System.nanoTime();
                    SearchAgent agent = new SearchAgent(p, alg);
                    double endTime = System.nanoTime();

                    PracBoard hcBoard = (PracBoard)alg.getGoalState();

                    double benef = hcBoard.beneficioTotal(false);
                    double beneficio = hcBoard.getBeneficioReal();
                    double tiempo = (endTime-startTime);

                    beneficioH1Total += benef;
                    beneficioH2Total += beneficio;
                    distanciaTotal += hcBoard.getTotalTravelDist();

                    bufferedWriter.write(nombresTiposSol[j] + "\t" + benef + "\t" + tiempo/1000000 + "\n");
                }
                mediaPorTipo[j] = beneficioH1Total/seeds.length;
                mediaRealPorTipo[j] = beneficioH2Total/seeds.length;
                distPorTipo[j] = distanciaTotal/seeds.length;
            }

            System.out.println("Numero de seeds: " + NUM_SEEDS);
            System.out.println();

            for(int i = 0; i < mediaPorTipo.length; ++i)
            {
                System.out.println(nombresTiposSol[i] + ": ");
                System.out.println("Beneficio medio H1: " + mediaPorTipo[i]);
                System.out.println("Beneficio medio real: " + mediaRealPorTipo[i]);
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
}