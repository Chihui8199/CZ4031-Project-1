package storage;

/**
 * Class representing the logical address of a record
 * It contains the ID of the block where the record is stored,
 * and the relative offset of the record within that block.
 */
public class Address {
    int blockID; //Id of block
    int offset; // relative offset of record stored within the block

    public Address(int blockID, int offset) {
        this.blockID = blockID;
        this.offset = offset;
    }

    /**
     * Returns the ID of the block where the record is stored.
     *
     * @return the ID of the block.
     */
    public int getBlockId() {
        return blockID;
    }

    /**
     * Returns the relative offset of the record within the block.
     *
     * @return the offset of the record.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns a string representation of the Address object.
     *
     * @return a string containing the block ID and the offset of the record.
     */
    @Override
    public String toString() {
        return String.format("blk %d offset %d", blockID, offset);
    }

}