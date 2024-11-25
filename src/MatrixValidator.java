public class MatrixValidator {
  public static void validateMatrix(int[][] matrix, String matrixName) throws IllegalArgumentException {
    if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
      throw new IllegalArgumentException("Invalid " + matrixName + " dimensions");
    }

    int cols = matrix[0].length;
    for (int i = 0; i < matrix.length; i++) {
      if (matrix[i].length != cols) {
        throw new IllegalArgumentException(matrixName + " is not rectangular");
      }
    }
  }
}
