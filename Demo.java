import IA.Bicing.Estaciones;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;


import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class Demo 
{
    private static final int ARGCOUNT = 9;
    private static final int ITERS_IF_RANDOM = 5;
    private static int ITERS = 1;

    private static final int SA_TEMP = 500000;
    private static final int SA_ITER = 1;
    private static final int SA_K = 5;
    private static final double SA_LAMBDA = 0.01;

    public static void main(String args[]) throws Exception
    {
        if(args.length != ARGCOUNT)
        {
            System.out.println("Has proporcionado " + args.length + " argumentos.");
            System.out.println("Usage: ./ejecuta.sh Demo [seed] [num_ests] [num_bicicletas] [max_furgonetas] [HC|SA] [Heur1|Heur2] [RUSH|EQUIL] [greedy|vacia|random]");
            System.exit(1);
        }

        //Else

        int seed = Integer.parseInt(args[1]);
        int numEsts = Integer.parseInt(args[2]);
        int numBicicletas = Integer.parseInt(args[3]);
        int maxFurgonetas = Integer.parseInt(args[4]);
        PracSuccessorFunction.SearchType searchType = PracSuccessorFunction.SearchType.HillClimbing;
        if(args[5].equals("HC"))
            searchType = PracSuccessorFunction.SearchType.HillClimbing;
        else if(args[5].equals("SA"))
            searchType = PracSuccessorFunction.SearchType.SimulatedAnnealing;
        else
        {
            System.out.println("El argumento [HC|SA] proporcionado es incorrecto.\nArgumento proporcionado: " + args[5]);
            System.exit(2);
        }
        PracHeuristicFunction.Function heurFunction = PracHeuristicFunction.Function.Heuristico_1;
        if(args[6].equals("Heur1"))
            heurFunction = PracHeuristicFunction.Function.Heuristico_1;
        else if(args[6].equals("Heur2"))
            heurFunction = PracHeuristicFunction.Function.Heuristico_2;
        else
        {
            System.out.println("El argumento [Heur1|Heur2] proporcionado es incorrecto.\nArgumento proporcionado: " + args[6]);
            System.exit(3);
        }
        int tipoDemanda = Estaciones.EQUILIBRIUM;
        if(args[7].equals("RUSH"))
            tipoDemanda = Estaciones.RUSH_HOUR;
        else if(args[7].equals("EQUIL"))
            tipoDemanda = Estaciones.EQUILIBRIUM;
        else
        {
            System.out.println("El argumento [RUSH|EQUIL] proporcionado es incorrecto.\nArgumento proporcionado: " + args[7]);
            System.exit(4);
        }
        PracBoard.TipoSolucion tipoSolucion = PracBoard.TipoSolucion.GREEDY2;
        if(args[8].equals("greedy"))
            tipoSolucion = PracBoard.TipoSolucion.GREEDY2;
        else if(args[8].equals("random"))
        {
            tipoSolucion = PracBoard.TipoSolucion.RANDOM;
            ITERS = ITERS_IF_RANDOM;
        }
        else if(args[8].equals("vacia"))
            tipoSolucion = PracBoard.TipoSolucion.VACIA;
        else
        {
            System.out.println("El argumento [greedy|vacia|random] proporcionado es incorrecto.\nArgumento proporcionado: " + args[8]);
            System.exit(5);
        }

        Estaciones estaciones = new Estaciones(numEsts, numBicicletas, tipoDemanda, seed);

        Random random = new Random(System.currentTimeMillis());

        PracBoard mejorBoard = new PracBoard(estaciones, maxFurgonetas);
        double heur = 10000.0;

        SearchAgent mejorAgent = null;
        double tiempo = 0.0;

        for(int i = 0; i < ITERS; ++i)
        {
            int solSeed = random.nextInt();

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(tipoSolucion, solSeed);

            Problem p = new Problem(board, new PracSuccessorFunction(searchType), new PracGoalTest(), new PracHeuristicFunction(heurFunction));

            Search alg;
            if(searchType == PracSuccessorFunction.SearchType.HillClimbing)
                alg = new HillClimbingSearch();
            else
                alg = new SimulatedAnnealingSearch(SA_TEMP, SA_ITER, SA_K, SA_LAMBDA);

            double startTime = System.nanoTime();
            SearchAgent agent = new SearchAgent(p, alg);
            double endTime = System.nanoTime();

            double tiempoMS = (endTime-startTime)/1000000;
            tiempo += tiempoMS;

            PracBoard goalBoard = (PracBoard)alg.getGoalState();
            double heuristic = goalBoard.heuristicFunction(heurFunction);

            if(heuristic < heur)
            {
                mejorBoard = goalBoard;
                mejorAgent = agent;
                heur = heuristic;
            }
        }        

        System.out.println("Operadores aplicados:");
        printActions(mejorAgent.getActions());
        System.out.println();

        mejorBoard.print();
        mejorBoard.beneficioTotal(true);

        DecimalFormat dFormat = new DecimalFormat("0.00");

        System.out.println("Ganancia (bicicletas bien transportadas): " + mejorBoard.beneficioTotal(false) + " euros");
        System.out.println("Beneficio (ganancia menos coste): " + dFormat.format(mejorBoard.getBeneficioReal()) + " euros");
        System.out.println("Distancia recorrida: " + dFormat.format(mejorBoard.getTotalTravelDist()) + "m");
        System.out.println("Tiempo de cálculo: " + dFormat.format(tiempo) + "ms");
    }  
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}
