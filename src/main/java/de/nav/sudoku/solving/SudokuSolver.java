package de.nav.sudoku.solving;

import de.nav.sudoku.solving.util.BitUtil;
import de.nav.sudoku.solving.util.FrequencyMapUtil;
import de.nav.sudoku.solving.util.TableUtil;
import de.nav.sudoku.solving.util.InverseMetaValueUtil;

import java.util.*;

/**
 * Core concepts needed to understand the code:
 *
 * Meta values are a binary representation of what numbers can still go into a field of a Sudoku.
 * That means each field of the Sudoku has exactly one meta value.
 * For example, a meta value of decimal 22 / binary 10110 means that the numbers 2, 3 and 5 can
 * still go into the corresponding field, since those bits are set.
 *
 * Inverse meta values are technically not exactly the inverse of meta values, but close.
 * Each inverse meta value is a binary representation of into what fields of a specific
 * group (row, column or box) a number can still go.
 * So meta values must be viewed in the context of the whole Sudoku table, while inverse
 * meta values must be viewed in the context of only a subgroup (row, column or box) of a
 * Sudoku table.
 * An inverse meta value of decimal 22 / binary 10110 would mean, that it's corresponding number
 * can still go into the fields 2, 3 and 5 of the corresponding group (row, column or box).
 */
public class SudokuSolver {

    int[][] initialTable;
    int[][] solutionTable;
    int[][] metaValueTable;

    private MetaValueManipulator metaValueManipulator;

    int level;

    public int[][] solve(int[][] sudoku, int level) {
        initialTable = sudoku;
        this.level = level;
        solutionTable = TableUtil.copyTable(initialTable);
        metaValueTable = new int[9][9];

        metaValueManipulator = new MetaValueManipulator();
        metaValueManipulator.initializeMetaValuesWithTable(initialTable, metaValueTable);

        SudokuChecker sudokuChecker = new SudokuChecker();

        if (!sudokuChecker.hasNoDeadEnds(solutionTable, metaValueTable))
            return null;

        int steps = 0;
        int[][] lastMetaValues = TableUtil.copyTable(metaValueTable);
        int[][] lastSolution = TableUtil.copyTable(solutionTable);
        while (true) {
            solutionStep();
            steps++;
            if (TableUtil.tablesAreEqual(lastMetaValues, metaValueTable)
                    && TableUtil.tablesAreEqual(lastSolution, solutionTable))
                break;
            lastMetaValues = TableUtil.copyTable(metaValueTable);
            lastSolution = TableUtil.copyTable(solutionTable);
        }
        System.out.println("Took " + steps + " step(s) with logic on level " + this.level);

        if (!sudokuChecker.isSolved(solutionTable))
            bifurcate();

        if (sudokuChecker.isSolved(solutionTable)
                && sudokuChecker.isValid(solutionTable)
                && sudokuChecker.matchesWithInitialTable(initialTable, solutionTable))
            return solutionTable;
        else
            return null;
    }

    private void bifurcate() {
        int[][] foundSolution = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (solutionTable[row][col] != 0) continue;
                for (int number = 0; number < 9; number++) {
                    if (BitUtil.valueHasBitSet(metaValueTable[row][col], number)) {
                        int[][] attemptTable = TableUtil.copyTable(solutionTable);
                        attemptTable[row][col] = number + 1;

                        SudokuSolver solver = new SudokuSolver();
                        attemptTable = solver.solve(attemptTable, this.level + 1);

                        if (attemptTable != null) {
                            System.out.println("Found working solution by bifurcation on level " + this.level);
                            System.out.println("Attempted for " + row + " / " + col + " with number " + (number + 1));
                            solutionTable = attemptTable;
                            foundSolution = TableUtil.copyTable(solutionTable);
                        }
                    }
                }
            }
        }
        if (!TableUtil.tablesAreEqual(foundSolution, solutionTable))
            solutionTable = new int[9][9];
    }

    private void solutionStep() {
        checkForSingles();
        for (int row = 0; row < 9; row++) {
            checkRows(row);
            checkRowsInverse(row);
        }
        for (int column = 0; column < 9; column++) {
            checkColumns(column);
            checkColumnsInverse(column);
        }
        for (int cornerRow = 0; cornerRow < 9; cornerRow += 3)
            for (int cornerColumn = 0; cornerColumn < 9; cornerColumn += 3) {
                checkBoxes(cornerRow, cornerColumn);
                checkBoxesInverse(cornerRow, cornerColumn);
            }
    }

    private void checkForSingles() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (BitUtil.isPowerOfTwo(metaValueTable[row][col])) {

                    int bits = metaValueTable[row][col];
                    metaValueManipulator.removeBitsFromRow(metaValueTable, row, bits, false);
                    metaValueManipulator.removeBitsFromColumn(metaValueTable, col, bits, false);
                    metaValueManipulator.removeBitsFromBox(metaValueTable, row, col, bits, false);

                    metaValueTable[row][col] = 0;

                    int value = 0;
                    while (bits != 0) {
                        value++;
                        bits >>= 1;
                    }
                    solutionTable[row][col] = value;
                }
            }
        }
    }

    private void checkRows(int row) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int col = 0; col < 9; col++)
            FrequencyMapUtil.addToFrequencyMap(frequencyMap, metaValueTable[row][col]);
        for (var entry : frequencyMap.entrySet()) {
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue()))
                metaValueManipulator.removeBitsFromRow(metaValueTable, row, entry.getKey(), true);
        }
    }

    private void checkRowsInverse(int row) {
        int[] inverseMetaValues = new int[9];
        Arrays.fill(inverseMetaValues, 0);
        for (int col = 0; col < 9; col++) {
            int value = metaValueTable[row][col];
            for (int number = 0; number < 9; number++)
                if (BitUtil.valueHasBitSet(value, number))
                    inverseMetaValues[number] = BitUtil.setBitOfValue(inverseMetaValues[number], col);
        }

        Map<Integer, Integer> frequencyMap = FrequencyMapUtil.createFrequencyMapOfInverseMetaValues(inverseMetaValues);

        for (var entry : frequencyMap.entrySet()) {
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue())) {
                List<Integer> fields = InverseMetaValueUtil.getAffectedFields(entry.getKey());
                List<Integer> numbers = InverseMetaValueUtil.getAffectedNumbers(inverseMetaValues, entry.getKey());
                int numbersValue = InverseMetaValueUtil.getNumbersValue(numbers);
                for (int number : numbers) {
                    for (int col = 0; col < 9; col++) {
                        if (fields.contains(col))
                            metaValueTable[row][col] = metaValueManipulator.removeBitsFromMetaValue(metaValueTable[row][col], BitUtil.singleBit(number));
                        else
                            metaValueTable[row][col] = numbersValue;
                    }
                }
            }
        }
    }

    private void checkColumns(int col) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int row = 0; row < 9; row++)
            FrequencyMapUtil.addToFrequencyMap(frequencyMap, metaValueTable[row][col]);
        for (var entry : frequencyMap.entrySet()) {
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue()))
                metaValueManipulator.removeBitsFromColumn(metaValueTable, col, entry.getKey(), true);
        }
    }

    private void checkColumnsInverse(int col) {
        int[] inverseMetaValues = new int[9];
        Arrays.fill(inverseMetaValues, 0);
        for (int row = 0; row < 9; row++) {
            int value = metaValueTable[row][col];
            for (int number = 0; number < 9; number++)
                if (BitUtil.valueHasBitSet(value, number))
                    inverseMetaValues[number] = BitUtil.setBitOfValue(inverseMetaValues[number], row);
        }

        Map<Integer, Integer> frequencyMap = FrequencyMapUtil.createFrequencyMapOfInverseMetaValues(inverseMetaValues);

        for (var entry : frequencyMap.entrySet()) {
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue())) {
                List<Integer> fields = InverseMetaValueUtil.getAffectedFields(entry.getKey());
                List<Integer> numbers = InverseMetaValueUtil.getAffectedNumbers(inverseMetaValues, entry.getKey());
                int numbersValue = InverseMetaValueUtil.getNumbersValue(numbers);
                for (int number : numbers)
                    for (int i = 0; i < 9; i++) {
                        if (fields.contains(i))
                            metaValueTable[i][col] = metaValueManipulator.removeBitsFromMetaValue(metaValueTable[i][col], BitUtil.singleBit(number));
                        else
                            metaValueTable[i][col] = numbersValue;
                }
            }
        }
    }

    private void checkBoxes(int cornerRow, int cornerColumn) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int colIncrement = 0; colIncrement < 3; colIncrement++)
            for (int rowIncrement = 0; rowIncrement < 3; rowIncrement++)
                FrequencyMapUtil.addToFrequencyMap(frequencyMap, metaValueTable[cornerRow + rowIncrement][cornerColumn + colIncrement]);
        for (var entry : frequencyMap.entrySet())
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue()))
                metaValueManipulator.removeBitsFromBox(metaValueTable, cornerRow, cornerColumn, entry.getKey(), true);
    }

    private void checkBoxesInverse(int cornerRow, int cornerColumn) {
        int[] inverseMetaValues = new int[9];
        Arrays.fill(inverseMetaValues, 0);
        for (int colIncrement = 0; colIncrement < 3; colIncrement++)
            for (int rowIncrement = 0; rowIncrement < 3; rowIncrement++) {
                int metaValue = metaValueTable[cornerRow + rowIncrement][cornerColumn + colIncrement];
                for (int number = 0; number < 9; number++)
                    if (BitUtil.valueHasBitSet(metaValue, number))
                        inverseMetaValues[number] = BitUtil.setBitOfValue(inverseMetaValues[number], colIncrement + 3 * rowIncrement);
            }

        Map<Integer, Integer> frequencyMap = FrequencyMapUtil.createFrequencyMapOfInverseMetaValues(inverseMetaValues);

        for (var entry : frequencyMap.entrySet()) {
            if (BitUtil.bitCountEquals(entry.getKey(), entry.getValue())) {
                List<Integer> fields = InverseMetaValueUtil.getAffectedFields(entry.getKey());
                List<Integer> numbers = InverseMetaValueUtil.getAffectedNumbers(inverseMetaValues, entry.getKey());
                int numbersValue = InverseMetaValueUtil.getNumbersValue(numbers);
                for (int number : numbers) {
                    for (int i = 0; i < 9; i++) {
                        int row = cornerRow + (i / 3);
                        int col = cornerColumn + (i % 3);
                        if (fields.contains(i))
                            metaValueTable[row][col] = metaValueManipulator.removeBitsFromMetaValue(metaValueTable[row][col], BitUtil.singleBit(number));
                        else
                            metaValueTable[row][col] = numbersValue;
                    }
                }
            }
        }
    }
}
