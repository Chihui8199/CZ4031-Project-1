package index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import storage.Address;

import java.lang.Math;

public class Node {

    // Max number of keys in each node (node size)
    private int nodeSize;
    private int minLeafNodeSize;
    private int minNonLeafNodeSize;
    static final int NODE_SIZE = 3;
    private boolean isLeaf;
    private boolean isRoot;
    private NonLeafNode parent;
    protected ArrayList<Address> records;
    protected TreeMap<Integer, ArrayList<Address>> map;

    public Node() {
        this.isLeaf = false;
        this.isRoot = false;
        this.nodeSize = NODE_SIZE;
        this.minLeafNodeSize = (int) (Math.floor((nodeSize + 1) / 2));
        this.minNonLeafNodeSize = (int) (Math.floor(nodeSize / 2));
        this.map = new TreeMap<Integer, ArrayList<Address>>();
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

    public void setParent(NonLeafNode setparent){
        if (this.getIsRoot())
        {
            this.setIsRoot(false);
            setparent.setIsRoot(true);
        }
        this.parent = setparent;
    }

    public NonLeafNode getParent(){
        return this.parent;
    }


   // get arraylist of all keys
    public List<Integer> getKeys() {
        Set<Integer> keySet = map.keySet();
        List<Integer> keys = new ArrayList<>(keySet);
        return keys;
    }

    // get key at index within node
    public int getKey(int index) {
        Set<Integer> keys = map.keySet();
        List<Integer> keyList = new ArrayList<>(keys);
        Integer key = keyList.get(index);
        return key;
    }


    // need to make sure that old node that was split, keys are updated correctly. same for new node as well
    // need to check if old node has parent node, if have, then connect the new one to it as well, if parent is full, call split again
    // if dont have, need to create new parent node which contains smallest key of new node

    public void splitNode(int key, Address add) {

        int keyToBeAdded = key;


        if (this.getIsLeaf()){
            //create a new node
            LeafNode newNode = new LeafNode();
            ArrayList<Address> addToBeAdded = new ArrayList<Address>();

            // ################# IMPORTANT #######################
            // add keyToBeAdded into the OLD NODES's treemap of keys (which contain arraylists of addresses), 
            // which is automatically sorted by keys, take the last minLeafNodeSize keys of the sorted treemap and insert into new Node's treemap
            addToBeAdded.add(add);
            newNode.map.put(keyToBeAdded, addToBeAdded);

            int n = minLeafNodeSize;
            int i = 0;
            int fromKey = 0;
            for (Map.Entry<Integer, ArrayList<Address>> entry : map.entrySet()) {
                if (i == n) {
                    fromKey = entry.getKey();
                    break;
                }
                i++;
            }
            
            newNode.map = (TreeMap<Integer, ArrayList<Address>>) map.subMap(fromKey, false, map.lastKey(), true);
            
            // if parent node exists insert new node into this node
            if (this.getParent() != null){
                this.getParent().addChild(newNode);
            }
            // else create new parent node, insert new node into this node
            else{
                NonLeafNode newParent = new NonLeafNode();
                this.setParent(newParent);
                this.getParent().addChild(newNode);
            }
            
        }

        else{
            //create a new node
            LeafNode newNode = new LeafNode();
            ArrayList<Address> addToBeAdded = new ArrayList<Address>();

            // ################# IMPORTANT #######################
            // add keyToBeAdded into the OLD NODES's treemap of keys (which contain arraylists of addresses), 
            // which is automatically sorted by keys, take the last minLeafNodeSize keys of the sorted treemap and insert into new Node's treemap
            addToBeAdded.add(add);
            newNode.map.put(keyToBeAdded, addToBeAdded);

            int n = minNonLeafNodeSize;
            int i = 0;
            int fromKey = 0;
            for (Map.Entry<Integer, ArrayList<Address>> entry : map.entrySet()) {
                if (i == n) {
                    fromKey = entry.getKey();
                    break;
                }
                i++;
            }
            
            newNode.map = (TreeMap<Integer, ArrayList<Address>>) map.subMap(fromKey, false, map.lastKey(), true);
            
            // if parent node exists insert new node into this node
            if (this.getParent() != null){
                this.getParent().addChild(newNode);
            }
            // else create new parent node, insert new node into this node
            else{
                NonLeafNode newParent = new NonLeafNode();
                this.setParent(newParent);
                this.getParent().addChild(newNode);
            }
        }
	}

    public void printNode() {
        Set<Integer> keys = map.keySet();
        System.out.println(keys);
    }



    // // delete key from index
    // public void deleteKey(int index) {
    // keys.remove(index);
    // }

    // // for deleting keys before splitting
    // public void deleteKeys() {
    // keys = new ArrayList<Integer>();
    // }

}
