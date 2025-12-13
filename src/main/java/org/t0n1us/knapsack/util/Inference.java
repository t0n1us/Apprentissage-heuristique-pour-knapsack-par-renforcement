package org.t0n1us.knapsack.util;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.t0n1us.knapsack.selectors.HeuristicVariableSelector;
import org.t0n1us.knapsack.selectors.rl.RLParams;
import org.t0n1us.knapsack.selectors.rl.RLVariableSelector;
import org.t0n1us.knapsack.selectors.rl.features.FeatureExtractor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Inference {

    public static List<Result> solveWithRL(double[] trained_weights, FeatureExtractor extractor, long ms_timelimit, List<KnapsackInstance> dataset) throws IOException {
        RLParams params = new RLParams(0, 0, 0.5, 0, 0, 0, 0, extractor.getFeaturesSize());
        params.setWeights(trained_weights);

        List<Result> results = new ArrayList<>();

        int k = 1;

        for (KnapsackInstance instance : dataset) {
            Model model = KnapsackModel.buildKnapsackModel(instance);
            Solver solver = model.getSolver();

            BoolVar[] x = model.retrieveBoolVars();
            IntVar totalValue = KnapsackModel.getTotalValue(model);

            int[] weights = instance.weights();
            int[] values = instance.values();
            int maxCapacity = instance.capacity();

            RLVariableSelector rlSelector =
                    new RLVariableSelector(x, weights, values, maxCapacity, params, extractor);

            IntValueSelector valSelector = var -> 1;

            solver.setSearch(Search.intVarSearch(rlSelector, valSelector, x));
            solver.limitTime(ms_timelimit);

            long t0 = System.currentTimeMillis();
            Solution solution = solver.findOptimalSolution(totalValue, true);
            long t1 = System.currentTimeMillis();

            int[] xSol = new int[x.length];
            for (int i = 0; i < x.length; i++) {
                xSol[i] = solution.getIntVal(x[i]);
            }

            int bestValue = solution.getIntVal(totalValue);
            long nodes = solver.getMeasures().getNodeCount();
            long time = t1 - t0;

            String name = "RL_" + k + "_" + instance.filename();

            results.add(new Result(name, bestValue, xSol, nodes, time, ms_timelimit));

            k++;
        }

        return results;
    }

    public static List<Result> solveWithHeuristic(long ms_timelimit, List<KnapsackInstance> dataset) throws IOException {
        List<Result> results = new ArrayList<>();
        int k = 1;

        for (KnapsackInstance instance : dataset) {
            Model model = KnapsackModel.buildKnapsackModel(instance);
            Solver solver = model.getSolver();

            BoolVar[] x = model.retrieveBoolVars();
            IntVar totalValue = KnapsackModel.getTotalValue(model);

            int[] weights = instance.weights();
            int[] values = instance.values();

            HeuristicVariableSelector heuristicVariableSelector = new HeuristicVariableSelector(x, weights, values);
            IntValueSelector valSelector = var -> 1;

            solver.setSearch(Search.intVarSearch(heuristicVariableSelector, valSelector, x));
            solver.limitTime(ms_timelimit);

            long t0 = System.currentTimeMillis();
            Solution solution = solver.findOptimalSolution(totalValue, true);
            long t1 = System.currentTimeMillis();

            int[] xSol = new int[x.length];
            for (int i = 0; i < x.length; i++) {
                xSol[i] = solution.getIntVal(x[i]);
            }

            int bestValue = solution.getIntVal(totalValue);
            long nodes = solver.getMeasures().getNodeCount();
            long time = t1 - t0;

            String name = "Heuristic_" + k + "_" + instance.filename();

            results.add(new Result(name, bestValue, xSol, nodes, time, ms_timelimit));

            k++;
        }

        return results;
    }

    public static List<Result> solveDefault(long ms_timelimit, List<KnapsackInstance> dataset) throws IOException {
        List<Result> results = new ArrayList<>();
        int k = 1;

        for (KnapsackInstance instance : dataset) {
            Model model = KnapsackModel.buildKnapsackModel(instance);
            Solver solver = model.getSolver();

            BoolVar[] x = model.retrieveBoolVars();
            IntVar totalValue = KnapsackModel.getTotalValue(model);

            solver.limitTime(ms_timelimit);

            long t0 = System.currentTimeMillis();
            Solution solution = solver.findOptimalSolution(totalValue, true);
            long t1 = System.currentTimeMillis();

            int[] xSol = new int[x.length];
            for (int i = 0; i < x.length; i++) {
                xSol[i] = solution.getIntVal(x[i]);
            }

            int bestValue = solution.getIntVal(totalValue);
            long nodes = solver.getMeasures().getNodeCount();
            long time = t1 - t0;

            String name = "Default_" + k + "_" + instance.filename();

            results.add(new Result(name, bestValue, xSol, nodes, time, ms_timelimit));

            k++;
        }

        return results;
    }

}
