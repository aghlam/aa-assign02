/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 * @studentAuthor Alan Lam s3436174
 */

package solver;

import grid.SudokuGrid;

import java.util.ArrayList;


/**
 * Backtracking solver for Killer Sudoku.
 */
public class KillerBackTrackingSolver extends KillerSudokuSolver {

    private ArrayList<String[]> cageList;
    private int gridDimension;
    int[] validSymbolsList;


    /**
     * Constructor
     */
    public KillerBackTrackingSolver() {

    } // end of KillerBackTrackingSolver()


    @Override
    public boolean solve(SudokuGrid grid) {

        cageList = grid.getCageList();
        gridDimension = grid.getGridDimension();
        validSymbolsList = grid.getValidSymbolsList();

        return backtrackingKiller(grid);

    } // end of solve()


    /**
     * Recursive backtracking method to solve sudoku grid by validating the constraints
     *
     * @param sudokuGrid grid to be solved
     * @return true if solved
     */
    private boolean backtrackingKiller(SudokuGrid sudokuGrid) {

        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                if (sudokuGrid.getGridLoc(i, j) == 0) {
                    for (int k = 0; k < gridDimension; k++) {
                        if (validate(i, j, validSymbolsList[k], sudokuGrid)) {
                            sudokuGrid.setGridLoc(i, j, validSymbolsList[k]);
                            if (solve(sudokuGrid)) {
                                return true;
                            } else {
                                sudokuGrid.setGridLoc(i, j, 0);
                            }
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Validates the usual constraints but also has a cage constraint to check
     *
     * @param i          row index to be validated
     * @param j          column index to be validated
     * @param num        symbol/number to be input
     * @param sudokuGrid sudoku grid to be solved
     * @return return true if passes all validations
     */
    private boolean validate(int i, int j, int num, SudokuGrid sudokuGrid) {

        return !rowCheck(i, num, sudokuGrid) && !columnCheck(j, num, sudokuGrid)
                && !boxCheck(i, j, num, sudokuGrid) && cageCheck(i, j, num, sudokuGrid);

    } // end of validate(int i, int j, int num)


    /**
     * Validates the row at index i
     *
     * @param i          row index to be checked
     * @param num        the symbol/number to be placed
     * @param sudokuGrid grid to be solved
     * @return true if can be placed
     */
    private boolean rowCheck(int i, int num, SudokuGrid sudokuGrid) {
        for (int j = 0; j < gridDimension; j++) {
            if (sudokuGrid.getGridLoc(i, j) == num) {
                return true;
            }
        }

        return false;

    } // end of rowCheck()


    /**
     * Validates the column at index j
     *
     * @param j          column index to be checked
     * @param num        the symbol/number to be placed
     * @param sudokuGrid grid to be solved
     * @return true if can be placed
     */
    private boolean columnCheck(int j, int num, SudokuGrid sudokuGrid) {
        for (int i = 0; i < gridDimension; i++) {
            if (sudokuGrid.getGridLoc(i, j) == num) {
                return true;
            }
        }

        return false;

    } // end of columnCheck()


    /**
     * Validates the box constraints for placing a number. This was harder to figure out. Idea and concepts
     * were learnt from:
     * Reference: https://medium.com/@george.seif94/solving-sudoku-using-a-simple-search-algorithm-3ac44857fee8
     *
     * @param rowStart   row index of where the box starts
     * @param colStart   column index of where box starts
     * @param num        the symbol/number to be placed
     * @param sudokuGrid grid to be solved
     * @return true if can be placed
     */
    private boolean boxCheck(int rowStart, int colStart, int num, SudokuGrid sudokuGrid) {

        int squareRoot = (int) Math.sqrt(gridDimension);
        int row = rowStart - rowStart % squareRoot;
        int col = colStart - colStart % squareRoot;

        for (int i = row; i < row + squareRoot; i++) {
            for (int j = col; j < col + squareRoot; j++) {
                if (sudokuGrid.getGridLoc(i, j) == num) {
                    return true;
                }
            }
        }

        return false;

    } // end of boxCheck()


    /**
     * The main check for the killer sudoku. Once all coordinates of a given cage is filled,
     * it adds up all the symbols/numbers and checks it against the give cage total
     *
     * @param row        row index of where the box starts
     * @param col        column index of where box starts
     * @param num        the symbol/number to be placed
     * @param sudokuGrid grid to be solved
     * @return true if cage total and sum total match
     */
    private boolean cageCheck(int row, int col, int num, SudokuGrid sudokuGrid) {
        // Cycle through all cages in list
        for (String[] currentCage : cageList) {
            // Get total for cage at i
            int currentCageTotal = Integer.parseInt(currentCage[0]);
            // Get number of elements in cage at i
            int currentCageLength = currentCage.length;
            /*
             * Obtain the coords of the last element in cage at i
             * Given that the input file is structured in the correct format, the last
             * element in the cage will the the last coordinate to be filled which will
             * complete the cage numbers
             */
            String[] coords = currentCage[currentCageLength - 1].split(",");
            // Check if coords match current ones
            if ((Integer.parseInt(coords[0]) == row) && (Integer.parseInt(coords[1]) == col)) {
                int coordsTotal = num;
                for (int j = 1; j < currentCageLength; j++) {
                    String[] tempCoords = currentCage[j].split(",");
                    coordsTotal += sudokuGrid.getGridLoc(Integer.parseInt(tempCoords[0]), Integer.parseInt(tempCoords[1]));
                }
                return coordsTotal == currentCageTotal;
            }
        }

        return true;

    } // end of cageCheck()


} // end of class KillerBackTrackingSolver()
