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
    private int totalRecords; // the total number of records in a single block
    private Record[] recordsList;
    //TODO: maybe move block size to some kind of constants file?
    public Block( int BLOCK_SIZE){
        this.curRecords = 0;
        this.totalRecords = BLOCK_SIZE /Record.getRecordSize(); // total number of records that can fit into a block
        this.recordsList = new Record[this.totalRecords];
    }

    public boolean isBlockAvailable(){
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
}
