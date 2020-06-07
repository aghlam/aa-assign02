/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 * @studentAuthor Alan Lam s3436174
 */

package solver;

import grid.SudokuGrid;


/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver {

    private int gridDimension;
    private int[] validSymbolsList;


    /**
     * Constructor
     */
    public BackTrackingSolver() {

    } // end of BackTrackingSolver()


    /**
     * Solve the given sudoku grid
     *
     * @param grid Input grid to solve.  The solver will write the solution to grid.
     * @return true if solved
     */
    @Override
    public boolean solve(SudokuGrid grid) {

        gridDimension = grid.getGridDimension();
        validSymbolsList = grid.getValidSymbolsList();

        return (backtrackingRecursion(grid));

    } // end of solve()

    /**
     * Uses a backtracking recursion and validation methods to solve the sudoku grid. The idea
     * and concepts learnt to create the recursion were obtained from here
     * Reference: https://medium.com/javarevisited/build-a-sudoku-solver-in-java-part-1-c308bd511481
     *
     * @param grid sudoku grid to be solved
     * @return true if solved
     */
    private boolean backtrackingRecursion(SudokuGrid grid) {

        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                if (grid.getGridLoc(i, j) == -1) {
                    for (int k = 0; k < gridDimension; k++) {
                        if (check(i, j, validSymbolsList[k], grid)) {
                            grid.setGridLoc(i, j, validSymbolsList[k]);
                            if (backtrackingRecursion(grid)) {
                                return true;
                            } else {
                                grid.setGridLoc(i, j, -1);
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
     * Validates if the number can be placed in at the i, j coordinates of the grid
     *
     * @param i          row index to be placed
     * @param j          column index to be placed
     * @param num        symbol/number to be placed
     * @param sudokuGrid the grid to be solved
     * @return true if symbol/number can be placed
     */
    private boolean check(int i, int j, int num, SudokuGrid sudokuGrid) {
        return !rowCheck(i, num, sudokuGrid) && !columnCheck(j, num, sudokuGrid) && !boxCheck(i, j, num, sudokuGrid);
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


} // end of class BackTrackingSolver()
