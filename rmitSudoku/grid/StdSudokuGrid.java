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
 * See the comments in SudokuGrid to understand what each overridden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {

    /*
     * Notes:
     * i used for rows
     * j used for columns
     */

    // Sudoku Grid Layout
    private int[][] sudokuGrid;
    private int gridDimension;

    private int[] validSymbolsList;
    private int validSymbolsTotal;

    // List of instructions of the .in file
    private ArrayList<String> sudokuList;

    /**
     * Constructor
     */
    public StdSudokuGrid() {

        super();
        sudokuList = new ArrayList<>();

    } // end of StdSudokuGrid()


    /* ********************************************************* */

    // Getters and setters
    @Override
    public int getGridLoc(int i, int j) {
        return sudokuGrid[i][j];
    }


    @Override
    public void setGridLoc(int i, int j, int num) {
        sudokuGrid[i][j] = num;
    }


    @Override
    public int[] getValidSymbolsList() {
        return validSymbolsList;
    }


    @Override
    public int getGridDimension() {
        return gridDimension;
    }

    // Not used for standard sudoku
    @Override
    public ArrayList<String[]> getCageList() {
        return null;
    }

    @Override
    public ArrayList<String> getSudokuList() {
        return sudokuList;
    }

    /* ********************************************************* */

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
                sudokuGrid[i][j] = -1;
            }
        }

        // Assign list of valid symbols and get total
        String[] validSymbols = sudokuList.get(1).split(" ");
        validSymbolsList = new int[gridDimension];
        for (int i = 0; i < gridDimension; i++) {
            validSymbolsList[i] = Integer.parseInt(validSymbols[i]);
            validSymbolsTotal += validSymbolsList[i];
        }

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

        if (!oneValueConstraintCheck()) {
            return false;
        } else if (!rowConstraintCheck()) {
            return false;
        } else if (!columnConstraintCheck()) {
            return false;
        } else {

            return boxConstraintCheck();
        }

    } // end of validate()

    /**
     * Checks if cell contains a value
     *
     * @return true if validated
     */
    private boolean oneValueConstraintCheck() {

        for (int i = 0; i < gridDimension; i++) {
            for (int j = 0; j < gridDimension; j++) {
                if (sudokuGrid[i][j] == -1) {
                    return false;
                }
            }
        }

        return true;

    } // end of oneValueConstraintCheck()

    /**
     * Checks for if rows contain a unique value
     * Consider arithmetic, all unique values will add up to a certain number.
     * Even if a row has repeat numbers to add up to the total, it will fail the column
     * check since the columns will contain repeat numbers.
     *
     * @return true if validated
     */
    private boolean rowConstraintCheck() {

        for (int i = 0; i < gridDimension; i++) {
            int sum = 0;

            for (int j = 0; j < gridDimension; j++) {
                sum += sudokuGrid[i][j];
            }

            if (sum != validSymbolsTotal) {
                return false;
            }
        }

        return true;
    } // end of rowConstraintCheck()

    /**
     * Checks for if columns contain a unique value
     * Consider arithmetic, all unique values will add up to a certain number.
     * Even if a column has repeat numbers to add up to the total, it will fail the row
     * check since the rows will contain repeat numbers.
     *
     * @return true if validated
     */
    private boolean columnConstraintCheck() {

        for (int j = 0; j < gridDimension; j++) {
            int sum = 0;

            for (int i = 0; i < gridDimension; i++) {
                sum += sudokuGrid[i][j];
            }

            if (sum != validSymbolsTotal) {
                return false;
            }
        }

        return true;

    } // end of columnConstraintCheck()

    /**
     * Checks for box contains unique numbers
     * Consider arithmetic, all unique values will add up to a certain number.
     *
     * @return true if validated
     */
    private boolean boxConstraintCheck() {
        int squareRoot = (int) Math.sqrt(gridDimension);

        // Defines which block to check
        for (int i = 0; i < gridDimension; i += squareRoot) {
            for (int j = 0; j < gridDimension; j += squareRoot) {

                int bounds = i + squareRoot;
                int subGridTotal = 0;

                // Iterate through each element in the block
                for (int k = i; k < bounds; k++) {
                    for (int l = i; l < bounds; l++) {
                        subGridTotal += sudokuGrid[k][l];
                    }
                } // End inner double for loop

                if (subGridTotal != validSymbolsTotal) {
                    return false;
                }
            }
        } // end outer double for loop

        return true;

    } // end of boxConstraintCheck()


} // end of class StdSudokuGrid