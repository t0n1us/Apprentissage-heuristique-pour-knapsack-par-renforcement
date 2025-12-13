package org.t0n1us.knapsack.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;

public record Result(String name, int bestValue, int[] x, long nodes, long timeMillis, long ms_timelimit) {
    public void toJsonFile(Path outputPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(outputPath.toFile(), this);
    }
}
