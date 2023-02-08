package index;

import java.util.ArrayList;
import java.lang.Math;

public abstract class Node{

    // Max number of keys in each node (node size)
    private int nodeSize;
    private int minLeafNodeSize;
    private int minNonLeafNodeSize;

    // List of keys currently in Node
    private ArrayList<Integer> keys;

    // Pointer to nonLeafNode node
    private NonLeafNode nonLeafNode;

    private boolean isLeaf;
    private boolean isRoot;

    
    public Node(){

        nodeSize = 3;
        minLeafNodeSize = (int)(Math.floor((nodeSize + 1) / 2));
        minNonLeafNodeSize = (int)(Math.floor(nodeSize / 2));
        keys = new ArrayList<Integer>();
        isLeaf = false;
        isRoot = false;
    }    

    // check whether node is a leaf
    public boolean getIsLeaf() {
        return isLeaf;
    }

    // set node as leaf
    public void setIsLeaf(boolean isALeaf) {
        isLeaf = isALeaf;
    }

    // check whether node is as root
    public boolean getIsRoot() {
        return isRoot;
    }

    // set node as root
    public void setIsRoot(boolean isARoot) {
        isRoot = isARoot;
    }

    // get leaf node's parent
    public NonLeafNode getParent() {
        return nonLeafNode;
    }

    // set leaf node's parent
    public void setParent(NonLeafNode parent) {
        nonLeafNode = parent;
    }

    // get arraylist of all keys
    public ArrayList<Integer> getKeys() {
        return keys;
    }

    // get key at index within node
    public int getKey(int index) {
        return keys.get(index);
    }
    

    // add key into the arraylist in the node
    public int addKey(int key) {

        // if node is empty, add key at index 0
        if (this.getKeys().size() == 0) {

            this.keys.add(key);
            return 0;
        }
        
        int n = keys.size();
        int i;

        // for current (i), if current key is greater than new key, break and savc index i
        for (i = 0; i < n; i++) {
            if (keys.get(i) >= key) 
                break;
        }

        // for every current key greater than new key, move current key to right empty index j + 1
        for (int j = n - 1; j >= i; j--) {
            keys.set(j + 1 , keys.get(j));
        }

        // insert new key into empty index i
        keys.set(i, key);
        return i;
    }
    

    // delete key from index
    public void deleteKey(int index) {
        keys.remove(index);
    }

    // for deleting keys before splitting
    public void deleteKeys() {
        keys = new ArrayList<Integer>();
    }

    
    
    // find smallest key (more for use by parentnode but placed here for first level of parents)
    
    
    // public int findSmallestKey() {

    //     int key;
    //     NonLeafNode copy;

    //     if (!this.getIsLeaf()) {

    //         copy = (NonLeafNode) this;

    //         while (!copy.getChild(0).getIsLeaf())
    //             copy = (NonLeafNode) copy.getChild(0);
            
    //         key = copy.getChild(0).getKey(0);
    //     }

    //     else 
    //         key = this.getKey(0);

    //     return key;
    // }

    // delete the node


    public void deleteNode() {

        // Removing child nodes of a non-leaf/parent node and setting the pointer of that node to be null
        if (nonLeafNode != null) {
            nonLeafNode.deleteChild(this);
            nonLeafNode = null;
        }

        // Removing the data block/records pointed by the "deleted" leaf node (by replacing with a new arraylist)
        // and ensuring that the "deleted" leaf node points to null
        if (this.isLeaf) {
            LeafNode copy = (LeafNode) this;
            copy.deleteRecords();
            copy.setNext(null);
        }

        // when nonLeafNode == null
        else {
            NonLeafNode copy = (NonLeafNode) this;
            copy.deleteChildren();
        }

        isLeaf = false;
        isRoot = false;
        keys = new ArrayList<Integer>();
    }

}

