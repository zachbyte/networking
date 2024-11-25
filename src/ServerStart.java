/*
Programmer: Zach Nowlin
Date: November 25, 2024
Purpose: Entry point for the server application. Initializes and starts the server,
         providing a command-line interface for server control and shutdown.
*/
import java.util.Scanner;

public class ServerStart {
  private static Server server;

  public static void main(String[] args) {
    server = new Server();

    // Start server in a separate thread
    Thread serverThread = new Thread(new Runnable() {
      @Override
      public void run() {
        server.startServer();
      }
    });
    serverThread.start();

    // Command line interface for server control
    Scanner scanner = new Scanner(System.in);
    System.out.println("Server commands:");
    System.out.println("Type 'exit' to stop the server");

    while (true) {
      String command = scanner.nextLine().trim().toLowerCase();
      if (command.equals("exit")) {
        server.stopServer();
        System.out.println("Server shutting down...");
        break;
      }
    }

    scanner.close();
    System.exit(0);
  }
}
