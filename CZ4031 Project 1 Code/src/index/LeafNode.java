package index;

import java.util.ArrayList;
import java.util.Collections;

import storage.Address;

public class LeafNode extends Node {

    protected ArrayList<Address> records;
    private LeafNode nextNode;

    public LeafNode(){
        super();
        records = new ArrayList<Address>();
        setIsLeaf(true);
        setNext(null);
    }

    // Add record
    public void addKey(int key, Address add) {
        int n = NODE_SIZE;
        System.out.println(this.getKeys().size());
        // System.out.println(this.map.size());


        this.records.add(add);
        this.map.put(key,records);


        if (this.getKeys().size() < n || this.map.containsKey(key) ){
             // for current (i), if current key is greater than new key, break and save index i
            this.records.add(add);
            this.map.put(key,records);
        }

        else{
            this.splitNode(key, add);
            System.out.print("node size capacity reached, could not insert key: ");
            System.out.println(key);
        }

    }


    // Set sibling node as nextNode
    public void setNext(LeafNode sibling) {
        nextNode = sibling;
    }
}