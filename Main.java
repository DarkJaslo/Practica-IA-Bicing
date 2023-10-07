import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import practica.PracBoard;
import practica.PracGoalTest;
import practica.PracHeuristicFunction;
import practica.PracSuccessorFunction;

/*
 * Basado en Main.java de ProbIA5
 */

public class Main {
    public static void main(String args[]) throws Exception{

        /*De momento, inicialización manual */

        int nest = 25;
        int nbic = 1250;
        int dem = Estaciones.EQUILIBRIUM;
        int seed = 1234;
        int maxFurgonetas = 5;

        Estaciones estaciones = new Estaciones(nest, nbic, dem, seed);
         /*Genera solución inicial */

        PracBoard board = new PracBoard(estaciones, maxFurgonetas);

        Problem p = new Problem(board, new PracSuccessorFunction(), new PracGoalTest(), new PracHeuristicFunction());

        Search alg = new HillClimbingSearch();

        SearchAgent agent = new SearchAgent(p, alg);
        
        /*La solución está en board? */
    }
};



