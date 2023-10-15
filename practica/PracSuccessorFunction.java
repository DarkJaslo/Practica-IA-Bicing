package practica;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class PracSuccessorFunction implements SuccessorFunction {
    
    public List getSuccessors(Object state){

        System.out.println("Generando sucesores");

        ArrayList<Successor> retval = new ArrayList<Successor>();
        PracBoard board = (PracBoard)state;

        /*
         * Añade todos los estados posibles a la lista
         */

        /*
         * Añade sucesores de changeEst
         */
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

        /*
         * Añade sucesores de swapEst
         */
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

        /*
         * Añade sucesores de addVan
         */
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

        return retval;
    }
}
