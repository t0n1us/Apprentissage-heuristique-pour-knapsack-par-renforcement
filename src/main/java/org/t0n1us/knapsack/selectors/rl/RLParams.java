package org.t0n1us.knapsack.selectors.rl;

public class RLParams {

    public final float learning_rate;
    public final float alpha;
    public final float lambda;

    public float epsilon;
    public double[] weights;

    public RLParams(float learning_rate, float alpha, float lambda, float epsilon, int n_features) {
        this.learning_rate = learning_rate;
        this.alpha = alpha;
        this.lambda = lambda;

        this.epsilon = epsilon;
        this.weights = new double[n_features];  // initialisé à 0 par défaut
    }

}
