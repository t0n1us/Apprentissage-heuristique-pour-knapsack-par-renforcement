package org.t0n1us.knapsack.selectors.rl;

public class RLParams {

    public final double learning_rate;  // Taux d'apprentissage des poids
    public final double baseline_memorisation;  // Taux de mémorisation du baseline
    public final double epsilon_decay;  // Taux d'oubli de epsilon (pour le epsilon-greedy)
    public final double weight_decay;  // Régularisation l2  [0.0001 et 0.0005]
    public final double dropout;  // Probabilité de "dropout" donc de ne pas appliquer de correction de poids pour un step

    public double epsilon;  // Probabilité d'exploré
    public final double epsilon_min;  // Probabilité minimum d'explorer
    public double[] weights;  // Les poids

    public RLParams(double learning_rate, double baseline_memorisation, double epsilon_decay, double epsilon, double epsilon_min, double weight_decay, double dropout, int n_features) {
        this.learning_rate = learning_rate;
        this.baseline_memorisation = baseline_memorisation;
        this.epsilon_decay = epsilon_decay;
        this.epsilon_min = epsilon_min;
        this.weight_decay = weight_decay;
        this.dropout = dropout;

        this.epsilon = epsilon;
        this.weights = new double[n_features];  // initialisé à 0 par défaut
    }

    public void decayEpsilon() {
        this.epsilon = this.epsilon_min + (this.epsilon - this.epsilon_min) * Math.exp(-this.epsilon_decay);
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

}
