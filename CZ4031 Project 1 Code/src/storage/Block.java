package storage;

/**
 * Class stimulating a block within the disk
 * Each block stores data in the form of records
 */
public class Block {
    private int curRecords; // amount of records in the block currently
    private static int totalRecords; // the total number of records in a single block
    private Record[] recordsList; // all records stored in a block

    public Block(int BLOCK_SIZE) {
        this.curRecords = 0;
        this.totalRecords = BLOCK_SIZE / Record.getRecordSize(); // total number of records that can fit into a block
        this.recordsList = new Record[this.totalRecords];
    }

    /**
     * Accepts and offset within the block and returns the actual Record object with the block
     *
     * @param recordPos record position with the blocl
     * @return Record Object stored withib the block
     */
    public Record getRecordFromBlock(int recordPos) {
        return recordsList[recordPos];
    }

    /***
     * Returns the current number of records stored in the block
     * @return the current size of the block
     */
    public int getCurSize() {
        return curRecords;
    }


    /**
     * Returns the total number of records the block can store
     *
     * @return the total size of the block
     */
    public static int getTotalRecords() {
        return totalRecords;
    }

    /**
     * @return a boolean value on whether block can accept one more record
     */
    public boolean isBlockAvailable() {
        return curRecords < totalRecords;
    }

    /**
     * Accepts a record object and returns the offset in the block it is stored
     *
     * @param rec Record Object
     * @return the offset within the block if record can be stored in current block else -1 if there's no space
     */
    public int insertRecordIntoBlock(Record rec) {
        //insert into first available space
        for (int i = 0; i < recordsList.length; i++) {
            if (recordsList[i] == null) {
                recordsList[i] = rec;
                this.curRecords++;
                return i;
            }
        }
        // no space to insert record
        return -1;
    }


    /**
     * Accepts an offset and returns the Record object at the offset in the current block
     *
     * @param offset position where the record is stored in the block
     * @return Record Object
     */
    public Record getRecord(int offset) {
        return recordsList[offset];
    }

    // TODO: Implement this after merge with delete node

    /**
     * Accepts an offset within the block and deletes the Record object at the offset within the block
     *
     * @param offset offset of the record within the block
     */
    public void deleteRecord(int offset) {
        recordsList[offset] = null;
        curRecords--;
    }

}
