package index;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import storage.Address;

public class LeafNode extends Node {

    protected TreeMap<Integer, ArrayList<Address>> map;
    protected ArrayList<Address> records;
    private LeafNode nextNode;
    private LeafNode next;

    public LeafNode() {
        super();
        setIsLeaf(true);
        setNext(null);
    }

    public LeafNode getNext() {
        return next;
    }

    public void setNext(LeafNode next) {
        this.next = next;
    }

    // Add record into both TreeMap and ArrayList of keys
    public void addRecord(int key, Address add) {
        int n = NODE_SIZE;

        System.out.printf("\nEntered addRecord\n");

        // if node is empty, create new Arraylist and TreeMap
        if (this.keys == null) {
            this.records = new ArrayList<Address>();
            this.records.add(add);
            System.out.printf("Record added to Address ArrayList:");
            System.out.print(this.records);
            this.map = new TreeMap<Integer, ArrayList<Address>>();
            this.map.put(key, records);
            System.out.printf("\nAddress ArrayList is added to key %d in TreeMap \n", key);
            System.out.print(this.map);

            this.keys = new ArrayList<Integer>();
            insertInOrder(this.keys, key);
            System.out.printf("\nKey added to Key ArrayList:");
            System.out.print(this.keys);
            System.out.printf("\nCurrent node's Key Size:");
            System.out.print(this.keys.size());
            return;
        }

        else if (this.map.containsKey(key) || this.keys.contains(key)) {

            // Get the existing list of records associated with the key
            ArrayList<Address> existingRecords = map.get(key);

            // Append the new record to the existing list of records
            existingRecords.add(add);

            // Put the updated list of records back into the map
            map.put(key, existingRecords);

            System.out.printf("Current Key: ");
            System.out.println(key);
            System.out.printf("Current Address: ");
            System.out.println(add);

            System.out.printf("\nAddress ArrayList is added to key %d in TreeMap \n", key);
            System.out.print(this.map);
        }

        // else if keysize not full, insert the key into the ArrayList in sorted order
        else if (this.keys.size() < n) {
            this.records = new ArrayList<Address>();
            this.records.add(add);
            System.out.printf("Record added to Address ArrayList:");
            System.out.print(this.records);

            this.map.put(key, records);
            System.out.printf("\nAddress ArrayList is added to key %d in TreeMap \n", key);
            System.out.print(this.map);

            insertInOrder(this.keys, key);
            System.out.printf("\nKey added to Key ArrayList:");
            System.out.print(this.keys);
            System.out.printf("\nCurrent node's Key Size:");
            System.out.print(this.keys.size());
        }

        // else, the arraylist and treemap is full, split the node
        else {
            System.out.printf("Keys in ArrayList Before Splitting******************************\n");
            System.out.println(this.keys);
            this.splitNode(key, add);
            System.out.printf("\nKeys in ArrayList After Splitting******************************\n");
            System.out.print(this.keys);
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
        System.out.printf("\nList:");
        System.out.print(list);
    }

    // Functions required to remove key and record from associated leaf node
    public void removeRecord(int key) {
        if (this.map.containsKey(key)) {
            ArrayList<Address> records = this.map.get(key);
            records.remove(0);
            if (records.isEmpty()) {
                this.map.remove(key);
                this.keys.remove((Integer) key);
            }
        }

    }

}