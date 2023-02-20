package index;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import storage.Address;

import java.lang.Math;

public class Node {

    // Max number of keys in each node (node size)
    private int nodeSize;
    private int minLeafNodeSize;
    private int minNonLeafNodeSize;

    // We set the node size to 3 first as its easier to check if its correct
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
    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isNonLeaf() {
        return !isLeaf;
    }

    // set node as leaf
    public void setIsLeaf(boolean isALeaf) {
        isLeaf = isALeaf;
    }

    // check whether node is as root
    public boolean isRoot() {
        return isRoot;
    }

    // set node as root
    public void setIsRoot(boolean isARoot) {
        isRoot = isARoot;
    }

    public void setParent(NonLeafNode setparent) {
        // If the current node was a root, have to make sure its parent will also be a
        // root, and also have to setIsRoot to false for the current node
        if (this.isRoot()) {
            this.setIsRoot(false);
            setparent.setIsRoot(true);
            setparent.setIsLeaf(false);
            testBplusTree.setRoot(setparent);
        } else {
            setparent.setIsLeaf(false);
        }
        this.parent = setparent;
    }

    public NonLeafNode getParent() {
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

    public int getKeySize() {
        return keys.size();
    }

    /**
     * Binary search stored keys. (wrapper of the recursive function)
     *
     * @param key        key to search
     * @param upperBound if set true, search for upperBound
     *                   if set false, search for exactKey
     * @return if key exists & upperBound false, the index of the key
     *         else, the index of upper bound of the key.
     */
    int searchKey(int key, boolean upperBound) {
        int keyCount = keys.size();
        return searchKey(0, keyCount - 1, key, upperBound);
    }

    private int searchKey(int left, int right, int key, boolean upperBound) {
        if (left > right)
            return left;

        int middle = (left + right) / 2;
        int middleKey = getKeyAt(middle);

        if (middleKey < key) {
            return searchKey(middle + 1, right, key, upperBound);
        } else if (middleKey > key) {
            return searchKey(left, middle - 1, key, upperBound);
        } else {
            while (middle < keys.size() && keys.get(middle) == key)
                middle++;
            if (!upperBound)
                return middle - 1;
            return middle;
        }
    }

    int getKeyAt(int index) {
        return keys.get(index);
    }

    int removeKeyAt(int index) {
        return keys.remove(index);
    }

    /**
     * Check if there a need to re-balance the tree
     * 
     * @param maxKeyCount
     * @return
     */
    boolean isUnderUtilized(int maxKeyCount) {
        if (isRoot()) { // root
            return (this.getKeySize() < 1);
        } else if (isLeaf()) { // leaf
            return (this.getKeySize() < (maxKeyCount + 1) / 2);
        } else { // non-leaf
            return (this.getKeySize() < maxKeyCount / 2);
        }
    }


    public static void insertInOrder(ArrayList<Integer> list, int num) {
        int i = 0;

        while (i < list.size() && list.get(i) < num) {
            i++;
        }
        list.add(i, num);
    }



    public void insertChildInOrder(NonLeafNode parent, NonLeafNode child) {
        int i = 0;
        int childToSort = child.getKeyAt(0);
        while (i < parent.getKeySize() && parent.getKeyAt(i) < childToSort) {
            i++;
        }
        parent.children.add(i + 1, child);
    }



    public void printNode() {
        Set<Integer> keys = ((LeafNode) this).map.keySet();
        System.out.println(keys);
    }




    public void updateKey(int keyIndex, int newKey) {
        keys.set(keyIndex, newKey);
    }


    
    public void insertNewNodeToParent(LeafNode newNode) {
        int index = 0;
        boolean insertedNode = false;

        try {
            for (Node currentNode : this.getParent().getChildren()) {

                // if there is a node > than newNode, insert inbetween that node
                if (newNode.getKey(newNode.getKeySize() - 1) < currentNode.getKey(0)) {
                    this.getParent().getChildren().add(index, newNode);
                    this.getParent().keys.add(index - 1, newNode.getKey(0));
                    insertedNode = true;
                    break;
                }
                index++;
            }

            if (insertedNode == false) {
                this.getParent().getChildren().add(newNode);
                this.getParent().keys.add(newNode.getKey(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.getParent().getChildren().add(newNode);
            this.getParent().keys.add(newNode.getKey(0));
        }

        newNode.setParent(this.getParent());

        if (this.getParent().getKeySize() > NODE_SIZE) {
            this.getParent().splitNonLeafNode();
        }

    }




    public void createFirstParentNode(LeafNode newNode) {
        NonLeafNode newParent = new NonLeafNode();
        newParent.keys = new ArrayList<Integer>();
        newParent.addChild(this);
        newParent.addChild(newNode);
        newParent.keys.add(newNode.getKey(0));
        this.setParent(newParent);
        newNode.setParent(newParent);
    }




    public void createRootNode(NonLeafNode newNode) {
        NonLeafNode newParent = new NonLeafNode();
        newParent.keys = new ArrayList<Integer>();
        newParent.addChild(this);
        newParent.addChild(newNode);
        newParent.keys.add(newNode.getKey(0));
        this.setParent(newParent);
        newNode.setParent(newParent);

        System.out.printf("\nKeys in new ParentNode's ArrayList:");
        System.out.print(newParent.keys);
    }




    public LeafNode leafSplitAndDistribute(int key, Address addr) {
        LeafNode newNode = new LeafNode();
        ((LeafNode) this).records = new ArrayList<Address>();
        ((LeafNode) this).records.add(addr);
        ((LeafNode) this).map.put(key, ((LeafNode) this).records);

        // Removing whats after the nth index into the new node
        int n = NODE_SIZE - minLeafNodeSize + 1;
        int i = 0;
        int fromKey = 0;

        // finding the nth index
        for (Map.Entry<Integer, ArrayList<Address>> entry : ((LeafNode) this).map.entrySet()) {
            if (i == n) {
                fromKey = entry.getKey();
                break;
            }
            i++;
        }

        SortedMap<Integer, ArrayList<Address>> lastnKeys = ((LeafNode) this).map.subMap(fromKey, true,
                ((LeafNode) this).map.lastKey(), true);

        newNode.map = new TreeMap<Integer, ArrayList<Address>>(lastnKeys);

        lastnKeys.clear();

        insertInOrder(this.keys, key);

        // adding keys after the nth index into the newNode's arraylist of keys
        newNode.keys = new ArrayList<Integer>(this.keys.subList(n, this.keys.size()));// after nth index

        // removing keys after the nth index for old node's arraylist of keys
        this.keys.subList(n, this.keys.size()).clear();

        if (((LeafNode) this).getNext() != null) {
            newNode.setNext(((LeafNode) this).getNext());
        }
        ((LeafNode) this).setNext(newNode);
        return newNode;
    }




    public NonLeafNode nonLeafSplitAndDistribute() {
        System.out.printf("\nProblematic split");

        NonLeafNode currentParent = (NonLeafNode) (this);
        /****** Removing rightmost children as well as the rightmost keys ******/

        System.out.printf("\nCurrent Parent's keys BEFORE removing: ");
        System.out.println(currentParent.keys);

        NonLeafNode newParent = new NonLeafNode();
        newParent.keys = new ArrayList<Integer>();

        int keyToSplitAt = currentParent.getKeyAt(minNonLeafNodeSize);
        for (int k = currentParent.getKeySize(); k > 0; k--) {
            if (currentParent.getKeyAt(k - 1) < keyToSplitAt) {
                break; // We've reached the end of the keys to move
            }
            int currentKey = currentParent.getKeyAt(k - 1);
            Node currentChild = currentParent.getChild(k);

            // Add node and keys to new parent
            newParent.children.add(0, currentChild);
            newParent.keys.add(0, currentKey);
            currentChild.setParent(newParent);

            // Remove node and keys from old parent
            currentParent.removeChild(currentParent.getChild(k));
            currentParent.keys.remove(k - 1);

        }

        System.out.printf("Current Parent's keys AFTER removing: ");
        System.out.println(currentParent.keys);
        System.out.printf("New Parent's keys AFTER adding: ");
        System.out.println(newParent.keys);

        return newParent;
    }





    public void splitLeafNode(int key, Address addr) {
        // Step 1 split and distribute

        LeafNode newNode = this.leafSplitAndDistribute(key, addr);

        // Step 2

        // If the leaf node has parent, add the new node to parent
        if (this.getParent() != null) {
            /** Insert new node to parent */
            this.insertNewNodeToParent(newNode);

            if (this.getParent().getKeySize() > NODE_SIZE) {
                this.getParent().splitNonLeafNode();
            }
        }
        // First leaf node when its full, create a new root node for it, which is also
        // the first parent node created
        else {
            this.createFirstParentNode(newNode);
        }

    }




    public void splitNonLeafNode() {
        NonLeafNode newParent = this.nonLeafSplitAndDistribute();

        if (this.getParent() != null) {

            insertChildInOrder(this.getParent(), newParent);

            newParent.setParent(this.getParent());

            // Remove the first key from the new parent and add it to new2Parent
            insertInOrder(this.getParent().keys, newParent.getKeyAt(0));

            newParent.keys.remove(0);

            if (this.getParent().getKeySize() > NODE_SIZE) {
                this.getParent().splitNonLeafNode();
            }

        } else {
            // it is the root
            System.out.println("NO PARENT");
            NonLeafNode newRoot = new NonLeafNode();
            newRoot.keys = new ArrayList<Integer>();
            newRoot.keys.add(newParent.getKeyAt(0));

            newParent.keys.remove(0);

            newRoot.addChild(this);
            newRoot.addChild(newParent);

            this.setParent(newRoot);
            newParent.setParent(newRoot);

            testBplusTree.setRoot(newRoot);
        }
    }



}
