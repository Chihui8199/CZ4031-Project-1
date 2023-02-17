package storage;
import storage.Block;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of an unspanned db/storage class - each record must be stored as a whole in single block
 */
public class Storage {

    private Block[] blocks;
    private Set<Integer> availableBlocks;
    private static int blockAccesses = 0;
    private Set<Integer> filledBlocks;

    // max size of disk
    int memdiskSize;
    // size of block
    int blkSize;
    //max amount of block in disk
    int numOfBlocksAvailable;
    // number of blocks assigned
    int numOfBlocksAssigned = 0;
    // blocks in a disk implemented with arraylist
    private ArrayList<Block> blockList;
    // size of each record
    private static final int RECORD_SIZE = (Float.SIZE / 8) + (Integer.SIZE / 8) + 9;
    private int numOfRecords = 0;

    public Storage(int diskSize, int blkSize){
        this.memdiskSize = diskSize;
        this.blkSize = blkSize;
        this.blocks = new Block[diskSize / blkSize];
        this.availableBlocks = new HashSet<>();
        this.filledBlocks = new HashSet<>();
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Block(blkSize);
            availableBlocks.add(i);
        }

    }

    public Address writeRecordToStorage(Record rec){
        int blockPtr = getFirstAvailableBlockId();
        Address addressofRecordStored = this.insertRecordIntoBlock(blockPtr, rec);
        return addressofRecordStored;
    }



    private int getFirstAvailableBlockId(){
        if (availableBlocks.isEmpty()) {
            return -1;
        }
        return availableBlocks.iterator().next();
    }

    /***
     * Writes a record to the current block, pointed to by blockPtr in the memory pool
     * @param blockPtr
     * @param rec
     */
    private Address insertRecordIntoBlock(int blockPtr, Record rec){
        if (blockPtr == -1) {
            return null;
        }
        int offset = blocks[blockPtr].insertRecordIntoBlock(rec);
        filledBlocks.add(blockPtr);
        if (!blocks[blockPtr].isBlockAvailable()) {
            availableBlocks.remove(blockPtr);
        }
        return new Address(blockPtr, offset);
    }

    public int getNumberBlockUsed(){
        return filledBlocks.size();
    }


    public void printDatabaseInfo(){
        System.out.println(String.format("Total Memory Size: %f MB", (float) memdiskSize/Math.pow(10, 6)));
        System.out.println(String.format("Size of Each Block: %d B", 200));
        System.out.println(String.format("Size of Each Record %d B", Record.getRecordSize()));
        System.out.println(String.format("Number of Blocks Allocated %d", numOfBlocksAssigned));
        System.out.println(String.format("Number of Blocks Remaining %d", numOfBlocksAvailable));
        System.out.printf("Total Number of Records Stored");
        System.out.println();
    }
}
