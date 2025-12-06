package org.t0n1us.knapsack.selectors.rl.features;

public class SimpleExtractor extends FeatureExtractor {

    public SimpleExtractor() {
        super(14);
    }

    @Override
    public double[] getFeatures(int value, int weight, State state) {
        double[] features = new double[this.n_features];

        features[0] = 1;  // Biais
        features[1] = value;
        features[2] = weight;
        features[3] = (double) value / weight;  // Même chose que l'heuristique

        features[4] = Math.log1p(value);  // log1p des valeur pour les valeurs très grandes
        features[5] = Math.log1p(weight);  // log1p des poids pour les poids très grands

        features[6] = state.remainingCapacity();  // Capacité restante
        features[7] = (double) value / state.remainingCapacity();
        features[8] = (double) weight / state.remainingCapacity();
        features[9] = (double) state.remainingCapacity() / weight;

        features[10] = state.depth();  // Profondeur de l'arbre de recherche
        features[11] = (double) state.depth() / state.nbVars();  // Profondeur normalisée sur le nombre de variables totales

        features[12] = state.remainingAvailable();  // Nombre d'objets restants
        features[13] = state.remainingCapacity() - weight;  //  Capacité restante après le poid

        return features;
    }
}
