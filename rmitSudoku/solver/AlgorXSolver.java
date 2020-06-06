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

    private ArrayList<Integer> partialMatrix;

    private int[][] coverMatrixTemp;
    private int[][] coverMatrix;

    private int exactCoverRows;
    private int exactCoverCols;

//    private int cellConstraintIndex = 0;
    private int cellConstraintIndex;
    private int rowContraintIndex;
    private int colConstraintIndex;
    private int boxConstraintIndex;

    private int gridDimension;


    public AlgorXSolver() {

        partialMatrix = new ArrayList<Integer>();

    } // end of AlgorXSolver()


    @Override
    public boolean solve(SudokuGrid grid) {

        // Initialise exact cover matrix
        this.gridDimension = grid.getGridDimension();

        initialiseCoverMatrix();

        printOutCoverMatrix();


        return false;
    } // end of solve()

    private boolean applyAlgorithmX() {

        if (coverMatrixTemp[0].length == 0) {
            return true;
        } else {

        }



        return false;
    }


    /**
     * For a sudoku of n dimension, the number of rows in the matrix will be n rows, n columns and n numbers
     * giving n*n*n number of matrix rows. For columns there are 4 separate constraints to account for:
     * cell (row-column), row-value, column-value and box-value hence for a sudoku of n dimensions with n rows and
     * n columns, the number of matrix columns will be n*n*4.
     */
    private void initialiseCoverMatrix() {

        // Number of rows in the matrix
        exactCoverRows = (gridDimension * gridDimension * gridDimension);
        exactCoverCols = (gridDimension * gridDimension * 4);

        coverMatrixTemp = new int[exactCoverRows][exactCoverCols];

//        coverMatrix[0][0] = 1;
//        System.out.println(coverMatrix[0][0]);

//

        cellConstraintIndex = 0;
        rowContraintIndex = gridDimension * gridDimension;
        colConstraintIndex = rowContraintIndex + (gridDimension * gridDimension);
        boxConstraintIndex = colConstraintIndex + (gridDimension * gridDimension);


        // Fill cell constraints with 1 where applicable
        int cellValue = 0;
        int rowValue = rowContraintIndex;
        int colValue = colConstraintIndex;
        int boxValue = boxConstraintIndex;

        for (int i = 0; i < exactCoverRows ; i++) {
            // Cell constraints
            coverMatrixTemp[i][cellValue] = 1;
            if ((i + 1)%gridDimension == 0) {
                cellValue++;
            }

            // Row constraints
            coverMatrixTemp[i][rowValue] = 1;
            rowValue++;
            if ((rowValue)%gridDimension == 0) {
                rowValue = rowValue - gridDimension;
            }
            if ((i + 1)%(gridDimension * gridDimension) == 0) {
                rowValue = rowValue + gridDimension;
            }

            // Col constraints
            coverMatrixTemp[i][colValue] = 1;
            colValue++;
            if ((i + 1)%(gridDimension * gridDimension) == 0) {
                colValue = colConstraintIndex;
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
                            coverMatrixTemp[index][boxValue] = 1;
                        }
                    }
                }
            }
        }

        // Convert to matrix with coords
        coverMatrix = new int[exactCoverRows+1][exactCoverCols+1];

        for (int j = 1; j < exactCoverCols+1 ; j++) {
            coverMatrix[0][j] = j;
        }
        for (int i = 0; i < exactCoverRows+1; i++) {
            coverMatrix[i][0] = i;
        }

        for (int i = 0; i < exactCoverRows; i++) {
            for (int j = 0; j < exactCoverCols; j++) {
                coverMatrix[i+1][j+1] = coverMatrixTemp[i][j];

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
                if(coverMatrixTemp[i][j] != 0) {

                    matrix.append(coverMatrixTemp[i][j]);
                    matrix.append(" ");

                } else {
                    matrix.append("0 ");

                }
            }
            matrix.append("\n");

        }

        System.out.println(matrix.toString());

    // -----------------------------------------------

        StringBuilder matrixNew = new StringBuilder();

        for (int i = 0; i < exactCoverRows+1; i++) {
            for (int j = 0; j < exactCoverCols+1; j++) {
                if(coverMatrix[i][j] != 0) {

                    matrixNew.append(coverMatrix[i][j]);
                    matrixNew.append(" ");

                } else {
                    matrixNew.append("  ");

                }
            }
            matrixNew.append("\n");

        }

        System.out.println(matrixNew.toString());
    }


} // end of class AlgorXSolver
