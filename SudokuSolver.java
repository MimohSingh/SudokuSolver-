import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SudokuSolver {


    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String CLEAR = OS.contains("win") ? "cls" : "clear";
    private static int userInputSize;
    private static int userInputPercentage;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user inputs for grid size and percentage of input
        userInputSize = getUserInputSize(scanner);
        userInputPercentage = getUserInputPercentage(scanner);

        // Load puzzle based on user input
        String puzzle = loadPuzzleData(userInputSize, userInputPercentage);

        // Initialize the Sudoku grid
        int[][] sudoku = new int[userInputSize][userInputSize];
        int row = 0;
        int col = 0;

        // Convert puzzle string to a 2D list
        for (int i = 0; i < puzzle.length(); i++) {
            sudoku[row][col] = Character.getNumericValue(puzzle.charAt(i));
            col++;
            if (col == userInputSize) {
                col = 0;
                row++;
            }
        }

        // Display the initial problem
        clearConsole();
        System.out.println("\nProblem: \n");
        printSudoku(sudoku);

        // Wait for user input before displaying the solution
        System.out.println("\nPress Enter to See the Solution...");
        scanner.nextLine();

        // Solve the Sudoku puzzle
        if (solve(sudoku)) {
            clearConsole();
            printSudoku(sudoku);
            if (solutionChecker(sudoku)) {
                System.out.println("\nThe solution is verified\n");
            } else {
                System.out.println("\nYour solution is incorrect. Please try again.");
            }
        } else {
            System.out.println("No solution exists.");
        }

        scanner.close();
    }

    private static int getUserInputSize(Scanner scanner) {
        while (true) {
            System.out.print("Enter the size of the Sudoku grid (4 or 9): ");
            try {
                int size = Integer.parseInt(scanner.nextLine());
                if (size == 4 || size == 9) {
                    return size;
                } else {
                    System.out.println("Invalid input. Please enter 4 or 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static int getUserInputPercentage(Scanner scanner) {
        while (true) {
            System.out.print("Enter the percentage of input you want (30 or 70): ");
            try {
                int percentage = Integer.parseInt(scanner.nextLine());
                if (percentage == 30 || percentage == 70) {
                    return percentage;
                } else {
                    System.out.println("Invalid input. Please enter 30 or 70.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static String loadPuzzleData(int size, int percentage) {
        String filename = "puzzles.txt";
        StringBuilder puzzle = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean found = false;
            String targetLabel = size + "x" + size + " " + percentage + "% puzzles";
            while ((line = reader.readLine()) != null) {
                if (line.contains(targetLabel)) {
                    found = true;
                    continue;
                }
                if (found) {
                    if (line.trim().isEmpty()) {
                        break;
                    }
                    puzzle.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (puzzle.length() == 0) {
            System.out.println("No puzzle found for size " + size + " and percentage " + percentage + ".");
            return "";
        }
        return puzzle.toString();
    }

    private static boolean solve(int[][] board) {
        int[] emptyCell = isEmpty(board);
        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int i = 1; i <= userInputSize; i++) {
            if (isValid(board, i, row, col)) {
                board[row][col] = i;
                clearConsole();
                System.out.println("\nSolution: \n");
                printSudoku(board);

                if (solve(board)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    private static int[] isEmpty(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static boolean isValid(int[][] board, int num, int row, int col) {
        for (int i = 0; i < board[0].length; i++) {
            if (board[row][i] == num && i != col) {
                return false;
            }
        }

        for (int i = 0; i < board.length; i++) {
            if (board[i][col] == num && i != row) {
                return false;
            }
        }

        int boxSize = (int) Math.sqrt(userInputSize);
        int boxRow = row / boxSize;
        int boxCol = col / boxSize;

        for (int i = boxRow * boxSize; i < boxRow * boxSize + boxSize; i++) {
            for (int j = boxCol * boxSize; j < boxCol * boxSize + boxSize; j++) {
                if (board[i][j] == num && (i != row || j != col)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean solutionChecker(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int num = board[i][j];
                if (!isValid(board, num, i, j)) {
                    System.out.println("Constraint violated at (" + i + ", " + j + "): " + num);
                    return false;
                }
            }
        }
        return true;
    }

    private static void printSudoku(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            if (i % Math.sqrt(userInputSize) == 0 && i != 0) {
                for (int k = 0; k < userInputSize + Math.sqrt(userInputSize) - 1; k++) {
                    System.out.print("--");
                }
                System.out.println();
            }

            for (int j = 0; j < board[i].length; j++) {
                if (j % Math.sqrt(userInputSize) == 0 && j != 0) {
                    System.out.print("| ");
                }

                if (j == userInputSize - 1) {
                    System.out.println(board[i][j] == 0 ? " " : board[i][j]);
                } else {
                    System.out.print((board[i][j] == 0 ? " " : board[i][j]) + " ");
                }
            }
        }
    }

    private static void clearConsole() {
        try {
            if (CLEAR.equals("cls")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}