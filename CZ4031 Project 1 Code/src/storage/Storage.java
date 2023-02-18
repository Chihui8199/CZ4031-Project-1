package storage;
import storage.Block;
import java.util.ArrayList;

/**
 * Implementation of an unspanned db/storage class - each record must be stored as a whole in single block
 */
public class Storage {

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

    public Storage(int diskSize, int blkSize){
        this.memdiskSize = diskSize;
        this.blkSize = blkSize;
        this.numOfBlocksAvailable = diskSize / blkSize;
        blockList = new ArrayList<>();


    }

    public Address writeRecordToStorage(Record rec){
        // insert the Record into the first available block
        int blockPtr = getFirstAvailableBlockId();
        // insert the record with data attributes into the block chosen
        Address addressofRecordStored = this.insertRecordIntoBlock(blockPtr, rec);
        System.out.println(String.format("%s is stored at %s", rec.toString(),addressofRecordStored.toString() ));
        return addressofRecordStored;
    }

    private int getFirstAvailableBlockId(){
        int blockId = -1;
        if (numOfBlocksAvailable < 0){
            System.out.println("Memory is full! No blocks available to be assigned");
            System.exit(-1);
        }
        for(int i=0; i < blockList.size(); i++){
            if(blockList.get(i).isBlockAvailable()){
                blockId = i;
                break;
            }
        }
        System.out.println(String.format("First Available Block %d", blockId));
        return blockId;
    }

    /***
     * Writes a record to the current block, pointed to by blockPtr in the memory pool
     * @param blockPtr
     * @param rec
     */
    private Address insertRecordIntoBlock(int blockPtr, Record rec){
        Block block = null;
        if(blockPtr >= 0){
            block = blockList.get(blockPtr);
        } else {
            // else there's no assigned block then create a new block
            block = new Block(blkSize);
            blockList.add(block);
            numOfBlocksAvailable -= 1;
            numOfBlocksAssigned += 1;
            blockPtr = (blockList.size() > 0) ? (blockList.size() - 1) : -1;
        }
        // inserting the actual record into the block
        int offset = block.insertRecordIntoBlock(rec);
        return new Address(blockPtr, offset);
    }

    /**
     * Retrieves the Block from Disk and uses the offset to return the record stored there.
     * @param add The add representing the location on disk of the record to be retrieved.
     * @return The record stored at the corresponding add in the disk.
     */
    // public Record getRecord(Address add) {
    //     int blockId = add.getBlockId();
    //     Block block = blocks[blockId];
    //     return block.getRecord(add.getOffset());
    // }
    // public int getNumberBlockUsed(){
    //     return filledBlocks.size();
    // }

    public void printDatabaseInfo(){
        System.out.println(String.format("Total Memory Size: %f MB", (float) memdiskSize/Math.pow(10, 6)));
        System.out.println(String.format("Size of Each Block: %d B", 200));
        System.out.println(String.format("Size of Each Record %d B", RECORD_SIZE));
        System.out.println(String.format("Number of Blocks Allocated %d", numOfBlocksAssigned));
        System.out.println(String.format("Number of Blocks Remaining %d", numOfBlocksAvailable));
        System.out.println();
    }
}
