import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

//used to read data from an input source to be inserted into the B+ tree.
//On creation : to be used with a bplustreebuilder
public class DataReader {
    private Scanner scanner;
  
    public DataReader(Scanner scanner) {
      this.scanner = scanner;
    }
  
    public int[] readData() {
      System.out.print("Enter the name of the file to read: ");
      String filename = scanner.nextLine();
  
      try {
        Scanner fileScanner = new Scanner(new File(filename));
        int[] data = new int[0];
        while (fileScanner.hasNextInt()) {
          int[] newData = new int[data.length + 1];
          System.arraycopy(data, 0, newData, 0, data.length);
          newData[data.length] = fileScanner.nextInt();
          data = newData;
        }
        fileScanner.close();
        return data;
      } catch (FileNotFoundException e) {
        System.out.println("Error: the file " + filename + " was not found.");
        return new int[0];
      }
    }
  }
