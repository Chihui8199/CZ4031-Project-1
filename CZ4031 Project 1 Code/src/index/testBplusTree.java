

package index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.text.AsyncBoxView.ChildLocator;

import storage.Address;
import index.NonLeafNode;

public class testBplusTree{

    static final int NODE_SIZE = 3;
    static Node rootNode;
    Node nodeToInsertTo;

    public testBplusTree(){
        rootNode = createFirstNode();
    }

  public LeafNode createFirstNode() {
        LeafNode newNode = new LeafNode();
        newNode.setIsRoot(true);
        newNode.setIsLeaf(true);
        setRoot(newNode);
        return newNode;
}


    public static Node createNode() {
        Node newNode = new Node();
        return newNode;
    }

    public static void setRoot(Node root){
        rootNode = root;
        rootNode.setIsRoot(true);
    }

    
    public static Node getRoot(){
        return rootNode;
    }

    public void insertKey(int key, Address add){
        System.out.printf("\n\n\nInserting Key %d\n", key);
        nodeToInsertTo = searchNode(key);
        System.out.printf("Keys of node to insert to: \n");
        System.out.print(nodeToInsertTo.getKeys());
        ((LeafNode) nodeToInsertTo).addRecord(key, add);
    }


    public LeafNode searchNode(int key){
        // first find the root node
        // LeafNode root = (LeafNode) getRoot();
        ArrayList<Integer> keys; 

        System.out.printf("Searching Node for Key %d\n",key);

        //root is at first level
        if (testBplusTree.rootNode.getIsLeaf()){
            // root = new LeafNode();
            setRoot(rootNode);
            System.out.printf("Found Node : Root\n");
            return (LeafNode)rootNode;
        }

        else{
            Node nodeToInsertTo = (NonLeafNode) getRoot();

        // if nodeToInsertTo's child is not a leaf node
        while (!((NonLeafNode) nodeToInsertTo).getChild(0).getIsLeaf()) {

            keys = nodeToInsertTo.getKeys();
            
            for (int i = keys.size() -1; i >= 0; i--) {

                if (nodeToInsertTo.getKey(i) <= key) {
                    System.out.printf("nodeToInsertTo = %d\n",nodeToInsertTo.getKey(i));
                    nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(i+1);
                    break;
                }

                else if (i == 0)
                    nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(0);
            }
        }

        return (LeafNode) nodeToInsertTo; 
        }

       
}

            // Version 2: while current node is not leaf node: iterate through node levels
            // Node newNode = root;
            // while(!newNode.getIsLeaf()){

            //     //iterate through keys
            //     for(int i : newNode.getKeys()){

            //         // if current key > key(i), enter the left node
            //         if( key >= newNode.getKey(i)){

            //             ArrayList<Node> childrenArray = ((NonLeafNode) newNode).getChildren(); 
            //             // Node nodeToFind;

            //                 // iterate through nodes
            //                 for (Node node : childrenArray){
            //                     if (node.getKey(i) == key ){
            //                         newNode = node;
            //                     }
            //                 }

            //         }
            //     }
            // }
            

            // return;
           
            // within each leaf node
            // use getKeys to retrieve each Arraylist of keys in each node, 
           
            // which is an ArrayList of nodes
            // then loop through using the line 68-73 logic until you get to the leaf node
            // within each node, check through the arraylist with line 68-73 logic again
            // then add key to the required node
        
        
        
//from this root we need to search for the node to insert the key 
// which means we need to search for the node which already has they key/ if not create the key
        // to search, we check if the key is less than the root, check the left child
        // else check the right Child
        //keep repeating until you find the node which contains the key/ should contain the key

    // public void insertNode(int key){
        
    //     // ArrayList<Integer> inputs = new ArrayList<>(Arrays.asList(10,20,30,40,50,60,70,80,90,100));
    //     // System.out.println(inputs);
    //     Node node = createNode();
    //     // System.out.println(node.getIsLeaf());
    //     node.addKey(key);

    // }



    
// Insertion on B+ Tree:
// 1. Perform a search to determine what node the new record should be inserted to
// 2. If the node is not full (at most n keys after the insertion), insert the record to the node
// 3. Otherwise,
//      1) Split the node into two
//      2) Distribute the keys among the two nodes
//      3) Insert the new node to the parent if any and create a new root otherwise
//      4) Repeat until a parent is found that need not split

    
    

    public static void deleteNode(){


// Deletion on B+ Tree (Case 3):
// 1. Neither adjacent sibling can be used
// 2. Merge the two nodes, deleting one of them
// 3. Adjust the parent
// 4. If the parent is not full enough, recursively apply the deletion algorithm in parent 
    
// Deletion on B+ Tree (Case 2):
// 1. An adjacent sibling can be used

// Deletion on B+ Tree (Case 1):
// 1. No changes required



}

    public static void searchValue(){
// 1. Start from the root node
// 2. While the node is not a leaf node
//      1) We decide which pointer to follow based on the target key and the keys maintained in the node
//      2) Follow the pointer, arrive a new node
// 3. Decide which pointer to follow
// 4. Follow the pointer and retrieve the data block
    }

    public static void searchRange(){
// 1. Start from the root node
// 2. While the node is not a leaf node
//      1) We decide which pointer to follow based on the target key (the lower bound of the range) and the keys maintained in the node
//      2) Follow the pointer, arrive a new node
// 3. Decide which pointer to follow
// 4. Follow the pointer and retrieve the data block
// 5. Keep scanning the following leaf nodes and the data blocks pointed by the pointers in the leaf nodes until we reach the upper bound of the range
    }

}