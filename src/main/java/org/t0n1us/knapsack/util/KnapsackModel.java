package org.t0n1us.knapsack.util;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

public class KnapsackModel {

    public static Model buildKnapsackModel(KnapsackInstance instance) {
        Model model = new Model("Knapsack");
        BoolVar[] x = model.boolVarArray("x", instance.n_items());

        IntVar totalWeight = model.intVar("totalWeight", 0, instance.capacity());
        model.scalar(x, instance.weights(), "=", totalWeight).post();

        model.arithm(totalWeight, "<=", instance.capacity()).post();

        int maxValue = 0;

        for (int v : instance.values())
            maxValue += v;

        IntVar totalValue = model.intVar("totalValue", 0, maxValue);
        model.scalar(x, instance.values(), "=", totalValue).post();

        return model;
    }

    public static IntVar getTotalValue(Model model) {
        for (Variable v : model.getVars()) {
            if ("totalValue".equals(v.getName())) {
                return (IntVar) v;
            }
        }
        throw new IllegalStateException("totalValue not found in model");
    }

}
