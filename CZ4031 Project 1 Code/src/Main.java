import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        
        System.out.println("Initial Commit");

        
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter the name of the file to read: ");
        String filename = consoleScanner.nextLine();
        File myFile = new File(filename);
        try {
            System.out.println("Attempting to read from file in: "+myFile.getCanonicalPath());
        } catch (IOException e) {
            System.out.println("Unable to read file: "+myFile);
            e.printStackTrace();
        }

        //Testing - check if TSVReader is working
        // List<String[]> tsvData = TSVFileReader.readTSVFile(filename);
        // for (String[] line : tsvData) {
        //     for (String field : line) {
        //         System.out.print(field + "\t");
        //     }
        //     System.out.println();
        // }
        // consoleScanner.close();
  }

}