package utils;

import java.io.IOException;

import java.io.*;
import storage.Address;
import storage.Record;
import storage.Storage;

import index.*;

public class Parser {

    private static final int BLOCK_SIZE = 200;
    /**
     * Loads in the data and stores it in the database
     * 
     * @param filePath takes in the file path of data.tsv
     */
    private static int counter = 0;
    public static void readTSVFile(String filePath, int diskCapacity) {
        try {
            // initialise database
            Storage db = new Storage(diskCapacity, BLOCK_SIZE);
            // start loading data
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // skip the first line (the column line)
            String line;

            testBplusTree tree = new testBplusTree(); // create a BP+ indexing as we read the file
            
            while ((line = reader.readLine()) != null) {
                counter++;
                if(counter%50000==0)
                    System.out.println(counter + " data rows read");
                String[] fields = line.split("\t");
                String tconst = fields[0];
                float averageRating = Float.parseFloat(fields[1]);
                int numVotes = Integer.parseInt(fields[2]);
                Record rec = createRecord(tconst, averageRating, numVotes);
                Address add = db.writeRecordToStorage(rec);
                int key = rec.getNumVotes();
                tree.insertKey(key, add);
            }
            db.printDatabaseInfo();

            tree.printBPlusTree(testBplusTree.getRoot());

            System.out.println("All data has been stored in database successfully!");
            System.out.println("\n---------------Experiment 1------------------");
            System.out.printf("Number of blocks used: %s\n", db.getNumberBlockUsed());
            System.out.printf("Size of database: %sMB\n", (float) db.getNumberBlockUsed() * BLOCK_SIZE/1000000);
            reader.close();
            testBplusTree.experimentTwo(tree); 
            testBplusTree.experimentThree(db, tree);
            // testBplusTree.experimentFour(db, tree); 
            testBplusTree.experimentFive(tree); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * for each line of data read in create a record object and stores it into the
     * database
     * 
     * @param tconst        alphanumeric unique identifier of the title
     * @param averageRating weighted average of all the individual user ratings
     * @param numVotes      number of votes the title has received
     */
    public static Record createRecord(String tconst, float averageRating, int numVotes) {
        Record rec = new Record(tconst, averageRating, numVotes);
        return rec;
    }

}
