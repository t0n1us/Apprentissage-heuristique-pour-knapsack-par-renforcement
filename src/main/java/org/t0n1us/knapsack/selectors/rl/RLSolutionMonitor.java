package org.t0n1us.knapsack.selectors.rl;

import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.IntVar;

public class RLSolutionMonitor implements IMonitorSolution {

    private final RLVariableSelector rlSelector;
    private int bestValue = Integer.MIN_VALUE;
    private final IntVar objective;

    public RLSolutionMonitor(RLVariableSelector rlSelector, IntVar objective) {
        this.rlSelector = rlSelector;
        this.objective = objective;
    }

    @Override
    public void onSolution() {
        int v = objective.getValue();
        int R;

        if (v > this.bestValue) {
            R = 1;
            this.bestValue = v;
        } else {
            R = 0;
        }

        rlSelector.updateWeights(R);
    }
}
