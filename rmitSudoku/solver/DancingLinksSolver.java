/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.SudokuGrid;

import javax.naming.PartialResultException;
import java.sql.SQLOutput;
import java.util.ArrayList;


/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver {

    private final AlgorXSolver algorXSolver;
    private ColumnHeader head;
    private final ArrayList<Node> solutionNodes;
    private final ArrayList<Integer> solutions;


    /**
     * Constructor
     */
    public DancingLinksSolver() {

        algorXSolver = new AlgorXSolver();
        solutionNodes = new ArrayList<>();
        solutions = new ArrayList<>();

    } // end of DancingLinksSolver()


    @Override
    public boolean solve(SudokuGrid grid) {

        int gridDimension = grid.getGridDimension();

        int[][] coverMatrix = algorXSolver.initialiseCoverMatrix(gridDimension, false);

        int[][] newMatrix = applyInitialGrid(grid, coverMatrix, gridDimension);

        head = createDLinksList(coverMatrix);

        if (applyAlgorithm()) {
            for (Node node : solutionNodes) {
                solutions.add(node.getRowIndex() + 1);
            }
        }

        return algorXSolver.applySolution(grid, gridDimension, solutions);

    } // end of solve()


    /**
     * Applies dancing links and algorithm x
     * Concepts and ideas learnt from:
     * Reference: https://www.ocf.berkeley.edu/~jchu/publicportal/sudoku/sudoku.paper.html
     *
     * @return true if solved
     */
    private boolean applyAlgorithm() {

        ColumnHeader currentHeader = head;

        if (currentHeader.getRight().equals(head)) {
            return true;

        } else {

            currentHeader = currentHeader.getRight().getColumnHeader();
            coverRowCol(currentHeader.getColumnHeader());

            for (Node row = currentHeader.getDown(); row != currentHeader.getColumnHeader(); row = row.getDown()) {
                solutionNodes.add(row);

                for (Node rightNode = row.getRight(); rightNode != row; rightNode = rightNode.getRight()) {
                    coverRowCol(rightNode.getColumnHeader());
                }

                if (applyAlgorithm()) {
                    return true;
                }

                solutionNodes.remove(solutionNodes.size() - 1);
                currentHeader = row.getColumnHeader();

                for (Node leftNode = row.getLeft(); leftNode != row; leftNode = leftNode.getLeft()) {
                    uncoverRowCol(leftNode.getColumnHeader());
                }

            }

            uncoverRowCol(currentHeader.getColumnHeader());

        }
        return false;

    } // end of applyAlgorithm()


    /**
     * Covers the column header node
     *
     * @param node of the column header
     */
    private void coverRowCol(Node node) {

        node.getRight().setLeft(node.getLeft());
        node.getLeft().setRight(node.getRight());

        for (Node row = node.getDown(); row != node; row = row.getDown()) {
            for (Node rightNode = row.getRight(); rightNode != row; rightNode = rightNode.getRight()) {

                rightNode.getUp().setDown(rightNode.getDown());
                rightNode.getDown().setUp(rightNode.getUp());
                rightNode.getColumnHeader().decreaseSize();
            }
        }

    } // end of coverRowCol()


    /**
     * Uncovers the column header node
     *
     * @param node of the column header
     */
    private void uncoverRowCol(Node node) {
        // Uncover up and down
        for (Node row = node.getUp(); row != node; row = row.getUp()) {
            for (Node leftNode = row.getLeft(); leftNode != row; leftNode = leftNode.getRight()) {
                leftNode.getUp().setDown(leftNode);
                leftNode.getDown().setUp(leftNode);
                leftNode.getColumnHeader().increaseSize();
            }
        }
        // Uncover left and right
        node.getRight().setLeft(node);
        node.getLeft().setRight(node);

    } // end of uncoverRowCol()


    /**
     * Creates the linked list using input exact cover matrix
     *
     * @param matrix exact cover matrix of sudoku grid
     * @return the header node of list
     */
    private ColumnHeader createDLinksList(int[][] matrix) {
        // New head node
        ColumnHeader head = new ColumnHeader(-1);
        head.setSize(matrix[0].length);
        // List to store all the column header nodes
        ArrayList<ColumnHeader> columnHeaders = new ArrayList<>();

        for (int i = 0; i < matrix[0].length; i++) {

            ColumnHeader node = new ColumnHeader(i);
            columnHeaders.add(node);

            node.setRight(head.getRight());
            node.getRight().setLeft(node);
            node.setLeft(head);
            head.setRight(node);

            head = node;

        }

        head = head.getRight().getColumnHeader();

        // For each row, iterate through the columns to find 1 and link the nodes
        for (int i = 0; i < matrix.length; i++) {
            Node prevNode = null;

            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 1) {
                    ColumnHeader columnHeader = columnHeaders.get(j);
                    Node node = new Node(columnHeader);
                    node.setRowIndex(i);

                    if (prevNode == null) {
                        prevNode = node;
                    }

                    // Set up and down
                    Node tempNode = columnHeader.getUp();
                    node.setDown(tempNode.getDown());
                    node.getDown().setUp(node);
                    node.setUp(tempNode);
                    tempNode.setDown(node);

                    // Set left and right
                    node.setRight(prevNode.getRight());
                    node.getRight().setLeft(node);
                    node.setLeft(prevNode);
                    prevNode.setRight(node);

                    prevNode = node;

                    columnHeader.increaseSize();

                }
            }
        }

        return head;
    } // end of createDLinksList()


    /**
     * Uses the input file from sudoku grid - applies the given coordinates of the grid to the exact cover matrix
     *
     * @param sudokuGrid    grid to be solved - used to obtain the symbols
     * @param matrix        exact cover matrix the initial grid values will be applied to
     * @param gridDimension dimenstions of the grid
     * @return exact cover matrix with rows /cols removed
     */
    private int[][] applyInitialGrid(SudokuGrid sudokuGrid, int[][] matrix, int gridDimension) {

        int[] symbolsList = sudokuGrid.getValidSymbolsList();
        int[][] newMatrix = matrix;
        ArrayList<String> sudokuList = sudokuGrid.getSudokuList();

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
            solutions.add(rowIndex - 1);

        }

        // Remove rows/cols from matrix
        for (int num : solutions) {
            for (int i = 0; i < newMatrix.length; i++) {
                if (num == newMatrix[i][0]) {
                    newMatrix = coverRowsCols(newMatrix, i);
                }
            }
        }

        return newMatrix;

    } // end of applyInitialGrid()


    /**
     * Finds the corresponding rows/cols from given row and adds the indexes to a list
     * Then create a new matrix from old one without the rows
     *
     * @param matrix exact cover matrix to delete
     * @param row    index of row to be deleted
     * @return the new smaller exact cover matrix
     */
    public int[][] coverRowsCols(int[][] matrix, int row) {

        ArrayList<Integer> rowsToDelete = new ArrayList<>();
        ArrayList<Integer> colsToDelete = new ArrayList<>();

        int[][] newMatrix;

        for (int j = 0; j < matrix[0].length; j++) {
            if (matrix[row][j] == 1) {
                for (int i = 0; i < matrix.length; i++) {
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
    } // end of checkSymbolLocation()


} // end of class DancingLinksSolver


/**
 * Node class
 */
class Node {

    private Node up, down, left, right;
    private ColumnHeader columnHeader;
    private int rowIndex;


    /**
     * Constructor
     */
    public Node() {

        up = this;
        down = this;
        left = this;
        right = this;

    }


    /**
     * Constructor with column header allocated
     *
     * @param columnHeader
     */
    public Node(ColumnHeader columnHeader) {

        this();
        this.columnHeader = columnHeader;

    }

    /* **************************** */
    // Getters and Setters

    public Node getUp() {
        return up;
    }

    public Node getDown() {
        return down;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public ColumnHeader getColumnHeader() {
        return columnHeader;
    }

    public void setUp(Node up) {
        this.up = up;
    }

    public void setDown(Node down) {
        this.down = down;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setColColumnHeader(ColumnHeader columnHeader) {
        this.columnHeader = columnHeader;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

} // end of Node class


/**
 * Column header node class
 */
class ColumnHeader extends Node {

    private int size;
    private int colIndex;

    /**
     * Constructor
     *
     * @param colIndex index of the column it represents
     */
    public ColumnHeader(int colIndex) {
        super();
        this.colIndex = colIndex;
        this.size = 0;
        setColColumnHeader(this);
    }


    // Getters and Setters

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColIndex() {
        return colIndex;
    }

    // Increase size count
    public void increaseSize() {
        size++;
    }

    // Decrease size count
    public void decreaseSize() {
        size--;
    }

} // end of ColumnHeader class
