package index;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import storage.Address;
import java.lang.Math;

/*
 * Class representing a node in a B+ tree
 */
public class Node {

    private int nodeSize;
    private int minLeafNodeSize;
    private int minNonLeafNodeSize;
    static final int NODE_SIZE = BplusTree.NODE_SIZE;
    private boolean isLeaf;
    private boolean isRoot;
    private NonLeafNode parent;
    protected ArrayList<Integer> keys;
    Node rootNode;

    /**
     * Constructs a Node object with the rootNode,
     *
     * @param rootNode           The rootNode of the B+ tree.
     * @param isLeaf             Boolean to check whether node is a leaf node.
     * @param isRoot             Boolean to check whether node is a root node.
     * @param nodeSize           The node size of the B+ tree.
     * @param minLeafNodeSize    The minimum size of a leaf node.
     * @param minNonLeafNodeSize The minimum size of a non leaf node.
     */
    public Node() {
        this.rootNode = BplusTree.getRoot();
        this.isLeaf = false;
        this.isRoot = false;
        this.nodeSize = NODE_SIZE;
        this.minLeafNodeSize = (int) (Math.floor((nodeSize + 1) / 2));
        this.minNonLeafNodeSize = (int) (Math.floor(nodeSize / 2));
    }

    /**
     * Returns minimum Leaf Node Size of current node.
     *
     * @return minimum Leaf Node Size of current node.
     */
    public int getminLeafNodeSize() {
        return this.minLeafNodeSize;
    }

    /**
     * Returns minimum Non Leaf Node Size of current node.
     *
     * @return minimum Non Leaf Node Size of current node.
     */
    public int getMinNonLeafNodeSize() {
        return this.minNonLeafNodeSize;
    }

    /**
     * Returns true/false boolean to check if node is a Leaf Node.
     *
     * @return boolean value of isLeaf.
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Returns true/false boolean.
     *
     * @return opposite boolean value of isLeaf.
     */
    public boolean isNonLeaf() {
        return !isLeaf;
    }

    /**
     * Update whether current node is a leaf node from boolean argument isALeaf.
     *
     * @param isALeaf set boolean value of node to isALeaf.
     */
    public void setIsLeaf(boolean isALeaf) {
        isLeaf = isALeaf;
    }

    /**
     * Returns true/false boolean to check if node is a Root Node.
     *
     * @return boolean value of isRoot.
     */
    public boolean isRoot() {
        return isRoot;
    }

    /**
     * Update whether current node is a root node from boolean argument isARoot.
     *
     * @param isARoot set boolean value of isRoot
     */
    public void setIsRoot(boolean isARoot) {
        isRoot = isARoot;
    }

    /**
     * Returns the parent of the current node.
     *
     * @return the parent of the current node.
     */
    public NonLeafNode getParent() {
        return this.parent;
    }

    /**
     * Update current node's parent with a new parent.
     * If the current node was a root, have to make sure the new parent is set as
     * root and also have to setIsRoot to false for the current node.
     *
     * @param setParent the Non-Leaf Node the function will use to set current
     *                  node's parent.
     */
    public void setParent(NonLeafNode setParent) {
        if (this.isRoot()) {
            this.setIsRoot(false);
            setParent.setIsRoot(true);
            setParent.setIsLeaf(false);
            BplusTree.setRoot(setParent);
        } else {
            setParent.setIsLeaf(false);
        }
        this.parent = setParent;
    }

    /**
     * Removes the last key in the current node's ArrayList<Integer> keys.
     *
     */
    public void removeKeyAtLast() {
        this.keys.remove(keys.size() - 1);
    }

    /**
     * Replaces the key at the index given in in ArrayList<Integer> keys, with the
     * given key
     *
     * @param index the index to replace the key at in ArrayList<Integer> keys.
     * @param key   the key to replace the key at the index given in in
     *              ArrayList<Integer> keys.
     */
    void replaceKeyAt(int index, int key) {

        keys.set(index, key);
    }

    /**
     * Returns all keys of current node as an ArrayList<Integer>.
     *
     * @return all keys of current node.
     */
    public ArrayList<Integer> getKeys() {
        return this.keys;
    }

    /**
     * Prints the keys in ArrayList<Integer> keys.
     * 
     */
    public void printNode() {
        Set<Integer> keys = ((LeafNode) this).map.keySet();
        System.out.println(keys);
    }
    
    /**
     * Returns curent key of the current node at the index.
     *
     * @param index the index used to find the key in the current node.
     * @return current key at index of current node.
     */
    public int getKey(int index) {
        return this.keys.get(index);
    }

    /**
     * Returns the total number of keys in the currrent node.
     *
     * @return current node's keysize.
     */
    public int getKeySize() {
        return keys.size();
    }

    /**
     * Returns the last key of the current node.
     *
     * @return current node's last key.
     */
    public int getLastKey() {
        return this.keys.get(keys.size() - 1);
    }

    /**
     * Returns the first key of the current node.
     *
     * @return current node's first key.
     */
    public int getFirstKey() {
        return this.keys.get(0);
    }

    /**
     * Returns integer value of the key that was removed.
     *
     * @param index the index to remove the key in ArrayList<Integer> keys.
     * @return the integer value of the key that was removed.
     */
    int removeKeyAt(int index) {
        return keys.remove(index);
    }

    /**
     * Binary search stored keys. (wrapper of the recursive function).
     *
     * @param key        key to search.
     * @param upperBound if set true, search for upperBound.
     *                   if set false, search for exactKey.
     * @return if key exists & upperBound false, the index of the key.
     *         else, the index of upper bound of the key.
     */
    int searchKey(int key, boolean upperBound) {
        int keyCount = keys.size();
        return searchKey(0, keyCount - 1, key, upperBound);
    }

    /**
     * Binary search stored keys.
     *
     * @param left the leftmost index.
     * @param right the rightmost index.
     * @param key the value the function is searching for.
     * @param upperbound if set true, search for upperBound.
     *                   if set false, search for exactKey.
     * @return if middle key is lesser than key, recursively enter searchKey and update leftmost node with middle + 1.
     *         if middle key is more than key, recursively enter searchKey and update rightmost node with middle -1.
     *         else, returns the middle value after getting the exact key
     */
    private int searchKey(int left, int right, int key, boolean upperBound) {
        if (left > right)
            return left;

        int middle = (left + right) / 2;
        int middleKey = getKeyAt(middle);

        if (middleKey < key) {
            return searchKey(middle + 1, right, key, upperBound);
        } else if (middleKey > key) {
            return searchKey(left, middle - 1, key, upperBound);
        } else {
            while (middle < keys.size() && keys.get(middle) == key)
                middle++;
            if (!upperBound)
                return middle - 1;
            return middle;
        }
    }

    /**
     * Returns integer value of the key at the given index.
     *
     * @param index the index to get the key in ArrayList<Integer> keys.
     * @return the integer value of the key at the given index.
     */
    int getKeyAt(int index) {
        return keys.get(index);
    }

    /**
     * Returns integer value of the last index in ArrayList<Integer> keys.
     *
     * @return integer value of the last index in ArrayList<Integer> keys.
     */
    public int getLastIdx() {
        int lastIdx = keys.size() - 1;
        return lastIdx;
    }

    /**
     * Inserts the given key at the given index.
     *
     * @param index the index to insert the given key in ArrayList<Integer> keys.
     * @param key the key to be inserted into ArrayList<Integer> keys at the given index.
     */
    void insertKeyAt(int index, int key) {
        keys.add(index, key);
    }

    /**
     * Check if there a need to re-balance the tree.
     *
     * @param maxKeyCount
     * @return
     */
    boolean isUnderUtilized(int maxKeyCount) {
        if (isRoot()) { // root
            return (this.getKeySize() < 1);
        } else if (isLeaf()) { // leaf
            return (this.getKeySize() < (maxKeyCount + 1) / 2);
        } else { // non-leaf
            return (this.getKeySize() < maxKeyCount / 2);
        }
    }

     /**
     * Inserts the given key into the given ArrayList<Integer> keys in correct ascending order.
     *
     * @param keys the ArrayList<Integer> keys where the given key is inserted into.
     * @param key the key to be inserted into the given ArrayList<Integer> keys in correct ascending order.
     */
    public static void insertInOrder(ArrayList<Integer> keys, int key) {
        int i = 0;

        while (i < keys.size() && keys.get(i) < key) {
            i++;
        }
        keys.add(i, key);
    }

    
     /**
     * Inserts the given child node into the given NonLeafNode parent in correct ascending order.
     *
     * @param parent the parent where the given child is inserted into.
     * @param child the child to be inserted into the given parent in correct ascending order.
     */
    public void insertChildInOrder(NonLeafNode parent, NonLeafNode child) {
        int i = 0;
        int childToSort = child.getKeyAt(0);
        while (i < parent.getKeySize() && parent.getKeyAt(i) < childToSort) {
            i++;
        }
        parent.children.add(i + 1, child);
    }


    /** 
     * Updates the key at the given keyIndex with newKey.
     * 
     * @param keyIndex the index in ArrayList<Integer> keys.
     * @param newKey the new key to be updated at keyIndex in ArrayList<Integer> keys.
     */
    public void updateOneKeyOnly(int keyIndex, int newKey) {
        if (keyIndex >= 0 && keyIndex < keys.size()) {
            keys.set(keyIndex, newKey);
        }
    }

    
    /** 
     * Update key of entire tree recursively after deletion.
     *
     * @param keyIndex the index of the key that is removed.
     * @param newKey the new key that is being updated into the tree.
     * @param leafNotUpdated checking if the leaf node is already updated. 
     * @param lowerbound check for lowerbound of current tree degree.
     */
    public void updateKey(int keyIndex, int newKey, boolean leafNotUpdated, int lowerbound) {
        // run only once to make leaf updated
        if (keyIndex >= 0 && keyIndex < keys.size() && !leafNotUpdated) {
            keys.set(keyIndex, newKey);
        }
        if (parent != null && parent.isNonLeaf()) {
            int childIndex = parent.getChildren().indexOf(this);

            if (childIndex >= 0) {
                if (childIndex > 0) {
                    parent.replaceKeyAt(childIndex - 1, keys.get(0));

                }
                parent.updateKey(childIndex - 1, newKey, false, lowerbound);
            }
        } else if (parent != null && parent.isLeaf()) {

            parent.updateKey(keyIndex, newKey, false, lowerbound);
        }

    }

    
    /** 
     * Returns the boolean indicating whether the node is able to give one key.
     * 
     * @param maxKeyCount the size of the node.
     * @return boolean indicating whether the node is able to give one key.
     */
    public boolean isAbleToGiveOneKey(int maxKeyCount) {
        if (isNonLeaf())
            return getKeySize() - 1 >= maxKeyCount / 2;
        return getKeySize() - 1 >= (maxKeyCount + 1) / 2;

    }

    
    /** 
     * Inserts new node to parent node in ascending order based on key values.
     * 
     * @param newNode is the leaf node to be inserted to the parent node.
     */
    public void insertNewNodeToParent(LeafNode newNode) {
        int index = 0;
        boolean insertedNode = false;

        try {
            for (Node currentNode : this.getParent().getChildren()) {

                // if there is a node > than newNode, insert inbetween that node
                if (newNode.getKey(newNode.getKeySize() - 1) < currentNode.getKey(0)) {
                    this.getParent().getChildren().add(index, newNode);
                    this.getParent().keys.add(index - 1, newNode.getKey(0));
                    insertedNode = true;
                    break;
                }
                index++;
            }

            if (insertedNode == false) {
                this.getParent().getChildren().add(newNode);
                this.getParent().keys.add(newNode.getKey(0));
            }

        } catch (Exception e) {
            this.getParent().getChildren().add(newNode);
            this.getParent().keys.add(newNode.getKey(0));
        }

        newNode.setParent(this.getParent());

        if (this.getParent().getKeySize() > NODE_SIZE) {
            this.getParent().splitNonLeafNode();
        }

    }

    
    /** 
     * Creates the first parent node in the B+ tree.
     * 
     * @param newNode is the leaf node which the parent node is added to.
     */
    public void createFirstParentNode(LeafNode newNode) {
        NonLeafNode newParent = new NonLeafNode();
        PerformanceRecorder.addOneNode();
        newParent.keys = new ArrayList<Integer>();
        newParent.addChild(this);
        newParent.addChild(newNode);
        newParent.keys.add(newNode.getKey(0));
        this.setParent(newParent);
        newNode.setParent(newParent);
    }

    
    /** 
     * Creates the a root node.
     * 
     * @param newNode is the non leaf node which the root node is added to.
     */
    public void createRootNode(NonLeafNode newNode) {
        NonLeafNode newParent = new NonLeafNode();
        PerformanceRecorder.addOneNode();
        newParent.keys = new ArrayList<Integer>();
        newParent.addChild(this);
        newParent.addChild(newNode);
        newParent.keys.add(newNode.getKey(0));
        this.setParent(newParent);
        newNode.setParent(newParent);

    }

    
    /** 
     * Called when leaf node is full. Split current leaf node into 2 and returns the new node.
     *
     * @param key the newly inserted key.
     * @param addr the address of the key.
     * @return a new node that is created from the splited leaf node.
     */
    public LeafNode leafSplitAndDistribute(int key, Address addr) {
        LeafNode newNode = new LeafNode();
        PerformanceRecorder.addOneNode();
        ((LeafNode) this).records = new ArrayList<Address>();
        ((LeafNode) this).records.add(addr);
        ((LeafNode) this).map.put(key, ((LeafNode) this).records);

        // Removing whats after the nth index into the new node
        int n = NODE_SIZE - minLeafNodeSize + 1;
        int i = 0;
        int fromKey = 0;

        // finding the nth index
        for (Map.Entry<Integer, ArrayList<Address>> entry : ((LeafNode) this).map.entrySet()) {
            if (i == n) {
                fromKey = entry.getKey();
                break;
            }
            i++;
        }

        SortedMap<Integer, ArrayList<Address>> lastnKeys = ((LeafNode) this).map.subMap(fromKey, true,
                ((LeafNode) this).map.lastKey(), true);

        newNode.map = new TreeMap<Integer, ArrayList<Address>>(lastnKeys);

        lastnKeys.clear();

        insertInOrder(this.keys, key);

        // adding keys after the nth index into the newNode's arraylist of keys
        newNode.keys = new ArrayList<Integer>(this.keys.subList(n, this.keys.size()));// after nth index

        // removing keys after the nth index for old node's arraylist of keys
        this.keys.subList(n, this.keys.size()).clear();

        if (((LeafNode) this).getNext() != null) {
            newNode.setNext(((LeafNode) this).getNext());
            ((LeafNode) this).getNext().setPrevious(newNode);
        }
        ((LeafNode) this).setNext(newNode);
        newNode.setPrevious(((LeafNode) this));
        return newNode;
    }

    
    /** 
     * Called when non-leaf node is full. Split current non-leaf node into 2 and returns the new parent node.
     *
     * @return the new Parent node that is now connected to the 2 new non-leaf node.
     */
    public NonLeafNode nonLeafSplitAndDistribute() {

        NonLeafNode currentParent = (NonLeafNode) (this);
        NonLeafNode newParent = new NonLeafNode();
        PerformanceRecorder.addOneNode();
        newParent.keys = new ArrayList<Integer>();

        int keyToSplitAt = currentParent.getKeyAt(minNonLeafNodeSize);
        for (int k = currentParent.getKeySize(); k > 0; k--) {
            if (currentParent.getKeyAt(k - 1) < keyToSplitAt) {
                break; // We've reached the end of the keys to move
            }
            int currentKey = currentParent.getKeyAt(k - 1);
            Node currentChild = currentParent.getChild(k);

            // Add node and keys to new parent
            newParent.children.add(0, currentChild);
            newParent.keys.add(0, currentKey);
            currentChild.setParent(newParent);

            // Remove node and keys from old parent
            currentParent.removeChild(currentParent.getChild(k));
            currentParent.keys.remove(k - 1);

        }

        return newParent;
    }

    
    /** 
     * If the current node has a parent, adds the new node returned from leafSplitAndDistribute to the current node's parents. If the parent node size is greater than the node size, call splitNonLeafNode. If the current node does not have a parent, call createFirstParentNode.
     * 
     * @param key the key to be added to current leaf node
     * @param addr the address to be added to current leaf node
     */
    public void splitLeafNode(int key, Address addr) {

        LeafNode newNode = this.leafSplitAndDistribute(key, addr);

        // If the leaf node has parent, add the new node to parent
        if (this.getParent() != null) {
            /** Insert new node to parent */
            this.insertNewNodeToParent(newNode);

            if (this.getParent().getKeySize() > NODE_SIZE) {
                this.getParent().splitNonLeafNode();
            }
        }
        // First leaf node when its full, create a new root node for it, which is also
        // the first parent node created
        else {
            this.createFirstParentNode(newNode);
        }

    }

    /** 
     * * If the current node has a parent, adds the new node returned from nonLeafSplitAndDistribute to the current node's parents. If the parent node size is greater than the node size, call splitNonLeafNode. If the current node does not have a parent, call createFirstParentNode.
     * 
     */
    public void splitNonLeafNode() {
        NonLeafNode newParent = this.nonLeafSplitAndDistribute();

        if (this.getParent() != null) {

            insertChildInOrder(this.getParent(), newParent);

            newParent.setParent(this.getParent());

            // Remove the first key from the new parent and add it to new2Parent
            insertInOrder(this.getParent().keys, newParent.getKeyAt(0));

            newParent.keys.remove(0);

            if (this.getParent().getKeySize() > NODE_SIZE) {
                this.getParent().splitNonLeafNode();
            }

        } else {
            // it is the root
            NonLeafNode newRoot = new NonLeafNode();
            PerformanceRecorder.addOneNode();
            newRoot.keys = new ArrayList<Integer>();
            newRoot.keys.add(newParent.getKeyAt(0));

            newParent.keys.remove(0);

            newRoot.addChild(this);
            newRoot.addChild(newParent);

            this.setParent(newRoot);
            newParent.setParent(newRoot);

            BplusTree.setRoot(newRoot);
        }
    }

}