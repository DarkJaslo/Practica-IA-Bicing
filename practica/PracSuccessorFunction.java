package practica;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        try {
            ArrayList<Successor> retval = new ArrayList<Successor>();

            /* Operador aleatorio */
            //0->change 1->swap 2->change2 3->add

            //Si se puede aplicar, aplica. Si no, busca otro

            boolean found = false;
            Random random = new Random(System.currentTimeMillis());
            double probsAdd[] = { 0.2,
                                0.3,
                                0.5,
                                1.0};

            double probsNoAdd[] = {0.4,
                                    0.6,
                                    1.0,
                                    1.1};

            PracBoard board = (PracBoard)state;
            double probs[];

            if(board.getFurgonetasEnUso() >= board.getMaxFurgonetas())
            {
                probs = new double[probsNoAdd.length];
                for(int i = 0; i < probsNoAdd.length; ++i)
                    probs[i] = probsNoAdd[i];
            }
            else
            {
                probs = new double[probsAdd.length];
                for(int i = 0; i < probsAdd.length; ++i)
                    probs[i] = probsAdd[i];
            }

            while(!found)
            {
                double rand = random.nextDouble();
                if(board.getFurgonetasEnUso() == 0 || rand >= probs[3]) //Add
                {
                    //System.out.println("Add");
                    //Random add
                    int nest = board.getEstaciones().size();
                    int origen = random.nextInt(nest);
                    int dest1 = random.nextInt(nest);
                    int dest2;
                    if(rand >= 0.5)
                        dest2 = random.nextInt(nest+1)-1;
                    else 
                        dest2 = -1;

                    if(board.canAddVan(origen, dest1, dest2))
                    {
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.addVan(origen, dest1, dest2);
                        String op = "add origen " + origen + " dest1 " + dest1 + " dest2 ";
                        if(dest2 == -1) op = op+"no";
                        else op = op+dest2;
                        retval.add(new Successor(op, auxBoard));
                        found = true;
                    }
                }
                else if(rand >= probs[2]) //Change2
                {
                    //System.out.println("Change2");
                    int f = random.nextInt(board.getFurgonetasEnUso());
                    int which1, which2;
                    if(rand >= 0.33){ which1 = board.origen(); which2 = board.destino1();}
                    else if(rand >= 0.66){ which1 = board.origen(); which2 = board.destino2();}
                    else { which1 = board.destino1(); which2 = board.destino2();}

                    int nest = board.getEstaciones().size();
                    int est1 = random.nextInt(nest);
                    int est2 = random.nextInt(nest);

                    if(board.canChange2Est(f,which1,which2,est1,est2))
                    {
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.change2Est(f, which1, which2, est1, est2);
                        retval.add(new Successor("change2 furg " + f + " " + board.getNombreEstacion(which1) + " " + est1 + "" + board.getNombreEstacion(which2) + " " + est2, auxBoard));
                        found = true;
                    }
                }
                else if(rand >= probs[1]) //Swap
                {
                    //System.out.println("Swap");
                    int f1 = random.nextInt(board.getFurgonetasEnUso());
                    int f2 = random.nextInt(board.getFurgonetasEnUso());
                    int ests[] = {board.origen(),board.origen(),
                                        board.destino1(),board.destino1(),
                                        board.destino1(),board.destino2(),
                                        board.destino2(),board.destino1(),
                                        board.destino2(),board.destino2()};
                    int index = 2*random.nextInt(5);
                    int which1 = ests[index];
                    int which2 = ests[index+1];
                    if(board.canSwapEst(f1, f2, which1, which2))
                    {
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.swapEst(f1, f2, which1, which2);
                        retval.add(new Successor("swap furg " + f1 + " furg " + f2 + " " + board.getNombreEstacion(ests[index]) + " " + board.getNombreEstacion(ests[index+1]), auxBoard));
                        found = true;
                    }
                } 
                else if(rand >= probs[0]) //Change
                {
                    //System.out.println("Change");
                    int f = random.nextInt(board.getFurgonetasEnUso());
                    int opts[] = {board.origen(), board.destino1(), board.destino2()};
                    int which = opts[random.nextInt(3)];
                    int est = random.nextInt(board.getEstaciones().size());

                    if(board.canChangeEst(f, which, est))
                    {
                        PracBoard auxBoard = PracBoard.copyOf(board);
                        auxBoard.changeEst(f, which, est);
                        retval.add(new Successor("change furg " + f + " " + board.getNombreEstacion(which) + " " + est, auxBoard));
                        found = true;
                    }
                }
            }

            return retval;
        } catch (Exception e) 
        {
            ArrayList<Successor> retval = new ArrayList<Successor>();
            e.printStackTrace();
            return retval;            
        }
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