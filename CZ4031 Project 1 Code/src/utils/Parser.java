package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import storage.Record;
import storage.Storage;

public class Parser {

    private static final int MIN_DISK_CAPACITY = 100 * 1024 * 1024;
    //private static final int MAX_DISK_CAPACITY = 500 * 1024 * 1024;
    private static final int BLOCK_SIZE = 200;
    private String filename;

    private static final int MAX_DISK_CAPACITY = 500 * (int)(Math.pow(10,6));


    /**
     * Loads in the data and stores it in the database
     * @param filePath takes in the file path of data.tsv
     */
    // TODO: change this to Buffered Reader implementation I couldn't figure out the conversin
    // TODO: each Record should be stored as a fixed length and is not now
    public static void readTSVFile(String filePath) {
        String line;
        try {
            // initialise database
            Storage db = new Storage(MAX_DISK_CAPACITY, BLOCK_SIZE);
            //start loading data
            Scanner sc = new Scanner(new FileReader(filePath));
            sc.nextLine(); //skip the first line (the column line)
            while(sc.hasNextLine()) {
                String newLine = sc.nextLine();
                String[] fields = newLine.split("\t");
                String tconst = fields[0];
                // TODO: parse each of the three fields to byteArray so that each Record can be initialise as a fixed length
                float averageRating = Float.parseFloat(fields[1]);
                int numVotes = Integer.parseInt(fields[2]);
                Record rec = createRecord(tconst, averageRating, numVotes);
                System.out.println(rec.toString());
                // write the each record object to the database
                db.writeRecordToStorage(rec);
                db.printDatabaseInfo();
                // create a BP+ indexing as we read the file
                //BPTree tree = new BPTree(); // TODO: to be implemented
                int key = rec.getNumVotes();
                //tree.insertKey(key) // TODO: not sure what are the other params tbc
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void checkIfDataExceedsDiskSize(byte[] data) {
//        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(filename, true))) {
//            File file = new File(filename);
//            long fileSize = file.length();
//            if (fileSize + data.length > MIN_DISK_CAPACITY && fileSize + data.length < MAX_DISK_CAPACITY) {
//                output.write(data);
//            } else {
//                System.out.println("Error: disk capacity exceeded");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



    /**
     * for each line of data read in create a record object and stores it into the database
     * @param tconst alphanumeric unique identifier of the title
     * @param averageRating weighted average of all the individual user ratings
     * @param numVotes number of votes the title has received
     */
    public static Record createRecord(String tconst, float averageRating, int numVotes){
        // creates a new Record object
        Record rec = new Record(tconst,averageRating, numVotes);
        return rec;

    }

}
