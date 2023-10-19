package practica;

import aima.search.framework.HeuristicFunction;

public class PracHeuristicFunction implements HeuristicFunction {

    public static enum Function{ Heuristico_1, Heuristico_2 }
    private Function func;

    public PracHeuristicFunction()
    {
        func = Function.Heuristico_1;
    }

    public PracHeuristicFunction(Function function)
    {
        func = function;
    }

    public double getHeuristicValue(Object n){
        switch(func)
        {
            case Heuristico_1:
                return ((PracBoard)n).heuristicFunction1();
            case Heuristico_2:
                return ((PracBoard)n).heuristicFunction2();
            default:
                return 0.0;
        }
    }
}
