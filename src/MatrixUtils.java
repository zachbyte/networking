import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MatrixUtils {
  public static String matrixToString(int[][] matrix) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        sb.append(String.format("%4d", matrix[i][j]));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public static void printMatrixInfo(int[][] matrix, String name) {
    System.out.println(name + " dimensions: " + matrix.length + "x" + matrix[0].length);
    System.out.println("Contents:");
    System.out.println(matrixToString(matrix));
  }

  public static boolean isValidMatrixFile(String filename) {
    File file = new File(filename);
    if (!file.exists() || !file.isFile()) {
      return false;
    }

    try (Scanner scanner = new Scanner(file)) {
      // Check if file has at least two integers for dimensions
      if (!scanner.hasNextInt())
        return false;
      int rows = scanner.nextInt();
      if (!scanner.hasNextInt())
        return false;
      int cols = scanner.nextInt();

      // Check if dimensions are valid
      if (rows <= 0 || cols <= 0 || rows > ConnectionConfig.MAX_MATRIX_SIZE
          || cols > ConnectionConfig.MAX_MATRIX_SIZE) {
        return false;
      }

      // Check if file has enough numbers for two matrices
      int expectedNumbers = rows * cols * 2;
      int count = 0;
      while (scanner.hasNextInt()) {
        scanner.nextInt();
        count++;
      }

      return count == expectedNumbers;

    } catch (FileNotFoundException e) {
      return false;
    }
  }
}
