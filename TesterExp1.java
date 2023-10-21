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
    private static int seeds[];
    private static double mediaPorTipo[];
    private static double mediaRealPorTipo[];
    private static double distPorTipo[];
    private static double tiempoPorTipo[];

    public static void main(String args[]) throws Exception
    {
        PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.VACIA;
        String modos[] = { "Change", "ChangeSwap", "ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange3SwapAdd", "ChangeChange2Change3SwapAdd", "ChangeChange2Change3Swap"};
        initVars(modos.length);
        
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

                //Inicialización estaciones ( en este caso, solo una vez )
                int numEstaciones = 25;
                int numBicis = 1250;
                int maxFurgonetas = 5;
                int tipoDemanda = Estaciones.EQUILIBRIUM;
                int seed = seeds[i];

                Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);

                //Búsqueda Hill Climbing

                //Enum para decir que heuristico usar
                PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
                setOperadores(successorFunction,modos[j]);

                PracBoard board = new PracBoard(estaciones, maxFurgonetas);
                board.setRedondeo(4);
                board.creaSolucionInicial(tipoSol);

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

                beneficioH1Total += hcBoard.beneficioTotal(false);
                beneficioH2Total += hcBoard.getBeneficioReal();
                distanciaTotal += hcBoard.getTotalTravelDist();
                tiempoTotal += (endTime-startTime);
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