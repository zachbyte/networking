import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Client extends JFrame {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
  private static final int PORT = 12345;
  private static final String SERVER_ADDRESS = "localhost";

  private JTextField enterField;
  private JTextArea displayArea;
  private JButton connectButton;
  private JButton clearButton;
  private JLabel statusLabel;
  private JPanel buttonPanel;

  private ObjectOutputStream output;
  private ObjectInputStream input;
  private Socket client;
  private volatile boolean isConnected;

  public Client() {
    super("Matrix Processing Client");
    setupLogger();
    initializeGUI();
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        closeConnection();
      }
    });
  }

  private void setupLogger() {
    try {
      FileHandler fh = new FileHandler("client_log.txt", true);
      fh.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(fh);
    } catch (IOException e) {
      System.err.println("Could not setup logger: " + e.getMessage());
    }
  }

  private void initializeGUI() {
    setLayout(new BorderLayout(5, 5));

    // North Panel with input field and status
    JPanel northPanel = new JPanel(new BorderLayout(5, 5));
    enterField = new JTextField("Enter matrix filename");
    enterField.setEditable(false);
    statusLabel = new JLabel("Status: Disconnected", SwingConstants.CENTER);
    statusLabel.setForeground(Color.RED);
    northPanel.add(enterField, BorderLayout.CENTER);
    northPanel.add(statusLabel, BorderLayout.SOUTH);
    add(northPanel, BorderLayout.NORTH);

    // Center Panel with display area
    displayArea = new JTextArea();
    displayArea.setEditable(false);
    displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane scrollPane = new JScrollPane(displayArea);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    add(scrollPane, BorderLayout.CENTER);

    // Button Panel
    buttonPanel = new JPanel(new FlowLayout());
    connectButton = new JButton("Connect to Server");
    clearButton = new JButton("Clear Display");
    buttonPanel.add(connectButton);
    buttonPanel.add(clearButton);
    add(buttonPanel, BorderLayout.SOUTH);

    // Add Action Listeners
    connectButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isConnected) {
          connectToServer();
        } else {
          closeConnection();
        }
      }
    });

    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        displayArea.setText("");
      }
    });

    enterField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        processFilename(e.getActionCommand());
      }
    });

    // Set window properties
    setSize(600, 400);
    setMinimumSize(new Dimension(400, 300));
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void connectToServer() {
    try {
      displayMessage("\nAttempting connection to server...");
      client = new Socket(SERVER_ADDRESS, PORT);
      setupStreams();
      isConnected = true;
      updateGUIForConnection(true);
      displayMessage("\nConnected to server at " + SERVER_ADDRESS);
      LOGGER.log(Level.INFO, "Connected to server successfully");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error connecting to server", e);
      displayMessage("\nError connecting to server: " + e.getMessage());
      JOptionPane.showMessageDialog(this,
          "Could not connect to server.\nPlease ensure server is running.",
          "Connection Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void setupStreams() throws IOException {
    output = new ObjectOutputStream(client.getOutputStream());
    output.flush();
    input = new ObjectInputStream(client.getInputStream());
    LOGGER.log(Level.INFO, "Streams established");
  }

  private void closeConnection() {
    displayMessage("\nClosing connection...");
    updateGUIForConnection(false);
    isConnected = false;

    try {
      if (output != null)
        output.close();
      if (input != null)
        input.close();
      if (client != null)
        client.close();
      LOGGER.log(Level.INFO, "Connection closed successfully");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error closing connection", e);
    }
  }

  private void updateGUIForConnection(boolean connected) {
    enterField.setEditable(connected);
    statusLabel.setText("Status: " + (connected ? "Connected" : "Disconnected"));
    statusLabel.setForeground(connected ? Color.GREEN.darker() : Color.RED);
    connectButton.setText(connected ? "Disconnect" : "Connect to Server");
    if (!connected) {
      enterField.setText("Enter matrix filename");
    }
  }

  private void processFilename(String filename) {
    if (!isConnected) {
      displayMessage("\nNot connected to server!");
      return;
    }

    try {
      MatrixData matrixData = readMatrixFile(filename);
      if (matrixData != null) {
        sendMatricesToServer(matrixData);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error processing file: " + filename, e);
      displayMessage("\nError processing file: " + e.getMessage());
    }
  }

  private MatrixData readMatrixFile(String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      throw new FileNotFoundException("File not found: " + filename);
    }

    Scanner fileScanner = new Scanner(file);
    try {
      // Read dimensions
      int rows = fileScanner.nextInt();
      int cols = fileScanner.nextInt();

      validateDimensions(rows, cols);

      int[][] matrix1 = new int[rows][cols];
      int[][] matrix2 = new int[rows][cols];

      // Read first matrix
      readMatrixData(fileScanner, matrix1, rows, cols);
      // Read second matrix
      readMatrixData(fileScanner, matrix2, rows, cols);

      return new MatrixData(rows, cols, matrix1, matrix2);

    } catch (Exception e) {
      throw new IOException("Invalid matrix file format: " + e.getMessage());
    } finally {
      fileScanner.close();
    }
  }

  private void validateDimensions(int rows, int cols) throws IOException {
    if (rows <= 0 || cols <= 0) {
      throw new IOException("Invalid matrix dimensions: rows=" + rows + ", cols=" + cols);
    }
    if (rows % 2 != 0 || cols % 2 != 0) {
      throw new IOException("Matrix dimensions must be even numbers for quadrant processing");
    }
  }

  private void readMatrixData(Scanner scanner, int[][] matrix, int rows, int cols) throws IOException {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (!scanner.hasNextInt()) {
          throw new IOException("Insufficient data in matrix");
        }
        matrix[i][j] = scanner.nextInt();
      }
    }
  }

  private void sendMatricesToServer(MatrixData matrixData) {
    try {
      output.writeObject(matrixData.matrix1);
      output.writeObject(matrixData.matrix2);
      output.flush();

      displayMessage("\nMatrices sent to server successfully!");
      displayMessage("\nMatrix 1:");
      displayMatrix(matrixData.matrix1);
      displayMessage("\nMatrix 2:");
      displayMatrix(matrixData.matrix2);

      LOGGER.log(Level.INFO, "Matrices sent to server: " +
          matrixData.rows + "x" + matrixData.cols);

    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error sending matrices to server", e);
      displayMessage("\nError sending matrices: " + e.getMessage());
      closeConnection();
    }
  }

  private void displayMatrix(int[][] matrix) {
    StringBuilder sb = new StringBuilder();
    for (int[] row : matrix) {
      for (int val : row) {
        sb.append(String.format("%4d", val));
      }
      sb.append("\n");
    }
    displayMessage(sb.toString());
  }

  private void displayMessage(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        displayArea.append(message);
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
      }
    });
  }

  private static class MatrixData implements Serializable {
    private static final long serialVersionUID = 1L;
    final int rows;
    final int cols;
    final int[][] matrix1;
    final int[][] matrix2;

    MatrixData(int rows, int cols, int[][] matrix1, int[][] matrix2) {
      this.rows = rows;
      this.cols = cols;
      this.matrix1 = matrix1;
      this.matrix2 = matrix2;
    }
  }
}
