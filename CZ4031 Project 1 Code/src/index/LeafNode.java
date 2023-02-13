package index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import storage.Address;

public class LeafNode extends Node {

    protected TreeMap<Integer, ArrayList<Address>> map;
    protected ArrayList<Address> records;
    private LeafNode nextNode;

    public LeafNode(){
        super();
        // records = new ArrayList<Address>();
        setIsLeaf(true);
        setNext(null);
        // this.map = new TreeMap<Integer, ArrayList<Address>>();
    }

    // Add record into both TreeMap and ArrayList of keys
    public void addRecord(int key, Address add) {
        int n = NODE_SIZE;

        System.out.printf("Entered addRecord\n");

        // if node is empty, create new Arraylist and TreeMap
        if (this.getKeys() == null){
            this.records = new ArrayList<Address>();
            this.records.add(add);
            System.out.printf("Address %d %d added to key %d in ArrayList \n",add.getBlockId(),add.getOffset(),key);
            this.map = new TreeMap<Integer, ArrayList<Address>>();
            this.map.put(key,records);
            System.out.printf("Record is added to key %d in TreeMap \n",key);
            return;
        }

        // if node full or contains duplicates, add records into TreeMap
        if (this.getKeys().size() < n || this.map.containsKey(key)){
            this.records.add(add);
            this.map.put(key,records);
        }

        // if contains duplicate in ArrayList of keys, don't need to change ArrayList
        if (this.keys.contains(key)){
            return;
        }

        // else if keysize not full, insert the key into the ArrayList in sorted order
        else if (this.keys.size() < n){
            insertInOrder(this.keys, key);
        }

        // else, the arraylist and treemap is full, split the node
        else{
            this.splitNode(key, add);
            System.out.print("node size capacity reached, could not insert key: ");
            System.out.println(key);
        }

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
}