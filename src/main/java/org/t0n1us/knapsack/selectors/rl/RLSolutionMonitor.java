package org.t0n1us.knapsack.selectors.rl;

import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.variables.IntVar;

public class RLSolutionMonitor implements IMonitorSolution {

    private final RLVariableSelector rlSelector;
    private final IntVar objective;

    int bestValue = Integer.MIN_VALUE;
    private double maxDelta;

    public RLSolutionMonitor(RLVariableSelector rlSelector, IntVar objective) {
        this.rlSelector = rlSelector;
        this.objective = objective;
    }

    @Override
    public void onSolution() {
        int v = objective.getValue();

        double delta = v - this.bestValue;
        this.maxDelta = Math.max(this.maxDelta, Math.abs(delta));

        double R = delta / this.maxDelta;
        R = Math.max(-1.0, Math.min(1.0, R));

        if (v > this.bestValue)
            this.bestValue = v;

        rlSelector.updateWeights(R);
    }
}
