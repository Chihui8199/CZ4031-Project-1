package index;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import storage.Address;
import storage.Record;
import storage.Storage;

public class testBplusTree {

    static final int NODE_SIZE = 17;
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
        System.out.printf("\n\nSearching Key %d --------------------------------------------------------------------------------------------------------------------\n", key);
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

    private int checkForLowerbound(int key){

        // System.out.print("\nChecking Lowerbound of Key -> "+key);

        NonLeafNode node = (NonLeafNode)rootNode;
        boolean found = false;
        int lowerbound = 0;

        // loop from back to front to find the first key that is smaller than the key
        for (int i = node.getKeySize() - 1; i >= 0; i--){
            if (key >= node.getKeyAt(i)){
                node = (NonLeafNode)node.getChild(i+1);
                found = true;
                break;
            }
        }
        if (found == false && key < node.getKeyAt(0)) {node = (NonLeafNode)node.getChild(0);}
        // System.out.println(node.getKeys());

        // loop till get leftmost key
        while (!node.getChild(0).isLeaf()){
                node = (NonLeafNode)node.getChild(0);
            }

        lowerbound = node.getChild(0).getKeyAt(0);
        // System.out.print("\nReturning LowerBound -> "+ lowerbound);
        return(lowerbound);

    }

    /**
     * Wrapper function for deleting node
     *
     * @param key key to be deleted
     * @return AraryList of address to be removed from database
     */
    public ArrayList<Address> deleteKey(int key) {
        int lowerbound = 0;
        int index = 0;
        //Get child from lower bound of subtree
        
        // System.out.println("\n\n -----------------------------Deleting Key: "+ key + "--------------------------------");
        lowerbound = checkForLowerbound(key);
        // System.out.print("\nLowerbound is: "+ lowerbound);
        return (deleteNode(rootNode, null, -1, -1, key, lowerbound));
    }

    //recursive
    public ArrayList<Address> deleteNode(Node node, NonLeafNode parent, int parentPointerIndex, int parentKeyIndex,
            int key, int lowerbound) {
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

            //Update Key after deleting
            int ptrIdx = node.searchKey(key, true); 
            keyIdx = ptrIdx - 1;
            
            LeafNode LeafNode = (LeafNode) node;
            int newLowerBound = 0;

            // Get newLowerBound (possible for current key taken to be the lowerbound) if KeyIdx is not KeySize
            if (LeafNode.getKeySize() >= (keyIdx+1)) {
                // System.out.print("DELETING HERE");
                newLowerBound = lowerbound;
                List<Integer> keys = LeafNode.getKeys();
                LeafNode.updateKey(ptrIdx - 1, keys.get(0), false, newLowerBound);
                // keys.set(ptrIdx-1, keys.get(0));
            }
            else{
                
                // System.out.print("DELETING OVERHERE");
                newLowerBound = checkForLowerbound(LeafNode.getKey(keyIdx+1)); //Get new lowerbound
                List<Integer> keys = LeafNode.getKeys();
                LeafNode.updateKey(ptrIdx - 1, keys.get(0), true, newLowerBound);
            }

        } else {
            // traverse to leaf node to find records to delete
            NonLeafNode nonLeafNode = (NonLeafNode) node;
            int ptrIdx = node.searchKey(key, true); 
            int keyIdx = ptrIdx - 1;

            // read the next level node (read action will be recorded in the next level)
            Node next = nonLeafNode.getChild(ptrIdx);
            addOfRecToDelete = deleteNode(next, nonLeafNode, ptrIdx, keyIdx, key, lowerbound);

        }

        // carry out re-balancing tree magic if needed
        if (node.isUnderUtilized(NODE_SIZE)) {
            // System.out.print("\n\n------------------------Rebalancing tree now ---------------------------\n\n");
            handleInvalidTree(node, parent, parentPointerIndex, parentKeyIndex);
        }

        return addOfRecToDelete;
    }

    private void handleInvalidTree(Node underUtilizedNode, NonLeafNode parent, int parentPointerIndex,
            int parentKeyIndex) throws IllegalStateException {
        if (parent == null) {
            handleInvalidRoot(underUtilizedNode);
        } else if (underUtilizedNode.isLeaf()) {
            System.out.print("\n\nEntering rebalancing of Leaf node!!!");
            handleInvalidLeaf(underUtilizedNode, parent,
                    parentPointerIndex, parentKeyIndex);
        } else if (underUtilizedNode.isNonLeaf()) {
            System.out.print("\n\nEntering rebalancing of Non-leaf node!!!");
            handleInvalidInternal(underUtilizedNode, parent,
                    parentPointerIndex, parentKeyIndex);
        } else {
            throw new IllegalStateException("state is wrong!");
        }
    }

    public void handleInvalidRoot(Node underUtilizedNode){
        // handleInvalidNonLeaf(underUtilizedNode);
        if (underUtilizedNode.isLeaf()) { // Only node in B+ Tree - Root
            ((LeafNode) underUtilizedNode).clear();
            // System.out.print("There exist no B+ Tree now.\n");
        } else {
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
        int numChildrenOfNextParent = 0;
        int numChildrenOfNodeParent = 0;
        LeafNode nextNode;
        nextNode = null;
        // load the neighbors
        LeafNode underUtilizedLeaf = (LeafNode) underUtilizedNode;
        if (underUtilizedLeaf.getNext() != null){
            nextNode = (LeafNode) underUtilizedLeaf.getNext();
            if (nextNode.getParent() != null) {
            numChildrenOfNextParent = nextNode.getParent().getChildren().size();
            }
        }
        
        LeafNode prevNode = (LeafNode) underUtilizedLeaf.getPrevious();

        
        if (underUtilizedNode.getParent() != null) {
            numChildrenOfNodeParent = underUtilizedNode.getParent().getChildren().size();
        }
        
        

        if(nextNode == null && prevNode == null)
            throw new IllegalStateException("Both prevNode and nextNode is null for " + underUtilizedNode + "This is wrong!");
            // 1. Check if we can redistribute with next
            // 2. Check if we can redistribute with prev
            // 3. If neither the adjacent sibling can be used -> merge the two nodes -> Adjust the parent -> if parent is not fill recusively apply deletion algorithm
            // a. first try merge with right
            // b. second try merge with left
        System.out.println("--> handle invalid leaf");
        if (nextNode != null && nextNode.isAbleToGiveOneKey(NODE_SIZE)){
            System.out.print("Move one key from right to left");
            // handle invalid leaf: leaf to right
            moveOneKey(nextNode, underUtilizedLeaf, false, parent, parentKeyIndex+1);
        }
        else if (prevNode != null && prevNode.isAbleToGiveOneKey(NODE_SIZE)){
            System.out.print("Move one key from left to righ");
            //handle invalid leaf: right to left 
            moveOneKey(prevNode, underUtilizedLeaf, true, parent, parentKeyIndex);
        }
         // we can't redistribute, try merging with next
        else if((prevNode!=null && (prevNode.getKeySize()+underUtilizedLeaf.getKeySize())<=NODE_SIZE && (numChildrenOfNodeParent >= underUtilizedNode.getParent().getMinNonLeafNodeSize())))
        {
            // it's the case where split node is in the left from parent
            System.out.print("merge with left node");
            mergeLeafNodes(prevNode, underUtilizedLeaf, parent, parentPointerIndex, parentKeyIndex, false);
        }
        else if(nextNode!=null && (nextNode.getKeySize() + underUtilizedLeaf.getKeySize()) <= NODE_SIZE && (numChildrenOfNextParent >=  underUtilizedNode.getParent().getMinNonLeafNodeSize())) {
            // it's the case where under utilized node is the left node from parent
            System.out.print("merge with right node");
            mergeLeafNodes(underUtilizedLeaf, nextNode, parent,parentPointerIndex+1, parentKeyIndex+1, true);
        }
        else {
            throw new IllegalStateException("Can't have both leaf " +
                    "pointers null and not be root or no " +
                    "common parent");
        }
    }

    private void handleInvalidInternal(Node underUtilizedNode,
                                       NonLeafNode parent,
                                       int parentPointerIndex,
                                       int parentKeyIndex) throws IllegalStateException {

        // Node underUtilizedInternal = underUtilizedNode;
        Node underUtilizedInternal = underUtilizedNode;

        // load the adjacent nodes
        NonLeafNode prevInternal = null;
        NonLeafNode nextInternal = null;
        try{
            nextInternal = (NonLeafNode) parent.getChild(parentPointerIndex + 1);
        }catch(Exception e){
            System.out.print(e);
        }
        
        try {
            prevInternal = (NonLeafNode) parent.getChild(parentPointerIndex - 1);
        } catch(Exception e){
            System.out.print(e);
        }
        

        System.out.print(nextInternal + "/n and /n "+ prevInternal +"/n!!!");

        if (nextInternal == null && prevInternal == null)
            throw new IllegalStateException("Both prevInternal and nextInternal is null for " + underUtilizedNode);
        // check if we can redistribute with the next node
        // check if we can redistribute with the previous node
        if (prevInternal != null && prevInternal.isAbleToGiveOneKey(NODE_SIZE)) {
            System.out.print(nextInternal + "/n Move one key from left non leaf node /n ");
            moveOneKeyNonLeafNode(prevInternal, (NonLeafNode)underUtilizedInternal, true, parent, parentKeyIndex);
            
        }
        else if (nextInternal != null && nextInternal.isAbleToGiveOneKey(NODE_SIZE)) {
            System.out.print(nextInternal + "/n Move one key from right non leaf node /n ");
            moveOneKeyNonLeafNode(nextInternal, (NonLeafNode)underUtilizedInternal, false, parent, parentKeyIndex + 1);
            
        }
        // now, check if we can merge with the left node
        else if (prevInternal != null &&
                (underUtilizedInternal.getKeySize() + prevInternal.getKeySize()) <= NODE_SIZE) {
                    System.out.print(nextInternal + "/n Merge with left Non-Leaf node ");
                    mergeNonLeafNodes(prevInternal, (NonLeafNode)underUtilizedInternal, parent,
                    parentPointerIndex, parentKeyIndex, true);
                }
        // // check if we can merge with the right node
        else if (nextInternal != null &&
                (underUtilizedInternal.getKeySize() + nextInternal.getKeySize()) <= NODE_SIZE) {
                    System.out.print(nextInternal + "/n Merge with Right Non-Leaf Node /n ");
                    mergeNonLeafNodes((NonLeafNode)underUtilizedInternal, nextInternal, parent,
                    parentPointerIndex + 1, parentKeyIndex + 1, false);
        }
        else {
            throw new IllegalStateException("Can't merge or redistribute internal node " + underUtilizedInternal);
        }
    }

    private void moveOneKeyNonLeafNode(NonLeafNode giver, NonLeafNode receiver,
                                   boolean giverOnLeft, NonLeafNode parent,
                                   int inBetweenKeyIdx){
        // new_key and old_key all refers to the ones in parent key arrayList
        int key;
        // int newKey, oldKey = parent.getKeyAt(inBetweenKeyIdx);

        if(giverOnLeft) {
            System.out.print("Moving one key from Left non-leaf sibling");
            
            //Get last key from giver non leaf node to the receiver non leaf node
            //Remove last key from giver
            // int giverKey = giver.getLastKey();
            giver.removeKeyAt(giver.getKeySize()-1);
            // receiver.getKeys().add(0,giverKey);
            
            //Remove child from the giver node
            //Add child to the non leaf node
            Node nodeToMove = giver.getChild(giver.getKeySize()); //get last child of giver
            giver.removeChild(nodeToMove); 
            receiver.addChild(nodeToMove);
            receiver.getKeys().add(receiver.getKeySize(), receiver.getChild(1).getFirstKey());
            
            System.out.print("\n\n######!!!!!!:"+ receiver.getKeys());

            key = receiver.getKeyAt(0);
        }
        else {
            System.out.print("Moving one key from Right non-leaf sibling");
            
            //Get first key from giver non leaf node to the receiver non leaf node
            //Remove first key from giver
            giver.removeKeyAt(0);
            
            //Remove child from the giver node
            //Add child to the non leaf node
            Node nodeToMove = giver.getChild(0); //get first child of giver
            giver.removeChild(nodeToMove); 
            receiver.addChild(nodeToMove);
            
            receiver.getKeys().add(receiver.getKeySize(), receiver.getChild(1).getFirstKey());
            
            System.out.print("\n\n######:"+ receiver.getKeys());

            key = receiver.getKeyAt(0);
        }
        // in either case update the parent key

        //UpdateKey at higher levels with the correct lowerbound
        int ptrIdx = receiver.searchKey(key, true); 
        int keyIdx = ptrIdx - 1;
        
        NonLeafNode LeafNode = (NonLeafNode) receiver;
        System.out.println(key);
        int lowerbound = checkForLowerbound(key);

        int newLowerBound = 0;

        // Get newLowerBound (possible for current key taken to be the lowerbound) if KeyIdx is not KeySize
        if (LeafNode.getKeySize() >= (keyIdx+1)) {
            newLowerBound = lowerbound;
            System.out.print("\nnew LB: "+ newLowerBound);
        }
        else{
            newLowerBound = checkForLowerbound(LeafNode.getKey(keyIdx+1)); //Get new lowerbound
            System.out.print("New lower bound ????: "+ newLowerBound);
            parent.updateKey(inBetweenKeyIdx-1, key, false, checkForLowerbound(key));
        
        }   
        parent.replaceKeyAt(inBetweenKeyIdx, newLowerBound);

    }


    private void mergeNonLeafNodes(NonLeafNode nodeToMergeTo, NonLeafNode current, NonLeafNode parent,
                                int rightPointerIdx,
                                int inBetweenKeyIdx, boolean mergeWithLeft){
        /*
         * -Check if sibling node exists
            -add the keys from the current node to the keys of the previous sibling node
            -Remove children from current node
            -Add the children of the current node to the previous sibling node
            -Check if parent key satisfy min node size then remove current node from parent if it is empty
            -After merging, UpdateKey at higher levels with the correct lowerbound

         */
        int keyToRemove;
        

        // merge the right node to left
        if (mergeWithLeft){ 
            System.out.print("MErging with left, testest");
            int moveKeyCount = current.getKeySize();

            // System.out.print(current.getKeys());
            // keyToRemove = current.getFirstKey();
            keyToRemove = nodeToMergeTo.getChild(nodeToMergeTo.getKeySize()).getLastKey();

            // move every key from current node into nodeToMergeTo
            for (int i = 0; i < moveKeyCount; i++) {
                nodeToMergeTo.getKeys().add(nodeToMergeTo.getKeySize(),current.getKeyAt(i));
            }

            // move every child from current node into nodeToMergeTo
            for (int i= 0; i < current.getChildren().size(); i ++) {
                nodeToMergeTo.getChildren().add(current.getChild(i));
                System.out.print("\nCHildren : "+nodeToMergeTo.getChildren());
            }

            //Update parent after merging
            System.out.print("\n nodetomergeto Node Keys: " + nodeToMergeTo.getKeys());
            nodeToMergeTo.getKeys().add(nodeToMergeTo.getKeySize(),nodeToMergeTo.getChild(nodeToMergeTo.getKeySize()+1).getFirstKey());
            current.getParent().removeChild(current);
            
            
        } 
        
        // merge the left node with right
        else { 
            
            System.out.print("MErging with right, testest");
            int moveKeyCount = current.getKeySize();

            keyToRemove = current.getFirstKey();

            // move every key from current node into nodeToMergeTo
            for (int i = 0; i < moveKeyCount; i++) {
                nodeToMergeTo.getKeys().add(0,current.getKeyAt(i));
            }
            for (int i= 0; i < current.getChildren().size(); i ++) {
                nodeToMergeTo.getChildren().add(current.getChild(i));
                System.out.print("\nCHildren : "+nodeToMergeTo.getChildren());
            }

            // current.getParent().getKeys().remove(keyToRemove);

            //Update parent after merging
            System.out.print("\n nodetomergeto Node Keys: " + nodeToMergeTo.getKeys());
            nodeToMergeTo.getKeys().add(0,nodeToMergeTo.getChild(1).getFirstKey());
            current.getParent().removeChild(current);

        }

        // UpdateKey at higher levels with the correct lowerbound
        int ptrIdx = nodeToMergeTo.searchKey(keyToRemove, true); 
        int keyIdx = ptrIdx - 1;
        
        NonLeafNode LeafNode = (NonLeafNode) nodeToMergeTo;
        int lowerbound = checkForLowerbound(keyToRemove);
        int newLowerBound = 0;

        // Get newLowerBound (possible for current key taken to be the lowerbound) if KeyIdx is not KeySize
        if (LeafNode.getKeySize() >= (keyIdx+1)) {
            newLowerBound = lowerbound;
            System.out.print("New lowe bound : "+ newLowerBound);
        }
        else{
            newLowerBound = checkForLowerbound(LeafNode.getKey(keyIdx+1)); //Get new lowerbound
            
            System.out.print("New lowerrrrrr bound : "+ newLowerBound);
            parent.updateKey(inBetweenKeyIdx-1, keyToRemove, false, checkForLowerbound(keyToRemove));
        
        }   
    }


    private void mergeLeafNodes(LeafNode nodeToMergeTo, LeafNode current, NonLeafNode parent,
                                int rightPointerIdx, int inBetweenKeyIdx, boolean mergetoright){
        int removedKey = 0;
        int moveKeyCount = current.getKeySize();
        int NoOfChildren = current.getParent().getChildren().size();
        for (int i = 0; i < moveKeyCount; i++) {
            removedKey = current.removeKeyAt(0); 
            int leftLastIdx = nodeToMergeTo.getLastIdx(); 
            nodeToMergeTo.insertKeyAt(leftLastIdx+1,removedKey);
            // 2. Move over the records
            nodeToMergeTo.insertByRedistribution(removedKey, current.getAddressesForKey(removedKey));
            current.removeKeyInMap(removedKey);
            
        }

        parent.removeChild(current); //To remove the empty leaf node
        if ((parent.getChildren().size()) == (parent.getKeySize())){ 
            System.out.print("\nNo need to update parent\n");
        }
        else{
            
            parent.removeKeyAt(inBetweenKeyIdx);
        }
        
        
        if (mergetoright == true){
            // update the prev pointer of right next node (if any)
            if(current.getNext() != null) {
                LeafNode currentNext = current.getNext();
                currentNext.setPrevious(current.getPrevious());
            }

            nodeToMergeTo.setNext(current.getNext());
            if(current.getKeySize()==0){

                NonLeafNode currParent = current.getParent();
                System.out.print("currParent's keys " + currParent.getKeys());
                currParent.removeChild(current);
                currParent.removeKeyAt(0);
                System.out.print("currParent's keys after deletion" + currParent.getKeys());

                System.out.print("\nTrying to set parent\n");
                // current.setParent(current.getNext().getParent());
                
                //Check if parent key satisfy min node size
                // if ((currParent.getKeySize()>currParent.getMinNonLeafNodeSize())&&(currParent.getChildren().size()>current.getMinNonLeafNodeSize())){
                //     currParent.removeKeyAt(0);
                // }
                
                
            }
        }
        else{
            // update the prev pointer of left getprevious node (if any)
            
            if(current.getPrevious() != null) {
                LeafNode currentPrev= current.getPrevious();
                if (currentPrev!= null && (currentPrev.getPrevious()!= null)){
                    currentPrev.getPrevious().setPrevious(current.getPrevious());
                }
                
            }
            
            if(current.getNext()!= null){
                nodeToMergeTo.setNext(current.getNext());
                current.getNext().setPrevious(nodeToMergeTo);
            }
            if(current.getKeySize()==0 ){
                
                //currParent.removeChild(current);
                NonLeafNode currParent = current.getParent();
                System.out.print("\ncurrParent's keys " + currParent.getKeys() +current);
                currParent.removeChild(current);
                System.out.print("indx: "+ inBetweenKeyIdx);
                // if (currParent.getKeySize() > 0){
                if (inBetweenKeyIdx < 0){
                    currParent.removeKeyAt(inBetweenKeyIdx+1);
                    
                } 
                else if(currParent.getKeySize()>0){
                    
                    currParent.removeKeyAt(inBetweenKeyIdx);
                }
                else{
                    currParent.removeKeyAt(0);
                }
                
                System.out.print("\ncurrParent's keys after deletion>>" + currParent.getKeys());

                System.out.print("\nTrying to set parent\n");
                // if ((currParent.getKeySize()>currParent.getMinNonLeafNodeSize())&&(currParent.getChildren().size()>current.getMinNonLeafNodeSize())){
                //     if(inBetweenKeyIdx>=0){
                //         currParent.removeKeyAt(inBetweenKeyIdx);
                //     }
                // }
            }
            else{

                NonLeafNode currParent = current.getNext().getParent();
                currParent.removeChild(current);
                //Check if parent key satisfy min node size
                if ((currParent.getKeySize()>currParent.getMinNonLeafNodeSize())&&(currParent.getChildren().size()>current.getMinNonLeafNodeSize())){
                    currParent.removeKeyAt(0);

                }
            }
        }
        
        int lowerbound = checkForLowerbound(removedKey);
        System.out.print("\n\nUpdating key for merge nodes>>>"+ lowerbound);
        System.out.print("\n\nptrIdex?>>>"+ nodeToMergeTo.searchKey(removedKey, true));
        // int ptrIdx = nodeToMergeTo.searchKey(removedKey, true); 
        // int keyIdx = ptrIdx - 1;
        
        LeafNode LeafNode = (LeafNode) current;
        int newLowerBound = 0;
        System.out.print("\n\nUpdating  for merge nodes>>>"+ current.getParent().getKeySize());

        // keyIndex = 
        // Get newLowerBound (possible for current key taken to be the lowerbound) if KeyIdx is not KeySize
        if (current.getParent().getKeySize() >= NoOfChildren) { //check if number of children == original # of children
            System.out.print("Entered here");
            newLowerBound = lowerbound;
        }
        else{
            // System.out.print("\nentered else statement:" + current.getParent().getFirstKey());
            // newLowerBound = checkForLowerbound(current.getParent().getFirstKey()); //Get new lowerbound
            newLowerBound=current.getParent().getChild(0).getFirstKey();

            System.out.print("\nNew lower bound is" +newLowerBound);
            // current.getParent().updateKey( inBetweenKeyIdx , parent.getKey(0), true, lowerbound);
            if(inBetweenKeyIdx==0){
                // if (mergetoright){
                //     current.getNext().getParent().updateKey( inBetweenKeyIdx, removedKey, true, newLowerBound);
                // }
                // else{
                //     // current.getPrevious().getParent().updateKey( inBetweenKeyIdx, removedKey, true, newLowerBound);
                // }
                
            } else{
                current.getParent().updateKey( inBetweenKeyIdx -1, newLowerBound, true, newLowerBound);
            }
            
        
        }   
        
    }



    private void moveOneKey(LeafNode giver, LeafNode receiver,
                            boolean giverOnLeft, NonLeafNode parent,
                            int inBetweenKeyIdx){
        int key;
        //true --> giver on left
        if (giverOnLeft) {
            System.out.println("--> move the key from left node to right");
            // move the key from left node to right
            // 1. Move and edit map records
            int giverKey = giver.getLastKey();
            receiver.insertByRedistribution(giverKey, giver.getAddressesForKey(giverKey));
            giver.removeKeyInMap(giverKey);
            System.out.print("\n\nGiver : "+ giverKey);
            // 2. Move and edit key in node
            receiver.insertKeyAt(0, giverKey);
            giver.removeKeyAtLast();
            // key = receiver.getKeyAt(0);
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
            // key = receiver.getKeyAt(0);
            
        }
       
        
        // Update receiver parent
        if (inBetweenKeyIdx == -1){
            System.out.print("\nDo not update parent");
        }
        else if (inBetweenKeyIdx>=0) {
            if(parent.getKeySize() == inBetweenKeyIdx){ 
                parent.replaceKeyAt(inBetweenKeyIdx-1, key);

                
                int lastParentChild = receiver.getParent().getKeys().size()-1;//point to last child
                int lastParentChildKey = receiver.getParent().getChild(receiver.getParent().getKeys().size()).getFirstKey();
                if (giver.getParent().getChild(giver.getParent().getChildren().size()-1).getFirstKey() != key){
                    receiver.getParent().replaceKeyAt( lastParentChild, lastParentChildKey);
                }
            }
             else{
                parent.replaceKeyAt(inBetweenKeyIdx, key);
                
                //if giver is from the same parent, update parent @ index+1 with firstkey
                if (giver.getParent().getChild(inBetweenKeyIdx+1).getFirstKey() != key){
                    giver.getParent().replaceKeyAt(inBetweenKeyIdx, giver.getParent().getChild(inBetweenKeyIdx+1).getFirstKey());
                }
            }

        }
        
        else{
            parent.replaceKeyAt(inBetweenKeyIdx-1, key);
        }
        
        int ptrIdx = receiver.searchKey(key, true); 
        int keyIdx = ptrIdx - 1;
        
        LeafNode LeafNode = (LeafNode) receiver;
        int lowerbound = checkForLowerbound(key);
        int newLowerBound = 0;

        // Get newLowerBound (possible for current key taken to be the lowerbound) if KeyIdx is not KeySize
        if (LeafNode.getKeySize() >= (keyIdx+1)) {
            newLowerBound = lowerbound;
        }
        else{
            newLowerBound = checkForLowerbound(LeafNode.getKey(keyIdx+1)); //Get new lowerbound
            parent.updateKey(inBetweenKeyIdx-1, parent.getChild(inBetweenKeyIdx).getFirstKey(), false, checkForLowerbound(key));
        
        }   
        
        // parent.updateKey(inBetweenKeyIdx-1, parent.getChild(inBetweenKeyIdx).getFirstKey(), false, checkForLowerbound(key));
        
    
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
                    if (leaf.getNext() == null) break; // if not just break the loop
                    // Traverse to the next node and start searching from index 0 within the next
                    // node again
                    leaf = (LeafNode) (leaf.getNext());
                    PerformanceRecorder.addOneRangeNodeReads();

                    ptrIdx = 0;
                    if (ptrIdx >= leaf.getKeySize())
                        throw new IllegalStateException("Range search found a node with 0 keys");
                }
                if (leaf.getKey(ptrIdx) > maxKey) break;
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

        }

    }

    private void countLevel(Node node) {
        while (!node.isLeaf()) {
            NonLeafNode nonLeaf = (NonLeafNode) node;
            node = nonLeaf.getChild(0);
            PerformanceRecorder.addOneTreeDegree();
        }
        PerformanceRecorder.addOneTreeDegree();
    }

    public static void experimentTwo(testBplusTree tree) {
        System.out.println("\n----------------------EXPERIMENT 2-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.println("Parameter n: " + NODE_SIZE);
        System.out.printf("No. of Nodes in B+ tree: %d\n", performance.getTotalNodes());
        tree.countLevel(tree.getRoot());
        System.out.printf("No. of Levels in B+ tree: %d\n", performance.getTreeDegree());
        System.out.println("Content of the root node: " + testBplusTree.getRoot().keys);
    }

    public static void experimentThree(Storage db, testBplusTree tree) {
        System.out.println("\n\n----------------------EXPERIMENT 3-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.println("Movies with the 'numVotes' equal to 500: ");

        long startTime = System.nanoTime();
        ArrayList<Address> resultAdd = tree.searchKey(500);
        long endTime = System.nanoTime();
        int totalAverageRating = 0;
        int totalCount = 0;
        ArrayList<Record> results = new ArrayList<>();
        if (resultAdd != null) {
            for (Address add : resultAdd) {
                Record record = db.getRecord(add);
                System.out.print("\n" + record);
                results.add(record);
                totalAverageRating += record.getAverageRating();
                totalCount++;
            }
        }
        System.out.printf("\n\nNo. of Index Nodes the process accesses: %d\n", performance.getNodeReads());
        System.out.printf("No. of Data Blocks the process accesses: %d\n", db.getBlockAccesses());
        System.out.printf("Average of 'averageRating's' of the records accessed: %.2f\n", (double) totalAverageRating / totalCount);
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("Running time of retrieval process: %d nanoseconds\n", duration);
        System.out.println("Number of Data Blocks Accessed by Brute Force (numVotes = 500):");
        System.out.println(db.getBlocksAccessedByForce(500, 500));
        System.out.printf("\nNo. of Data Blocks accessed reduced in total: %d\n ", db.getBlockAccessReduced());
    }

    public static void experimentFour(Storage db, testBplusTree tree) {
        System.out.println("\n\n----------------------EXPERIMENT 4-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.println("Movies with the 'numVotes' from 30,000 to 40,000, both inclusively: ");
        long startTime = System.nanoTime();
        ArrayList<Address> resultAdd = tree.rangeSearch(30000, 40000);
        long endTime = System.nanoTime();
        int totalAverageRating = 0;
        int totalCount = 0;
        ArrayList<Record> results = new ArrayList<>();
        if (resultAdd != null) {
            for (Address add : resultAdd) {
                Record record = db.getRecord(add);
                System.out.print("\n" + record);
                results.add(record);
                totalAverageRating += record.getAverageRating();
                totalCount++;
            }
        }
        System.out.printf("\n\nNo. of Index Nodes the process accesses: %d\n", performance.getRangeNodeReads());
        System.out.printf("No. of Data Blocks the process accesses: %d\n", db.getBlockAccesses());
        System.out.printf("Average of 'averageRating's' of the records accessed: %.2f", (double) totalAverageRating / totalCount);
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("\nRunning time of retrieval process: %d nanoseconds\n", duration);
        System.out.println("Number of Data Blocks Accessed by Brute Force (30000<=numVotes<=40000): %d");
        System.out.println(db.getBlocksAccessedByForce(30000, 40000));
        System.out.printf("\nNo. of Data Blocks accessed reduced in total: %d\n ", db.getBlockAccessReduced());
    }

    public static void experimentFive(Storage db, testBplusTree tree) {
        System.out.println("\n\n----------------------EXPERIMENT 5-----------------------");
        PerformanceRecorder performance = new PerformanceRecorder();
        System.out.println("-- Deleting all records with 'numVotes' of 1000 -- ");
        long startTime = System.nanoTime();
        tree.deleteKey(1000);
        long endTime = System.nanoTime();
        System.out.printf("No. of Nodes in updated B+ tree: %d\n", performance.getTotalNodes());
        System.out.printf("No. of Levels in updated B+ tree: %d\n", performance.getTreeDegree());
        System.out.printf("Content of the root node in updated B+ tree: %s\n", testBplusTree.getRoot().keys);
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.printf("Running time of retrieval process: %d nanoseconds\n", duration);
        System.out.println("Number of Data Blocks Accessed by Brute Force (numVotes=10000): %d");
        System.out.println(db.getBlocksAccessedByForce(1000, 1000));
        System.out.printf("\nNo. of Data Blocks accessed reduced in total: %d\n ", db.getBlockAccessReduced());
    }

}