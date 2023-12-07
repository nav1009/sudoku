package de.nav.sudoku.solving.util;

import java.util.HashMap;
import java.util.Map;

public class FrequencyMapUtil {

    static public Map<Integer, Integer> createFrequencyMapOfInverseMetaValues(int[] inverseMetaValues) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int number = 0; number < 9; number++)
            addToFrequencyMap(frequencyMap, inverseMetaValues[number]);
        return frequencyMap;
    }

    static public void addToFrequencyMap(Map<Integer, Integer> frequencyMap, int value) {
        if (frequencyMap.containsKey(value))
            frequencyMap.put(value, frequencyMap.get(value) + 1);
        else
            frequencyMap.put(value, 1);
    }
}