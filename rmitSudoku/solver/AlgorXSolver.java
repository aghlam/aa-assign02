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

    private final ArrayList<Integer> solution;
    private ArrayList<String> sudokuList;

    private int[][] coverMatrix;
    private int[][] masterCoverMatrix;
//    private int[][] coverMatrixCopy;

    private int exactCoverRows;
    private int exactCoverCols;

//    private int cellConstraintIndex = 0;
    private int cellConstraintIndex;
    private int rowContraintIndex;
    private int colConstraintIndex;
    private int boxConstraintIndex;

    private int gridDimension;


    public AlgorXSolver() {

//        partialMatrix = new ArrayList<Integer>();
        solution = new ArrayList<>();
        sudokuList = new ArrayList<>();

    } // end of AlgorXSolver()


    @Override
    public boolean solve(SudokuGrid grid) {

        // Initialise exact cover matrix
        this.gridDimension = grid.getGridDimension();
        this.sudokuList = grid.getSudokuList();

//        for (String string : sudokuList) {
//            System.out.println(string);
//        }

        masterCoverMatrix = initialiseCoverMatrix();

        coverMatrix =  applyInitialGrid(grid, masterCoverMatrix);



        // testing - delete after
//        printMatrix(coverMatrixTemp);
//        System.out.println();
//        printMatrix(coverMatrix);

        // KEEP THIS
        boolean temp = applyAlgorithmX(coverMatrix);

        // Testing - delete after
        for (int num : solution) {
            System.out.print(num + " ");
        }
        System.out.println();


        return false;
    } // end of solve()

    private int[][] applyInitialGrid(SudokuGrid sudokuGrid, int[][] matrix) {

        int[] symbolsList = sudokuGrid.getValidSymbolsList();
        int[][] newMatrix = matrix;

//        printMatrix(coverMatrix);

        for (int i = 2; i < sudokuList.size(); i++) {
//        for (int i = 2; i < 4; i++) {
            // Split string into coords and value
            String[] temp = sudokuList.get(i).split(" ");
            // Assign value
            int value = 1 + checkSymbolLocation(symbolsList, Integer.parseInt(temp[1]));
            System.out.println("Value: " + value);
            // Split coords string into two separate values using "," and parse as int
            String[] coords = temp[0].split(",");

            // Calculate row in cover matrix according to coordinates and value
            int rowIndex = (Integer.parseInt(coords[0])*gridDimension*gridDimension) + (Integer.parseInt(coords[1])*gridDimension) + value;

            System.out.println("Index row number: " + rowIndex);

            System.out.println(matrix[rowIndex][0]);

            solution.add(matrix[rowIndex][0]);


        }

        for (int num : solution) {
            for (int i = 0; i < newMatrix.length; i++) {
                if (num == newMatrix[i][0]) {
                    newMatrix = coverRowsCols(newMatrix, i);
                }

            }

        }

//        printMatrix(newMatrix);

        return newMatrix;


        // Testing - delete after
//        String[] coords = new String[2];
//        coords[0] = "3";
//        coords[1] = "3";
//        int value = 4;
//
//        int row = (Integer.parseInt(coords[0])*gridDimension*gridDimension) + (Integer.parseInt(coords[1])*gridDimension) + value;
//
//        System.out.println("Index row number: " + row);
//        System.out.println(coverMatrix[3][0]);
//
    }

    private int checkSymbolLocation(int[] symbolsList, int num) {
        for (int i = 0; i < symbolsList.length; i++) {
            if (num == symbolsList[i]) {
                return i;
            }
        }

        return 0;
    }

    private boolean applyAlgorithmX(int[][] matrix) {
        // Check if there are columns in matrix
        if (matrix[0].length == 1) {
            System.out.println("SOLVED");
            return true;
        } else {
            // Pick a column
            for (int col = 1; col < matrix[0].length; col++) {
                // pick a row where [row][col] = 1;
                for (int row = 1; row < matrix.length; row++) {
                    if (matrix[row][col] == 1) {
                        solution.add(matrix[row][0]);

                        //Delete rows/cols
                        int[][]tempMatrix = coverRowsCols(matrix, row);
//                        printMatrix(tempMatrix);

                        // Apply recursive
                        if (applyAlgorithmX(tempMatrix)) {
                            return true;
                        } else {
//                            for (int num : solution) {
//                                System.out.print(num + " ");
//                            }
//                            System.out.println();
//                            System.out.println("Number to remove: " + matrix[row][0]);
                            // Remove last added solution if false
                            solution.remove(solution.size()-1);
                        }
                    }
                }

                return false;
            }
        }

        return false;
    }

    private int[][] coverRowsCols(int[][] matrix, int row) {

//        printMatrix(matrix);
//        System.out.println();

        ArrayList<Integer> rowsToDelete = new ArrayList<>();
        ArrayList<Integer> colsToDelete = new ArrayList<>();

        int[][] newMatrix;
        // Deleting rows/cols

        for (int j = 1; j < matrix[0].length; j++) {
            if (matrix[row][j] == 1) {
                for (int i = 1; i < matrix.length ; i++) {
//                    if (!rowToDelete.contains(matrix[i][0])) {
                        if (matrix[i][j] == 1) {
                            //delete row here - store row number
                            if(!rowsToDelete.contains(matrix[i][0])) {
                                rowsToDelete.add(matrix[i][0]);
                            }
//                        }
                    }
                }
                //delete col here - store col number
                colsToDelete.add(matrix[0][j]);
            }
        }

        newMatrix = new int[matrix.length - rowsToDelete.size()][matrix[0].length - colsToDelete.size()];

//        System.out.println("Matrix row Length: " + matrix.length);
//        System.out.println("Matrix col Length: " + matrix[0].length);
//        System.out.println("rowDeleteSize: " + rowToDelete.size());
//        System.out.println("colDeleteSize: " + colToDelete.size());
//
//        System.out.println("newMatrix row length: " + newMatrix.length);
//        System.out.println("newMatrix col length: " + newMatrix[0].length);

//        for(int num : colToDelete) {
//            System.out.print(num + " ");
//        }
//        System.out.println();
//        for (int num : rowToDelete) {
//            System.out.print(num + " ");
//        }
//        System.out.println();

        int rowCounter = 0;
        int colCounter = 0;

//        printMatrix(newMatrix);

        for (int[] rowNum : matrix) {
            if (!rowsToDelete.contains(rowNum[0])) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (!colsToDelete.contains(matrix[0][j])) {
//                        System.out.println("original matrix at row " + i + " col " + j + " : " + matrix[i][j]);
                        newMatrix[rowCounter][colCounter] = rowNum[j];
                        colCounter++;
                    }
                }
                colCounter = 0;
                rowCounter++;
//                printMatrix(newMatrix);
            }
        }

//        printMatrix(newMatrix);

        return newMatrix;
    }

    /**
     * For a sudoku of n dimension, the number of rows in the matrix will be n rows, n columns and n numbers
     * giving n*n*n number of matrix rows. For columns there are 4 separate constraints to account for:
     * cell (row-column), row-value, column-value and box-value hence for a sudoku of n dimensions with n rows and
     * n columns, the number of matrix columns will be n*n*4.
     */
    private int[][] initialiseCoverMatrix() {

        // Number of rows/cols in the matrix
        exactCoverRows = (gridDimension * gridDimension * gridDimension);
        exactCoverCols = (gridDimension * gridDimension * 4);

        int[][] coverMatrixTemp = new int[exactCoverRows][exactCoverCols];

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
        int[][] matrix = new int[exactCoverRows+1][exactCoverCols+1];

        for (int j = 1; j < exactCoverCols+1 ; j++) {
            matrix[0][j] = j;
        }
        for (int i = 0; i < exactCoverRows+1; i++) {
            matrix[i][0] = i;
        }

        for (int i = 0; i < exactCoverRows; i++) {
            if (exactCoverCols >= 0) System.arraycopy(coverMatrixTemp[i], 0, matrix[i + 1], 1, exactCoverCols);
        }

        return matrix;

        // Testing print matrix
//        printMatrix(masterCoverMatrix);

    } // end of initialiseCoverMatrix()

    private int indexInCoverMatrix(int row, int col, int num) {
        return (row - 1) * gridDimension * gridDimension + (col - 1) * gridDimension + (num - 1);
    }

    // For testing purposes
    private void printMatrix(int[][] matrix) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j] != 0) {
                    sb.append(matrix[i][j]);
                    sb.append(" ");

                } else {
                    sb.append("0 ");

                }
            }
            sb.append("\n");

        }

        System.out.println(sb.toString());
    }


} // end of class AlgorXSolver
