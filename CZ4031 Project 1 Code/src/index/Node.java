package index;

import java.util.ArrayList;
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
        System.out.println("Setting parent (parent has these keys)" + setparent.getKeys());

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

    // need to make sure that old node that was split, keys are updated correctly.
    // same for new node as well
    // need to check if old node has parent node, if have, then connect the new one
    // to it as well, if parent is full, call split again
    // if dont have, need to create new parent node which contains smallest key of
    // new node
    public void splitNode(int key, Address addr) {

        // Is a LeafNode
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // for leaf node, need to handle both ArrayList of keys and TreeMap of keys and
        // value pairs (which are keys and the Address Arraylist respectively)
        if (this.isLeaf()) {
            System.out.print("\nROOT NODE IS:");
            System.out.print(testBplusTree.getRoot().getKeys());

            System.out.println("\nSPLITTING LEAF NODE************************************************");
            // create a new node
            LeafNode newNode = new LeafNode();

            // Handling the
            // TreeMap-----------------------------------------------------------------------
            // add keyToBeAdded into the OLD NODES's treemap of keys (which contain
            // arraylists of addresses),
            // which is automatically sorted by keys, take the last minLeafNodeSize keys of
            // the sorted treemap and insert into new Node's treemap
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

            System.out.printf("\n\n\nMap of old node Before Removing:\n");
            System.out.println(((LeafNode) this).map);

            // Creates a submap based on current map, taking only key-value pairs that are
            // after the nth index
            SortedMap<Integer, ArrayList<Address>> lastnKeys = ((LeafNode) this).map.subMap(fromKey, true,
                    ((LeafNode) this).map.lastKey(), true);

            // Let the newNode be equals to the submap created
            newNode.map = new TreeMap<Integer, ArrayList<Address>>(lastnKeys);

            // removing keys after the nth index for old node
            lastnKeys.clear();

            System.out.printf("\nMap of old node After Removing:\n");
            System.out.println(((LeafNode) this).map);

            System.out.printf("\nMap of new node:\n");
            System.out.println(newNode.map);

            // Handling the ArrayList of
            // keys-----------------------------------------------------------------------
            // used/created a simple function which inserts a key in sorted order into the
            // ArrayList
            insertInOrder(this.keys, key);

            // adding keys after the nth index into the newNode's arraylist of keys
            newNode.keys = new ArrayList<Integer>(this.keys.subList(n, this.keys.size()));// after nth index

            // removing keys after the nth index for old node's arraylist of keys
            this.keys.subList(n, this.keys.size()).clear();

            System.out.printf("\n\n\nKeys in old Node's ArrayList After Removing:\n");
            System.out.print(this.keys);

            System.out.printf("\nKeys in new Node's ArrayList:\n");
            System.out.print(newNode.keys);

            if (((LeafNode) this).getNext() != null) {
                newNode.setNext(((LeafNode) this).getNext());
                ((LeafNode) this).getNext().setPrevious(newNode);
            }

            // setting next node
            ((LeafNode) this).setNext(newNode);
            System.out.printf("\nKeys in old Node's next node:\n");
            System.out.print(((LeafNode) this).getNext().getKeys());

            // setting previous node
            (newNode).setPrevious((LeafNode) this);
            System.out.printf("\nKeys in new Node's previous node:\n");
            System.out.print(newNode.getPrevious().getKeys());

            // Handling the parent node of the old node---------------------------------------------------------------
            // if parent node exists, insert new node into this node

            if (this.getParent() != null) {

                System.out.printf("\nKeys in parent node:\n");
                System.out.print(this.getParent().keys);

                // Check if parent is full, if yes
                if (this.getParent().keys.size() == NODE_SIZE) {

                    // if the newNode is added to the end
                    if (newNode.getNext() == null) {

                        System.out.printf("\n\nProblematic split\n");

                        System.out.printf("\n\nTHE CURRENT PARENT IS A ROOT:");
                        System.out.println(this.getParent().isRoot());

                        // First, create a new node, new2Parent, that will be the parent of the
                        // current's node parent
                        NonLeafNode new2Parent = new NonLeafNode();
                        new2Parent.keys = new ArrayList<Integer>();
                        new2Parent.addChild(this.getParent());

                        // if the current node's parent is a root, new2Parent which is its parent will
                        // become a root
                        if (this.getParent().isRoot()) {
                            this.getParent().setIsRoot(false);
                            new2Parent.setIsRoot(true);
                            testBplusTree.setRoot(new2Parent);
                        }
                        this.getParent().setParent(new2Parent);

                        // Removing rightmost child as well as the rightmost key
                        this.getParent().removeChild(this);
                        this.getParent().keys.remove(this.keys.size());
                        this.removeParent(this.getParent());

                        System.out.printf("\n\nAdding key %d in NEW parent node\n", newNode.getKey(0));
                        NonLeafNode newParent = new NonLeafNode();
                        newParent.keys = new ArrayList<Integer>();
                        newParent.addChild(this);
                        newParent.addChild(newNode);
                        newParent.keys.add(newNode.getKey(0));
                        this.setParent(newParent);
                        newNode.setParent(newParent);

                        System.out.printf("\nKeys in newParent Node's ArrayList: ");
                        System.out.println(newParent.keys);

                        new2Parent.addChild(newParent);
                        new2Parent.keys.add(this.getKey(0));

                        System.out.printf("\nKeys in new2Parent Node's ArrayList: ");
                        System.out.println(new2Parent.keys);

                        System.out.printf("\nKeys in new Node's ArrayList: ");
                        System.out.println(newNode.keys);

                        System.out.printf("\nKeys in new2Parent's index 0 child: ");
                        System.out.print(new2Parent.getChild(0).keys);
                        System.out.printf("\nKeys in new2Parent's index 1 child: ");
                        System.out.println(new2Parent.getChild(1).keys);

                        System.out.printf("\nKeys in newParent's index 0 child: ");
                        System.out.print(newParent.getChild(0).keys);
                        System.out.printf("\nKeys in newParent's index 1 child: ");
                        System.out.println(newParent.getChild(1).keys);

                        System.out.printf("\n******************KEYS IN ROOT: ");
                        System.out.println(testBplusTree.getRoot().getKeys());
                    }

                    else {

                        System.out.printf("\n\nProblematic split\n");

                        System.out.printf("\n\nTHE CURRENT PARENT IS A ROOT:");
                        System.out.println(this.getParent().isRoot());

                        // First, create a new node, new2Parent, that will be the parent of the
                        // current's node parent
                        NonLeafNode new2Parent = new NonLeafNode();
                        new2Parent.keys = new ArrayList<Integer>();
                        new2Parent.addChild(this.getParent());

                        // if the current node's parent is a root, new2Parent which is its parent will
                        // become a root
                        if (this.getParent().isRoot()) {
                            this.getParent().setIsRoot(false);
                            new2Parent.setIsRoot(true);
                            testBplusTree.setRoot(new2Parent);
                        }
                        this.getParent().setParent(new2Parent);

                        // Removing rightmost child as well as the rightmost key
                        this.getParent().removeChild(newNode.getNext());
                        this.getParent().keys.remove(this.keys.size());

                        System.out.printf("\n\nAdding key %d in NEW parent node\n", newNode.getKey(0));
                        NonLeafNode newParent = new NonLeafNode();
                        newParent.keys = new ArrayList<Integer>();
                        newParent.addChild(newNode);
                        newParent.addChild(newNode.getNext());
                        newParent.keys.add(newNode.getNext().getKey(0));
                        newNode.setParent(newParent);
                        newNode.getNext().setParent(newParent);

                        System.out.printf("\nKeys in newParent Node's ArrayList: ");
                        System.out.println(newParent.keys);

                        new2Parent.addChild(newParent);
                        new2Parent.keys.add(newNode.getKey(0));
                        newParent.setParent(new2Parent);

                        System.out.printf("\nKeys in new2Parent Node's ArrayList: ");
                        System.out.println(new2Parent.keys);

                        System.out.printf("\nKeys in new Node's ArrayList: ");
                        System.out.println(newNode.keys);

                        System.out.printf("\nKeys in new2Parent's index 0 child: ");
                        System.out.print(new2Parent.getChild(0).keys);
                        System.out.printf("\nKeys in new2Parent's index 1 child: ");
                        System.out.println(new2Parent.getChild(1).keys);

                        System.out.printf("\nKeys in newParent's index 0 child: ");
                        System.out.print(newParent.getChild(0).keys);
                        System.out.printf("\nKeys in newParent's index 1 child: ");
                        System.out.println(newParent.getChild(1).keys);

                        System.out.printf("\n******************KEYS IN ROOT: ");
                        System.out.println(testBplusTree.getRoot().getKeys());
                    }

                }

                // else if parent is not full or parent is empty
                else if (this.getParent().keys == null || this.getParent().keys.size() != NODE_SIZE) {
                    System.out.printf("\n\nAdding key %d in OLD parent node\n", newNode.getKey(0));
                    this.getParent().addChild(newNode);
                    this.getParent().keys.add(newNode.getKey(0));
                    newNode.setParent(this.getParent());

                }

                System.out.printf("\nCurrent Children Size:\n");
                System.out.println(this.getParent().getChildren().size());
                for (int k = 0; k < this.getParent().getChildren().size(); k++) {
                    System.out.print(this.getParent().getChild(k).keys);
                    System.out.print(" ");
                }

                // // Check if children nodes are full or more, if yes, split them
                // if (this.getIsLeaf() != true || this.getParent().getChildren().size() >
                // NODE_SIZE + 1){

                // System.out.printf("\n\n\nChildren nodes are full, splitting them\n");

                // System.out.printf("\nKeys in parent node:\n");
                // System.out.print(this.getParent().keys);

                // // Splitting the children nodes
                // splitNode(this.getKey(minLeafNodeSize), null);

                // }

            }

            // else parent node does not exist, have to create new parent node, insert new
            // and old node into this node
            else {
                System.out.printf("\n\nAdding key %d in NEW parent node\n", newNode.getKey(0));
                NonLeafNode newParent = new NonLeafNode();
                newParent.keys = new ArrayList<Integer>();
                newParent.addChild(this);
                newParent.addChild(newNode);
                newParent.keys.add(newNode.getKey(0));
                this.setParent(newParent);
                newNode.setParent(newParent); // <- adding this gave me an infinite loop

                System.out.printf("\nKeys in new ParentNode's ArrayList:");
                System.out.print(newParent.keys);

                try {
                    System.out.print("\nParent: ");
                    System.out.print(this.getParent().keys);
                    System.out.print("\nChild at index 0: ");
                    System.out.print(this.getParent().getChild(0).getKeys());
                    System.out.print("\nChild at index 1: ");
                    System.out.print(this.getParent().getChild(1).getKeys());
                } catch (Exception e) {
                    System.out.print("No parent");
                }
            }

        }

        // Is a NonLeafNode
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // similar to LeafNode, but don't need to handle the TreeMap
        else {
            System.out.println("\nSPLITTING NON LEAF NODE************************************************");

            LeafNode newNode = new LeafNode();
            // Handling the ArrayList of
            // keys-----------------------------------------------------------------------
            int n = NODE_SIZE - minNonLeafNodeSize;
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

            // Handling the parent node of the old
            // node---------------------------------------------------------------
            // if parent node exists insert new node into this node
            if (this.getParent() != null) {

                System.out.printf("\n**Keys in parent node------------------------------\n");
                System.out.print(this.getParent().keys);

                // Check if parent is full, if no
                if (this.getParent().keys == null || this.getParent().keys.size() != NODE_SIZE) {
                    System.out.printf("Adding key %d\n", newNode.getKey(0));
                    // Add new node into old node's parent
                    this.getParent().addChild(newNode);
                    this.getParent().keys.add(newNode.getKey(0));

                } else if (this.getParent().keys.size() == NODE_SIZE) {
                    splitNode(key, null);
                }
            }
            // else create new parent node, insert new and old node into this node
            else {
                NonLeafNode newParent = new NonLeafNode();
                newParent.keys = new ArrayList<Integer>();
                newParent.addChild(this);
                newParent.addChild(newNode);
                newParent.keys.add(newNode.getKey(0));
                this.setParent(newParent);
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
        Set<Integer> keys = ((LeafNode) this).map.keySet();
        System.out.println(keys);
    }

    public void updateKey(int keyIndex, int newKey) {
        if (keyIndex >= 0 && keyIndex < keys.size()) {
            keys.set(keyIndex, newKey);
    }
    

}
