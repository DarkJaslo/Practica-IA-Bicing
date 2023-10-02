package practica;

import aima.search.framework.GoalTest;

public class PracGoalTest implements GoalTest {
    
    /*
     * Según el enunciado, siempre false
     */
    public boolean isGoalState(Object state){
        return false;
    }
}
