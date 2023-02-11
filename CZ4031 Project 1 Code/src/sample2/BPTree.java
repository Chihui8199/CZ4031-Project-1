package b_plus_tree;
//reference: https://github.com/andylamp/BPlusTree/blob/master/src/main/java/ds/bplus/bptree/BPlusTree.java

import physical_storage.Pointer;

import java.util.ArrayList;

public class BPTree {
    private final int keyLimit; // member to record key limit of current tree
    private Node root;  // member to record root
    private Node toBeSplitNode;  // used to contain splitting node in insertion of keys
    private final MainMemory mainMemory; // mimic storage & retrieval of index nodes
    private final PerformanceRecorder recorder; // to record the event in the trees

    /**
     *
     * @param keyLimit define the keyLimit of each node in the current tree
     */
    public BPTree(int keyLimit) {
        // create a new memory object
        mainMemory = new MainMemory();
        recorder = new PerformanceRecorder();

        // create a new root
        root=new LeafNode(NodeType.ROOT_LEAF, -1, -1);
        root.setBlockIndex(mainMemory.addBlock(root));
        recorder.addOneLeafNode();
        recorder.addOneTreeDegree();

        this.keyLimit = keyLimit;
    }

    /**
     *
     * @param key the key to be inserted into the b+ tree
     * @param pointer the record pointer of key on the disk
     * @param unique if true, do not insert record if key is duplicated
     *               if false, insert record to LeafNode's duplicates hashmap
     */

    public void insertKey(int key, Pointer pointer, boolean unique){
        // check if our root is full
        insertSplit(root, null, 0,key, pointer, unique);
        recorder.addOneInsertions();
    }

    /**
     * A function to split the root when it is full,
     * create a new internal node and use it to update BPTree's root.
     */
    private void handleFullRoot(){

        toBeSplitNode = this.root;
        // create a new internal node with one level higher than root
        int childLevel = this.root.isLeaf()? 1 : ((InternalNode)root).getChildLevel() + 1;
        recorder.addOneTreeDegree();
        InternalNode newRoot = new InternalNode(NodeType.ROOT_INTERNAL, childLevel);
        recorder.addOneInternalNode();

        //add the old root block index as the first pointer
        newRoot.insertPointerAt(0, toBeSplitNode.getBlockIndex());
        newRoot.setBlockIndex(mainMemory.addBlock(newRoot));
        recorder.addOneInsertionWrites();

        this.root = newRoot;
        // split the old root
        splitNode(newRoot, 0);  // insertion writes incremented in the splitNode function
    }

    /**
     * Recursive function. Base case: leaf node insertion
     * always split first and handle split after insertion
     * @param node      base node for searching (typically root)
     * @param key       the key to be inserted
     * @param pointer   pointer of the key
     * @param unique    if true, duplicated key won't be inserted
     */
    private void insertSplit(Node node, InternalNode parentNode, int lstIdx, int key, Pointer pointer, boolean unique){
        int upperIndex = node.searchKey(key, true); //always search for the upperBound
        recorder.addOneInsertionReads();
        // check if we have a leaf
        if (node.isLeaf()) {
            recorder.addOneLeafNodeReads();
            LeafNode leaf = (LeafNode) node;
            //
            int prevKey = upperIndex-1 < 0 ? 0 :
                    (upperIndex >= node.getKeyCount() ? node.getKeyCount()-1 : upperIndex-1);
            if (node.getKeyCount() > 0 && node.getKeyAt(prevKey) == key){
                if(!unique){ // if records doesn't need to be unique, insert a duplicate
                    leaf.insertOneDuplicate(key, pointer);
                }
                return;
            }
            // we have a new key insert
            leaf.insertPointerAt(upperIndex, pointer);
            leaf.insertKeyAt(upperIndex, key);
            // commit changes
            mainMemory.overWriteNode(leaf);
            recorder.addOneInsertionWrites();   // for standard insertion write
            recorder.addOneLeafNodeWrites();

            if (leaf.needSplit(keyLimit)){  // recorders not needed in this part
                if (leaf.isRoot())          // because everything is taken care of inside splitNode function
                    handleFullRoot();
                else{
                    toBeSplitNode = leaf;
                    splitNode(parentNode, lstIdx);
                }
                recorder.addOneLeafSplits();
            }

        } else {

            // Suppose we are searching for key 3
            //
            // 1. searchKey function will return us index 1
            // (i.e. the index of upper limit of key 3 (5) )
            //
            // 2. following the index, we retrieve the pointer to node [3,5)
            // by calling parentNode.getPointerAt(upperIndex)
            //
            // 3. with the pointer, we can retrieve the block from mainMem
            // using the blockIndex (the pointer) returned in last function
            // If no node needs splitting along the way, we follow the index
            // till we reach the leaf node
            //
            //              parentNode
            //        3         5         7
            // [1,3)     [3,5)     [5,7)     [7,10)

            //recorder.addOneInternalNodeReads();
            InternalNode internal = (InternalNode) node;    // cast to internalNode
            toBeSplitNode = mainMemory.getBlockAt(internal.getPointerAt(upperIndex));   //retrieve next node
            insertSplit( toBeSplitNode, internal, upperIndex, key, pointer, unique);    //traverse to the next level

            // after lower node insert & split, check if this node needs splitting
            if (internal.needSplit(keyLimit)){
                if(internal.isRoot())
                    handleFullRoot();
                else {
                    toBeSplitNode = internal;
                    splitNode(parentNode, lstIdx);
                }
            }
        }
    }

    /**
     * When the split happens, toBeSplitNode should have keyLimit + 1 keys.
     * keys are moved from toBeSplitNode to newly created node
     * afterwards, the first key of the right node between the two nodes
     * is popped and inserted to parent to
     *
     * @param parentNode     parent of the toBeSplitNode
     * @param parentPointerIndex index of the pointer directing to toBeSplitNode in parentNode's pointer arrays
     * <p>
     * Input Example:
     *                        parentNode
     *         [ptr]    key     [ptr]    key    [ptr]
     *        (index)
     * -----------------------------------------------------------------
     *    full child node    normal child     normal child
     *      (toBeSplit)
     *
     * <p>
     */
    private void splitNode(InternalNode parentNode, int parentPointerIndex) {

        int moveKeyCount = (keyLimit + 1) / 2;

        if (toBeSplitNode.isInternal()) {

            // create a new node as the right node of the full node
            InternalNode rightInternal = (InternalNode) toBeSplitNode;
            InternalNode leftInternal = new InternalNode(NodeType.INTERNAL, rightInternal.getChildLevel());
            leftInternal.setBlockIndex(mainMemory.addBlock(leftInternal));
            recorder.addOneInternalNode();

            int i;
            //image two nodes (last pointer ignored as it is not implemented for now
            //                parentNode
            //         [0,5)    5   [5,10)
            //      left(new)          right(full)
            // [0,1)1[1,3)3[3,5)       [5,8)8[8,10)
            // initial structure
            //                      parentNode
            //          [0,10)
            //----------------------------------------
            //      toBeSplitNode
            // [0,1)1[1,3)3[3,5)5[5,8)8[8,10)
            for (i = 0; i < moveKeyCount; i++) {
                leftInternal.insertKeyAt(i, rightInternal.removeKeyAt(0));
                leftInternal.insertPointerAt(i, rightInternal.removePointerAt(0));
            }
            //after loop (incorrect structure)
            //                parentNode
            //                          [0,10)
            //      left(new)        right(toBeSplit)
            // [0,1)1[1,3)3         [3,5)5[5,8)8[8,10)
            // Need to move the right node's first pointer to left node's end
            leftInternal.insertPointerAt(i, rightInternal.removePointerAt(0));
            //after moving the rightInternal Pointer
            //                parentNode
            //                          [0,10)
            //      left(new)        right(toBeSplit)
            // [0,1)1[1,3)3[3,5)        5[5,8)8[8,10)
            // Need to move the tight node's key up to parent's key, and act as the
            // indicator of right node's lower bound

            // update pointer at parentNode_{index+1}
            parentNode.insertPointerAt(parentPointerIndex, leftInternal.getBlockIndex());
            // update key value at n[index]
            parentNode.insertKeyAt(parentPointerIndex, rightInternal.removeKeyAt(0));
            // update toBeSplitNode.

            // Cleaning up:
            // if the full node was  root, invalidate it and make it a regular internal node
            if (rightInternal.isRoot()) {
                rightInternal.setNodeType(NodeType.INTERNAL);
                recorder.addOneRootSplits();
            }
            recorder.addOneInternalNodeSplits();    // increment node split after it's done

            // write the changes we make to the node back to mainMemory
            mainMemory.overWriteNode(rightInternal);
            mainMemory.overWriteNode(leftInternal);
            mainMemory.overWriteNode(parentNode);

            // record the write back action
            recorder.addToInternalNodeWrites(3);
            recorder.addToInsertionNodeWrites(3);

        }
    
        // we have a leaf, create a node to the right of the splitNode
        // to avoid the situation where the childToBeSplit is the first
        // pointer, and we need to check whether the key needs updating
        else {
            //                              parentNode
            //           [1,7)                   7           [7, ...)
            // -----------------------------------------------------------
            //      childToBeSplit(full)                anotherChild (not full)
            // [p] 1 [p] 2 [p] 3 [p] 4 [p] 5          [p] 7 [p] ...
            LeafNode leftLeaf = (LeafNode) toBeSplitNode;
            // create a new leftLeafNode with nextPointer set to be rightLeaf block Index
            // and previous pointer the rightLeafPrev
            LeafNode rightLeaf = new LeafNode(NodeType.LEAF,
                    leftLeaf.getBlockIndex(),leftLeaf.getNextNodePointer());
            rightLeaf.setBlockIndex(mainMemory.addBlock(rightLeaf));
            recorder.addOneLeafNode();
            // update the next pointer in left node
            leftLeaf.setNextNodePointer(rightLeaf.getBlockIndex());
            // update previous pointer of the node after inserted node
            if (rightLeaf.getNextNodePointer() != -1){
                LeafNode rightNext = (LeafNode) mainMemory.getBlockAt((rightLeaf.getNextNodePointer()));
                rightNext.setPrevNodePointer(rightLeaf.getBlockIndex());
                mainMemory.overWriteNode(rightNext);
                recorder.addOneInsertionReads();
                recorder.addOneInsertionWrites();
                recorder.addOneLeafNodeReads();
                recorder.addOneLeafNodeWrites();
            }

            for (int i = 0; i < moveKeyCount; i++) {
                // move over the key
                int removedKey = leftLeaf.removeKeyAtLast();
                rightLeaf.insertKeyAt(0, removedKey);
                // move over the duplicates of keys
                if(leftLeaf.hasDuplicates(removedKey))
                    rightLeaf.insertAllDuplicates(removedKey, leftLeaf.removeAllDuplicates(removedKey));
                // move over the pointer
                rightLeaf.insertPointerAt(0, leftLeaf.removePointerAtLast());
            }
            //                              parentNode
            //                  [1,7)                   7           [7, ...)
            // -----------------------------------------------------------
            // leftNode(have pointer)        rightNode                anotherChild (not full)
            // [p] 1 [p] 2 [p] 3    <--->   [p] 4 [p] 5          [p] 7 [p] ...
            // update pointer at n_{index+1}

            parentNode.insertPointerAt(parentPointerIndex+1, rightLeaf.getBlockIndex());
            // update key value at n[index]
            parentNode.insertKeyAt(parentPointerIndex, rightLeaf.getKeyAt(0));
            // adjust capacity

            // Clean up:
            // if it was the root, invalidate it and make it a regular leaf
            if (leftLeaf.isRoot()) {
                leftLeaf.setNodeType(NodeType.LEAF);
                recorder.addOneRootSplits();
            }
            recorder.addOneLeafSplits();
            // commit node changes
            mainMemory.overWriteNode(leftLeaf);
            mainMemory.overWriteNode(rightLeaf);
            mainMemory.overWriteNode(parentNode);

            // record the actions
            recorder.addToLeafNodeWrites(2);
            recorder.addOneInternalNodeWrites();
        }
    }


    // ====== SINGLE SEARCH KEY ======= //
     /**
     * function to search for a key inside B+tree.
     *  * unique flag true: return the *first* record pointer whose key matches the given key
     *  * unique flag false: return *all* record pointer whose keys match the given key
     *
     * @param key key to match
     * @param unique return *all* matching record pointers or the *first* found one
     * @return an array of pointers that contains all the matching records.
     * null if no key matches
     */
    @SuppressWarnings("unused")
    public ArrayList<Pointer> searchKey(int key, boolean unique, boolean printTrace) {
        recorder.addOneSearches();
        return(searchKey(this.root, key, unique, printTrace));
    }

    /**
     * The true searchKey recursive function to return the result.
     * @param node the current node for searching
     * @param key the target value that we are searching for
     * @param unique unique is a boolean to determine whether the result
     *              includes duplicates
     * @return an arraylist of record pointers. if no key matches, return null
     */
    private ArrayList<Pointer> searchKey(Node node, int key, boolean unique, boolean printTrace) {
        int ptrIdx;
        recorder.addOneSearchReads();
        if(printTrace)
            System.out.println(node);

        if (node.isLeaf()) {
            // If key is within the actual leaf node
            ptrIdx = node.searchKey(key, false);
            recorder.addOneLeafNodeReads();

            if (ptrIdx >= 0 && ptrIdx < node.getKeyCount() && key == node.getKeyAt(ptrIdx)) {
                // key is found, depending on the duplicate flag, find pointers accordingly.
                ArrayList<Pointer> resultList = new ArrayList<>(); // create a new arraylist of pointer
                resultList.add(((LeafNode) node).getPointerAt(ptrIdx)); // always add in the pointer in pointers list
                if (!unique) {
                    LeafNode leaf = (LeafNode) node;
                    if (leaf.hasDuplicates(leaf.getKeyAt(ptrIdx))) // if there are duplicates
                        // Add all duplicates from the hashMap into resultList
                        resultList.addAll(leaf.getAllDuplicates(leaf.getKeyAt(ptrIdx)));
                }
                return resultList;
            }
            return null;
        }
        // If it's an internal node, descend until we reach a leaf
        else {
            ptrIdx = node.searchKey(key, true); // find the upperbound of key
            recorder.addOneInternalNodeReads();

            // Descend into leaf node
            InternalNode internalNode = (InternalNode) node;
            Node childNode = mainMemory.getBlockAt(internalNode.getPointerAt(ptrIdx));
            return (searchKey(childNode, key, unique, printTrace));
        }
    }

    // ======== RANGE SEARCH ========= // 
    
    /**
     * function to search for a range of keys [minKey, maxKey] inside B+tree.
     * both boundaries are inclusive
     *  * unique flag true: return the *first* record pointer whose key falls within the range
     *  * unique flag false: return *all* record pointer whose keys fall within the range
     *
     * @param minKey min key of the range (inclusive)
     * @param maxKey max key of the range (inclusive)
     * @param unique return only *first* encounter of the pointer
     *               return *all* copies
     * @return an arraylist of pointers, null if no match was found
     */
    @SuppressWarnings("unused")
    public ArrayList<Pointer> rangeSearch(int minKey, int maxKey, boolean unique, boolean printTrace) {
        recorder.addOneRangeQueries();
        return rangeSearch(minKey, maxKey, this.root, unique, printTrace);
    }

    /**
     *
     * @param minKey min key of the range (inclusive)
     * @param maxKey max key of the range (inclusive)
     * @param node currently searching node; if is internal, recursive call
     *             if is leaf, search for keys
     * @param unique return whether the *first* or *all* pointers to a certain key
     *               within the [minKey, maxKey] range
     * @return an arraylist of pointers, null if no match was found
     */
    private ArrayList<Pointer> rangeSearch(int minKey, int maxKey, Node node, boolean unique, boolean printTrace)
    throws IllegalStateException{
        int ptrIdx;
        ArrayList<Pointer> resultList = new ArrayList<>();
        recorder.addOneRangeQueryReads();
        if(printTrace)
            System.out.println(node);

        if(node.isLeaf()){
            ptrIdx = node.searchKey(minKey, false); //if minKey is in key array, get key index
            recorder.addOneLeafNodeReads();

            LeafNode leaf = (LeafNode)node;
            while (true){
                if(ptrIdx == leaf.getKeyCount()) {
                    // check if we have a next node to load. Assuming NextNode Pointer returns -1 if there is no more nodes.
                    if(leaf.getNextNodePointer() < 0)
                        break; // if not just break the loop
                    // Traverse to the next node and start searching from index 0 within the next node again
                    leaf = (LeafNode)mainMemory.getBlockAt(leaf.getNextNodePointer());
                    recorder.addOneRangeQueryReads();
                    recorder.addOneLeafNodeReads();
                    ptrIdx = 0;
                    if(ptrIdx >= leaf.getKeyCount())
                        throw new IllegalStateException("Range search found a node with 0 keys");
                    // if(printTrace)
                    //     System.out.println(leaf);
                }
                if(leaf.getKeyAt(ptrIdx) > maxKey)
                    break;
                resultList.add(leaf.getPointerAt(ptrIdx));
                if(!unique) {
                    if (leaf.hasDuplicates(leaf.getKeyAt(ptrIdx)))
                        // Add all duplicates from the hashMap into resultList
                        resultList.addAll(leaf.getAllDuplicates(leaf.getKeyAt(ptrIdx)));
                }
                ptrIdx++;
            }
            return (resultList.size()>0 ? resultList : null);
        }
        else {
            ptrIdx =  node.searchKey(minKey, true);
            recorder.addOneInternalNodeReads();
            // Descend into leaf node
            InternalNode internalNode = (InternalNode) node;
            Node childNode = mainMemory.getBlockAt(internalNode.getPointerAt(ptrIdx));
            return(rangeSearch(minKey, maxKey, childNode, unique, printTrace));
        }
    }

    /**
     * function that deletes a certain key if it is in the tree
     * @param key the key of which the user wants to delete
     * @param unique if true, delete the *first* pointer in the tree
     *               else, delete *all* pointers of key
     * @return an arraylist of pointers that was deleted from the tree
     * null if no key match was found
     */
    @SuppressWarnings("unused")
    public ArrayList<Pointer> deleteKey(int key, boolean unique) {
        recorder.addOneDeletions();
        return (deleteKey(root, null, -1, -1, key, unique));
    }

    /**
     * the true delete key function that handles merges or redistribution
     * of tree after deletion
     * @param node currently handling node (if is internal, call recursive)
     *             (if is leaf, search & delete)
     * @param parent parent node of the current node (null if the <code>node</code> is root)
     * @param parentPointerIndex the index of the pointer in the parent pointer list that
     *                           points to the node. e.g. :
     *                                           parent
     * pass in index of this pointer  -->  [p1]   key   [p2]
     *                                     node      some other node
     *
     * @param parentKeyIndex the index of the key in the parent key list that corresponds
     *                       to the lower bound of current node. e.g. :
     *                                           parent
     * pass in index of this key --> key1  [p1]   key2   [p2]
     *                                     node       some other node
     *
     * @param key target of delete
     * @param unique delete the *first* copy of pointer if unique is true
     *               *all* copies if false
     * @return an arraylist of pointers that was deleted from the tree
     * null if no key match was found
     */

    private ArrayList<Pointer> deleteKey(Node node, InternalNode parent,
                                         int parentPointerIndex, int parentKeyIndex,
                                         int key, boolean unique) {

        ArrayList<Pointer> pointers = new ArrayList<>();
        recorder.addOneDeletionReads();
        if (node.isLeaf()) {
            recorder.addOneLeafNodeReads();
            LeafNode leaf = (LeafNode) node;
            int keyIdx= node.searchKey(key, false); // search for the key
            // check if we actually found the key
            if ((keyIdx == leaf.getKeyCount()) || (key != leaf.getKeyAt(keyIdx)))
                return null;

            if (!unique){// delete all records
                if (leaf.hasDuplicates(key))
                    pointers.addAll(leaf.removeAllDuplicates(key));
                pointers.add(leaf.removePointerAt(keyIdx));
                leaf.removeKeyAt(keyIdx);
            }
            else{ // if unique is true, delete only the first copy
                if(leaf.hasDuplicates(key)) {
                    // if there are duplicates, delete only the pointer inside pointer list
                    pointers.add(leaf.getPointerAt(keyIdx));    //get copy from
                    Pointer newPointer = leaf.removeFirstDuplicate(key);
                    leaf.replacePointerAt(keyIdx, newPointer);
                }
                else{
                    pointers.add(leaf.removePointerAt(keyIdx));
                    leaf.removeKeyAt(keyIdx);
                }
            }
            mainMemory.overWriteNode(leaf);
            recorder.addToDeletionWrites(1);
            recorder.addOneLeafNodeWrites();
        }
        // check if it's an internal node
        else { //if (current.isInternal()), traverse is still needed
            recorder.addOneInternalNodeReads();

            InternalNode internal = (InternalNode) node;
            int ptrIdx = node.searchKey(key, true);
            int keyIdx = ptrIdx - 1;
            // read the next level node (read action will be recorded in the next level)
            Node next = mainMemory.getBlockAt(internal.getPointerAt(ptrIdx));
            pointers = deleteKey(next, internal, ptrIdx, keyIdx, key, unique);
        }
        // check if the current node is still valid after deletion
        if (node.isUnderUtilized(keyLimit)) {
            handleInvalidNodes(node, parent, parentPointerIndex, parentKeyIndex);
        }
        return pointers;
    }
    /**
     * This function handles the merging or redistribution of the nodes depending
     * on the keyLimit; if possible, we just redistribute the keys, if not we merge.
     *
     * @param underUtilizedNode current node, when passed in, should be underutilized.
     *                         (can be both leaf or internal node)
     * @param parent parent of current node, *always* an internal node
     *               (or null if the redistributed node is root)
     * @param parentPointerIndex index of the pointer to underUtilizedNode
     *                           in parent pointer array
     * @param parentKeyIndex index of the key who is the lowerBound of the
     *                       underUtilizedNode in parent's key array
     * @throws IllegalStateException is thrown when the tree is not ready
     */
    private void handleInvalidNodes(Node underUtilizedNode, InternalNode parent,
                                   int parentPointerIndex, int parentKeyIndex)
            throws IllegalStateException {

        // that index should not be present
        if(parent != null && parentPointerIndex < 0) {
            throw new IllegalStateException("index < 0");
        }
        // this is the only case that the tree shrinks, from the root.
        if(parent == null) {
            handleInvalidRoot(underUtilizedNode);
        }
        else if(underUtilizedNode.isLeaf()) {
            handleInvalidLeaf(underUtilizedNode, parent,
                    parentPointerIndex, parentKeyIndex);
        }
        else if(underUtilizedNode.isInternal()) {
            handleInvalidInternal(underUtilizedNode, parent,
                    parentPointerIndex, parentKeyIndex);
        }
        else {
            throw new IllegalStateException("Read unknown or overflow page while merging");
        }
    }

    /**
     * Handle the root node cases (leaf and internal node)
     *
     * @param underUtilizedRoot root node that's under utilized
     */
    private void handleInvalidRoot(Node underUtilizedRoot) {
        if (underUtilizedRoot.isLeaf()) {
            System.out.println("Root is leaf, no need to change.");
            return;
        }
        InternalNode rootInternal = (InternalNode) underUtilizedRoot; //cast the root
        // load first 2 children
        Node onlyChild = mainMemory.getBlockAt(rootInternal.getPointerAt(0));
        recorder.addOneDeletionReads();
        if (onlyChild == null )
            throw new IllegalStateException("Child of root is null");
        // check their type
        if (onlyChild.isLeaf()){
            recorder.addOneLeafNodeReads();
            onlyChild.setNodeType(NodeType.ROOT_LEAF);
            recorder.addOneLeafNodeWrites();    //for rewriting the node
        }
        else{
            recorder.addOneInternalNodeReads();
            onlyChild.setNodeType(NodeType.ROOT_INTERNAL);
            recorder.addOneInternalNodeWrites();
        }
        mainMemory.removeBlockAt(underUtilizedRoot.getBlockIndex());
        mainMemory.overWriteNode(onlyChild);
        recorder.addToDeletionWrites(2);
        recorder.deleteOneInternalNode();
        recorder.deleteOneTreeDegree();

        this.root = onlyChild;
    }


    /**
     * Handle the leaf section of the redistribution/merging
     *
     * @param underUtilizedNode the leaf node to process
     * @param parent the parent node of current leaf node
     * @param parentPointerIndex index of pointer in parent's pointer list
     *                           that leads to the underUtilizedNode
     * @param parentKeyIndex index of which key that is the lower bound of
     *                       underutilizedNode in parent's key list
     */
    private void handleInvalidLeaf(Node underUtilizedNode,
                                   InternalNode parent,
                                   int parentPointerIndex,
                                   int parentKeyIndex) throws IllegalStateException {
        // load the neighbors
        LeafNode underUtilizedLeaf = (LeafNode)underUtilizedNode;
        LeafNode nextLeaf = (LeafNode)mainMemory.getBlockAt(underUtilizedLeaf.getNextNodePointer()),
                prevLeaf = (LeafNode)mainMemory.getBlockAt(underUtilizedLeaf.getPrevNodePointer());
        recorder.addOneDeletionReads();
        recorder.addOneDeletionReads();
        recorder.addOneLeafNodeReads();
        recorder.addOneLeafNodeReads();

        if(nextLeaf == null && prevLeaf == null)
            throw new IllegalStateException("Both prevLeaf and nextLeaf is null for "+underUtilizedNode);

        // validate neighbouring nodes
        if(prevLeaf != null) {
            if(underUtilizedLeaf.getPrevNodePointer() != prevLeaf.getBlockIndex())
                throw new IllegalStateException("In handleInvalidLeaf(), prevLeaf block number inconsistency:\n"+
                        "underUtilizedLeaf.getPrevNodePointer(): "+underUtilizedLeaf.getPrevNodePointer()+
                        "; prevLeaf.getBlockIndex(): "+prevLeaf.getBlockIndex());
            else if(prevLeaf.getNextNodePointer() != underUtilizedLeaf.getBlockIndex())
                throw new IllegalStateException("In handleInvalidLeaf(), prevLeaf block number inconsistency:\n"+
                        "prevLeaf.getNextNodePointer(): "+prevLeaf.getNextNodePointer()+
                        "; underUtilizedLeaf.getBlockIndex(): "+underUtilizedLeaf.getBlockIndex());
        }

        if(nextLeaf != null) {
            if(underUtilizedLeaf.getNextNodePointer() != nextLeaf.getBlockIndex())
                throw new IllegalStateException("In handleInvalidLeaf(), nextLeaf block number inconsistency:\n"+
                        "underUtilizedLeaf.getNextNodePointer(): "+underUtilizedLeaf.getNextNodePointer()+
                        "; nextLeaf.getBlockIndex(): "+nextLeaf.getBlockIndex());
            else if(nextLeaf.getPrevNodePointer() != underUtilizedLeaf.getBlockIndex())
                throw new IllegalStateException("In handleInvalidLeaf(), nextLeaf block number inconsistency:\n"+
                        "nextLeaf.getPrevNodePointer(): "+nextLeaf.getPrevNodePointer()+
                        "; underUtilizedLeaf.getBlockIndex(): "+underUtilizedLeaf.getBlockIndex());
        }

        // check if we can redistribute with next
        if(nextLeaf!=null && nextLeaf.canGiveOneKey(keyLimit) &&
                isSameParent(nextLeaf, parent, parentPointerIndex+1)) {
            moveOneKey(nextLeaf, underUtilizedLeaf, false, parent, parentKeyIndex+1);
            recorder.addOneLeafRedistributes();
        }
        // now check if we can redistribute with prev
        else if(prevLeaf!=null && prevLeaf.canGiveOneKey(keyLimit) &&
                isSameParent(prevLeaf, parent, parentPointerIndex-1)) {
            moveOneKey(prevLeaf, underUtilizedLeaf, true, parent, parentKeyIndex);
            recorder.addOneLeafRedistributes();
        }
        // we can't redistribute, try merging with next
        else if(nextLeaf!=null && isSameParent(nextLeaf, parent, parentPointerIndex+1) &&
                (nextLeaf.getKeyCount()+underUtilizedLeaf.getKeyCount())<=keyLimit) {
            // it's the case where under utilized node is the left node from parent
            mergeNodes(underUtilizedLeaf, nextLeaf, parent,
                    parentPointerIndex+1, parentKeyIndex+1);
            recorder.addOneLeafMerges();
        }
        // last chance, try merging with prev
        else if(prevLeaf!=null && isSameParent(prevLeaf, parent, parentPointerIndex-1) &&
                (prevLeaf.getKeyCount()+underUtilizedLeaf.getKeyCount())<=keyLimit) {
            // it's the case where split node is in the left from parent
             mergeNodes(prevLeaf, underUtilizedLeaf, parent, parentPointerIndex, parentKeyIndex);
             recorder.addOneLeafMerges();
        }
        else {
            throw new IllegalStateException("Can't have both leaf " +
                "pointers null and not be root or no " +
                "common parent");
        }

    }


    /**
     * Handle the internal node redistribution/merging
     *
     * @param underUtilizedNode node to process
     * @param parent the parent node
     * @param parentPointerIndex parent pointer index
     * @param parentKeyIndex parent key index that underUtilizedNode is child
     */
    private void handleInvalidInternal(Node underUtilizedNode,
                                       InternalNode parent,
                                       int parentPointerIndex,
                                       int parentKeyIndex) throws IllegalStateException {

        InternalNode underUtilizedInternal = (InternalNode) underUtilizedNode;

        // load the adjacent nodes
        InternalNode nextInternal = (InternalNode) mainMemory.getBlockAt(parent.getPointerAt(parentPointerIndex + 1));
        InternalNode prevInternal = (InternalNode) mainMemory.getBlockAt(parent.getPointerAt(parentPointerIndex - 1));
        recorder.addOneDeletionReads();
        recorder.addOneDeletionReads();
        recorder.addOneInternalNodeReads();
        recorder.addOneInternalNodeReads();

        if (nextInternal == null && prevInternal == null)
            throw new IllegalStateException("Both prevInternal and nextInternal is null for " + underUtilizedNode);
        // check if we can redistribute with the next node
        if (nextInternal != null && nextInternal.canGiveOneKey(keyLimit)) {
            moveOneKey(nextInternal, underUtilizedInternal, false, parent, parentKeyIndex + 1);
            recorder.addOneInternalNodeRedistributes();
        }
        // check if we can redistribute with the previous node
        else if (prevInternal != null && prevInternal.canGiveOneKey(keyLimit)) {
            moveOneKey(prevInternal, underUtilizedInternal, true, parent, parentKeyIndex);
            recorder.addOneInternalNodeRedistributes();
        }
        // // check if we can merge with the right node
        else if (nextInternal != null &&
                (underUtilizedInternal.getKeyCount() + nextInternal.getKeyCount()) <= keyLimit) {
            mergeNodes(underUtilizedInternal, nextInternal, parent,
                    parentPointerIndex + 1, parentKeyIndex + 1);
            recorder.addOneInternalNodeMerges();
        }
        // now, check if we can merge with the left node
        else if (prevInternal != null &&
                (underUtilizedInternal.getKeyCount() + prevInternal.getKeyCount()) <= keyLimit) {
            mergeNodes(prevInternal, underUtilizedInternal, parent,
                    parentPointerIndex, parentKeyIndex);
            recorder.addOneInternalNodeMerges();
        } else {
            throw new IllegalStateException("Can't merge or redistribute internal node " + underUtilizedInternal);
        }
    }

    /**
     * Check if the node is a child of parent
     *
     * @param node the node that we want to check
     * @param parent the assumed parent node of node
     * @param parentIndex the assumed index of pointer in parent's pointer list
     *                   that points to the node
     * @return true if node is parent's child at that pointer index
     * false if it's not
     */
    private boolean isSameParent(Node node, InternalNode parent, int parentIndex) {
        return(parent.getKeyCount() >= parentIndex && parentIndex >= 0 &&
                (node.getBlockIndex() == parent.getPointerAt(parentIndex)));
    }

    /**
     * Function that move one key & pointer pair among two leaf nodes
     * while updating the referring key of the parent node (always an internal node).
     * We have two distinct cases which are the following:
     * This case is when the giver is on the *left* side of receiver:
     * |--------|  <-----  |----------|
     * | giver  |          | receiver |
     * |--------|  ----->  |----------|
     * In this case:
     * 1. *remove* the *last* 1 key & pointer from giver
     * 2. *insert* them at *first* into the receiver node
     * 3. *update* the parent key-pointer with the first value of the receiver.
     * The other case is when the giver is on the *right* side of receiver:
     * |--------------|  <-----  |---------|
     * |   receiver   |          |  giver  |
     * |--------------|  ----->  |---------|
     * In this case:
     * 1. *remove* the *first* 1 key & pointer from giver
     * 2. *insert* them at *last* into the receiver.
     * 3. *update* the parent key-pointer with the first value of the giver.
     *
     * @param giver node to receive (Key, Value) pairs
     * @param receiver node that we take the (Key, Value) pairs
     * @param giverOnLeft if true, giving node is on the left
     *                    else, receiving node on the right
     * @param parent the internal node parenting both leaf nodes
     * @param inBetweenKeyIdx index of the key that is the lowerBound
     *                       of the givingNode in parent key array
     */
    private void moveOneKey(LeafNode giver, LeafNode receiver,
                                   boolean giverOnLeft, InternalNode parent,
                                   int inBetweenKeyIdx){
        int key;
        // move one key from
        if(giverOnLeft) {
            // move over the pointer
            receiver.insertPointerAt(0,giver.removePointerAtLast());
            // move over the key
            int removedKey = giver.removeKeyAtLast();
            receiver.insertKeyAt(0, removedKey);
            // move over the duplicates of key
            if(giver.hasDuplicates(removedKey))
               receiver.insertAllDuplicates(removedKey, giver.removeAllDuplicates(removedKey));
            // get the key from the left node
            key = receiver.getKeyAt(0);

        }
        // handle the case when redistributing using next
        else {
            receiver.insertKeyAtLast(giver.removeKeyAt(0));
            receiver.insertPointerAtLast(giver.removePointerAt(0));
            // get the key from the right node
            key = giver.getKeyAt(0);
        }

        // in either case update parent pointer
        parent.replaceKeyAt(inBetweenKeyIdx, key);
        // finally commit the changes
        mainMemory.overWriteNode(giver);
        mainMemory.overWriteNode(receiver);
        mainMemory.overWriteNode(parent);
        recorder.addToLeafNodeWrites(2);
        recorder.addOneInternalNodeWrites();
        recorder.addToDeletionWrites(3);
    }

    /**
     * Move one key from receiver to giver, update parent key afterwards
     *
     * @param receiver node to receive 1 key and 1 pointers
     * @param giver node that we take 1 key and 1 pointers
     * @param giverOnLeft if true, move last n pointer to receiver
     *                    if false, move first n
     * @param parent the internal node parenting both
     * @param inBetweenKeyIdx index of the key in parent's key list
     *                       that is in between the neighbor receiver & giver pair
     */
    private void moveOneKey(InternalNode giver, InternalNode receiver,
                                   boolean giverOnLeft, InternalNode parent,
                                   int inBetweenKeyIdx){
        // new_key and old_key all refers to the ones in parent key arrayList
        int newKey, oldKey = parent.getKeyAt(inBetweenKeyIdx);
        if(giverOnLeft) {
            receiver.insertPointerAt(0, giver.removePointerAtLast());
            receiver.insertKeyAt(0, oldKey);
            newKey = giver.removeKeyAtLast();
        }
        else {
            receiver.insertKeyAtLast(oldKey);
            receiver.insertPointerAtLast(giver.removePointerAt(0));
            newKey = giver.removeKeyAt(0);
        }
        // in either case update the parent key
        parent.replaceKeyAt(inBetweenKeyIdx, newKey);
        // commit the chances
        mainMemory.overWriteNode(receiver);
        mainMemory.overWriteNode(giver);
        mainMemory.overWriteNode(parent);
        recorder.addToDeletionWrites(3);
        recorder.addToInternalNodeWrites(3);
    }

    /**
     * This function is responsible for the merging of one leaf node to its
     * previous node and next node
     * Input structure assumption
     *                parent
     *      ...  [p1]   key  [p2]  <-- pass in p2's index as rightNodeParentPointerIndex
     *                  ^--- pass in key's index as betweenNodeParentKeyIndex
     *            /            \
     *        left leaf | right leaf |
     * The merge happens from right -> left thus the final result would
     * be like this:
     *                                  parent
     *      ...  [p1]              key-deleted       [p2-deleted]
     *            /                                        \
     *        left leaf (enlarged)                right leaf-deleted
     * @param left left-most leaf to merge
     * @param right right-most leaf to merge
     * @param parent parent of both leaves (internal node)
     * @param rightPointerIdx the index of right node in the parent
     *                                pointer array
     * @param inBetweenKeyIdx the key that's in between the merging pair
     *                              of node
     */
    private void mergeNodes(LeafNode left, LeafNode right, InternalNode parent,
                            int rightPointerIdx, int inBetweenKeyIdx){

        // join the two leaves together.
        int moveKeyCount = right.getKeyCount();
        for (int i = 0; i < moveKeyCount; i++) {
            // move over the key
            int removedKey = right.removeKeyAt(0);
            left.insertKeyAtLast(removedKey);
            // move over the duplicates
            if(right.hasDuplicates(removedKey))
                left.insertAllDuplicates(removedKey, right.removeAllDuplicates(removedKey));
            // move over the pointers
            left.insertPointerAtLast(right.removePointerAt(0));
        }

        // update the double-linked pointers
        left.setNextNodePointer(right.getNextNodePointer());
        // update the prev pointer of right next node (if any)
        if(right.getNextNodePointer() != -1) {
            LeafNode rightNext = (LeafNode)mainMemory.getBlockAt(right.getNextNodePointer());
            rightNext.setPrevNodePointer(left.getBlockIndex());
            mainMemory.overWriteNode(rightNext);
        }

        // now handle the top pointer
        parent.removePointerAt(rightPointerIdx);
        parent.removeKeyAt(inBetweenKeyIdx);
        //fixParentPointer(backup, parent, parentPointerIndex,
        //        parentKeyIndex, useNextPointer);

        // commit changes made to nodes
        mainMemory.overWriteNode(parent);
        mainMemory.overWriteNode(left);
        mainMemory.removeBlockAt(right.getBlockIndex()); // remove the right leaf

        // record the changes
        recorder.addToLeafNodeWrites(2);
        recorder.addOneInternalNodeWrites();
        recorder.deleteOneLeafNode();
        recorder.addToDeletionWrites(3);
    }

    /**
     * Expected structure:
     *                            parent
     *      ... [leftNodePointer]  key  [rightNodePointer] <-- pass in index of
     *                              ^                           rightNodePointer as rightParentPointerIndex
     *                              ^---- pass in index of key as the betweenNodeParentKeyIndex
     *                  /                        \
     *          [lp1] left [lp2]        [rp1]  right    [rp2]
     *
     * @param left left-most internal node to merge
     * @param right right-most internal node to merge
     * @param parent parent of both internal nodes.
     * @param rightPointerIdx index of the parent that has these two pointers
     * @param inBetweenKeyIdx the key that's in between the merging pair
     *                              of node
     */
    private void mergeNodes(InternalNode left, InternalNode right, InternalNode parent,
                                int rightPointerIdx,
                                int inBetweenKeyIdx){

        left.insertKeyAtLast(parent.removeKeyAt(inBetweenKeyIdx));

        // merge the right node to left
        int moveKeyCount = right.getKeyCount();
        for (int i = 0; i < moveKeyCount; i++) {
            left.insertKeyAtLast(right.removeKeyAt(0));
            left.insertPointerAtLast(right.removePointerAt(0));
        }
        // move over the last pointer
        left.insertPointerAtLast(right.getPointerAt(0));
        // handle parent's pointers
        parent.removePointerAt(rightPointerIdx);

        // commit changes made to nodes
        mainMemory.overWriteNode(parent);
        mainMemory.overWriteNode(left);
        mainMemory.removeBlockAt(right.getBlockIndex());// remove right node

        // record the changes
        recorder.addToInternalNodeWrites(3);
        recorder.deleteOneInternalNode();
        recorder.addToDeletionWrites(3);
    }

    /**
     * print the whole tree using breadth first expansion
     */
    @SuppressWarnings("unused")
    public void printTree(){
        ArrayList<Integer> opened = new ArrayList<>();
        opened.add(this.root.getBlockIndex());
        while(!opened.isEmpty()){
            Node curNode=mainMemory.getBlockAt(opened.remove(0));
            System.out.println(curNode);
            if(curNode.isInternal()){
                InternalNode curInternal=(InternalNode) curNode;
                for(int i=0;i<=curInternal.getKeyCount();i++){
                    opened.add(opened.size(), curInternal.getPointerAt(i));
                }
            }
        }
    }

    /**
     * print to Nth level from root where N=level(parameter)
     * @param level print up to the level from root of the tree
     */
    @SuppressWarnings("unused")
    public void printTree(int level){
        ArrayList<Integer> opened = new ArrayList<>();
        int curLevel = 0;
        opened.add(this.root.getBlockIndex());
        while(!opened.isEmpty() && curLevel<=level){
            Node curNode=mainMemory.getBlockAt(opened.remove(0));
            int newLevel;
            if(curNode.isInternal())
                newLevel = ((InternalNode)root).getChildLevel() - ((InternalNode)curNode).getChildLevel();
            else
                newLevel = root.isLeaf()? 0: ((InternalNode)root).getChildLevel();
            if (newLevel>level)
                return;

            if(newLevel != curLevel){
                System.out.println("Printing Level "+newLevel+":\n");
                curLevel = newLevel;
            }
            System.out.println(curNode);
            if(curNode.isInternal()){
                InternalNode curInternal=(InternalNode) curNode;
                for(int i=0;i<=curInternal.getKeyCount();i++){
                    opened.add(opened.size(), curInternal.getPointerAt(i));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public void printTree(int level, int maxPrintNode){
        ArrayList<Integer> opened = new ArrayList<>();
        int curLevel = 0, printCnt = 0;
        opened.add(this.root.getBlockIndex());
        while(!opened.isEmpty() && curLevel<=level && printCnt <= maxPrintNode){
            Node curNode=mainMemory.getBlockAt(opened.remove(0));
            int newLevel;
            if(curNode.isInternal())
                newLevel = ((InternalNode)root).getChildLevel() - ((InternalNode)curNode).getChildLevel();
            else
                newLevel = root.isLeaf()? 0: ((InternalNode)root).getChildLevel();
            if (newLevel>level)
                return;

            if(newLevel != curLevel){
                System.out.println("Printing Level "+newLevel+":\n");
                curLevel = newLevel;
            }
            System.out.println(curNode);
            printCnt++;
            if(curNode.isInternal()){
                InternalNode curInternal=(InternalNode) curNode;
                for(int i=0;i<=curInternal.getKeyCount();i++){
                    opened.add(opened.size(), curInternal.getPointerAt(i));
                }
            }
        }
    }

    public int getRecordCount(){
        return mainMemory.CountRecords();
    }
    @SuppressWarnings("unused")
    public void printRecorder(){
        System.out.println(this.recorder);
    }

    @SuppressWarnings("unused")
    public PerformanceRecorder getRecorder(){
        return this.recorder;
    }

}