import java.util.List;

import IA.Bicing.Estaciones;
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

        /*De momento, inicialización manual. Caso del test para el punto extra */
        int nest = 25;
        int nbic = 1250;
        int dem = Estaciones.EQUILIBRIUM;
        int seed = 1234;
        int maxFurgonetas = 5;

        Estaciones estaciones = new Estaciones(nest, nbic, dem, seed);
        
        /*Genera solución inicial mediante magia negra (sin hacer, solución vacía)*/
        PracBoard board = new PracBoard(estaciones, maxFurgonetas);
        board.creaSolucionBuena();
        //board.creaSolucionBuenaRandom(seed);

        System.out.println("La solucion inicial usa " + board.getFurgonetasEnUso() + " furgonetas");
        board.print();
        System.out.println("Beneficio por bicis: " + board.beneficioTotal(true));
        System.out.println("Beneficio real: " + board.getBeneficioReal());
        System.out.println("Distancia total recorrida: " + board.getTotalTravelDist());
        System.out.println();

        Problem p = new Problem(board, new PracSuccessorFunction(), new PracGoalTest(), new PracHeuristicFunction());

        Search alg = new HillClimbingSearch();

        SearchAgent agent = new SearchAgent(p, alg);
        
        System.out.println();
        System.out.println("Actions:");
        printActions(agent.getActions());
        System.out.println();

        PracBoard finalBoard = (PracBoard)alg.getGoalState();

        System.out.println("Furgonetas usadas: " + finalBoard.getFurgonetasEnUso());
        finalBoard.print();
        System.out.println("Beneficio por bicis: " + finalBoard.beneficioTotal(true));
        System.out.println("Beneficio real: " + finalBoard.getBeneficioReal());
        System.out.println("Distancia total recorrida: " + finalBoard.getTotalTravelDist());
    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
};



