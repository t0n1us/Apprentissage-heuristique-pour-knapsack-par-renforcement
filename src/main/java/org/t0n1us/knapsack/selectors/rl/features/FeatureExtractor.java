package org.t0n1us.knapsack.selectors.rl.features;

public abstract class FeatureExtractor {

    protected final int n_features;

    public FeatureExtractor(int n_features) {
        this.n_features = n_features;
    }

    public int getFeaturesSize() {
        return this.n_features;
    }

    public abstract double[] getFeatures(int value, int weight, State state);

}
