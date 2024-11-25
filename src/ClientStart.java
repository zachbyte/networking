
// ClientStart.java
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
