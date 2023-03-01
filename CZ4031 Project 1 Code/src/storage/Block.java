package storage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Class stimulating a block within the disk
 * Each block stores data in the form of records
 */
public class Block {
    private int curRecords;
    private static int totalRecords; // the total number of records in a single block
    private Record[] recordsList;

    public Block(int BLOCK_SIZE) {
        this.curRecords = 0;
        this.totalRecords = BLOCK_SIZE / Record.getRecordSize(); // total number of records that can fit into a block
        this.recordsList = new Record[this.totalRecords];
    }

    public static int getTotalRecords() {
        return totalRecords;
    }

    public boolean isBlockAvailable() {
        return curRecords < totalRecords;
    }

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

    // getRecord accepts an offset, and returns the physical_storage.Record at the offset in this block
    public Record getRecord(int offset) {
        return recordsList[offset];
    }

    // deleteRecord accepts an offset, deletes the physical_storage.Record at that offset in this block

    // TODO: Implement this after merge with delete node
    public void deleteRecord(int offset) {
        recordsList[offset] = null;
        curRecords--;
    }


}
