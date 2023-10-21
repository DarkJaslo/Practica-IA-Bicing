import IA.Bicing.Estaciones;
import practica.PracBoard;
import practica.PracHeuristicFunction;
import practica.PracSearch;

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
            PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_2;
            
            //Tipo de solución inicial
            PracBoard.TipoSolucion tipoSol = PracBoard.TipoSolucion.NORMAL;

            double startTime = System.nanoTime();

            PracBoard hcBoard = PracSearch.hillClimbing(estaciones,maxFurgonetas,heuristicoHC,tipoSol);

            double endTime = System.nanoTime();

            tiempoTotal += (endTime-startTime);

            if(i == ejecuciones-1)
            {
                //Print datos
                System.out.println("Beneficio: " + hcBoard.getBeneficioReal());
                System.out.println("Distancia: " + hcBoard.getTotalTravelDist());
            }
        }

        double mediaTiempo = tiempoTotal/ejecuciones;
        mediaTiempo /= 1000000.0; //Nanosegundos a milisegundos
        System.out.println("Tiempo medio de " + ejecuciones + " ejecuciones: " + mediaTiempo + "ms");
    }
}   
