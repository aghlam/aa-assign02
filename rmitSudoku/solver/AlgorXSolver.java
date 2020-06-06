/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import grid.SudokuGrid;

import java.util.ArrayList;


/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver {

    // Exact Cover Matrix variable

    private ArrayList<Integer> coverMatrix[];

    private int[][] exactCoverMatrix;
    private int exactCoverRows;
    private int exactCoverCols;

//    private int cellConstraintIndex = 0;
    private int cellValIndex;
    private int rowValIndex;
    private int colValIndex;
    private int boxValIndex;

    private int gridDimension;

    public AlgorXSolver() {

    } // end of AlgorXSolver()


    @Override
    public boolean solve(SudokuGrid grid) {

        // Initialise exact cover matrix
        this.gridDimension = grid.getGridDimension();

        initialiseCoverMatrix();

        printOutCoverMatrix();




        return false;
    } // end of solve()

    
    /**
     * For a sudoku of n dimension, the number of rows in the matrix will be n rows, n columns and n numbers
     * giving n*n*n number of matrix rows. For columns there are 4 separate constraints to account for:
     * cell (row-column), row-value, column-value and box-value hence for a sudoku of n dimensions with n rows and
     * n columns, the number of matrix columns will be n*n*4.
     */
    private void initialiseCoverMatrix() {

        // Number of rows in the matrix
        exactCoverRows = gridDimension * gridDimension * gridDimension;
        exactCoverCols = gridDimension * gridDimension * 4;

        exactCoverMatrix = new int[exactCoverRows][exactCoverCols];

        cellValIndex = 0;
        rowValIndex = gridDimension * gridDimension;
        colValIndex = rowValIndex + (gridDimension * gridDimension);
        boxValIndex = colValIndex + (gridDimension * gridDimension);


        // Fill cell constraints with 1 where applicable
        int cellValue = 0;
        int rowValue = rowValIndex;
        int colValue = colValIndex;
        int boxValue = boxValIndex;

        for (int i = 0; i < exactCoverRows ; i++) {
            // Cell constraints
            exactCoverMatrix[i][cellValue] = 1;
            if ((i + 1)%gridDimension == 0) {
                cellValue++;
            }

            // Row constraints
            exactCoverMatrix[i][rowValue] = 1;
            rowValue++;
            if ((rowValue)%gridDimension == 0) {
                rowValue = rowValue - gridDimension;
            }
            if ((i + 1)%(gridDimension * gridDimension) == 0) {
                rowValue = rowValue + gridDimension;
            }

            // Col constraints
            exactCoverMatrix[i][colValue] = 1;
            colValue++;
            if ((i + 1)%(gridDimension * gridDimension) == 0) {
                colValue = colValIndex;
            }

        }

        int boxSize = (int) Math.sqrt(gridDimension);

        // Could not figure out a way to create box constraints on my own so sourced this:
        // Reference: https://medium.com/javarevisited/building-a-sudoku-solver-in-java-with-dancing-links-180274b0b6c1
        for (int row = 1; row <= gridDimension; row += boxSize) {
            for (int col = 1; col <= gridDimension; col += boxSize) {
                for (int n = 1; n <= gridDimension; n++, boxValue++) {
                    for (int rowDelta = 0; rowDelta < boxSize; rowDelta++) {
                        for (int colDelta = 0; colDelta < boxSize; colDelta++) {
                            int index = indexInCoverMatrix(row+rowDelta, col + colDelta, n);
                            exactCoverMatrix[index][boxValue] = 1;
                        }
                    }
                }
            }
        }

    } // end of initialiseCoverMatrix()

    private int indexInCoverMatrix(int row, int col, int num) {
        return (row - 1) * gridDimension * gridDimension + (col - 1) * gridDimension + (num - 1);
    }

    private void printOutCoverMatrix() {

        StringBuilder matrix = new StringBuilder();

        for (int i = 0; i < exactCoverRows; i++) {
            for (int j = 0; j < exactCoverCols; j++) {
                if(exactCoverMatrix[i][j] != 0) {

                    matrix.append(exactCoverMatrix[i][j]);
                    matrix.append(" ");

                } else {
                    matrix.append("  ");

                }
            }
            matrix.append("\n");

        }

        System.out.println(matrix.toString());
    }


} // end of class AlgorXSolver
