
package index;

import java.util.ArrayList;

import storage.Address;
import storage.Record;
import storage.Storage;

public class testBplusTree {

    static final int NODE_SIZE = 3;
    static Node rootNode;
    Node nodeToInsertTo;

    // have to initialise the first node as root node
    public testBplusTree() {
        rootNode = createFirstNode();
    }

    public LeafNode createFirstNode() {
        LeafNode newNode = new LeafNode();
        PerformanceRecorder.addOneNode();
        newNode.setIsRoot(true);
        newNode.setIsLeaf(true);
        setRoot(newNode);
        return newNode;
    }

    public static Node createNode() {
        Node newNode = new Node();
        PerformanceRecorder.addOneNode();
        return newNode;
    }

    public static void setRoot(Node root) {
        rootNode = root;
        rootNode.setIsRoot(true);
    }

    public static Node getRoot() {
        return rootNode;
    }

    // have to first search for the LeafNode to insert to, then add a record add
    // that LeafNode
    public void insertKey(int key, Address add) {
        // System.out.printf(
                // "\n\n\nInserting Key %d --------------------------------------------------------------------------------------------------------------------\n",
                // key);
        // System.out.printf("Current Root:");
        // System.out.println(testBplusTree.getRoot().getKeys());

        // nodeToInsertTo is the leafnode
        nodeToInsertTo = searchNode(key);

        // System.out.print("Keys of node to insert to: ");
        // System.out.println(nodeToInsertTo.getKeys());

        ((LeafNode) nodeToInsertTo).addRecord(key, add);
    }

    // TODO: remove this --> we can do this recusively. Look at searchKey function
    public ArrayList<Address> searchKey2(int key) {
        System.out.printf(
                "\n\nSearching Key %d --------------------------------------------------------------------------------------------------------------------\n",
                key);
        // System.out.printf("Current Root:");
        // System.out.println(testBplusTree.getRoot().getKeys());

        // nodeToInsertTo is the leafnode
        nodeToInsertTo = searchNode(key);

        // System.out.printf("Keys of node to search: ");
        // System.out.println(nodeToInsertTo.getKeys());

        return ((LeafNode) nodeToInsertTo).findRecord(key);
    }

    // finding the leaf node to find/insert the key to
    public LeafNode searchNode(int key) {
        ArrayList<Integer> keys;

        // If root is a leaf node, means its still at the first node, hence return the
        // rootNode
        if (testBplusTree.rootNode.isLeaf()) {
            setRoot(rootNode);
            return (LeafNode) rootNode;
        }

        // else, it is not a leaf node
        else {
            Node nodeToInsertTo = (NonLeafNode) getRoot();

            // Starting from the rootnode, keep looping (going down) until the current
            // node's (nodeToInsertTo) child is a leaf node

            while (!((NonLeafNode) nodeToInsertTo).getChild(0).isLeaf()) {
                

                keys = nodeToInsertTo.getKeys();

                // loops through keys of current node (nodeToInsertTo)

                for (int i = keys.size() - 1; i >= 0; i--) {

                    // if there exists a key in the node where it's value is smaller or equals to
                    // the key,
                    // set the current node to the child node corresponding to that node

                    if (nodeToInsertTo.getKey(i) <= key) {
                        nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(i + 1);
                        break;
                    }

                    // if the index reaches 0, means that the key is smaller than the smallest key
                    // in the node (at index 0)
                    // set the current node to the child node that corresponds to that node

                    else if (i == 0) {
                        nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(0);
                    }
                }

                if (nodeToInsertTo.isLeaf()) {
                    break;
                }

            }

            keys = nodeToInsertTo.getKeys();

            // System.out.print("Keys of found parent: ");
            // System.out.println(keys);

            // Looping through the current node's indexes to find which of its leaf/child
            // node to insert the key into, similar to above but this is to obtain the leaf
            // node
            // return the child node once found
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (keys.get(i) <= key) {
                    return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(i + 1);
                }
            }

            // if the key is smaller than the smallest key in the current node, return the
            // child corresponding to the smallest key
            return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(0);
        }

    }

    /**
     * Wrapper function for deleting node
     * 
     * @param key key to be deleted
     * @return AraryList of address to be removed from database
     */
    public ArrayList<Address> deleteKey(int key) {
        return (deleteNode(rootNode, null, -1, -1, key));
    }

    public ArrayList<Address> deleteNode(Node node, NonLeafNode parent, int parentPointerIndex, int parentKeyIndex,
            int key) {
        ArrayList<Address> addOfRecToDelete = new ArrayList<>();

        if (node.isLeaf()) {
            // search for the key to delete
            LeafNode leaf = (LeafNode) node;
            int keyIdx = node.searchKey(key, false);
            if ((keyIdx == leaf.getKeySize()) || (key != leaf.getKeyAt(keyIdx))) {
                return null;
            }

            // found keys to delete: 1) remove key in map 2) remove idx in records
            addOfRecToDelete.addAll(leaf.getAddressesForKey(key));
            leaf.removeKeyAt(keyIdx);
            leaf.removeKeyInMap(key);

        } else {
            // traverse to leaf node to find records to delete
            NonLeafNode nonLeafNode = (NonLeafNode) node;
            int ptrIdx = node.searchKey(key, true);
            int keyIdx = ptrIdx - 1;

            // read the next level node (read action will be recorded in the next level)
            Node next = nonLeafNode.getChild(ptrIdx);
            addOfRecToDelete = deleteNode(next, nonLeafNode, ptrIdx, keyIdx, key);

            // update keys in non-leaf node
            nonLeafNode.updateKey(ptrIdx - 1, next.getKeys().get(0));
        
        }

        // carry out re-balancing tree magic if needed
        // TODO: change this to the calculated node_size once finalized
        // TODO: handle deletion in main memory
        if (node.isUnderUtilized(NODE_SIZE)) {
            System.out.print("------------------------Rebalancing tree now ---------------------------\n\n");
            handleInvalidTree(node, parent, parentPointerIndex, parentKeyIndex);
        }

        return addOfRecToDelete;
    }

    /**
     * This function is used to handle the redistribution of nodes (i.e borrowing
     * from sibiling) or merging or nodes
     * 
     * @param underUtilizedNode  current tree is wrong and current node is
     *                           underutlised
     * @param parent
     * @param parentPointerIndex
     * @param parentKeyIndex
     */
    private void handleInvalidTree(Node underUtilizedNode, NonLeafNode parent, int parentPointerIndex,
            int parentKeyIndex) throws IllegalStateException {
        if (parent == null) {
           handleInvalidRoot(underUtilizedNode);
        } else if (underUtilizedNode.isLeaf()) {
            handleInvalidLeaf(underUtilizedNode, parent,
                    parentPointerIndex, parentKeyIndex);
            System.out.print("Tree rebalanced now.sdasdasd");
        } else if (underUtilizedNode.isNonLeaf()) {
            
        } else {
            throw new IllegalStateException("state is wrong!");
        }
    }

    public void handleInvalidRoot(Node underUtilizedNode){
        System.out.println("HANDING PARENT == NULLL");
        // handleInvalidNonLeaf(underUtilizedNode);
        if(underUtilizedNode.isLeaf()){ // Only node in B+ Tree - Root
            ((LeafNode)underUtilizedNode).clear();
            System.out.print("There exist no B+ Tree now.\n");
        }
        else{
            NonLeafNode nonLeafRoot = (NonLeafNode) underUtilizedNode;
            Node newRoot = nonLeafRoot.getChild(0);
            newRoot.setParent(null);
            rootNode = newRoot;
        }
    }
    private void handleInvalidLeaf(Node underUtilizedNode,
                                   NonLeafNode parent,
                                   int parentPointerIndex,
                                   int parentKeyIndex) throws IllegalStateException {

        // load the neighbors
        LeafNode underUtilizedLeaf = (LeafNode) underUtilizedNode;
        LeafNode nextNode = (LeafNode) underUtilizedLeaf.getNext();
        LeafNode prevNode = (LeafNode) underUtilizedLeaf.getPrevious();
        if(nextNode == null && prevNode == null)
            throw new IllegalStateException("Both prevNode and nextNode is null for " + underUtilizedNode + "This is wrong!");
        // 2. Check if we can redistribute with next
        // 1. Check if we can redistribute with prev
        // 3. If neither the adjacent sibling can be used -> merge the two nodes -> Adjust the parent -> if parent is not fill recusively apply deletion algorithm
        // a. first try merge with right
        // b. second try merge with left
        System.out.println("--> handle invalid leaf");
        if (prevNode != null && prevNode.isAbleToGiveOneKey(NODE_SIZE)){
            System.out.println("--> handle invalid leaf: right to left ");
            moveOneKey(prevNode, underUtilizedLeaf, true, parent, parentKeyIndex+1);
        }
        else if (nextNode != null && nextNode.isAbleToGiveOneKey(NODE_SIZE)){
            System.out.println("--> handle invalid leaf: leaf to right");
            moveOneKey(nextNode, underUtilizedLeaf, false, parent, parentKeyIndex+1);
        } // we can't redistribute, try merging with next
        else if(nextNode!=null && (nextNode.getKeySize() + underUtilizedLeaf.getKeySize()) <= NODE_SIZE) {
            // it's the case where under utilized node is the left node from parent
            System.out.printf("PRINTING KEYS cur LEFT"+ underUtilizedNode.keys);
            System.out.printf("PRINTING KEYS NEXT RIGHT"+ nextNode.keys);

            mergeLeafNodes(underUtilizedLeaf, nextNode, parent,parentPointerIndex+1, parentKeyIndex+1);
        }
        else if((prevNode!=null && (prevNode.getKeySize()+underUtilizedLeaf.getKeySize())<=NODE_SIZE))
        {
            System.out.printf("PRINTING KEYS 2" + nextNode.keys);

            // it's the case where split node is in the left from parent
            mergeLeafNodes(prevNode, underUtilizedLeaf, parent, parentPointerIndex, parentKeyIndex);
        }
        else {
            throw new IllegalStateException("Can't have both leaf " +
                    "pointers null and not be root or no " +
                    "common parent");
        }
    }

    private void mergeLeafNodes(LeafNode left, LeafNode right, NonLeafNode parent,
                                int rightPointerIdx, int inBetweenKeyIdx){
        System.out.printf("++++++++++++\n"+ inBetweenKeyIdx);

        int rightKey = right.getKeyAt(0);

        int moveKeyCount = right.getKeySize();
        for (int i = 0; i < moveKeyCount; i++) {
            int removedKey = right.removeKeyAt(0); 
            int leftLastIdx = left.getLastIdx(); 
            left.insertKeyAt(leftLastIdx+1,removedKey);
            // 2. Move over the records
            left.insertByRedistribution(removedKey, right.getAddressesForKey(removedKey));
            right.removeKeyInMap(removedKey);
            
        }


        // now handle the top pointer
        parent.removeChild(right);
        parent.removeKeyAt(inBetweenKeyIdx);
        
        System.out.print("PArent keys: " + parent.getKeys());

        // update the double-linked pointers
        left.setNext(right.getNext());
        // update the prev pointer of right next node (if any)
        if(right.getNext() != null) {
            LeafNode rightNext = right.getNext();
        //    rightNext.setPrevious(left.getBlockIndex());
            rightNext.setPrevious(right.getPrevious());
        }

    }


    // TODO: modify this
//    private boolean isSameParent(Node node, NonLeafNode parent, int parentIndex) {
//        return(parent.getKeySize() >= parentIndex && parentIndex >= 0 &&
//                (node.getBlockIndex() == parent.getPointerAt(parentIndex)));
//    }


    private void moveOneKey(LeafNode giver, LeafNode receiver,
                            boolean giverOnLeft, NonLeafNode parent,
                            int inBetweenKeyIdx){
        int key;
        //true --> giver on left
        if(giverOnLeft){
            System.out.println("--> move the key from left node to right");
            // move the key from left node to right
            // 1. Move and edit map records
            int giverKey = giver.getLastKey();
            receiver.insertByRedistribution(giverKey, giver.getAddressesForKey(giverKey));
            giver.removeKeyInMap(giverKey);
            // 2. Move and edit key in node
            receiver.insertKeyAt(0, giverKey);
            giver.removeKeyAtLast();
            key = receiver.getKeyAt(0);
        } else {
            System.out.println("--> move key from right node to left node\n");
            // move the key from right node to left
            // 1. Move and edit map records
            int giverKey = giver.getFirstKey();
            receiver.insertByRedistribution(giverKey, giver.getAddressesForKey(giverKey));
            giver.removeKeyInMap(giverKey);
            // 2. Move and edit key in node
            giver.removeKeyAt(0);
            receiver.insertKeyAt(receiver.getKeySize(), giverKey);
            key = giver.getKeyAt(0);
        }
        // update parent ptr
        parent.replaceKeyAt(inBetweenKeyIdx, key);
    }

    /**
     * Wrapper function on top of searchNode
     * 
     * @param key
     * @return ArrayList of Address in the database
     */
    public ArrayList<Address> searchKey(int key) {
        return (searchValue(this.rootNode, key));
    }

    public ArrayList<Address> searchValue(Node node, int key) {
        PerformanceRecorder.addOneNodeReads();

        // Find if key is within the rootNode       
        if (node.isLeaf()) {
            int ptrIdx = node.searchKey(key, false);
            if (ptrIdx >= 0 && ptrIdx < node.getKeySize() && key == node.getKeyAt(ptrIdx)) {
                return ((LeafNode) node).getAddressesForKey(key); // returns an ArrayList of addresses
            }
            return null;
        }
        // If it's an internal node, descend until we reach a leaf node to find the
        // results
        else {
            int ptrIdx = node.searchKey(key, false); // looks for the upper bound of the key in the node
            NonLeafNode nonLeafNode = (NonLeafNode) node; // descends into childnode at the corresponding ptr
            Node childNode = ((NonLeafNode) node).getChild(ptrIdx);
            return (searchValue(childNode, key));
        }
    }

    /**
     * Wrapper Function of rangeSearch
     * 
     * @param minKey min key of the range (inclusive)
     * @param maxKey max key of the range (inclusive)
     */
    public ArrayList<Address> rangeSearch(int minKey, int maxKey) {
        return searchValuesInRange(minKey, maxKey, this.rootNode);
    }

    public static ArrayList<Address> searchValuesInRange(int minKey, int maxKey, Node node) {
        int ptrIdx;
        ArrayList<Address> resultList = new ArrayList<>();
        PerformanceRecorder.addOneRangeNodeReads();
        if (node.isLeaf()) {
            ptrIdx = node.searchKey(minKey, false); // if minKey is in key array, get key index
            LeafNode leaf = (LeafNode) node;
            while (true) {
                if (ptrIdx == leaf.getKeySize()) {
                    // check if we have a next node to load.
                    // Assuming that next node return a null if there's no next node
                    if (leaf.getNext() == null)
                        break; // if not just break the loop
                    // Traverse to the next node and start searching from index 0 within the next
                    // node again
                    leaf = (LeafNode) (leaf.getNext());
                    PerformanceRecorder.addOneRangeNodeReads();

                    ptrIdx = 0;
                    if (ptrIdx >= leaf.getKeySize())
                        throw new IllegalStateException("Range search found a node with 0 keys");
                }
                if (leaf.getKey(ptrIdx) > maxKey)
                    break;
                int key = leaf.getKey(ptrIdx);
                resultList.addAll(leaf.getAddressesForKey(key));
                ptrIdx++;
            }
            return (resultList.size() > 0 ? resultList : null);
        } else {
            ptrIdx = node.searchKey(minKey, true);
            // Descend into leaf node
            NonLeafNode nonLeafNode = (NonLeafNode) node;
            Node childNode = ((NonLeafNode) node).getChild(ptrIdx);
            return (searchValuesInRange(minKey, maxKey, childNode));
        }
    }

    /**
     * Prints the B+ tree rooted at the given node in a readable format.
     * 
     * @param root the root of the tree to print
     */
    public void printBPlusTree(Node root) {
        printBPlusTreeHelper(root, "");
    }

    /**
     * Helper method to print the subtree rooted at the given node.
     * 
     * @param node   the root of the subtree to print
     * @param indent the current indentation level
     */
    private void printBPlusTreeHelper(Node node, String indent) {
        
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;
            System.out.print(indent + "LeafNode: ");
            for (int key : leaf.getKeys()) {
                System.out.print(key + " ");
            }
            System.out.println();
        } else {
            
            NonLeafNode nonLeaf = (NonLeafNode) node;
            System.out.print(indent + "NonLeafNode: ");
            for (int key : nonLeaf.getKeys()) {
                System.out.print(key + " ");
            }
            System.out.println();
            for (Node child : nonLeaf.getChildren()) {
                printBPlusTreeHelper(child, indent + "  ");
            }
            PerformanceRecorder.addOneTreeDegree();
        }
        
    }
    
    public static void experimentTwo(){
        System.out.println("\n----------------------EXPERIMENT 2-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.println("Parameter n: " + NODE_SIZE);

        System.out.print("No. of Nodes in B+ tree: ");
        System.out.println(performance.getTotalNodes());

        // Only works if you call printBPlusTree() before this
        System.out.print("No. of Levels in B+ tree: ");
        PerformanceRecorder.deleteOneTreeDegree(); // - minus one because the count starts from 1
        System.out.println(performance.getTreeDegree());
        
        System.out.print("Content of the root node: ");
        System.out.println(testBplusTree.getRoot().keys);
    }

    public static void experimentThree(Storage db, testBplusTree tree){
        System.out.println("\n----------------------EXPERIMENT 3-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.print("\nMovies with the 'numVotes' equal to 500: ");
        
        long startTime = System.nanoTime();
        ArrayList<Address> resultAdd = tree.searchKey(500);
        ArrayList<Record> results = new ArrayList<>();
         if(resultAdd != null){
             for(Address add:resultAdd) {
                 Record record = db.getRecord(add);
                 results.add(record);
             }
         }
        System.out.printf("Records: %s", results);

        //TODO: have to return the actual records with block no. and offset from searchKey
        long endTime = System.nanoTime();

        //TODO: have to return the actual records with block no. and offset from searchKey
        for ( Address address : searchResults) {
            
            System.out.print("\n" + address );
            
        }
        

        // TODO: searchKey counter not counting smth
        System.out.print("\nNo. of Index Nodes the process accesses: ");
        System.out.println(performance.getNodeReads());

        // System.out.print("No. of Data Blocks the process accesses: ");

        // System.out.print("Average of 'averageRating's' of the records accessed: ");
        
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("\nRunning time of retrieval process: %d nanoseconds\n",duration);

        // System.out.print("No. of data blocks: ");

        // System.out.print("Running time of brute-force linear scan:  ");

    }

    public static void experimentFour(testBplusTree tree){
        System.out.println("\n----------------------EXPERIMENT 4-----------------------");

        PerformanceRecorder performance = new PerformanceRecorder();
        
        
        System.out.print("\nMovies with the 'numVotes' from 30,000 to 40,000, both inclusively: ");
        
        long startTime = System.nanoTime();
        System.out.println(tree.rangeSearch(30000,40000));
        long endTime = System.nanoTime();

        // TODO: rangeSearch not counting all the levels?
        System.out.print("\nNo. of Index Nodes the process accesses: ");
        System.out.println(performance.getRangeNodeReads());

        // System.out.print("No. of Data Blocks the process accesses: ");
        
        // System.out.print("Average of 'averageRating's' of the records accessed: ");

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("\nRunning time of retrieval process: %d nanoseconds\n",duration);
        
        // System.out.print("No. of data blocks: ");
        
        // System.out.print("Running time of brute-force linear scan on: ");

    }

    public static void experimentFive(testBplusTree tree){
        System.out.println("\n----------------------EXPERIMENT 5-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        long startTime = System.nanoTime();
        //TODO: carry out deletion here
        long endTime = System.nanoTime();


        System.out.print("No. of Nodes in updated B+ tree: ");
        System.out.println(performance.getTotalNodes());

        // Only works if you call printBPlusTree() before this
        System.out.print("No. of Levels in updated B+ tree: ");
        System.out.println(performance.getTreeDegree());
        
        System.out.print("Content of the root node in updated B+ tree: ");
        System.out.println(testBplusTree.getRoot().keys);

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("\nRunning time of retrieval process: %d nanoseconds\n",duration);
        
        // System.out.print("No. of data blocks: ");
        
        // System.out.print("Running time of brute-force linear scan on: ");
    }

}