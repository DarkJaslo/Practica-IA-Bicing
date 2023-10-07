package practica;

import aima.search.framework.HeuristicFunction;

public class PracHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){
        return ((PracBoard)n).heuristicFunction();
    }
}
