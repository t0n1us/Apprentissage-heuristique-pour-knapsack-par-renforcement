package org.t0n1us.knapsack.selectors.rl.features;

import java.util.List;

public record State(int remainingCapacity, int depth, int nbVars, int remainingAvailable) {
}
