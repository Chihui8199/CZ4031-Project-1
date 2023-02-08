import storage.Storage;
import utils.Parser;

public class Main {

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        String fileName = dir + "/utils/data/data.tsv";
        // Read in the data and write to db
        Parser.readTSVFile(fileName);

        // when insert
        // Storage storage = new Storage("database.txt");
        // String data = "string to upload to database";
        //Parser.checkIfDataExceedsDiskSize(data.getBytes());

//        System.out.println("Initial Commit");
//
//
//        Scanner consoleScanner = new Scanner(System.in);
//        System.out.print("Enter the name of the file to read: ");
//        String filename = consoleScanner.nextLine();
//
//
//
//        List<Record> data = TSVFileReader.readTSVFile(filename);
//        for (Record record : data) {
//            System.out.println("Key: " + record.getKey());
//            System.out.println("Field 1: " + record.getAvgRating());
//            System.out.println("Field 2: " + record.getNumVotes());
//            System.out.println();
//        }
//
//
//        consoleScanner.close();

  }

}