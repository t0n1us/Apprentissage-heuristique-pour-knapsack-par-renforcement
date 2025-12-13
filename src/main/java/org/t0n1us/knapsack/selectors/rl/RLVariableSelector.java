package org.t0n1us.knapsack.selectors.rl;

import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.IntVar;
import org.t0n1us.knapsack.selectors.rl.features.FeatureExtractor;
import org.t0n1us.knapsack.selectors.rl.features.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RLVariableSelector implements VariableSelector<IntVar> {

    private final IntVar[] vars;
    private final int[] weights;
    private final int[] values;

    private final int maxCapacity;

    private RLParams params;
    private final FeatureExtractor extractor;

    private final Random random;

    private double b;

    private List<double[]> steps;

    public RLVariableSelector(IntVar[] vars, int[] weights, int[] values, int maxCapacity, RLParams params, FeatureExtractor extractor) {
        this.vars = vars;
        this.weights = weights;
        this.values = values;
        this.maxCapacity = maxCapacity;

        this.params = params;
        this.extractor = extractor;

        this.random = new Random();

        this.b = 0.0d;
        this.steps = new ArrayList<>();
    }

    private int getRemainingCapacity(List<Integer> active) {
        int weight_sum = 0;

        for (int idx : active) {
            weight_sum += this.weights[idx];
        }

        return this.maxCapacity - weight_sum;
    }

    private int getNbVars() {
        return vars.length;
    }

    private IntVar explore(List<Integer> active, List<Integer> excluded, List<Integer> available) {
        int random_idx = this.random.nextInt(available.size());
        int idx = available.get(random_idx);

        int value = this.values[idx];
        int weight = this.weights[idx];

        int remainingCapacity = this.getRemainingCapacity(active);
        int depth = active.size() + excluded.size();
        int nbVars = this.getNbVars();
        int remainingAvailable = available.size();

        State state = new State(remainingCapacity, depth, nbVars, remainingAvailable);

        double[] features = this.extractor.getFeatures(value, weight, state);

        this.steps.add(features);

        return this.vars[idx];
    }

    private double score(double[] features) {
        double score = 0.0;

        for (int i = 0; i < features.length; i++) {
            score += this.params.weights[i] * features[i];
        }

        return score;
    }

    private IntVar exploit(List<Integer> active, List<Integer> excluded, List<Integer> available) {
        int remainingCapacity = this.getRemainingCapacity(active);
        int depth = active.size() + excluded.size();
        int nbVars = this.getNbVars();
        int remainingAvailable = available.size();

        State state = new State(remainingCapacity, depth, nbVars, remainingAvailable);

        int best_idx = -1;
        double best_score = Double.NEGATIVE_INFINITY;
        double[] best_features = null;

        for (int idx : available) {
            int value = this.values[idx];
            int weight = this.weights[idx];

            double [] features = this.extractor.getFeatures(value, weight, state);

            double score = this.score(features);

            if (score > best_score) {
                best_score = score;
                best_idx = idx;
                best_features = features;
            }
        }

        this.steps.add(best_features);

        return this.vars[best_idx];
    }

    @Override
    public IntVar getVariable(IntVar[] intVars) {
        List<Integer> active = new ArrayList<>();  // Instancié à 1
        List<Integer> excluded = new ArrayList<>();  // Instancié à 0
        List<Integer> available = new ArrayList<>();  // Pas encore instancié

        for (int i = 0; i < this.vars.length; i++) {
            IntVar variable = this.vars[i];

            if (!variable.isInstantiated()) {
                available.add(i);
            } else {
                if (variable.getValue() == 0) {
                    excluded.add(i);
                } else {
                    active.add(i);
                }
            }
        }

        if (available.isEmpty())  // Plus rien de disponible
            return null;

        if (this.random.nextDouble() < this.params.epsilon) {
            return explore(active, excluded, available);
        } else {
            return exploit(active, excluded, available);
        }
    }

    public void updateWeights(double R) {
        for (double[] features : this.steps.reversed()) {

            if (this.random.nextDouble() < this.params.dropout)
                continue;

            for (int i = 0; i < features.length; i++) {
                this.params.weights[i] += this.params.learning_rate * ((R - this.b) * features[i]
                        - this.params.weight_decay * this.params.weights[i]);  // Maj des poids avec régularisation
            }
        }

        this.b = this.b + this.params.baseline_memorisation * (R - this.b);  // Maj du baseline
        this.steps.clear();
    }

    public void clearSteps() {
        this.steps.clear();
    }

}
