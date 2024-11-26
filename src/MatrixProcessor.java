import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class MatrixProcessor {
  public static int[][] processConcurrently(int[][] matrix1, int[][] matrix2)
      throws InterruptedException, ExecutionException {
    if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
      throw new IllegalArgumentException("Matrices must have the same dimensions");
    }

    int rows = matrix1.length;
    int cols = matrix1[0].length;
    int[][] result = new int[rows][cols];

    // Calculate section sizes
    int numThreads = 4; // We'll still use 4 threads
    int rowsPerSection = Math.max(1, rows / 2);
    int colsPerSection = Math.max(1, cols / 2);

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<QuadrantResult>> futures = new ArrayList<>();

    // Process sections
    for (int i = 0; i < rows; i += rowsPerSection) {
      for (int j = 0; j < cols; j += colsPerSection) {
        int endRow = Math.min(i + rowsPerSection, rows);
        int endCol = Math.min(j + colsPerSection, cols);
        futures.add(executor.submit(new QuadrantProcessor(matrix1, matrix2, i, j, endRow, endCol)));
      }
    }

    // Collect results
    for (Future<QuadrantResult> future : futures) {
      QuadrantResult qResult = future.get();
      copyQuadrantToResult(result, qResult);
    }

    executor.shutdown();
    return result;
  }

  private static void copyQuadrantToResult(int[][] result, QuadrantResult qResult) {
    for (int i = qResult.startRow; i < qResult.endRow; i++) {
      for (int j = qResult.startCol; j < qResult.endCol; j++) {
        result[i][j] = qResult.values[i - qResult.startRow][j - qResult.startCol];
      }
    }
  }

  private static class QuadrantProcessor implements Callable<QuadrantResult> {
    private final int[][] matrix1;
    private final int[][] matrix2;
    private final int startRow, startCol, endRow, endCol;

    QuadrantProcessor(int[][] matrix1, int[][] matrix2, int startRow, int startCol, int endRow, int endCol) {
      this.matrix1 = matrix1;
      this.matrix2 = matrix2;
      this.startRow = startRow;
      this.startCol = startCol;
      this.endRow = endRow;
      this.endCol = endCol;
    }

    @Override
    public QuadrantResult call() {
      int rows = endRow - startRow;
      int cols = endCol - startCol;
      int[][] result = new int[rows][cols];

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          result[i][j] = matrix1[startRow + i][startCol + j] + matrix2[startRow + i][startCol + j];
        }
      }

      return new QuadrantResult(result, startRow, startCol, endRow, endCol);
    }
  }

  private static class QuadrantResult {
    int[][] values;
    int startRow, startCol, endRow, endCol;

    QuadrantResult(int[][] values, int startRow, int startCol, int endRow, int endCol) {
      this.values = values;
      this.startRow = startRow;
      this.startCol = startCol;
      this.endRow = endRow;
      this.endCol = endCol;
    }
  }
}
