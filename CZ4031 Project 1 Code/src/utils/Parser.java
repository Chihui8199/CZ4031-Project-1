package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import storage.Record;
import storage.Storage;

public class Parser {
    /**
     * Loads in the data and stores it in the database
     * @param filePath takes in the file path of data.tsv
     */
    // TODO: change this to Buffered Reader implementation I couldn't figure out the conversin
    // TODO: each Record should be stored as a fixed length and is not now
    public static void readTSVFile(String filePath) {
        String line;
        try {
            //start loading data
            Scanner sc = new Scanner(new FileReader(filePath));
            sc.nextLine(); //skip the first line (the column line)
            while(sc.hasNextLine()) {
                String newLine = sc.nextLine();
                String[] fields = newLine.split("\t");
                String tconst = fields[0];
                float averageRating = Float.parseFloat(fields[1]);
                int numVotes = Integer.parseInt(fields[2]);
                createRecord(tconst, averageRating, numVotes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * for each line of data read in create a record object and stores it into the database
     * @param tconst alphanumeric unique identifier of the title
     * @param averageRating weighted average of all the individual user ratings
     * @param numVotes number of votes the title has received
     */
    public static void createRecord(String tconst, float averageRating, int numVotes){
        // creates a new Record object
        Record rec = new Record(tconst,averageRating, numVotes);
        // write the Record to the database
        Storage db = new Storage(500000000, 200);
        db.writeRecordToStorage(rec);
        // create a BP+ indexing as we read the file
        //BPTree tree = new BPTree(); // TODO: to be implemented
        int key = rec.getNumVotes();
        //tree.insertKey(key) // TODO: not sure what are the other params tbc

    }

}
