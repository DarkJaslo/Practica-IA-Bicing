package practica;

import IA.Bicing.Estaciones;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class PracSearch
{
    /*
     * Parámetros Simulated Annealing
     */
    private static final int SIM_ANN_TEMP = 2000000000;
    private static final int SIM_ANN_ITER = 1;
    private static final int SIM_ANN_K = 1;
    private static final double SIM_ANN_LAMBDA = 0.001;

    /*
     * Devuelve el board resultante de hacer la búsqueda con Hill Climbing. No devuelve los operadores aplicados, para eso debes utilizar este código y obtenerlas de agent.
     */
    public static PracBoard hillClimbing(Estaciones estaciones, int maxFurgonetas, PracHeuristicFunction.Function function, PracBoard.TipoSolucion tipoSolucion) throws Exception
    {
        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
        if(function == PracHeuristicFunction.Function.Heuristico_2)
            board.setRedondeo(4);
        else
            board.setRedondeo(0);
            
        board.creaSolucionInicial(tipoSolucion);

        Problem p = new Problem(board, new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing), new PracGoalTest(), new PracHeuristicFunction(function));

        Search alg = new HillClimbingSearch();

        SearchAgent agent = new SearchAgent(p, alg);

        PracBoard goalBoard = (PracBoard)alg.getGoalState();

        return goalBoard;
    }

    /*
     * Devuelve el board resultante de hacer la búsqueda con Simulated Annealing. No devuelve los operadores aplicados, para eso debes utilizar este código y obtenerlas de agent.
     */
    public static PracBoard simulatedAnnealing(Estaciones estaciones, int maxFurgonetas, PracHeuristicFunction.Function function) throws Exception
    {
        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
        board.setRedondeo(4);
        board.creaSolucionInicial(PracBoard.TipoSolucion.GREEDY);

        Problem p = new Problem(board, new PracSuccessorFunction(PracSuccessorFunction.SearchType.SimulatedAnnealing), new PracGoalTest(), new PracHeuristicFunction(function));

        Search alg = new SimulatedAnnealingSearch(SIM_ANN_TEMP,SIM_ANN_ITER,SIM_ANN_K,SIM_ANN_LAMBDA);

        SearchAgent agent = new SearchAgent(p, alg);

        PracBoard goalBoard = (PracBoard)alg.getGoalState();

        return goalBoard;
    }
}
