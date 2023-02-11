package index;

import java.util.ArrayList;
import java.util.Collections;

import storage.Address;

public class LeafNode extends Node {

    private ArrayList<Address> records;
    private LeafNode nextNode;
    private NonLeafNode parent;

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
        System.out.println(this.keys.size());

        ArrayList<Integer> keyBlkOffset;
        keyBlkOffset.add(key);
        keyBlkOffset.add(add.getBlockId());
        keyBlkOffset.add(add.getOffset());

        // if node is empty, add key at index 0
        if (this.getKeys().size() == 0) {
            this.keys.add();
            return;
        }
        

        // insert at the back
        if (keys.size() < n){
             // for current (i), if current key is greater than new key, break and save index i
             this.keys.add(key);
            Collections.sort(keys);
            System.out.println(keys);
        }

        else{
            this.splitNode();
            System.out.print("node size capacity reached, could not insert key: ");
            System.out.println(key);
        }

    }


    // Set sibling node as nextNode
    public void setNext(LeafNode sibling) {
        nextNode = sibling;
    }

    public void setParent(NonLeafNode setparent){
        parent = setparent;
    }

    public Node getParent(){
        return parent;
    }

}