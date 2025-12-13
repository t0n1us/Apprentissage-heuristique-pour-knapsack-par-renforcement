package org.t0n1us.knapsack.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public record KnapsackInstance(@JsonIgnore String filename, int n_items, int[] weights, int[] values, int capacity) {

    public static KnapsackInstance load_from_json(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        KnapsackInstance tmp = mapper.readValue(new File(filename), KnapsackInstance.class);
        String name = Path.of(filename).getFileName().toString();
        return new KnapsackInstance(name, tmp.n_items(), tmp.weights(), tmp.values(), tmp.capacity());
    }

}
