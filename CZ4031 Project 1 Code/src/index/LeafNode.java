package index;

import java.util.ArrayList;
import java.util.TreeMap;

import storage.Address;
import index.Node;

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

    public void insertByRedistribution(int key, ArrayList<Address> add) {
        map.put(key, add);
    }

    public void removeKeyInMap(int key) {
        map.remove(key);
    }


    // TODO: Have to create function for findRecord
    public ArrayList<Address> findRecord(int key) {
        if (this.map.containsKey(key) || this.keys.contains(key)) {

            // Get the existing list of records associated with the key
            ArrayList<Address> existingRecords = map.get(key);
            return existingRecords;
        }
        return null;
    }

    public ArrayList<Address> getAddressesForKey(int key) {
        return map.get(key);
    }


    // Add record into both TreeMap and ArrayList of keys
    public void addRecord(int key, Address add) {
        int n = NODE_SIZE;

        // if node is empty, create new Arraylist and TreeMap
        if (this.keys == null) {

            this.records = new ArrayList<Address>();
            this.records.add(add);

            this.map = new TreeMap<Integer, ArrayList<Address>>();
            this.map.put(key, records);
            // System.out.print(this.map);

            this.keys = new ArrayList<Integer>();
            insertInOrder(this.keys, key);
            return;
        } else if (this.map.containsKey(key) || this.keys.contains(key)) {

            // Get the existing list of records associated with the key
            ArrayList<Address> existingRecords = map.get(key);

            // Append the new record to the existing list of records
            existingRecords.add(add);

            // Put the updated list of records back into the map
            map.put(key, existingRecords);

            // System.out.print(this.map);
        }

        // else if keysize not full, insert the key into the ArrayList in sorted order
        else if (this.keys.size() < n) {
            this.records = new ArrayList<Address>();
            this.records.add(add);

            this.map.put(key, records);
            // System.out.print(this.map);

            insertInOrder(this.keys, key);
        }

        // else, the arraylist and treemap is full, split the node
        else {
            this.splitLeafNode(key, add);
        }

    }

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

    public static void insertInOrder(ArrayList<Integer> list, int num) {
        int i = 0;

        while (i < list.size() && list.get(i) < num) {
            i++;
        }
        list.add(i, num);
    }

    // Set sibling node as nextNode
    public void setNext(LeafNode sibling) {
        nextNode = sibling;
    }

    public LeafNode getNext() {
        return nextNode;
    }

    public void setPrevious(LeafNode prev) {
        prevNode = prev;
    }

    public LeafNode getPrevious() {
        return prevNode;
    }

    Address removeAddPointerAt(int index) {
        return records.remove(index);
    }

    @Override
    public String toString() {
        return String.format("\n--------LEAF NODE CONTAINS: map %s records %s, nextNode ------------\n", map.toString(),
                records, nextNode);
    }

    public TreeMap<Integer, ArrayList<Address>> getMap() {
        return map;
    }

    public void clear() {
        keys.clear();
        records.clear();
    }

}
