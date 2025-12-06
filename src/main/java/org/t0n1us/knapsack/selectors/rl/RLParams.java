package org.t0n1us.knapsack.selectors.rl;

public class RLParams {

    public final double learning_rate;
    public final double alpha;
    public final double lambda;

    public double epsilon;
    public final double epsilon_min;
    public double[] weights;

    public RLParams(double learning_rate, double alpha, double lambda, double epsilon, double epsilon_min, int n_features) {
        this.learning_rate = learning_rate;
        this.alpha = alpha;
        this.lambda = lambda;
        this.epsilon_min = epsilon_min;

        this.epsilon = epsilon;
        this.weights = new double[n_features];  // initialisé à 0 par défaut
    }

    public void decayEpsilon() {
        this.epsilon = this.epsilon_min + (this.epsilon - this.epsilon_min) * Math.exp(-this.lambda);
    }

}
