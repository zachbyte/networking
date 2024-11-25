/*
Programmer: Zach Nowlin
Date: November 25, 2024
Purpose: Server component of matrix processing system. Accepts client connections,
         receives matrices, and manages multiple client connections concurrently.
         Displays received matrices and prepares for future matrix calculations.
*/
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Server {
  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
  private static final int PORT = 12345;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private ServerSocket server;
  private final AtomicInteger clientCount = new AtomicInteger(0);
  private volatile boolean running;

  public Server() {
    setupLogger();
  }

  private void setupLogger() {
    try {
      FileHandler fh = new FileHandler("server_log.txt", true);
      fh.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(fh);
    } catch (IOException e) {
      System.err.println("Could not setup logger: " + e.getMessage());
    }
  }

  public void startServer() {
    try {
      server = new ServerSocket(PORT, 100);
      running = true;
      LOGGER.log(Level.INFO, "Server started on port " + PORT);
      System.out.println("Server started on port " + PORT);

      while (running) {
        waitForConnection();
      }

    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Server error", e);
      System.err.println("Server error: " + e.getMessage());
    }
  }

  private void waitForConnection() {
    try {
      System.out.println("\nWaiting for connection...");
      Socket connection = server.accept();

      int clientId = clientCount.incrementAndGet();
      LOGGER.log(Level.INFO, "Client " + clientId + " connected from " +
          connection.getInetAddress().getHostAddress());

      ClientHandler handler = new ClientHandler(connection, clientId);
      new Thread(handler).start();

    } catch (IOException e) {
      if (running) {
        LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
      }
    }
  }

  private class ClientHandler implements Runnable {
    private final Socket connection;
    private final int clientId;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientHandler(Socket connection, int clientId) {
      this.connection = connection;
      this.clientId = clientId;
    }

    @Override
    public void run() {
      try {
        setupStreams();
        processClient();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error handling client " + clientId, e);
      } finally {
        closeConnection();
      }
    }

    private void setupStreams() throws IOException {
      output = new ObjectOutputStream(connection.getOutputStream());
      output.flush();
      input = new ObjectInputStream(connection.getInputStream());
      LOGGER.log(Level.INFO, "Streams established for client " + clientId);
    }

    private void processClient() {
      try {
        while (true) {
          // Read matrices from client
          int[][] matrix1 = (int[][]) input.readObject();
          int[][] matrix2 = (int[][]) input.readObject();

          // Log receipt of matrices
          String timestamp = DATE_FORMAT.format(new Date());
          LOGGER.log(Level.INFO, String.format(
              "Received matrices from client %d at %s - Dimensions: %dx%d",
              clientId, timestamp, matrix1.length, matrix1[0].length));

          // Display matrices
          System.out.println("\nReceived from client " + clientId + ":");
          System.out.println("Matrix 1:");
          displayMatrix(matrix1);
          System.out.println("\nMatrix 2:");
          displayMatrix(matrix2);
        }
      } catch (EOFException e) {
        LOGGER.log(Level.INFO, "Client " + clientId + " closed connection");
      } catch (ClassNotFoundException | IOException e) {
        LOGGER.log(Level.SEVERE, "Error processing matrices from client " + clientId, e);
      }
    }

    private void displayMatrix(int[][] matrix) {
      for (int[] row : matrix) {
        for (int val : row) {
          System.out.printf("%4d", val);
        }
        System.out.println();
      }
    }

    private void closeConnection() {
      try {
        if (output != null)
          output.close();
        if (input != null)
          input.close();
        if (connection != null)
          connection.close();
        LOGGER.log(Level.INFO, "Connection closed for client " + clientId);
        System.out.println("Connection closed for client " + clientId);
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error closing connection for client " + clientId, e);
      }
    }
  }

  public void stopServer() {
    running = false;
    try {
      if (server != null && !server.isClosed()) {
        server.close();
        LOGGER.log(Level.INFO, "Server stopped");
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error stopping server", e);
    }
  }
}
