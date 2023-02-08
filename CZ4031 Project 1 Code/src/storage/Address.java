package storage;

/**
 * Class representing the logical address of a record
 */
public class Address{
    //Id of block
    int blockID;
    // relative offset of record stored within the block
    int offset;

    public Address(int blockID, int offset){
        this.blockID = blockID;
        this.offset = offset;
    }

    public int getBlockId(){
        return blockID;
    }

    public void setBlockId(int blockID) {
        this.blockID = blockID;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    @Override
    public String toString(){
        return String.format("blk %d offset %d", blockID, offset);
    }
}