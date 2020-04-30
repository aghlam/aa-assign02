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

    public int[][] sudokuGrid;
    ArrayList<String> sudokuList;
    int listSize;
    int gridDimension;


    public StdSudokuGrid() {
        super();
        sudokuList = new ArrayList<String>();
        listSize = 0;
        gridDimension = 0;
    } // end of StdSudokuGrid()


    /* ********************************************************* */


    @Override
    public void initGrid(String filename)
            throws FileNotFoundException, IOException {
        // TODO
        // Implement reading 'list of valid symbols' - line 2 of file


        // Open file and read each line using BufferedReader
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = null;
        // Given that the text file follows the proper structure
        // Assigns read line to variable 'line' and adds to list
        while ((line = reader.readLine()) != null) {
            sudokuList.add(line);
        }

        reader.close();

        listSize = sudokuList.size();
        // Obtain size of the input grid - first line of file
        gridDimension = Integer.parseInt(sudokuList.get(0));
        // Initialise sudoku grid/array to all zeroes
        sudokuGrid = new int[gridDimension][gridDimension];
        for (int i = 0; i != gridDimension; ++i) {
            for (int j = 0; j != gridDimension; ++j) {
                sudokuGrid[i][j] = 0;
            }
        }

        // Assign values read in from file
        for (int i = 2; i != listSize; ++i) {
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

    } // end of initBoard()


    @Override
    public void outputGrid(String filename)
            throws FileNotFoundException, IOException {
        // Writes string representation of SudokuGrid to filename
        File file = new File(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(toString());

        writer.close();

    } // end of outputBoard()


    @Override
    public String toString() {

        StringBuilder gridLayout = new StringBuilder();

        for (int i = 0; i != gridDimension; ++i) {
            for (int j = 0; j != gridDimension; ++j) {
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
        // TODO

        // placeholder
        return false;
    } // end of validate()

} // end of class StdSudokuGrid
