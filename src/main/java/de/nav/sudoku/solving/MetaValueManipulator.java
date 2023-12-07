package de.nav.sudoku.solving;

import de.nav.sudoku.solving.util.BitUtil;

import java.util.Arrays;

public class MetaValueManipulator {

    protected void initializeMetaValuesWithTable(int[][] table, int[][] metaValueTable) {
        int defaultMetaValue = 0b111111111;
        for (int[] row : metaValueTable)
            Arrays.fill(row, defaultMetaValue);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (table[row][col] != 0 && table[row][col] <= 9) {
                    metaValueTable[row][col] = 0;

                    int value = table[row][col];
                    removeBitsFromRow(metaValueTable, row, BitUtil.singleBit(value), false);
                    removeBitsFromColumn(metaValueTable, col, BitUtil.singleBit(value), false);
                    removeBitsFromBox(metaValueTable, row, col, BitUtil.singleBit(value), false);
                }
            }
        }
    }

    protected void removeBitsFromRow(int[][] metaValueTable, int row, int value, boolean exceptSelf) {
        for (int col = 0; col < 9; col++)
            if (!exceptSelf || metaValueTable[row][col] != value)
                metaValueTable[row][col] = removeBitsFromMetaValue(metaValueTable[row][col], value);
    }

    protected void removeBitsFromColumn(int[][] metaValueTable, int col, int value, boolean exceptSelf) {
        for (int row = 0; row < 9; row++)
            if (!exceptSelf || metaValueTable[row][col] != value)
                metaValueTable[row][col] = removeBitsFromMetaValue(metaValueTable[row][col], value);
    }

    protected void removeBitsFromBox(int[][] metaValueTable, int row, int col, int value, boolean exceptSelf) {
        int cornerRow = (row / 3) * 3;
        int cornerCol = (col / 3) * 3;
        for (int colIncrement = 0; colIncrement < 3; colIncrement++)
            for (int rowIncrement = 0; rowIncrement < 3; rowIncrement++) {
                int currentValue = metaValueTable[cornerRow + rowIncrement][cornerCol + colIncrement];
                if (!exceptSelf || currentValue != value)
                    metaValueTable[cornerRow + rowIncrement][cornerCol + colIncrement] =
                            removeBitsFromMetaValue(currentValue, value);
            }
    }

    protected int removeBitsFromMetaValue(int metaValue, int bits) {
        return (~bits & metaValue);
    }
}