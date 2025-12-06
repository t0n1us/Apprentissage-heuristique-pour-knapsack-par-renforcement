package org.t0n1us.knapsack.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public record KnapsackInstance(int n_items, int[] weights, int[] values, int capacity) {

    public static KnapsackInstance load_from_json(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filename), KnapsackInstance.class);
    }

}
