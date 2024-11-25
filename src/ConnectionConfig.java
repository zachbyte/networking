public class ConnectionConfig {
  public static final int PORT = 12345;
  public static final String SERVER_ADDRESS = "localhost";
  public static final int SOCKET_TIMEOUT = 10000; // 10 seconds
  public static final int MAX_MATRIX_SIZE = 1000; // Maximum matrix dimension

  private ConnectionConfig() {
    // Private constructor to prevent instantiation
  }
}
