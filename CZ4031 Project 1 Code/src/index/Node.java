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
        System.out.println("Setting parent (parent has these keys)" + setparent.getKeys());
        if (this.getIsRoot())
        {
            this.setIsRoot(false);
            setparent.setIsRoot(true);
            setparent.setIsLeaf(false);
            testBplusTree.setRoot(setparent);
        }
        else{
            setparent.setIsLeaf(false);
        }
        this.parent = setparent;
    }

    public NonLeafNode getParent(){
        return this.parent;
    }

    private void removeParent(NonLeafNode parent) {
        this.parent = null;
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
            System.out.print("\nROOT NODE IS:");
            System.out.print(testBplusTree.getRoot().getKeys());

            System.out.println("\nSPLITTING LEAF NODE************************************************");
            //create a new node
            LeafNode newNode = new LeafNode();
            //ArrayList<Address> addrToBeAdded = new ArrayList<Address>();

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
            this.keys.subList(n, this.keys.size()).clear();



            System.out.printf("\n**Keys in old Node's ArrayList After Removing\n");
            System.out.print(this.keys);

            System.out.printf("\n**Keys in new Node's ArrayList\n");
            System.out.print(newNode.keys);


            // Handling the parent node of the old node---------------------------------------------------------------
            // if parent node exists insert new node into this node
            
            if (this.getParent() != null){

                System.out.printf("\n**Keys in parent node------------------------------\n");
                System.out.print(this.getParent().keys);

                //Check if parent is full, if no
                
                if (this.getParent().keys.size() == NODE_SIZE ){
                    System.out.printf("\n\nProblematic split\n");
                    
                    System.out.printf("\n\n############################THE CURRENT PARENT IS A ROOT:");
                    System.out.println(this.getParent().getIsRoot());

                    System.out.printf("\n Creating new root node when a nonleaf node is full\n");
                    NonLeafNode new2Parent = new NonLeafNode();
                    new2Parent.keys = new ArrayList<Integer>();
                    new2Parent.addChild(this.getParent());
                    

                    if (this.getParent().getIsRoot()){
                        this.getParent().setIsRoot(false);
                        new2Parent.setIsRoot(true);
                        testBplusTree.setRoot(new2Parent);
                    }

                    this.getParent().setParent(new2Parent);



                    System.out.printf("\n Removing rightmost child when the nonleaf node is full\n");
                    this.getParent().removeChild(this);
                    this.getParent().keys.remove(this.keys.size());
                    this.removeParent(this.getParent());
                    
                    System.out.printf("\n\nAdding key %d in NEW parent node\n",newNode.getKey(0));
                    NonLeafNode newParent = new NonLeafNode();
                    newParent.keys = new ArrayList<Integer>();
                    newParent.addChild(this);
                    newParent.addChild(newNode);
                    newParent.keys.add(newNode.getKey(0));
                    this.setParent(newParent);
                    newNode.setParent(newParent); //<- adding this gave me an infinite loop

                    System.out.printf("\nKeys in new ParentNode's ArrayList:");
                    System.out.print(newParent.keys);


                    new2Parent.addChild(newParent);
                    new2Parent.keys.add(newParent.getKey(0));
                    newNode.setParent(new2Parent); //<- adding this gave me an infinite loop
                    System.out.printf("\nKeys in new2 ParentNode's ArrayList:");
                    System.out.println(new2Parent.keys);

                    System.out.println(newNode.keys);

                    System.out.print(new2Parent.getChild(0).keys);
                    System.out.println(new2Parent.getChild(1).keys);

                    System.out.print(newParent.getChild(0).keys);
                    System.out.print(newParent.getChild(1).keys);
                    
                }
            
                
                else if(this.getParent().keys == null || this.getParent().keys.size() != NODE_SIZE){
                    System.out.printf("\n\nAdding key %d in OLD parent node\n",newNode.getKey(0));
                    //Add new node into old node's parent
                    this.getParent().addChild(newNode);
                    this.getParent().keys.add(newNode.getKey(0));
                    newNode.setParent(this.getParent());

                } 
                
            }

            // else create new parent node, insert new and old node into this node
            else{
                System.out.printf("\n\nAdding key %d in NEW parent node\n",newNode.getKey(0));
                NonLeafNode newParent = new NonLeafNode();
                newParent.keys = new ArrayList<Integer>();
                newParent.addChild(this);
                newParent.addChild(newNode);
                newParent.keys.add(newNode.getKey(0));
                this.setParent(newParent);
                newNode.setParent(newParent); //<- adding this gave me an infinite loop

                System.out.printf("\nKeys in new ParentNode's ArrayList:");
                System.out.print(newParent.keys);

                try{
                    System.out.print("\nParent: ");
                    System.out.print(this.getParent().keys);
                    System.out.print("\nChild at index 0: ");
                    System.out.print(this.getParent().getChild(0).getKeys());
                    System.out.print("\nChild at index 1: ");
                    System.out.print(this.getParent().getChild(1).getKeys());
                    }
                catch(Exception e){ System.out.print("No parent");}
            }
            
        }



        // Is a NonLeafNode ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        else{
            System.out.println("\nSPLITTING NON LEAF NODE************************************************");

            LeafNode newNode = new LeafNode();
             // Handling the ArrayList of keys-----------------------------------------------------------------------
             int n = NODE_SIZE - minLeafNodeSize;
             System.out.printf("\n**Keys in ArrayList Before Removing\n");
             System.out.print(this.keys);
             
             insertInOrder(this.keys, key);
 
             newNode.keys = new ArrayList<Integer>(this.keys.subList(n, this.keys.size()));// after nth index
 
             // removing keys after the nth index for old node
             this.keys.subList(n, this.keys.size()).clear();
 
 
 
             System.out.printf("\n**Keys in old Node's ArrayList After Removing\n");
             System.out.print(this.keys);
 
             System.out.printf("\n**Keys in new Node's ArrayList\n");
             System.out.print(newNode.keys);
 
 
             // Handling the parent node of the old node---------------------------------------------------------------
             // if parent node exists insert new node into this node
             if (this.getParent() != null){
 
                 System.out.printf("\n**Keys in parent node------------------------------\n");
                 System.out.print(this.getParent().keys);
 
                 //Check if parent is full, if no
                 if(this.getParent().keys == null || this.getParent().keys.size() != NODE_SIZE){
                     System.out.printf("Adding key %d\n",newNode.getKey(0));
                     //Add new node into old node's parent
                     this.getParent().addChild(newNode);
                     this.getParent().keys.add(newNode.getKey(0));
 
                 } else if (this.getParent().keys.size() == NODE_SIZE){
                    splitNode(key, null);
                }
             }
             // else create new parent node, insert new and old node into this node
             else{
                 NonLeafNode newParent = new NonLeafNode();
                 newParent.keys = new ArrayList<Integer>();
                 newParent.addChild(this);
                 newParent.addChild(newNode);
                 newParent.keys.add(newNode.getKey(0));
                 this.setParent(newParent);
             }
            }

            

        //     //create a new node
        //     LeafNode newNode = new LeafNode();


        //     // Removing whats after the nth index into the new node
        //     int n = NODE_SIZE - minLeafNodeSize;

        //     // Handling the ArrayList of keys-----------------------------------------------------------------------
        //     insertInOrder(this.keys, key);

        //     newNode.keys = new ArrayList<Integer>(this.keys.subList(n+1, this.keys.size()));// after nth index

        //     this.keys.subList(n, this.keys.size()).clear();



        //     // Handling the parent node of the old node---------------------------------------------------------------
        //     // if parent node exists insert new node into this node
        //     if (this.getParent() != null){

        //         //Check if parent is full, if no
        //         if(this.getParent().keys == null || this.getParent().getKeys().size() != NODE_SIZE){
                    
        //             //Add new node into old node's parent
        //             this.getParent().addChild(newNode);
        //             this.getParent().keys.add(newNode.getKey(0));

        //         } else {
        //             splitNode(key, null);
        //         }
        //     }
        //     // else create new parent node, insert new node into this node
        //     else{
        //         NonLeafNode newParent = new NonLeafNode();
        //         newParent.keys = new ArrayList<Integer>();
        //         newParent.addChild(this);
        //         newParent.addChild(newNode);
        //         newParent.keys.add(newNode.getKey(0));
        //         this.setParent(newParent);
        //     }

        // }
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
