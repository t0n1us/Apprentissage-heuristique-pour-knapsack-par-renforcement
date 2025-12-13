package org.t0n1us.knapsack;

import org.t0n1us.knapsack.selectors.rl.RLParams;
import org.t0n1us.knapsack.selectors.rl.features.SimpleExtractor;
import org.t0n1us.knapsack.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KnapsackMain {

    public static void main(String[] args) {
        try {
             gridTrainWeights();  // Boucle d'entrainnement en grille

            List<KnapsackInstance> dataset = KnapsackDataset.getTestDataset();
            double[] weights = DoubleArrayIO.load("src/main/resources/saved_weights/e5_lr0.0020_a0.050_l0.00_eps0.95_epsmin0.10_wd0.00050_do0.00_nf200ms.weights");

            for (int ms_timelimist = 150; ms_timelimist <= 250; ms_timelimist += 10){
                List<Result> results_rl = Inference.solveWithRL(weights, new SimpleExtractor(), ms_timelimist, dataset);
                List<Result> results_heuristic = Inference.solveWithHeuristic(ms_timelimist, dataset);
                List<Result> results_default = Inference.solveDefault(ms_timelimist, dataset);

                Path folder = Paths.get("src/main/resources/résultats/" + ms_timelimist);

                Files.createDirectory(folder);

                List<Result> allResults = new ArrayList<>();
                allResults.addAll(results_rl);
                allResults.addAll(results_heuristic);
                allResults.addAll(results_default);

                for (Result r : allResults)
                    r.toJsonFile(folder.resolve(r.name()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void gridTrainWeights() throws IOException {
        int[] grid_epochs = {1, 3, 5};
        long[] grid_ms_timelimit = {100, 200};

        SimpleExtractor extractor = new SimpleExtractor();

        RLParams[] grid_params = {
                new RLParams(1e-3, 0.05, 1e-3, 0.5, 0.05, 5e-4, 0.1, extractor.getFeaturesSize()),
                new RLParams(8e-4, 0.05, 8e-4, 0.5, 0.03, 3e-4, 0.05, extractor.getFeaturesSize()),
                new RLParams(1e-3, 0.05, 1e-3, 0.5, 0.05, 5e-4, 0.10, extractor.getFeaturesSize()),
                new RLParams(1.2e-3, 0.05, 1.5e-3, 0.5, 0.07, 8e-4, 0.15, extractor.getFeaturesSize()),
                new RLParams(5e-4, 0.05, 5e-4, 0.5, 0.02, 2e-4, 0.00, extractor.getFeaturesSize()),
                new RLParams(3e-4, 0.05, 2e-4, 0.5, 0.01, 1e-4, 0.00, extractor.getFeaturesSize()),
                new RLParams(7e-4, 0.05, 5e-4, 0.5, 0.03, 3e-4, 0.05, extractor.getFeaturesSize()),
                new RLParams(1.5e-3, 0.05, 2e-3, 0.5, 0.08, 1e-3, 0.20, extractor.getFeaturesSize()),
                new RLParams(2e-3, 0.05, 3e-3, 0.5, 0.10, 5e-4, 0.00, extractor.getFeaturesSize()),
                new RLParams(6e-4, 0.05, 1e-4, 0.5, 0.02, 8e-4, 0.30, extractor.getFeaturesSize())
        };

        double best_avg = 0.0;
        double[] best_weights = null;
        String best_model_name = "";
        String folder_path = "src/main/resources/saved_weights/";

        for (int epochs : grid_epochs) {
            for (long ms_timelimit : grid_ms_timelimit) {
                for (RLParams params : grid_params) {
                    TrainResult train_result = RLTrainWrapper.train(epochs, params, ms_timelimit, extractor);

                    if (train_result.lastBestAvg() > best_avg) {
                        best_avg = train_result.lastBestAvg();
                        best_weights = train_result.weights();
                        best_model_name = String.format(
                                "e%d_lr%.4f_a%.3f_l%.2f_eps%.2f_epsmin%.2f_wd%.5f_do%.2f_nf%dms.weights",
                                epochs,
                                params.learning_rate,
                                params.baseline_memorisation,
                                params.epsilon_decay,
                                params.epsilon,
                                params.epsilon_min,
                                params.weight_decay,
                                params.dropout,
                                ms_timelimit
                        );
                    }
                }
            }
        }

        DoubleArrayIO.save(best_weights, folder_path + best_model_name);
    }
}
