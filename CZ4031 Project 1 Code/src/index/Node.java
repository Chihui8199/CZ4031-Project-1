package index;

import java.util.ArrayList;
import java.util.Collections;

import storage.Address;

import java.lang.Math;

public class Node{

    // Max number of keys in each node (node size)
    private int nodeSize;
    private int minLeafNodeSize;
    private int minNonLeafNodeSize;
    static final int NODE_SIZE = 3;
    // List of keys currently in Node
    
    // Pointer to nonLeafNode node
    // private NonLeafNode nonLeafNode;
    

    private boolean isLeaf;
    private boolean isRoot;


//************************************************************************/
protected ArrayList<Integer> keys;
    // TODO: change it to hashmap type, keys are index and the value is the arraylist [Blk,Offset]
    // eg. [key 1: [[blk 2, offset 3], [blk 5, offset 7]]]

    HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
    ArrayList<Integer> list = new ArrayList<>();
    list.add(1);
    list.add(2);
    list.add(3);
    map.put(1, list);
    ArrayList<Integer> retrievedList = map.get(1);


//************************************************************************/



    
    public Node(){
        this.isLeaf = false;
        this.isRoot = false;
        this.nodeSize = NODE_SIZE;
        this.minLeafNodeSize = (int)(Math.floor((nodeSize + 1) / 2));
        this.minNonLeafNodeSize = (int)(Math.floor(nodeSize / 2));
        this.keys = new ArrayList<Integer>();
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











    // get arraylist of all keys
    public ArrayList<Integer> getKeys() {
        return keys;
    }

    // get key at index within node
    public int getKey(int index) {
        return keys.get(index);
    }
    


    public void printNode(){
        System.out.println(this.keys); 
    }
    

    // delete key from index
    public void deleteKey(int index) {
        keys.remove(index);
    }

    // for deleting keys before splitting
    public void deleteKeys() {
        keys = new ArrayList<Integer>();
    }

	public void splitNode() {
        if (this.getIsLeaf()){
            for (int i =0; i <= minLeafNodeSize; i++){
                
            }
        }

        else{
            // All the indexes here are gonna be in the left node
            // the remaining ones are gonna be in a right node
            Node leftNode = new Node();

            for (int i =0; i <= minNonLeafNodeSize; i++){
                
                leftNode.addKey(this.i, null);
            }
        }
	}

}

    