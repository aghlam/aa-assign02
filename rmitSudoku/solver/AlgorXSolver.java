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

    private final ArrayList<Integer> solution;
    private ArrayList<String> sudokuList;

    private int gridDimension;

    /**
     * Constructor
     */
    public AlgorXSolver() {

        solution = new ArrayList<>();
        sudokuList = new ArrayList<>();

    } // end of AlgorXSolver()

    /**
     * Initialises exact cover matrix from sudoku grid and then solves it
     *
     * @param grid Input grid to solve.  The solver will write the solution to grid.
     * @return true if solved
     */
    @Override
    public boolean solve(SudokuGrid grid) {

        this.gridDimension = grid.getGridDimension();
        this.sudokuList = grid.getSudokuList();

        // Initialise exact cover matrix for the sudoku grid size
        int[][] coverMatrix = initialiseCoverMatrix();

        /*
         * Three methods are called here
         * Apply the values of the initial grid and remove the corresponding rows from the exact cover matrix
         * Apply Algorithm X on the matrix
         * Apply solution to the sudoku grid
         * Return true if successful
         */
        if (applyAlgorithmX(applyInitialGrid(grid, coverMatrix))) {
            return applySolution(grid);
        }

        return false;

    } // end of solve()

    /**
     * Solves the sudoku grid by using the rows found from the exact cover matrix
     *
     * @param sudokuGrid the sudoku grid to be solved
     * @return true if sudoku grid successfully solved
     */
    private boolean applySolution(SudokuGrid sudokuGrid) {

        // List of valid symbols
        int[] symbolsList = sudokuGrid.getValidSymbolsList();
        // List to hold the coordinates of the solution
        ArrayList<int[]> solutionCoords = new ArrayList<>();


        // Setup for columns - Spacing the columns into appropriate coords
        int[] colLocationList = new int[gridDimension * gridDimension];
        int location = 0;

        for (int i = 1; i <= colLocationList.length; i++) {
            colLocationList[i - 1] = location;
            if (i % gridDimension == 0) {
                location++;
            }
        }

        // Find the correct row, col coordinates of the grid from the row saved from matrix
        for (int row : solution) {

            int[] coords = new int[3];

            // Get row index - each corresponding row is spaced by blocks of gridDimension^2
            // The correct row coords is found by find by matching the matrix row to the correct block of rows
            for (int i = 1; i <= gridDimension; i++) {
                if (row <= i * gridDimension * gridDimension) {
                    coords[0] = i - 1;
                    break;
                }
            }

            // Get col index - using the array setup above, matches the row mod gridDimension^2 to the correct index
            if (row % (gridDimension * gridDimension) == 0) {
                coords[1] = gridDimension - 1;
            } else {
                coords[1] = colLocationList[row % (gridDimension * gridDimension) - 1];
            }

            // Get index of value - simply row mod gridDimension - then apply corresponding symbols
            if (row % gridDimension == 0) {
                coords[2] = symbolsList[gridDimension - 1];
            } else {
                coords[2] = symbolsList[row % gridDimension - 1];
            }

            solutionCoords.add(coords);

        }

        try {
            // Apply coordinates onto sudoku grid for solution
            for (int[] coords : solutionCoords) {
                sudokuGrid.setGridLoc(coords[0], coords[1], coords[2]);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    } // end of applySolution()

    /**
     * Uses the input file from sudoku grid - applies the given coordinates of the grid to the exact cover matrix
     *
     * @param sudokuGrid grid to be solved - used to obtain the symbols
     * @param matrix     exact cover matrix the initial grid values will be applied to
     * @return exact cover matrix with rows /cols removed
     */
    private int[][] applyInitialGrid(SudokuGrid sudokuGrid, int[][] matrix) {

        int[] symbolsList = sudokuGrid.getValidSymbolsList();
        int[][] newMatrix = matrix;

        for (int i = 2; i < sudokuList.size(); i++) {
            // Split string into coords and value
            String[] temp = sudokuList.get(i).split(" ");
            // Assign value
            int value = 1 + checkSymbolLocation(symbolsList, Integer.parseInt(temp[1]));
            // Split coords string into two separate values using "," and parse as int
            String[] coords = temp[0].split(",");

            // Calculate row in cover matrix according to coordinates and value - took me a long time to figure this out!
            int rowIndex = (Integer.parseInt(coords[0]) * gridDimension * gridDimension) + (Integer.parseInt(coords[1]) * gridDimension) + value;

            // Add corresponding row to solutions
            solution.add(matrix[rowIndex][0]);

        }

        // Remove rows/cols from matrix
        for (int num : solution) {
            for (int i = 0; i < newMatrix.length; i++) {
                if (num == newMatrix[i][0]) {
                    newMatrix = coverRowsCols(newMatrix, i);
                }
            }
        }

        return newMatrix;

    } // end of applyInitialGrid()

    /**
     * Looks for the index of the symbol in the list of valid symbols
     *
     * @param symbolsList list of valid symbols/numbers
     * @param num         the symbol/number to be checked for
     * @return index of symbol
     */
    private int checkSymbolLocation(int[] symbolsList, int num) {
        for (int i = 0; i < symbolsList.length; i++) {
            if (num == symbolsList[i]) {
                return i;
            }
        }

        return 0;
    }

    /**
     * Applies Algorithm X to the matrix to solve
     *
     * @param matrix the exact cover matrix to solve
     * @return true if solved
     */
    private boolean applyAlgorithmX(int[][] matrix) {
        // Check if there are columns in matrix
        if (matrix[0].length == 1) {
            return true;

        } else {
            // Pick a column
            for (int col = 1; col < matrix[0].length; col++) {
                // Pick a row where [row][col] = 1;
                for (int row = 1; row < matrix.length; row++) {
                    if (matrix[row][col] == 1) {
                        // Add to solution
                        solution.add(matrix[row][0]);

                        //Delete rows/cols
                        int[][] tempMatrix = coverRowsCols(matrix, row);

                        // Apply recursive
                        if (applyAlgorithmX(tempMatrix)) {
                            return true;
                        } else {
                            // Remove last added solution if false
                            solution.remove(solution.size() - 1);
                        }
                    }
                }

                return false;
            }
        }

        return false;

    } // end of applyAlgorithmX()

    /**
     * Finds the corresponding rows/cols from given row and adds the indexes to a list
     * Then create a new matrix from old one without the rows
     *
     * @param matrix exact cover matrix to delete
     * @param row    index of row to be deleted
     * @return the new smaller exact cover matrix
     */
    private int[][] coverRowsCols(int[][] matrix, int row) {

        ArrayList<Integer> rowsToDelete = new ArrayList<>();
        ArrayList<Integer> colsToDelete = new ArrayList<>();

        int[][] newMatrix;

        for (int j = 1; j < matrix[0].length; j++) {
            if (matrix[row][j] == 1) {
                for (int i = 1; i < matrix.length; i++) {
                    if (matrix[i][j] == 1) {
                        if (!rowsToDelete.contains(matrix[i][0])) {
                            rowsToDelete.add(matrix[i][0]);
                        }
                    }
                }
                // Store column index to be deleted
                colsToDelete.add(matrix[0][j]);
            }
        }

        // Create new exact cover matrix from old one
        newMatrix = new int[matrix.length - rowsToDelete.size()][matrix[0].length - colsToDelete.size()];

        int rowCounter = 0;
        int colCounter = 0;

        for (int[] rowNum : matrix) {
            if (!rowsToDelete.contains(rowNum[0])) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (!colsToDelete.contains(matrix[0][j])) {
                        newMatrix[rowCounter][colCounter] = rowNum[j];
                        colCounter++;
                    }
                }

                colCounter = 0;
                rowCounter++;
            }
        }

        return newMatrix;

    } // end of coverRowsCols()

    /**
     * Create an exact cover matrix given the dimensions of the sudoku grid
     *
     * @return an exact cover matrix
     */
    private int[][] initialiseCoverMatrix() {

        // Number of rows/cols in the matrix
        int exactCoverRows = (gridDimension * gridDimension * gridDimension);
        int exactCoverCols = (gridDimension * gridDimension * 4);

        int[][] coverMatrixTemp = new int[exactCoverRows][exactCoverCols];

        // Private int cellConstraintIndex = 0;
        int cellConstraintIndex = 0;
        int rowContraintIndex = gridDimension * gridDimension;
        int colConstraintIndex = rowContraintIndex + (gridDimension * gridDimension);
        int boxConstraintIndex = colConstraintIndex + (gridDimension * gridDimension);


        // Fill cell constraints with 1 where applicable
        int cellValue = 0;
        int rowValue = rowContraintIndex;
        int colValue = colConstraintIndex;
        int boxValue = boxConstraintIndex;

        for (int i = 0; i < exactCoverRows; i++) {
            // Cell constraints
            coverMatrixTemp[i][cellValue] = 1;
            if ((i + 1) % gridDimension == 0) {
                cellValue++;
            }

            // Row constraints
            coverMatrixTemp[i][rowValue] = 1;
            rowValue++;
            if ((rowValue) % gridDimension == 0) {
                rowValue = rowValue - gridDimension;
            }
            if ((i + 1) % (gridDimension * gridDimension) == 0) {
                rowValue = rowValue + gridDimension;
            }

            // Col constraints
            coverMatrixTemp[i][colValue] = 1;
            colValue++;
            if ((i + 1) % (gridDimension * gridDimension) == 0) {
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
                            int index = indexInCoverMatrix(row + rowDelta, col + colDelta, n);
                            coverMatrixTemp[index][boxValue] = 1;
                        }
                    }
                }
            }
        }

        // Convert to matrix with coords
        int[][] matrix = new int[exactCoverRows + 1][exactCoverCols + 1];

        for (int j = 1; j < exactCoverCols + 1; j++) {
            matrix[0][j] = j;
        }
        for (int i = 0; i < exactCoverRows + 1; i++) {
            matrix[i][0] = i;
        }

        for (int i = 0; i < exactCoverRows; i++) {
            if (exactCoverCols >= 0) System.arraycopy(coverMatrixTemp[i], 0, matrix[i + 1], 1, exactCoverCols);
        }

        return matrix;

    } // end of initialiseCoverMatrix()

    // Method to help initialise exact cover matrix box constraints
    private int indexInCoverMatrix(int row, int col, int num) {
        return (row - 1) * gridDimension * gridDimension + (col - 1) * gridDimension + (num - 1);
    }

    // TODO
//    // For testing purposes - delete before submitting!
//    private void printMatrix(int[][] matrix) {
//
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < matrix.length; i++) {
//            for (int j = 0; j < matrix[0].length; j++) {
//                if (matrix[i][j] != 0) {
//                    sb.append(matrix[i][j]);
//                    sb.append(" ");
//
//                } else {
//                    sb.append("0 ");
//
//                }
//            }
//            sb.append("\n");
//
//        }
//
//        System.out.println(sb.toString());
//    }


} // end of class AlgorXSolver
