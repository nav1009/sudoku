package de.nav.sudoku.solving.util;

import java.util.Arrays;

public class TableUtil {

    static public int[][] copyTable(int[][] table) {
        return Arrays.stream(table).map(int[]::clone).toArray(int[][]::new);
    }

    static public boolean tablesAreEqual(int[][] table1, int[][] table2) {
        for (int row = 0; row < 9; row++)
            for (int col = 0; col < 9; col++)
                if (table1[row][col] != table2[row][col])
                    return false;
        return true;
    }

    static public void printTable(int[][] table) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int val = table[row][col];
                if (val < 10) System.out.print("  ");
                else if (val < 100) System.out.print(" ");
                System.out.print(val + " ");
                if (col == 2 || col == 5)
                    System.out.print("|");
            }
            System.out.println();
            if (row == 2 || row == 5)
                System.out.println("––––––––––––––––––––––––––––––––––––––");
        }
        System.out.println();
    }
}