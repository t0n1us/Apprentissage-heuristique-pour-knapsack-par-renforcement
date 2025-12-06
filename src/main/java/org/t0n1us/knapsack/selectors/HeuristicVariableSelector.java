package org.t0n1us.knapsack.selectors;

import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.IntVar;

public class HeuristicVariableSelector implements VariableSelector<IntVar> {

    private final IntVar[] vars;
    private final int[] weights;
    private final int[] values;

    public HeuristicVariableSelector(IntVar[] vars, int[] weights, int[] values) {
        this.vars = vars;
        this.weights = weights;
        this.values = values;
    }

    @Override
    public IntVar getVariable(IntVar[] intVars) {
        int bestIndex = -1;
        double bestScore = -1;

        for (int i = 0; i < this.vars.length; i++){
            IntVar variable = this.vars[i];

            if (!variable.isInstantiated()) {
                double score = (double) values[i] / (double) weights[i];
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }

        if (bestIndex == -1) {
            return null;
        }

        return this.vars[bestIndex];
    }

}
