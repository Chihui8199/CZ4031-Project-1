package storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of an unspanned and unclustered disk class -
 * each record must be stored as a whole in single block
 */
public class Disk {
    //an array of Block objects representing the memory pool of the disk.
    private Block[] blocks;
    //a set of integers representing the indices of the available blocks in the memory pool.
    private Set<Integer> availableBlocks;
    //an integer representing the number of block accesses made by the disk.
    private static int blockAccesses = 0;
    //a set of integers representing the indices of the filled blocks in the memory pool.
    private Set<Integer> filledBlocks;
    int memdiskSize; //an integer representing the size of the disk in bytes.
    int blkSize; // an integer representing the size of each block in bytes.
    private int numOfRecords = 0; // an integer representing the number of records stored in the disk.

    // an integer representing the number of block accesses that were reduced due to the presence
    // of the LRU cache.
    private int blockAccessReduced = 0;
    //an object of the LRUCache class, which is used to cache recently accessed blocks.
    private LRUCache lruCache;

    public Disk(int diskSize, int blkSize) {
        this.memdiskSize = diskSize;
        this.blkSize = blkSize;
        this.blocks = new Block[diskSize / blkSize];
        this.availableBlocks = new HashSet<>();
        this.filledBlocks = new HashSet<>();
        // initialise all available blocks in hashMap
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Block(blkSize);
            availableBlocks.add(i);
        }
        int lruCacheSize = (int) (256.0 / 500000.0 * diskSize / blkSize);
        this.lruCache = new LRUCache(lruCacheSize);
    }

    /**
     * Writes the given record to storage by inserting it into the first available block
     * and returns the address of the newly stored record.
     *
     * @param rec the record to be stored
     * @return the {@link Address} of the newly stored record
     */
    public Address writeRecordToStorage(Record rec) {
        numOfRecords++;
        int blockPtr = getFirstAvailableBlockId();
        Address addressofRecordStored = this.insertRecordIntoBlock(blockPtr, rec);
        return addressofRecordStored;
    }

    /**
     * Returns the total number of records stored in the storage system.
     *
     * @return the number of records stored in the system
     */
    public int getNumberOfRecords() {
        return numOfRecords;
    }

    /**
     * Searches through all the blocks on the disk to locate the first block that
     * is currently available for storing additional records.
     * The method then returns the block number of this available block.
     *
     * @return blockNumber of the first available block on Disk.
     */
    private int getFirstAvailableBlockId() {
        if (availableBlocks.isEmpty()) {
            return -1;
        }
        return availableBlocks.iterator().next();
    }

    /***
     * Writes a record to the current block, pointed to by blockPrt
     * @param blockPtr position to write to within the block
     * @param rec record to write to
     */
    private Address insertRecordIntoBlock(int blockPtr, Record rec) {
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

    public int getNumberBlockUsed() {
        return filledBlocks.size();
    }

    public int getBlockAccesses() {
        return blockAccesses;
    }


    /**
     * Retrieves a block from Disk, either from the LRU cache or directly from Disk, given its block number.
     * If the block is on cache, return that block.
     * Else, load the block onto the cache from the Disk.
     *
     * @param blockNumber The block number to be retrieved.
     * @return The requested Block object.
     */
    private Block getBlock(int blockNumber) {
        Block block = lruCache.get(blockNumber);
        if (block != null) {
            blockAccessReduced++;
        }
        if (block == null && blockNumber >= 0) {
            // 1 I/O
            block = blocks[blockNumber];
            blockAccesses++;
            lruCache.put(blockNumber, block);
        }
        return block;
    }

    /**
     * Retrieves the number of block accesses reducted due to cache hits
     *
     * @return number of block accessed reduced
     */
    public int getBlockAccessReduced() {
        return blockAccessReduced;
    }


    /**
     * Retrieves the record stored at the specified address.
     *
     * @param add The address of the record to retrieve.
     * @return The record stored at the specified address.
     */
    public Record getRecord(Address add) {
        Block block = getBlock(add.getBlockId());
        return block.getRecord(add.getOffset());
    }

    /**
     * Wrapper function for runningBruteForceSearch
     * Searches for blocks that contain records with a number of votes within a given range using brute force search.
     *
     * @param numVotesValue           the lower limit of the vote count range to search for
     * @param numVotesValueUpperRange the upper limit of the vote count range to search for
     * @return the total number of blocks accessed during the search
     */
    public int getBlocksAccessedByForce(int numVotesValue, int numVotesValueUpperRange) {
        return runBruteForceSearch(numVotesValue, numVotesValueUpperRange);
    }

    /**
     * Runs a brute force search to find records within a given vote count range in all filled blocks.
     * Iterates through all blocks and all records
     *
     * @param numVotesValue           the lower limit of the vote count range to search for
     * @param numVotesValueUpperRange the upper limit of the vote count range to search for
     * @return the total number of blocks accessed during the search
     */
    public int runBruteForceSearch(int numVotesValue, int numVotesValueUpperRange) {
        Record rec;
        int curNumVotes;
        int countBlockAccess = 0;
        ArrayList<Record> finalRes = new ArrayList<>();
        for (int blockPtr : filledBlocks) {
            countBlockAccess++;
            Block block = blocks[blockPtr];
            int numberOfRecordsInBlock = block.getCurSize();
            for (int i = 0; i < numberOfRecordsInBlock; i++) {
                // retrieve the record
                rec = block.getRecordFromBlock(i);
                curNumVotes = rec.getNumVotes();
                if (numVotesValue <= curNumVotes && curNumVotes <= numVotesValueUpperRange) {
                    finalRes.add(rec);
                }
            }
        }
        for (Record record : finalRes)
            System.out.printf("Found Records %s\n", record);
        return countBlockAccess;
    }


    public void experimentOne() {
        System.out.println("\n----------------------EXPERIMENT 1-----------------------");
        System.out.printf("Total Number of Records Stored: %d\n", this.getNumberOfRecords());
        System.out.println(String.format("Size of Each Record: %d Bytes", Record.getRecordSize()));
        System.out.printf("Number of Records Stored in a Block: %d\n", Block.getTotalRecords());
        System.out.println(String.format("Number of Blocks Allocated: %d\n", this.getNumberBlockUsed()));
    }
}
