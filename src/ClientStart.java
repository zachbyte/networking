/*
Programmer: Zach Nowlin
Date: November 25, 2024
Purpose: Entry point for the client application. Initializes and launches the
         client GUI with proper window closing behavior.
*/
import javax.swing.JFrame;

public class ClientStart {
  public static void main(String[] args) {
    try {
      Client client = new Client();
      client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } catch (Exception e) {
      System.err.println("Error starting client: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
