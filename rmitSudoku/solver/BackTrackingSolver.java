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


    public BackTrackingSolver() {

    } // end of BackTrackingSolver()

    @Override
    public boolean solve(SudokuGrid grid) {

        gridDimension = grid.getGridDimension();
        int[] validSymbolsList = grid.getValidSymbolsList();

        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                if (grid.getGridLoc(i, j) == 0) {
                    for (int k = 0; k < gridDimension; k++) {
                        if (validate(i, j, validSymbolsList[k], grid)) {
                            grid.setGridLoc(i, j, validSymbolsList[k]);
                            if (solve(grid)) {
                                return true;
                            } else {
                                grid.setGridLoc(i, j, 0);
                            }
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    } // end of solve()

    private boolean validate(int i, int j, int num, SudokuGrid sudokuGrid) {

        return !rowCheck(i, num, sudokuGrid) && !columnCheck(j, num, sudokuGrid) && !boxCheck(i, j, num, sudokuGrid);

    } // end of validate(int i, int j, int num)

    private boolean rowCheck(int i, int num, SudokuGrid sudokuGrid) {
        for (int j = 0; j < gridDimension; j++) {
            if (sudokuGrid.getGridLoc(i, j) == num) {
                return true;
            }
        }

        return false;
    } // end of rowCheck()

    private boolean columnCheck(int j, int num, SudokuGrid sudokuGrid) {
        for (int i = 0; i < gridDimension; i++) {
            if (sudokuGrid.getGridLoc(i, j) == num) {
                return true;
            }
        }

        return false;
    } // end of columnCheck()

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
