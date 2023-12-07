package de.nav.sudoku.solving;

public class SudokuChecker {

    protected boolean isSolved(int[][] table) {
        for (int row = 0; row < 9; row++)
            for (int col = 0; col < 9; col++)
                if (table[row][col] == 0)
                    return false;
        return true;
    }

    protected boolean isValid(int[][] table) {
        for (int row = 0; row < 9; row++)
            if (!isRowValid(table, row))
                return false;
        for (int col = 0; col < 9; col++)
            if (!isColumnValid(table, col))
                return false;
        for (int row = 0; row < 9; row += 3)
            for (int col = 0; col < 9; col += 3)
                if (!isBoxValid(table, row, col))
                    return false;
        return true;
    }

    private boolean isRowValid(int[][] table, int row) {
        boolean[] numbersPresent = new boolean[9];
        for (int col = 0; col < 9; col++)
            if (numberIsAlreadyPresent(numbersPresent, table[row][col]))
                return false;
        return true;
    }

    private boolean isColumnValid(int[][] table, int col) {
        boolean[] numbersPresent = new boolean[9];
        for (int row = 0; row < 9; row++)
            if (numberIsAlreadyPresent(numbersPresent, table[row][col]))
                return false;
        return true;
    }

    private boolean isBoxValid(int[][] table, int row, int col) {
        row = (row / 3) * 3;
        col = (col / 3) * 3;
        boolean[] numbersPresent = new boolean[9];
        for (int rowIncrement = 0; rowIncrement < 3; rowIncrement++)
            for (int colIncrement = 0; colIncrement < 3; colIncrement++)
                if (numberIsAlreadyPresent(numbersPresent, table[row + rowIncrement][col + colIncrement]))
                    return false;
        return true;
    }

    private boolean numberIsAlreadyPresent(boolean[] numbersPresent, int i) {
        if (i > 0) {
            int number = i - 1;
            if (numbersPresent[number])
                return true;
            numbersPresent[number] = true;
        }
        return false;
    }

    protected boolean matchesWithInitialTable(int[][] initialTable, int[][] solution) {
        for (int row = 0; row < 9; row++)
            for (int col = 0; col < 9; col++)
                if (initialTable[row][col] != 0)
                    if (initialTable[row][col] != solution[row][col])
                        return false;
        return true;
    }

    protected boolean hasNoDeadEnds(int[][] solution, int[][] metaValueTable) {
        for (int row = 0; row < 9; row++)
            for (int col = 0; col < 9; col++)
                if (solution[row][col] == 0) // No number found for solution
                    if (metaValueTable[row][col] == 0) // And no number possible anymore
                        return false;
        return true;
    }

}
