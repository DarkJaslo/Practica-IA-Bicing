import IA.Bicing.Estaciones;
import practica.PracBoard;
import practica.PracHeuristicFunction;
import practica.PracSearch;

/*
 * Clase de ejemplo para un tester.
 */
public class TesterExp2
{
    private static final int NUM_SEEDS = 1000;
    private static int seeds[];
    private static double mediaPorTipo[];
    private static double mediaRealPorTipo[];
    private static double distPorTipo[];

    public static void main(String args[]) throws Exception
    {
        initVars();
        PracBoard.TipoSolucion tiposSol[] = {PracBoard.TipoSolucion.VACIA, PracBoard.TipoSolucion.NORMAL, PracBoard.TipoSolucion.NORMAL_RANDOM, PracBoard.TipoSolucion.GREEDY};
        String nombresTiposSol[] = {"Vacía", "Normal", "Normal random", "Greedy"};
        
        for(int j = 0; j < tiposSol.length; ++j) //Itera tipos de solución
        {
            System.out.println(nombresTiposSol[j] + ":");
            double beneficioH1Total = 0.0;
            double beneficioH2Total = 0.0;
            double distanciaTotal = 0.0;
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
                PracHeuristicFunction.Function heuristicoHC = PracHeuristicFunction.Function.Heuristico_2;
                PracBoard.TipoSolucion tipoSol = tiposSol[j];
                PracBoard hcBoard = PracSearch.hillClimbing(estaciones,maxFurgonetas,heuristicoHC,tipoSol);

                /*
                System.out.println("Tipo de solución: " + nombresTiposSol[j]);
                System.out.println("Seed: " + seeds[i]);
                System.out.println("Beneficio H1: " + hcBoard.beneficioTotal(false));
                System.out.println("Beneficio H2: " + hcBoard.getBeneficioReal());
                System.out.println("Distancia: " + hcBoard.getTotalTravelDist());
                System.out.println();
                */

                beneficioH1Total += hcBoard.beneficioTotal(false);
                beneficioH2Total += hcBoard.getBeneficioReal();
                distanciaTotal += hcBoard.getTotalTravelDist();
            }
            mediaPorTipo[j] = beneficioH1Total/seeds.length;
            mediaRealPorTipo[j] = beneficioH2Total/seeds.length;
            distPorTipo[j] = distanciaTotal/seeds.length;
        }

        System.out.println("Número de seeds: " + NUM_SEEDS);
        System.out.println();

        for(int i = 0; i < mediaPorTipo.length; ++i)
        {
            System.out.println(nombresTiposSol[i] + ": ");
            System.out.println("Beneficio medio H1: " + mediaPorTipo[i]);
            System.out.println("Beneficio medio real: " + mediaRealPorTipo[i]);
            System.out.println("Distancia media: " + distPorTipo[i]);
            System.out.println();
        }
    }

    static private void initVars()
    {
        seeds = new int[NUM_SEEDS];

        for(int i = 0; i < seeds.length; ++i)
        {
            seeds[i] = i*3;
        }

        mediaPorTipo = new double[4];
        mediaRealPorTipo = new double[4];
        distPorTipo = new double[4];
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
}