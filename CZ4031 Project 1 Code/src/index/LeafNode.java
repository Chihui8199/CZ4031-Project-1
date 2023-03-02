package index;

import java.util.ArrayList;
import java.util.TreeMap;
import storage.Address;

/*
 * Class representing a Leaf Node in a B+ tree
 */
public class LeafNode extends Node {

    protected TreeMap<Integer, ArrayList<Address>> map;
    protected ArrayList<Address> records;
    private LeafNode nextNode;
    private LeafNode prevNode;

    public LeafNode() {
        super();
        setIsLeaf(true);
        setNext(null);
        setPrevious(null);
    }

    /**
     * Finding record of key.
     * 
     * @param key the key of the records.
     * @return ArrayList<Address> the list of current exisitng records.
     */
    public ArrayList<Address> findRecord(int key) {
        if (this.map.containsKey(key) || this.keys.contains(key)) {

            // Get the existing list of records associated with the key
            ArrayList<Address> existingRecords = map.get(key);
            return existingRecords;
        }
        return null;
    }

    /**
     * Get Addresses for key.
     *
     * @param key the key of the addresses.
     * @return ArrayList<Address> the addresses.
     */
    public ArrayList<Address> getAddressesForKey(int key) {
        return map.get(key);
    }

    /**
     * Adding the given key and address object into the TreeMap and ArrayList of
     * keys
     * 
     * @param key the key to be added into the TreeMap and ArrayList of keys
     * @param add the address object to be added into the TreeMap
     */
    public void addRecord(int key, Address add) {
        int n = NODE_SIZE;

        // if node is empty, create new Arraylist and TreeMap
        if (this.keys == null) {

            this.records = new ArrayList<Address>();
            this.records.add(add);

            this.map = new TreeMap<Integer, ArrayList<Address>>();
            this.map.put(key, records);

            this.keys = new ArrayList<Integer>();
            insertInOrder(this.keys, key);
            return;
        }

        else if (this.map.containsKey(key) || this.keys.contains(key)) {

            // Get the existing list of records associated with the key
            ArrayList<Address> existingRecords = map.get(key);

            // Append the new record to the existing list of records
            existingRecords.add(add);

            // Put the updated list of records back into the map
            map.put(key, existingRecords);

        }

        // else if keysize not full, insert the key into the ArrayList in sorted order
        else if (this.keys.size() < n) {
            this.records = new ArrayList<Address>();
            this.records.add(add);

            this.map.put(key, records);

            insertInOrder(this.keys, key);
        }

        // else, the arraylist and treemap is full, split the node
        else {
            this.splitLeafNode(key, add);
        }

    }

    /**
     * Find current node by key
     * 
     * @param key      the key within the node
     * @param rootNode the starting node
     * @return return found node with key
     */
    public Node findNodeByKey(int key, Node rootNode) {
        if (rootNode == null) {
            return null;
        }
        for (Node child : ((NonLeafNode) rootNode).getChildren()) {
            Node foundNode = findNodeByKey(key, child);
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }

    /**
     * Inserts the given key into the given ArrayList<Integer> keys in correct
     * ascending order.
     *
     * @param keys the ArrayList<Integer> keys where the given key is inserted into.
     * @param key  the key to be inserted into the given ArrayList<Integer> keys in
     *             correct ascending order.
     */
    public static void insertInOrder(ArrayList<Integer> keys, int key) {
        int i = 0;

        while (i < keys.size() && keys.get(i) < key) {
            i++;
        }
        keys.add(i, key);
    }

    /**
     * Sets nextNode with argument sibling
     * 
     * @param sibling the leaf node that is set as the nextNode for the current node
     */
    // Set sibling node as nextNode
    public void setNext(LeafNode sibling) {
        nextNode = sibling;
    }

    /**
     * Returns nextNode
     * 
     * @return nextNode, the leaf node that is on the right of the current node
     */
    public LeafNode getNext() {
        return nextNode;
    }

    /**
     * Sets prevNode with agrument prev
     * 
     * @param prev the leaf node that is set as the prevNode for the current Node
     */
    public void setPrevious(LeafNode prev) {
        prevNode = prev;
    }

    /**
     * Returns prevNode
     * 
     * @return prevNode, the leaf node that is on the left of the current node
     */
    public LeafNode getPrevious() {
        return prevNode;
    }

    /**
     * Clears the ArrayList<Integer> keys and ArrayList<Address> records of the
     * current leaf node
     * 
     */
    public void clear() {
        keys.clear();
        records.clear();
    }

    /**
     * Inserting the given key and address into the TreeMap
     * 
     * @param key the key to be inserted into the TreeMap
     * @param add the address object to be inserted into the keymap
     */
    public void insertByRedistribution(int key, ArrayList<Address> add) {
        map.put(key, add);
    }

    /**
     * Removing the given key in the TreeMap
     * 
     * @param key the key that is removed
     */
    public void removeKeyInMap(int key) {
        map.remove(key);
    }

    /**
     * Returns a formatted string representation of the TreeMap, ArrayList<Address>
     * records and the nextNode
     * 
     * @return A formatted string representation of the TreeMap, ArrayList<Address>
     *         records and the nextNode
     */
    @Override
    public String toString() {
        return String.format("\n--------LEAF NODE CONTAINS: map %s records %s, nextNode ------------\n", map.toString(),
                records, nextNode);
    }

}
