package index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;

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
    protected ArrayList<Integer> keys;
    Node rootNode;

    public Node() {
        this.rootNode = testBplusTree.getRoot();
        this.isLeaf = false;
        this.isRoot = false;
        this.nodeSize = NODE_SIZE;
        this.minLeafNodeSize = (int) (Math.floor((nodeSize + 1) / 2));
        this.minNonLeafNodeSize = (int) (Math.floor(nodeSize / 2));
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
            setparent.setIsLeaf(false);
            testBplusTree.setRoot(setparent);
        }
        this.parent = setparent;
    }

    public NonLeafNode getParent(){
        return this.parent;
    }


   // get arraylist of all keys
    public ArrayList<Integer> getKeys() {
        return this.keys;
    }

    // get key at index within node
    public int getKey(int index) {
        return this.keys.get(index);
    }


    // need to make sure that old node that was split, keys are updated correctly. same for new node as well
    // need to check if old node has parent node, if have, then connect the new one to it as well, if parent is full, call split again
    // if dont have, need to create new parent node which contains smallest key of new node
    public void splitNode(int key, Address addr) {

        // Is a LeafNode ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        if (this.getIsLeaf()){
            //create a new node
            LeafNode newNode = new LeafNode();
            //ArrayList<Address> addrToBeAdded = new ArrayList<Address>();

            System.out.printf("\n\nSplitting LeafNode");
            // Handling the TreeMap-----------------------------------------------------------------------
            // add keyToBeAdded into the OLD NODES's treemap of keys (which contain arraylists of addresses), 
            // which is automatically sorted by keys, take the last minLeafNodeSize keys of the sorted treemap and insert into new Node's treemap
            ((LeafNode)this).records = new ArrayList<Address>();
            ((LeafNode)this).records.add(addr);
            ((LeafNode)this).map.put(key, ((LeafNode)this).records);
  
            // Removing whats after the nth index into the new node
            int n = NODE_SIZE - minLeafNodeSize+1;
            int i = 0;
            int fromKey = 0;

            // get last key in the node(?)
            for (Map.Entry<Integer, ArrayList<Address>> entry : ((LeafNode)this).map.entrySet()) {
                if (i == n) {
                    fromKey = entry.getKey();
                    break;
                }
                i++;
            }

            // newNode with correct TreeMap created by using SubMap which creates a treemap of keys after the nth index

            System.out.printf("\nMap of old node Before Removing\n");
            System.out.print(((LeafNode)this).map);

            SortedMap<Integer, ArrayList<Address>> lastnKeys = 
                        ((LeafNode)this).map.subMap(
                        fromKey, 
                        false, 
                        ((LeafNode)this).map.lastKey(), 
                        true);

            newNode.map = new TreeMap<Integer, ArrayList<Address>>(lastnKeys);
            
            // removing keys after the nth index for old node
            lastnKeys.clear();

            System.out.printf("\nMap of old node After Removing\n");
            System.out.print(((LeafNode)this).map);

            System.out.printf("\nMap of new node\n");
            System.out.print(newNode.map);

            // Handling the ArrayList of keys-----------------------------------------------------------------------
            
            System.out.printf("\n**Keys in ArrayList Before Removing\n");
            System.out.print(this.keys);
            
            insertInOrder(this.keys, key);

            newNode.keys = new ArrayList<Integer>(this.keys.subList(n, this.keys.size()));// after nth index

            // removing keys after the nth index for old node
            this.keys.subList(n, this.keys.size()).clear(); //<- TODO: HERE ISSUE



            System.out.printf("\n**Keys in old Node's ArrayList After Removing\n");
            System.out.print(this.keys);

            System.out.printf("\n**Keys in new Node's ArrayList\n");
            System.out.print(newNode.keys);


            // Handling the parent node of the old node---------------------------------------------------------------
            // if parent node exists insert new node into this node
            if (this.getParent() != null){

                //Check if parent is full, if no
                if(this.getParent().getKeys().size() != NODE_SIZE){
                    
                    //Add new node into old node's parent
                    this.getParent().addChild(newNode);

                } else {
                    splitNode(key, null);
                }
            }
            // else create new parent node, insert new node into this node
            else{
                NonLeafNode newParent = new NonLeafNode();
                this.setParent(newParent);
                this.getParent().addChild(newNode);
            }
            
        }



        // Is a NonLeafNode ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        else{


            //create a new node
            LeafNode newNode = new LeafNode();


            // Removing whats after the nth index into the new node
            int n = NODE_SIZE - minLeafNodeSize;

            // Handling the ArrayList of keys-----------------------------------------------------------------------
            insertInOrder(this.keys, key);

            newNode.keys = new ArrayList<Integer>(this.keys.subList(n+1, this.keys.size()));// after nth index

            this.keys.subList(n, this.keys.size()).clear();



            // Handling the parent node of the old node---------------------------------------------------------------
            // if parent node exists insert new node into this node
            if (this.getParent() != null){

                //Check if parent is full, if no
                if(this.getParent().getKeys().size() != NODE_SIZE){
                    
                    //Add new node into old node's parent
                    this.getParent().addChild(newNode);

                } else {
                    splitNode(key, null);
                }
            }
            // else create new parent node, insert new node into this node
            else{
                NonLeafNode newParent = new NonLeafNode();
                this.setParent(newParent);
                this.getParent().addChild(newNode);

                // if (this.getIsRoot()){
                //     this.setIsRoot(false);
                //     newParent.setIsRoot(true);
                //     testBplusTree.setRoot(newParent);
                // }
            }

        }
	}


    public static void insertInOrder(ArrayList<Integer> list, int num) { 
        int i = 0; 
        
        while (i < list.size() && list.get(i) < num) { 
            i++; 
        } 
        list.add(i, num); 
    }


    
    public void printNode() {
        Set<Integer> keys = ((LeafNode)this).map.keySet();
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
