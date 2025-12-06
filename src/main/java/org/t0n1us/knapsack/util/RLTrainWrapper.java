package org.t0n1us.knapsack.util;

import org.chocosolver.solver.Model;
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

    public static double[] train(int epochs, double learning_rate, double alpha, double lambda, double epsilon, double epsilon_min, int ms_timelimit) throws IOException {
        List<KnapsackInstance> trainDataset = KnapsackDataset.getTrainDataset(true);

        FeatureExtractor extractor = new SimpleExtractor();
        RLParams params = new RLParams(learning_rate, alpha, lambda, epsilon, epsilon_min, extractor.getFeaturesSize());

        for (int epoch = 0; epoch < epochs; epoch++) {
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

                solver.solve();

                rl_varSelector.clearSteps();
            }

            params.decayEpsilon();  // On diminue epsilon
            Collections.shuffle(trainDataset);  // On mélange le dataset

            System.out.println("Epoch " + Integer.toString(epoch + 1) + " completed!");
        }

        return params.weights;  // On retourne les poids entrainnés
    }

}
