package utils;

import java.io.IOException;
import java.util.Scanner;

import java.io.*;

import storage.Address;
import storage.Record;
import storage.Disk;

import index.*;

public class Parser {
    private static final int BLOCK_SIZE = 200;
    private static int counter = 0;

    public static void readTSVFile(String filePath, int diskCapacity) {
        try {
            String line;
            // initialise database
            Disk db = new Disk(diskCapacity, BLOCK_SIZE);
            // start loading data
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.readLine(); // skip the first line (the column line)

            // initialise a new B+ tree
            BplusTree tree = new BplusTree();

            while ((line = reader.readLine()) != null) {
                counter++;
                if (counter % 100000 == 0)
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
            reader.close();

            // TODO: to run the experiments independently of one another
            // Choose Experiment number
            try {

                int index = 0;
                while (true) {
                    try {
                        System.out.println("\nChoose Experiment (1-5):");
                        Scanner sc = new Scanner(System.in);
                        index = sc.nextInt();

                        if (index > 0 && index < 6) {
                            break;
                        } else {
                            System.out.println("\nPlease only input 1-5!");
                        }
                    } catch (Exception e) {
                        System.out.println("\nPlease only input 1-5!");
                    }
                }

                switch (index) {
                    case 1:
                        db.experimentOne();
                        break;
                    case 2:
                        BplusTree.experimentTwo(tree);
                        break;
                    case 3:
                        BplusTree.experimentThree(db, tree);
                        break;
                    case 4:
                        BplusTree.experimentFour(db, tree);
                        break;
                    case 5:
                        BplusTree.experimentFive(db, tree);
                        break;
                }

            } catch (Exception e) {

            }

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
