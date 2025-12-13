package org.t0n1us.knapsack.util;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.t0n1us.knapsack.selectors.rl.RLParams;
import org.t0n1us.knapsack.selectors.rl.RLSolutionMonitor;
import org.t0n1us.knapsack.selectors.rl.RLVariableSelector;
import org.t0n1us.knapsack.selectors.rl.features.FeatureExtractor;
import org.t0n1us.knapsack.selectors.rl.features.SimpleExtractor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RLTrainWrapper {

    public static TrainResult train(int epochs, RLParams params, long ms_timelimit, FeatureExtractor extractor) throws IOException {
        List<KnapsackInstance> trainDataset = KnapsackDataset.getTrainDataset(true);

        double last_best_avg = -1.0;

        for (int epoch = 0; epoch < epochs; epoch++) {

            double best_avg = 0.0;

            for (KnapsackInstance instance : trainDataset) {
                Model model = KnapsackModel.buildKnapsackModel(instance);
                Solver solver = model.getSolver();

                BoolVar[] x = model.retrieveBoolVars();
                IntVar totalValue = KnapsackModel.getTotalValue(model);

                int[] weights = instance.weights();
                int[] values = instance.values();
                int maxCapacity = instance.capacity();

                RLVariableSelector rl_varSelector = new RLVariableSelector(x, weights, values, maxCapacity, params, extractor);
                RLSolutionMonitor rl_monitor = new RLSolutionMonitor(rl_varSelector, totalValue);
                IntValueSelector valSelector = var -> 1;

                solver.setSearch(Search.intVarSearch(rl_varSelector, valSelector, x));
                solver.plugMonitor(rl_monitor);
                solver.limitTime(ms_timelimit);

                Solution solution = solver.findOptimalSolution(totalValue, true);

                if (solution == null)
                    best_avg += 0;
                else
                    best_avg += solution.getIntVal(totalValue);

                rl_varSelector.clearSteps();
            }

            params.decayEpsilon();  // On diminue epsilon
            Collections.shuffle(trainDataset);  // On mélange le dataset

            best_avg /= trainDataset.size();
            last_best_avg = best_avg;

            System.out.println("Epoch " + (epoch + 1) + " completed!");
            System.out.println("Average best value was :: "  + best_avg);
            System.out.println();
        }

        return new TrainResult(params.weights, last_best_avg);  // On retourne les poids entrainnés
    }

}
