// Ask user to enter the matrix file
// *** not covered:
//	  Perform the multi-thread computation and gather the result
//	  Display the result on the JScrollPane
// Demo: Display the content of the matrix from the file opened by the previous step

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class ReadWriteData extends JFrame {
  private JTextField enterField; // JTextField to enter the matrix file name
  private JTextArea displayArea; // JTextAreato display the computation result
  private File file;
  private Scanner input;

  // set up GUI
  public ReadWriteData() {
    super("Open File and Output Data Example");

    // create enterField and register its listener
    enterField = new JTextField("Enter file name here");
    add(enterField, BorderLayout.NORTH);

    displayArea = new JTextArea();
    add(new JScrollPane(displayArea), BorderLayout.CENTER);

    enterField.addActionListener(
        new ActionListener() {
          // get the file name specified by user
          public void actionPerformed(ActionEvent event) {
            getFile(event.getActionCommand());
          }
        });

    setSize(400, 300); // set size of window
    setVisible(true); // show window
  }

  // load document
  private void getFile(String file_name) {
    file = new File(file_name);
    try {
      input = new Scanner(file);
    } catch (FileNotFoundException e) {
      System.out.printf("%nError on file: %s (either enpty or wrong file format)%n%n", file);
      e.printStackTrace();
      System.exit(1);
    }

    String s;
    while (input.hasNextLine()) {
      s = input.nextLine();
      displayArea.append(s + "\n");
    }
  }
}
