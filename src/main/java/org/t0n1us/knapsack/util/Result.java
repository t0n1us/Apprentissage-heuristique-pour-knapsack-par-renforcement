package org.t0n1us.knapsack.util;

public record Result(String name, int bestValue, int[] x, long nodes, long timeMillis) {
    // TODO export to json
}
