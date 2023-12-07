package de.nav.sudoku.solving.util;

import java.util.ArrayList;
import java.util.List;

public class InverseMetaValueUtil {

    static public int getNumbersValue(List<Integer> numbers) {
        int numbersValue = 0b0;
        for (int number : numbers)
            numbersValue += 1 << (number - 1);
        return numbersValue;
    }

    static public List<Integer> getAffectedNumbers(int[] inverseMetaValues, int inverseMetaValue) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            if (inverseMetaValues[i] == inverseMetaValue)
                numbers.add(i + 1);
        return numbers;
    }

    static public List<Integer> getAffectedFields(int inverseMetaValue) {
        List<Integer> fields = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            if (((inverseMetaValue >> i) & 1) != 1)
                fields.add(i);
        return fields;
    }
}