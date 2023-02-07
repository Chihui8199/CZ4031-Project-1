import utils.Parser;

public class Main {

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        String fileName = dir + "/utils/data/data.tsv";
        // Read in the data and write to db
        Parser.readTSVFile(fileName);
  }

}