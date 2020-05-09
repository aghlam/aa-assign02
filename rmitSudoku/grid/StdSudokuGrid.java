/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 * @studentAuthor Alan Lam s3436174
 */
package grid;

import java.io.*;
import java.util.ArrayList;

/**
 * Class implementing the grid for standard Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task A and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {

    /**
     * Notes/Questions:
     * - What do we fill the blank spaces with?
     * - Are we allowed to use streams?
     * - Better to assign sudoku numbers as int or char?
     * - Are the sudoku puzzles only limited to numbers or numbers and symbols
     */

    /**
     * Notes:
     * i used for rows
     * j used for columns
     */

    // Sudoku Grid Layout
    private int[][] sudokuGrid;
    private int gridDimension;

    private int[] validSymbolsList;
    private int validSymbolsTotal;

    // List of initial sudoku layout instructions
    private final ArrayList<String> sudokuList = new ArrayList<>();
//    private int listSize;


    public StdSudokuGrid() {
        super();
    } // end of StdSudokuGrid()


    /* ********************************************************* */


    public int[] getValidSymbolsList() {
        return validSymbolsList;
    }


    public int getGridLoc(int i, int j) {
        return sudokuGrid[i][j];
    }

    @Override
    public void setGridLoc(int i, int j, int num) {
        sudokuGrid[i][j] = num;
    }

    public int getGridDimension() {
        return gridDimension;
    }

    @Override
    public void initGrid(String filename)
            throws FileNotFoundException, IOException {

        // Open file and read each line using BufferedReader
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        // Given that the text file follows the proper structure
        // Assigns read line to variable 'line' and adds to list
        while ((line = reader.readLine()) != null) {
            sudokuList.add(line);
        }

        reader.close();

        // Obtain size of the input grid - first line of file
        gridDimension = Integer.parseInt(sudokuList.get(0));
        // Initialise sudoku grid/array to all zeroes
        sudokuGrid = new int[gridDimension][gridDimension];
        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                sudokuGrid[i][j] = 0;
            }
        }

        // Assign list of valid symbols - use stream or no stream?
        // Valid symbols individual list or total;
        String[] validSymbols = sudokuList.get(1).split(" ");
//        int length = validSymbols.length;
        validSymbolsList = new int[gridDimension];
        for (int i = 0; i < gridDimension; i++) {
            validSymbolsList[i] = Integer.parseInt(validSymbols[i]);
            validSymbolsTotal += validSymbolsList[i];
        }
//        for (String validSymbol : validSymbols) {
//            validSymbolsTotal += Integer.parseInt(validSymbol);
//        }


        // Assign values read in from file
        for (int i = 2; i < sudokuList.size(); i++) {
            // Split string into coords and value
            String[] temp = sudokuList.get(i).split(" ");
            // Assign value
            int value = Integer.parseInt(temp[1]);
            // Split coords string into two separate values using "," and parse as int
            String[] coords = temp[0].split(",");
            int row = Integer.parseInt(coords[0]);
            int column = Integer.parseInt(coords[1]);

            sudokuGrid[row][column] = value;

        }

    } // end of initGrid()


    @Override
    public void outputGrid(String filename)
            throws FileNotFoundException, IOException {
        // Writes string representation of SudokuGrid to filename
        File file = new File(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(toString());

        writer.close();

    } // end of outputGrid()


    @Override
    public String toString() {

        StringBuilder gridLayout = new StringBuilder();

        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                gridLayout.append(sudokuGrid[i][j]);
                if (j != gridDimension - 1) {
                    gridLayout.append(",");
                }
            }
            gridLayout.append("\n");

        }

        return gridLayout.toString();

    } // end of toString()


    @Override
    public boolean validate() {

        // Main validation
        if (!oneValueConstraintCheck()) {
            return false;
        }
         else if (!rowConstraintCheck()) {
            return false;
        }

         else if (!columnConstraintCheck()) {
            return false;
        } else {

            return boxConstraintCheck();
        }

         // Alternative validation

    } // end of validate()


    // Checks for the 'One value per cell' constraint
    private boolean oneValueConstraintCheck() {

        for (int i = 0; i < gridDimension ; i++) {
            for (int j = 0; j < gridDimension ; j++) {
                if (sudokuGrid[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    } // end of oneValueConstraintCheck()


    /**
     * Checks for if rows contain a unique value
     * Works in conjunction with columnConstraint()
     * Consider arithmetic, all unique values will add up to a certain number.
     * Even if a row has repeat numbers to add up to the total, it will fail the column
     * check since the columns will contain repeat numbers.
     */
    private boolean rowConstraintCheck() {

        for (int i = 0; i < gridDimension ; i++) {
            int sum = 0;
            for (int j = 0; j < gridDimension; j++) {
                sum += sudokuGrid[i][j];
            }
//            System.out.println(sum);
            if(sum != validSymbolsTotal) {
                return false;
            }
        }
        return true;
    } // end of rowConstraintCheck()




    /**
     * Checks for if columns contain a unique value
     * Works in conjunction with rowConstraint()
     * Consider arithmetic, all unique values will add up to a certain number.
     * Even if a column has repeat numbers to add up to the total, it will fail the row
     * check since the rows will contain repeat numbers.
     */
    private boolean columnConstraintCheck() {

        for (int j = 0; j < gridDimension ; j++) {
            int sum = 0;
            for (int i = 0; i < gridDimension; i++) {
                sum += sudokuGrid[i][j];
            }
//            System.out.println(sum);
            if(sum != validSymbolsTotal) {
                return false;
            }
        }
        return true;
    }


    // Checks box for unique values
    private boolean boxConstraintCheck() {
        int squareRoot = (int) Math.sqrt(gridDimension);

        // defines which block to check
        for (int i = 0; i < gridDimension; i += squareRoot) {
            for (int j = 0; j < gridDimension; j += squareRoot) {

                int bounds = i + squareRoot;
                int subGridTotal = 0;

                // Iterate through each element in the block
                for (int k = i; k < bounds; k++) {
                    for (int l = i; l < bounds; l++) {
                        subGridTotal += sudokuGrid[k][l];
                    }
                } // end inner double for loop
//                System.out.println(subGridTotal);
                if(subGridTotal != validSymbolsTotal) {
                    return false;
                }

            }
        } // end outer double for loop

        return true;
    }


    /**
     * Alternatives for constraint validation
     */

    public boolean validate(int i, int j, int num) {

        return !rowCheck(i, num) && !columnCheck(j, num) && !boxCheck(i, j, num);


    } // end of validate(int i, int j, int num)


    // Alternative row check
    private boolean rowCheck(int i, int num) {
        for (int j = 0; j < gridDimension; j++) {
            if (sudokuGrid[i][j] == num) {
                return true;
            }

        }

        return false;
    } // end of rowCheck()


    // Alternative column check
    private boolean columnCheck(int j, int num) {
        for (int i = 0; i < gridDimension; i++) {
            if (sudokuGrid[i][j] == num) {
                return true;
            }

        }

        return false;
    } // end of columnCheck()


    //Alternative box check
    private boolean boxCheck(int rowStart, int colStart, int num) {
        int squareRoot = (int) Math.sqrt(gridDimension);
        int row = rowStart - rowStart%squareRoot;
        int col = colStart - colStart%squareRoot;

        for (int i = row; i < row + squareRoot ; i++) {
            for (int j = col; j < col + squareRoot; j++) {
                if (sudokuGrid[i][j] == num) {
                    return true;
                }

            }

        }

        return false;
    } // end of boxCheck()


} // end of class StdSudokuGrid