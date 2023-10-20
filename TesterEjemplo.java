import IA.Bicing.Estaciones;
import practica.PracBoard;
import practica.PracHeuristicFunction;
import practica.PracSearch;

/*
 * Clase de ejemplo para un tester.
 */
public class TesterEjemplo
{
    public static void main(String args[]) throws Exception
    {
        //Inicialización estaciones ( en este caso, solo una vez )
        int numEstaciones = 25;
        int numBicis = numEstaciones*50;
        int maxFurgonetas = numEstaciones/5;
        int tipoDemanda = Estaciones.EQUILIBRIUM;
        int seed = 1234;

        Estaciones estaciones = new Estaciones(numEstaciones, numBicis, tipoDemanda, seed);


        //Búsqueda Hill Climbing

        //Enum para decir que heuristico usar
        PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_1;
        PracBoard.TipoSolucion tipoSolucion = PracBoard.TipoSolucion.VACIA;

        long startTime, endTime;

        startTime = System.nanoTime();
        PracBoard hcBoard = PracSearch.hillClimbing(estaciones,maxFurgonetas,heuristicoHC,tipoSolucion);
        endTime = System.nanoTime();
        System.out.println("Execution time (HC): " + (endTime - startTime));

        double blabla = hcBoard.beneficioTotal(false);
        double blabla2 = hcBoard.getTotalTravelDist();
        
        //Posible print de debug
        System.out.println("Beneficio (sin coste) HC: " + hcBoard.beneficioTotal(false) + "  Distancia HC: " + hcBoard.getTotalTravelDist());
        //etc


        //Búsqueda Simulated Annealing (aún no está implementada, ignora los resultados de momento)
        PracHeuristicFunction.Function heuristicoSA = PracHeuristicFunction.Function.Heuristico_2;

        startTime = System.nanoTime();
        PracBoard saBoard = PracSearch.simulatedAnnealing(estaciones, maxFurgonetas, heuristicoSA);
        endTime = System.nanoTime();
        System.out.println("Execution time (SA): " + (endTime - startTime));

        double blablabla = saBoard.getBeneficioReal();
        double blablabla2 = saBoard.getTotalTravelDist();

        //Posible print de debug
        System.out.println("Beneficio SA: " + saBoard.getBeneficioReal() + "  Distancia SA: " + saBoard.getTotalTravelDist());
        //etc
    }
}