package com.technicman.techo.util;

import java.util.Arrays;
import java.util.List;

public interface Weighted {
    int getWeight();
    static Weighted randomChoice(List<? extends Weighted> list, int seed) {
        int[] weights = new int[list.size()];
        weights[0] = list.get(0).getWeight();
        for (int i=1; i< list.size(); i++) {
            weights[i] = weights[i-1] + list.get(0).getWeight();
        }
        return list.get( Arrays.binarySearch(weights, seed % weights[weights.length-1]) + 1 );
    }

    static void validateWeight(int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Expected weight to be greater than 0 (current value: " + weight + ")");
        }
    }
}
