public class MatrixValidator {
  public static void validateMatrix(int[][] matrix, String matrixName) throws IllegalArgumentException {
    if (matrix == null) {
      throw new IllegalArgumentException(matrixName + " is null");
    }

    if (matrix.length == 0) {
      throw new IllegalArgumentException(matrixName + " has zero rows");
    }

    int cols = matrix[0].length;
    if (cols == 0) {
      throw new IllegalArgumentException(matrixName + " has zero columns");
    }

    // Check if all rows have the same length
    for (int i = 0; i < matrix.length; i++) {
      if (matrix[i].length != cols) {
        throw new IllegalArgumentException(
            matrixName + " is not rectangular (row " + i + " has different length)");
      }
    }

    // Check if dimensions are even (required for quadrant processing)
    if (matrix.length % 2 != 0 || cols % 2 != 0) {
      throw new IllegalArgumentException(
          matrixName + " dimensions must be even numbers for quadrant processing");
    }
  }

  public static void validateMatrixPair(int[][] matrix1, int[][] matrix2)
      throws IllegalArgumentException {
    validateMatrix(matrix1, "Matrix 1");
    validateMatrix(matrix2, "Matrix 2");

    // Check if matrices have same dimensions
    if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
      throw new IllegalArgumentException(
          "Matrices have different dimensions: " +
              "Matrix 1 is " + matrix1.length + "x" + matrix1[0].length + ", " +
              "Matrix 2 is " + matrix2.length + "x" + matrix2[0].length);
    }
  }
}
