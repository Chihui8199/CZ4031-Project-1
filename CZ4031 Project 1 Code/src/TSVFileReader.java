import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

//TSVFileReader class reads inputs from a tab-separated values (TSV) file

public class TSVFileReader {
    public static int[] readValuesFromFile(String filename) throws FileNotFoundException {
      Scanner fileScanner = new Scanner(new File(filename));
      StringBuilder valuesString = new StringBuilder();
  
      while (fileScanner.hasNextLine()) {
        String line = fileScanner.nextLine();
        valuesString.append(line).append("\t");
      }
  
      fileScanner.close();
      String[] values = valuesString.toString().split("\t");
      int[] keys = new int[values.length];
      for (int i = 0; i < values.length; i++) {
        keys[i] = Integer.parseInt(values[i]);
      }
  
      return keys;
    }
  }