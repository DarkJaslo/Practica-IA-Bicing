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

        int ejecuciones = 10;
        double tiempoTotal = 0.0;

        for(int i = 0; i < ejecuciones; ++i)
        {
            Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);
            
            //Heurístico usado
            PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_1;
            
            //Tipo de solución inicial
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.VACIA;

            PracBoard board = new PracBoard(estaciones, maxFurgonetas);
            board.setRedondeo(0);
            board.creaSolucionInicial(tipoSol);

            PracSuccessorFunction successorFunction = new PracSuccessorFunction(PracSuccessorFunction.SearchType.HillClimbing);
            successorFunction.disableChange3Est();

            Problem p = new Problem(board, successorFunction, new PracGoalTest(), new PracHeuristicFunction(heuristicoHC));

            Search alg = new HillClimbingSearch();

            double startTime = System.nanoTime();
            SearchAgent agent = new SearchAgent(p, alg);
            double endTime = System.nanoTime();

            PracBoard hcBoard = (PracBoard)alg.getGoalState();

            tiempoTotal += (endTime-startTime);

            if(i == ejecuciones-1)
            {
                //Print datos
                System.out.println("Pago por bicicletas bien transladadas: " + hcBoard.beneficioTotal(false) + "€");
                System.out.println("Beneficio: " + hcBoard.getBeneficioReal() + "€");
                System.out.println("Distancia: " + hcBoard.getTotalTravelDist() + "m");
            }
        }

        double mediaTiempo = tiempoTotal/ejecuciones;
        mediaTiempo /= 1000000.0; //Nanosegundos a milisegundos
        System.out.println("Tiempo medio (" + ejecuciones + " ejecuciones): " + mediaTiempo + "ms");
    }
}   
