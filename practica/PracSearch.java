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
    private static int SIM_ANN_TEMP;
    private static int SIM_ANN_ITER;
    private static int SIM_ANN_K;
    private static double SIM_ANN_LAMBDA;

    public PracSearch()
    {
        SIM_ANN_TEMP = 100;
        SIM_ANN_ITER = 100;
        SIM_ANN_K = 100;
        SIM_ANN_LAMBDA = 0.01;
    }
    
    /*
     * Devuelve el board resultante de hacer la búsqueda con Hill Climbing. No devuelve los operadores aplicados, para eso debes utilizar este código y obtenerlas de agent.
     */
    public PracBoard hillClimbing(Estaciones estaciones, int maxFurgonetas, PracHeuristicFunction.Function function) throws Exception
    {
        PracBoard board = new PracBoard(estaciones, maxFurgonetas);

        Problem p = new Problem(board, new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing), new PracGoalTest(), new PracHeuristicFunction(function));

        Search alg = new HillClimbingSearch();

        SearchAgent agent = new SearchAgent(p, alg);

        return (PracBoard)alg.getGoalState();
    }

    /*
     * Devuelve el board resultante de hacer la búsqueda con Simulated Annealing. No devuelve los operadores aplicados, para eso debes utilizar este código y obtenerlas de agent.
     */
    public PracBoard simulatedAnnealing(Estaciones estaciones, int maxFurgonetas, PracHeuristicFunction.Function function) throws Exception
    {
        PracBoard board = new PracBoard(estaciones, maxFurgonetas);

        Problem p = new Problem(board, new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing), new PracGoalTest(), new PracHeuristicFunction(function));

        Search alg = new SimulatedAnnealingSearch(SIM_ANN_TEMP,SIM_ANN_ITER,SIM_ANN_K,SIM_ANN_LAMBDA);

        SearchAgent agent = new SearchAgent(p, alg);

        return (PracBoard)alg.getGoalState();
    }
}
