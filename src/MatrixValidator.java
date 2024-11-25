/*
Programmer: Zach Nowlin
Date: November 25, 2024
Purpose: Utility class for matrix validation. Ensures matrices meet the required
         format and dimension specifications for processing.
*/
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
