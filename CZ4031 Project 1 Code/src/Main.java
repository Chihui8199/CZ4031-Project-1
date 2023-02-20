import java.io.File;
import index.testBplusTree;

import utils.Parser;

public class Main {

  public static void main(String[] args) {
    String dir = System.getProperty("user.dir");
    // String fileName = dir + File.separator + File.separator + "src" + File.separator + "utils" + File.separator + "data"
    //     + File.separator + "data.tsv";
    // String fileName = "/Users/chihui/Desktop/CZ4031 Project/CZ4031 Project 1
    // Code/src/utils/data/data.tsv";
    // String fileName = "/Users/ragne/OneDrive/Documents/GitHub/CZ4031-Project-1/CZ4031 Project 1 Code/src/utils/data/data.tsv";
    String fileName = "/Users/shannenlee/Documents/GitHub/CZ4031-Project-1/CZ4031 Project 1 Code/src/utils/data/data.tsv";
    // Read in the data and write to db
    File file = new File(fileName);
    if (file.exists()) {
      System.out.print("Yes File Exist");
      Parser.readTSVFile(fileName);
    } else {
      System.out.print("No, file does not exist");
    }

    // testBplusTree.insertNode();

    // when insert
    // Storage storage = new Storage("database.txt");
    // String data = "string to upload to database";
    // Parser.checkIfDataExceedsDiskSize(data.getBytes());

    // System.out.println("Initial Commit");
    //
    //
    // Scanner consoleScanner = new Scanner(System.in);
    // System.out.print("Enter the name of the file to read: ");
    // String filename = consoleScanner.nextLine();
    //
    //
    //
    // List<Record> data = TSVFileReader.readTSVFile(filename);
    // for (Record record : data) {
    // System.out.println("Key: " + record.getKey());
    // System.out.println("Field 1: " + record.getAvgRating());
    // System.out.println("Field 2: " + record.getNumVotes());
    // System.out.println();
    // }
    //
    //
    // consoleScanner.close();

  }

}