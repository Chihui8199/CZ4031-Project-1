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

        System.out.printf("\nEntered addRecord\n");

        // if node is empty, create new Arraylist and TreeMap
        if (this.keys == null){
            this.records = new ArrayList<Address>();
            this.records.add(add);
            this.records.add(add);
            System.out.printf("Record added to Address ArrayList:");
            System.out.print(this.records);
            // System.out.printf("\nAddress %d %d added to key %d in ArrayList \n",add.getBlockId(),add.getOffset(),key);
            this.map = new TreeMap<Integer, ArrayList<Address>>();
            this.map.put(key,records);
            System.out.printf("\nAddress ArrayList is added to key %d in TreeMap \n",key);
            System.out.print(this.map);

            this.keys = new ArrayList<Integer>();
            insertInOrder(this.keys, key);
            System.out.printf("\nKey added to Key ArrayList:");
            System.out.print(this.keys);
            System.out.printf("\nCurrent node's Key Size:");
            System.out.print(this.keys.size());
            return;
        }

        else if (this.map.containsKey(key) || this.keys.contains(key)){
            this.records.add(add);
            this.map.put(key,records);
        }

        // else if keysize not full, insert the key into the ArrayList in sorted order
        else if (this.keys.size() < n){
            this.records = new ArrayList<Address>();
            this.records.add(add);
            this.records.add(add);
            System.out.printf("Record added to Address ArrayList:");
            System.out.print(this.records);
            // System.out.printf("\nAddress %d %d added to key %d in ArrayList \n",add.getBlockId(),add.getOffset(),key);

            this.map.put(key,records);
            System.out.printf("\nAddress ArrayList is added to key %d in TreeMap \n",key);
            System.out.print(this.map);

            insertInOrder(this.keys, key);
            System.out.printf("\nKey added to Key ArrayList:");
            System.out.print(this.keys);
            System.out.printf("\nCurrent node's Key Size:");
            System.out.print(this.keys.size());
        }

        // else, the arraylist and treemap is full, split the node
        else{
            System.out.printf("**Keys in ArrayList Before Splitting\n");
            System.out.print(this.keys);
            this.splitNode(key, add);
            // System.out.print("node size capacity reached, could not insert key: ");
            // System.out.println(key);
            System.out.printf("\n**Keys in ArrayList After Splitting\n");
            System.out.print(this.keys);
        }

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

    // Set sibling node as nextNode
    public void setNext(LeafNode sibling) {
        nextNode = sibling;
    }
}