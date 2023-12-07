package de.nav.sudoku;

import de.nav.sudoku.solving.SudokuSolver;
import de.nav.sudoku.solving.util.TableUtil;

public class Main {

    public static void main(String[] args) {
        int[][] sudoku =
                {{0,7,0,0,0,0,4,0,5},
                        {0,0,0,0,0,1,0,0,6},
                        {2,0,0,0,7,0,0,0,0},
                        {0,0,4,2,0,0,0,0,8},
                        {0,0,0,7,0,0,0,1,0},
                        {1,3,0,0,0,5,0,0,9},
                        {0,0,0,5,0,0,1,0,0},
                        {9,0,0,3,0,0,0,6,0},
                        {6,0,0,0,0,0,0,0,4}};

        SudokuSolver solver = new SudokuSolver();

        int[][] solvedTable = solver.solve(sudoku, 0);

        if (solvedTable != null) {
            System.out.println("Found solution: ");
            TableUtil.printTable(solvedTable);
        } else {
            System.out.println("Sudoku is not solvable");
        }

    }

}
