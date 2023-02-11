package b_plus_tree;

import java.util.ArrayList;

abstract class Node {
    final ArrayList<Integer> keys;    // key array, declare final to prevent
    // rebind to point to a different collection instance:
    private NodeType nodeType;        // actual node type
    private int blockIndex;           // node page index (assume each node is store in one page in memory)
    private int keyCount;            // currently stored key number


    /**
     * Constructor which takes into the node type as well as the page index
     * No modifiers to specify package accessibility
     *
     * @param nodeType the actual node type
     */
    Node(NodeType nodeType) {
        this.nodeType = nodeType;
        this.keyCount = 0;
        this.keys = new ArrayList<>();
    }

    void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    NodeType getNodeType(){
        return this.nodeType;
    }

    int getBlockIndex() {
        return blockIndex;
    }

    void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    int getKeyCount() {
        return keyCount;
    }


    // ------ ArrayList operations
    void replaceKeyAt(int index, int key) {
        keys.set(index, key);
    }


    int getKeyAt(int index) {
        return keys.get(index);
    }

    /**
     * Insert the key at the specified index in this list. Shift subsequent keys backwards
     *
     * @param index index of where the key should be inserted
     */
    void insertKeyAt(int index, int key) {
        keys.add(index, key);
        keyCount++;
    }

    void insertKeyAtLast(int key){
        insertKeyAt(keys.size(), key);
    }

    /**
     * Removes the key at the specified index in this list. Shift subsequent keys forwards
     *
     * @param index index of the key to be removed
     * @return removed key
     */
    int removeKeyAt(int index) {
        keyCount--;
        return keys.remove(index);
    }

    int removeKeyAtLast(){
        int lstIndex = keys.size()-1;
        return removeKeyAt(lstIndex);
    }

    //TODO:remove keys need to consider size of list

    // ------ Below are functions to tell the state of nodes
    boolean isRoot() {
        return (nodeType == NodeType.ROOT_INTERNAL || nodeType == NodeType.ROOT_LEAF);
    }

    boolean isInternal() {
        return (nodeType == NodeType.INTERNAL || nodeType == NodeType.ROOT_INTERNAL);
    }

    boolean isLeaf() {
        return (nodeType == NodeType.LEAF || nodeType == NodeType.ROOT_LEAF);
    }

    /**
     * Check if the node is full
     *
     * @param maxKeyCount set when BPTree was created.
     * @return true is the node is full, false if it's not.
     */
    boolean needSplit(int maxKeyCount) {
        return (keyCount-1) >= maxKeyCount;
    }

    boolean canGiveOneKey(int maxKeyCount){
        if (isInternal())
            return keyCount - 1 >= maxKeyCount / 2;
        return keyCount - 1 >= (maxKeyCount + 1) / 2;

    }

    /**
     * Check if the node is under min storage and needs to be merged
     *
     * @param maxKeyCount set when BPTree was created.
     * @return true if the node contains fewer keys than min storage
     * requirements, false if it's not
     */
    boolean isUnderUtilized(int maxKeyCount) {
        // for roots (internal or leaf) return true only when empty
        if (isRoot()) {
            return (keyCount < 1);
        } else if (isLeaf()) {
            return (keyCount < (maxKeyCount + 1) / 2);
        } else {// internal
            return (keyCount < maxKeyCount / 2);
        }
    }

    /**
     * Binary search stored keys. (wrapper of the recursive function)
     *
     * @param key        key to search
     * @param upperBound if set true, search for upperBound
     *                   if set false, search for exactKey
     * @return if key exists & upperBound false, the index of the key
     * else, the index of upper bound of the key.
     */
    int searchKey(int key, boolean upperBound) {
        return searchKey(0, keyCount-1, key, upperBound);
    }

    private int searchKey(int left, int right, int key, boolean upperBound) {
        if (left > right)
            return left;

        int middle = (left + right) / 2;
        int middleKey = getKeyAt(middle);

        if (middleKey < key) {
            return searchKey(middle + 1, right, key, upperBound);
        } else if (middleKey > key) {
            return searchKey(left, middle - 1, key, upperBound);
        } else { // this is equal
            while (middle < keyCount && keys.get(middle) == key)
                middle++;
            if (!upperBound)
                return middle-1;
            return middle;
        }
    }

    @Override
    public String toString(){
        return this.getNodeType() + ": block_index=" + this.getBlockIndex() +
                "\nkey_count=" + this.getKeyCount() + "\t";
    }
}
