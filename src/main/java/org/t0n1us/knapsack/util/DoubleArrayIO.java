package org.t0n1us.knapsack.util;

import java.io.*;

public class DoubleArrayIO {

    public static void save(double[] data, String filename) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            out.writeInt(data.length);
            for (double v : data) out.writeDouble(v);
        }
    }

    public static double[] load(String filename) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int size = in.readInt();
            double[] data = new double[size];
            for (int i = 0; i < size; i++) data[i] = in.readDouble();
            return data;
        }
    }
}