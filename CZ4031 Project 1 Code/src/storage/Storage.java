package storage;
import index.testBplusTree;
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
        numOfRecords++;
        int blockPtr = getFirstAvailableBlockId();
        Address addressofRecordStored = this.insertRecordIntoBlock(blockPtr, rec);
        return addressofRecordStored;
    }

    public int getNumberOfRecords(){
        return numOfRecords;
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

    public int getBlockAccesses(){
        return blockAccesses;
    }
    private Block getBlock(int blockNumber) {
        Block block = null;
        if (blockNumber >= 0) {
            // 1 I/O
            block = blocks[blockNumber];
            blockAccesses++;
        }
        return block;
    }

    public Record getRecord(Address add) {
        Block block = getBlock(add.getBlockId());
        return block.getRecord(add.getOffset());
    }

    public void experimentOne(){
        System.out.println("\n----------------------EXPERIMENT 1-----------------------");
        System.out.printf("Total Number of Records Stored: %d\n", this.getNumberOfRecords());
        System.out.println(String.format("Size of Each Record: %d Bytes", Record.getRecordSize()));
        System.out.printf("Number of Records Stored in a Block: %d\n", Block.getTotalRecords());
        System.out.println(String.format("Number of Blocks Allocated: %d\n", this.getNumberBlockUsed()));
    }
}
