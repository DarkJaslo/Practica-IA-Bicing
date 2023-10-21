package practica;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class PracSuccessorFunction implements SuccessorFunction {

    public static enum SearchType{ HillClimbing, SimulatedAnnealing }
    private SearchType type;
    private boolean changeEstEnabled;
    private boolean change2EstEnabled;
    private boolean change3EstEnabled;
    private boolean swapEstEnabled;
    private boolean addVanEnabled;

    public PracSuccessorFunction()
    {
        type = SearchType.HillClimbing;
        enableAllOperators();
    }

    public PracSuccessorFunction(SearchType type)
    {
        this.type = type;
        enableAllOperators();
    }

    public void enableAllOperators()
    {
        changeEstEnabled = true;
        change2EstEnabled = true;
        change3EstEnabled = true;
        swapEstEnabled = true;
        addVanEnabled = true;
    }
    
    public List getSuccessors(Object state)
    {
        switch(type)
        {
            case HillClimbing:
                return getSuccessorsHC(state);
            case SimulatedAnnealing:
                return getSuccessorsSA(state);
            default:
                ArrayList<Successor> retval = new ArrayList<Successor>();
                return retval;
        }
    }

    /*
     * Devuelve lista de sucesores para Hill Climbing (todos los posibles)
     */
    private List getSuccessorsHC(Object state)
    {
        ArrayList<Successor> retval = new ArrayList<Successor>();
        PracBoard board = (PracBoard)state;

        /*
         * Añade todos los estados posibles a la lista
         */

        /*
         * Añade sucesores de changeEst
         */

        if(changeEstEnabled)
        {
            for(int i = 0; i < board.getFurgonetasEnUso(); ++i)
            {
                for(int j = 0; j < board.getEstaciones().size(); ++j)
                {
                    if(board.canChangeEst(i, board.origen(), j)){
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.changeEst(i, board.origen(), j);
                        retval.add(new Successor("change furg " + i + " origen " + j, auxBoard));
                    }
                    if(board.canChangeEst(i, board.destino1(), j)){
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.changeEst(i, board.destino1(), j);
                        retval.add(new Successor("change furg " + i + " dest1 " + j, auxBoard));
                    }
                    if(board.canChangeEst(i, board.destino2(), j)){
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.changeEst(i, board.destino2(), j);
                        retval.add(new Successor("change furg " + i + " dest2 " + j, auxBoard));
                    }
                }
            }
        }
        

        /*
         * Añade sucesores de swapEst
         */
        if(swapEstEnabled)
        {
            for(int f1 = 0; f1 < board.getFurgonetasEnUso(); ++f1)
            {
                for(int f2 = 0; f2 < board.getFurgonetasEnUso(); ++f2)
                {
                    int ests[] = {board.origen(),board.origen(),
                                    board.destino1(),board.destino1(),
                                    board.destino1(),board.destino2(),
                                    board.destino2(),board.destino1(),
                                    board.destino2(),board.destino2()};

                    for(int i = 0; i < 10; i+=2)
                    {
                        if(board.canSwapEst(f1, f2, ests[i], ests[i+1]))
                        {
                            PracBoard auxBoard = PracBoard.copyOf(board);
                            auxBoard.swapEst(f1, f2, ests[i], ests[i+1]);
                            retval.add(new Successor("swap furg " + f1 + " furg " + f2 + " " + board.getNombreEstacion(ests[i]) + " " + board.getNombreEstacion(ests[i+1]), auxBoard));
                        }
                    }
                }
            }
        }


        /*
         * Añade sucesores de addVan
         */
        if(addVanEnabled)
        {
            for(int o = 0; o < board.getEstaciones().size(); ++o)
            {
                for(int d1 = 0; d1 < board.getEstaciones().size(); ++d1)
                {
                    if(board.canAddVan(o, d1, -1))
                    {
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.addVan(o, d1, -1);
                        retval.add(new Successor("add origen " + o + " dest1 " + d1 + " dest2 no", auxBoard));
                    }
                    for(int d2 = 0; d2 < board.getEstaciones().size(); ++d2)
                    {
                        if(board.canAddVan(o, d1, d2))
                        {
                            PracBoard auxBoard = PracBoard.copyOf(board);
                            auxBoard.addVan(o, d1, d2);
                            retval.add(new Successor("add origen " + o + " dest1 " + d1 + " dest2 " + d2, auxBoard));
                        }
                    }
                }
            }
        }
        

        /*
         * Añade sucesores de change2Est y change3Est
         */
        for(int i = 0; i < board.getFurgonetasEnUso(); ++i)
        {
            for(int j = 0; j < board.getEstaciones().size(); ++j)
            {
                for(int k = 0; k < board.getEstaciones().size(); ++k)
                {
                    if(change2EstEnabled)
                    {
                        /*  Origen <-> d1, Origen <-> d2, D1 <-> D2  */
                        if(board.canChange2Est(i, board.origen(),board.destino1(), j,k)){
                            PracBoard auxBoard = PracBoard.copyOf(board);
                            auxBoard.change2Est(i, board.origen(),board.destino1(), j,k);
                            retval.add(new Successor("change2 furg " + i + " origen " + j + " dest1 " + k, auxBoard));
                        }
                        if(board.canChange2Est(i, board.origen(),board.destino2(), j,k)){
                            PracBoard auxBoard = PracBoard.copyOf(board);
                            auxBoard.change2Est(i, board.origen(),board.destino2(), j,k);
                            retval.add(new Successor("change2 furg " + i + " origen " + j + " dest2 " + k, auxBoard));
                        }
                        if(board.canChange2Est(i, board.destino1(),board.destino2(), j,k)){
                            PracBoard auxBoard = PracBoard.copyOf(board);
                            auxBoard.change2Est(i, board.destino1(),board.destino2(), j,k);
                            retval.add(new Successor("change2 furg " + i + " dest1 " + j + " dest2 " + k, auxBoard));
                        }
                    }

                    /*
                     * Sección change3Est
                     */
                    if(change3EstEnabled)
                    {
                        for(int l = 0; l < board.getEstaciones().size(); ++l)
                        {
                            if(board.canChange3Est(i, j, k, l))
                            {
                                PracBoard auxBoard = PracBoard.copyOf(board);
                                auxBoard.change3Est(i, j, k, l);
                                retval.add(new Successor("change3 furg " + i + " origen " + j + " dest1 " + k + " dest2 " + l, auxBoard));
                            }
                        }
                    }
                }
            }
        }

        return retval;
    }

    /*
     * Devuelve lista de sucesores para Simulated Annealing (uno aleatorio)
     */
    private List getSuccessorsSA(Object state)
    {
        ArrayList<Successor> retval = new ArrayList<Successor>();
        return retval;
    }

    public void disableChangeEst()
    {
        changeEstEnabled = false;
    }

    public void disableChange2Est()
    {
        change2EstEnabled = false;
    }

    public void disableChange3Est()
    {
        change3EstEnabled = false;
    }

    public void disableSwapEst()
    {
        swapEstEnabled = false;
    }

    public void disableAddVan()
    {
        addVanEnabled = false;
    }
}