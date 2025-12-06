package org.t0n1us.knapsack.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public record KnapsackInstance(int n_items, int[] weights, int[] values, int capacity) {

    public static KnapsackInstance load_from_json(String filename) throws IOException {
        InputStream input_file = KnapsackInstance.class.getResourceAsStream("/" + filename);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(input_file, KnapsackInstance.class);
    }

}
