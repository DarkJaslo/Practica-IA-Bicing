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

public class TesterExp8 
{
    public static void main(String args[]) throws Exception
    {
        int numEstaciones = 25;
        int numBicis = 1250;
        int maxFurgonetas = 5;
        int tipoDemanda = Estaciones.EQUILIBRIUM;
        int seed = 1234;

        final int PRUEBAS_RANDOM = 5;

        Random random = new Random(seed);

        double calidad = 0.0;
        double beneficio = 0.0;
        double distancia = 0.0;
        double heuristico = 1000000.0;
        double tiempo = 0.0;

        cargaEnCache();

        for(int i = 0; i < PRUEBAS_RANDOM; ++i)
        {
            int solSeed = random.nextInt();

            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);
            
            //Heurístico usado
            PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_1;
            
            //Tipo de solución inicial
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.RANDOM;

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(tipoSol,solSeed);

            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(heuristicoHC));

            Search alg = new HillClimbingSearch();

            double startTime = System.nanoTime();
            SearchAgent agent = new SearchAgent(p, alg);
            double endTime = System.nanoTime();

            PracBoard hcBoard = (PracBoard)alg.getGoalState();

            double heur = hcBoard.heuristicFunction1();
            if(heur < heuristico)
            {
                heuristico = heur;
                calidad = hcBoard.beneficioTotal(false);
                beneficio = hcBoard.getBeneficioReal();
                distancia = hcBoard.getTotalTravelDist();
            }

            tiempo += (endTime-startTime);
        }

        //Print datos
        System.out.println("Heuristico primer criterio:");
        System.out.println("Pago por bicicletas bien transladadas: " + calidad + " euros");
        System.out.println("Beneficio: " + beneficio + " euros");
        System.out.println("Distancia: " + distancia + "m");
        System.out.println("Tiempo (" + PRUEBAS_RANDOM + " ejecuciones): " + tiempo/1000000 + "ms");

        tiempo = 0.0;
        heuristico = 100000.0;

        Random random2 = new Random(seed);

        for(int i = 0; i < PRUEBAS_RANDOM; ++i)
        {
            int solSeed = random2.nextInt();
            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);
            
            //Heurístico usado
            PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_2;
            
            //Tipo de solución inicial
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.RANDOM;

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(tipoSol,solSeed);

            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(heuristicoHC));

            Search alg = new HillClimbingSearch();

            double startTime = System.nanoTime();
            SearchAgent agent = new SearchAgent(p, alg);
            double endTime = System.nanoTime();

            PracBoard hcBoard = (PracBoard)alg.getGoalState();

            double heur = hcBoard.heuristicFunction2();
            if(heur < heuristico)
            {
                heuristico = heur;
                calidad = hcBoard.beneficioTotal(false);
                beneficio = hcBoard.getBeneficioReal();
                distancia = hcBoard.getTotalTravelDist();
            }

            tiempo += (endTime-startTime);
        }

        //Print datos
        System.out.println();
        System.out.println("Heuristico primer y segundo criterio:");
        System.out.println("Pago por bicicletas bien transladadas: " + calidad + " euros");
        System.out.println("Beneficio: " + beneficio + " euros");
        System.out.println("Distancia: " + distancia + "m");
        System.out.println("Tiempo (" + PRUEBAS_RANDOM + " ejecuciones): " + tiempo/1000000 + "ms");
    }

    static private void cargaEnCache() throws Exception {
        try
        {
            int seed = -1;
            int numEstaciones = 25;
            int numBicis = 1250;
            int maxFurgonetas = 5;

            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, Estaciones.EQUILIBRIUM, seed);

            //Búsqueda Hill Climbing

            //Enum para decir que heuristico usar
            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            successorFunction.disableChange2Est();
            successorFunction.disableChange3Est();

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.creaSolucionInicial(PracBoard.TipoSolucion.GREEDY2,seed);

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(PracHeuristicFunction.Function.Heuristico_1));

            Search alg = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(p, alg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}   
