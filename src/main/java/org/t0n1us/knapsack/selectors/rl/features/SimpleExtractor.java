package org.t0n1us.knapsack.selectors.rl.features;

public class SimpleExtractor extends FeatureExtractor {

    public SimpleExtractor() {
        super(11);
    }

    @Override
    public double[] getFeatures(int value, int weight, State state) {
        double[] features = new double[this.n_features];

        double log1p_value = Math.log1p(value);
        double log1p_weight = Math.log1p(weight);
        double log1p_capacity = Math.log1p(state.remainingCapacity());

        features[0] = 1;  // Biais
        features[1] = log1p_value;
        features[2] = log1p_weight;

        features[3] = log1p_value - log1p_weight;  // Même chose que l'heuristique mais log
        features[4] = log1p_value - log1p_capacity;
        features[5] = log1p_weight - log1p_capacity;

        features[6] = Math.log1p(state.remainingCapacity() - weight);

        features[7] = (weight < state.remainingCapacity() * 0.1) ? 1.0 : 0.0;
        features[8] = (weight > state.remainingCapacity() * 0.5) ? 1.0 : 0.0;

        features[9] = ((double) state.depth() / state.nbVars()) * (log1p_value - log1p_weight);
        features[10] = ((double) state.remainingAvailable() / state.nbVars()) * (log1p_value - log1p_weight);

        return features;
    }
}
