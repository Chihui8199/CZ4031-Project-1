

package index;

import java.util.ArrayList;
import java.util.Arrays;

public class testBplusTree {

    
    public static void test(){

        ArrayList<Integer> inputs = new ArrayList<>(Arrays.asList(10,20,30,40,50,60,70,80,90,100));
        System.out.println(inputs);

    }

    public static void insertNode(){

// Insertion on B+ Tree:
// 1. Perform a search to determine what node the new record should be inserted to
// 2. If the node is not full (at most n keys after the insertion), insert the record to the node
// 3. Otherwise,
//      1) Split the node into two
//      2) Distribute the keys among the two nodes
//      3) Insert the new node to the parent if any and create a new root otherwise
//      4) Repeat until a parent is found that need not split

    
    

}

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