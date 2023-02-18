package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.*;

import java.util.ArrayList;
import java.util.Scanner;

import storage.Address;
import storage.Record;
import storage.Storage;

import index.*;

public class Parser {



    private static final int MIN_DISK_CAPACITY = 100 * 1024 * 1024; // TODO: i think this calculation is not correct

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
        // try {
            Address addr1 = new Address(1,1);
            Address addr2 = new Address(2,2);
            Address addr3 = new Address(3,3);
            Address addr4 = new Address(4,4);
            Address addr5 = new Address(5,5);
            testBplusTree tree = new testBplusTree(); 
            tree.createFirstNode();

            tree.insertKey(1,addr1);
            tree.insertKey(4,addr2);
            tree.insertKey(4, addr3);
            tree.insertKey(7,addr3);
            tree.insertKey(10,addr4); //
            tree.insertKey(17,addr5); //
            tree.insertKey(21,addr5);
            tree.insertKey(31,addr2);
            tree.insertKey(25,addr1);
            tree.insertKey(19,addr2); //
            tree.insertKey(20,addr4); //
            tree.insertKey(28,addr5);
            tree.insertKey(42,addr5);

            
            // 1 4 7 10 17 21 31 25 19 20 28 42

            // tree.insertKey(1,addr1);
            // tree.insertKey(2,addr2);
            // tree.insertKey(3,addr3);
            // tree.insertKey(4,addr4);
            // tree.insertKey(5,addr5);
            // tree.insertKey(6,addr5);
            // tree.insertKey(4,addr2);
            // tree.insertKey(2,addr1);
            // tree.insertKey(3,addr2);
            // tree.insertKey(4,addr4);
            // tree.insertKey(5,addr5);
            // tree.insertKey(6,addr5);
            // tree.insertKey(7,addr1);
            // tree.insertKey(8,addr3);
            // tree.insertKey(9,addr4);
            // tree.insertKey(10,addr2);
            // tree.insertKey(11,addr4);
            // tree.insertKey(12,addr3);
            // tree.insertKey(13,addr2);
            // tree.insertKey(14,addr4);
            // tree.insertKey(15,addr3);

            System.out.print(tree.searchKey2(4));
            System.out.printf("++++PRINTING NEW SEARCH METHODS++++\n");
            System.out.printf("\nKEY SEARCH %s", tree.keySearch(1));
            System.out.printf("\nRANGE SEARCH %s", tree.rangeSearch(1, 4));
            //System.out.printf("\nFINDING " + tree.keySearch(4));
            // tree.insertKey(16,addr2);
            // tree.insertKey(17,addr4);
            // tree.insertKey(18,addr3);
            // tree.insertKey(19,addr2);
            // tree.insertKey(20,addr4);
            // tree.insertKey(21,addr3);
            

            // // initialise database
            // Storage db = new Storage(MAX_DISK_CAPACITY, BLOCK_SIZE);
            // // start loading data
            // BufferedReader reader = new BufferedReader(new FileReader(filePath));
            // reader.readLine(); // skip the first line (the column line)
            // String line;

            // while ((line = reader.readLine()) != null) {
            //     String[] fields = line.split("\t");
            //     String tconst = fields[0];
            //     // TODO: parse each of the three fields to byteArray so that each Record can be initialise as a fixed length
            //     float averageRating = Float.parseFloat(fields[1]);
            //     int numVotes = Integer.parseInt(fields[2]);
            //     Record rec = createRecord(tconst, averageRating, numVotes);
            //     System.out.println(rec.toString());
            //     // write the each record object to the database
            //     Address add = db.writeRecordToStorage(rec);
            //     db.printDatabaseInfo();
            //     // create a BP+ indexing as we read the file
            //     // TODO: to be implemented
            //     int key = rec.getNumVotes();
            //     tree.insertKey(key, add); // TODO: not sure what are the other params tbc
            // }
            // reader.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        //}
    }


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

    public void checkIfDataExceedsDiskSize(byte[] data) {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(filename, true))) {
            File file = new File(filename);
            long fileSize = file.length();
            if (fileSize + data.length > MIN_DISK_CAPACITY && fileSize + data.length < MAX_DISK_CAPACITY) {
                output.write(data);
            } else {
                System.out.println("Error: disk capacity exceeded");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
