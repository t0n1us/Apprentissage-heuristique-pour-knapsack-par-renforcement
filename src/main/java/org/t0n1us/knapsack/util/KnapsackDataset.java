package org.t0n1us.knapsack.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KnapsackDataset {

    private static List<KnapsackInstance> getDataset(String directory) throws IOException {
        List<KnapsackInstance> train_dataset = new ArrayList<>();

        File dir = new File("src/main/resources/instances/" + directory);

        if (!dir.exists() || !dir.isDirectory())
            throw new IOException("Directory not found: " + dir.getAbsolutePath());

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (files == null)
            throw new IOException("Failed to list files in: " + dir.getAbsolutePath());

        for (File file : files) {
            train_dataset.add(
                    KnapsackInstance.load_from_json(file.getAbsolutePath())
            );
        }

        return train_dataset;
    }


    public static List<KnapsackInstance> getTrainDataset(boolean shuffle) throws IOException {
        List<KnapsackInstance> trainDataset = getDataset("train");

        if (shuffle)
            Collections.shuffle(trainDataset);

        return trainDataset;
    }

    public static List<KnapsackInstance> getTestDataset() throws IOException {
//        List<KnapsackInstance> x = new ArrayList<>();
//        x.add(getDataset("test").getFirst());
        return getDataset("test");
    }

    public static List<KnapsackInstance> getTrickyDataset() throws IOException {
//        List<KnapsackInstance> x = new ArrayList<>();
//        x.add(getDataset("test_tricky").getFirst());
        return getDataset("test_tricky");
    }
}
