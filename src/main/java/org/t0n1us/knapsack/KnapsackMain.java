package org.t0n1us.knapsack;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.t0n1us.knapsack.selectors.HeuristicVariableSelector;
import org.t0n1us.knapsack.selectors.rl.RLVariableSelector;
import org.t0n1us.knapsack.util.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class KnapsackMain {

    public static void main(String[] args) {
        try {
            double[] weights = RLTrainWrapper.train(5, 0.001, 0.05, 0.95, 0.30, 0.05, 1000);
            System.out.println(Arrays.toString(weights));


//            Result result_test = solve("Default", null, KnapsackInstance.load_from_json("instance_facile_test.json"), 5000);
//            System.out.println(result_test);
//
//            Result result_heuristic = solve("Heuristic", HeuristicVariableSelector.class, KnapsackInstance.load_from_json("instances/instance_facile_test.json"), 5000);
//            System.out.println(result_heuristic);

//            Result result_rl = solve("RL", RLVariableSelector.class, KnapsackInstance.load_from_json("instances/instance_facile_test.json"), 5000);
//            System.out.println(result_rl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO va falloir gerer le RLSelector
    public static Result solve(String selectorName, Class<? extends VariableSelector<IntVar>> variableSelectorClass, KnapsackInstance instance, long ms_timelimit) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Model model = KnapsackModel.buildKnapsackModel(instance);
        Solver solver = model.getSolver();

        solver.limitTime(ms_timelimit);

        BoolVar[] x = model.retrieveBoolVars();
        IntVar totalValue = KnapsackModel.getTotalValue(model);

        if (variableSelectorClass != null) {
            IntValueSelector valSelector = var -> 1;

            VariableSelector<IntVar> varSelector = variableSelectorClass.getConstructor(IntVar[].class, int[].class, int[].class).newInstance(x, instance.weights(), instance.values());

            solver.setSearch(Search.intVarSearch(varSelector, valSelector, x));
        }

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

        return new Result(selectorName, bestValue, xSol, nodes, time);
    }

}
