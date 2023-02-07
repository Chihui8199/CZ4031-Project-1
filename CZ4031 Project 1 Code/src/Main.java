import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        
        System.out.println("Initial Commit");

        
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter the name of the file to read: ");
        String filename = consoleScanner.nextLine();

        try {
            int[] keys = TSVFileReader.readValuesFromFile(filename);
            System.out.print(keys); //Testing
            // BPlusTreeBuilder.buildTree(keys, 2, true);
            // Tree builder code ^
        } catch (FileNotFoundException e) {
            System.out.println("Error: the file " + filename + " was not found.");
        }

        consoleScanner.close();
  }

}