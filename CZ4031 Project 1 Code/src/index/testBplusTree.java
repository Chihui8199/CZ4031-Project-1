

package index;

import java.util.ArrayList;
import storage.Address;

public class testBplusTree{

    static final int NODE_SIZE = 3;
    static Node rootNode;
    Node nodeToInsertTo;

    // have to initialise the first node as root node
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

    // have to first search for the LeafNode to insert to, then add a record add that LeafNode
    public void insertKey(int key, Address add){
        System.out.printf("\n\n\nInserting Key %d --------------------------------------------------------------------------------------------------------------------\n", key);
        System.out.printf("Current Root:");
        System.out.println(testBplusTree.getRoot().getKeys());
        
        // nodeToInsertTo is the leafnode 
        nodeToInsertTo = searchNode(key);

        System.out.printf("Keys of node to insert to: ");
        System.out.print(nodeToInsertTo.getKeys());

        ((LeafNode) nodeToInsertTo).addRecord(key, add);
    }

    // TODO: remove this --> we can do this recusively. Look at searchKey function
    public ArrayList<Address> searchKey2(int key){
        System.out.printf("\n\nSearching Key %d --------------------------------------------------------------------------------------------------------------------\n", key);
        System.out.printf("Current Root:");
        System.out.println(testBplusTree.getRoot().getKeys());
        
        // nodeToInsertTo is the leafnode 
        nodeToInsertTo = searchNode(key);

        System.out.printf("Keys of node to search: ");
        System.out.println(nodeToInsertTo.getKeys());

        return ((LeafNode) nodeToInsertTo).findRecord(key);
    }

    // finding the leaf node to find/insert the key to
    public LeafNode searchNode(int key){
        ArrayList<Integer> keys; 

        System.out.printf("Searching Node for Key %d\n",key);

        // If root is a leaf node, means its still at the first node, hence return the rootNode
        if (testBplusTree.rootNode.getIsLeaf()){
            setRoot(rootNode);
            System.out.printf("Found Node : Root\n");
            return (LeafNode)rootNode;
        }
        
        //  else, it is not a leaf node
        else{
            Node nodeToInsertTo = (NonLeafNode) getRoot();

            // Starting from the rootnode, keep looping (going down) until the current node's (nodeToInsertTo) child is a leaf node 

            while (!((NonLeafNode) nodeToInsertTo).getChild(0).getIsLeaf() ) {

                keys = nodeToInsertTo.getKeys();

                // loops through keys of current node (nodeToInsertTo)

                for (int i = keys.size() -1; i >= 0; i--) {

                    // if there exists a key in the node where it's value is smaller or equals to the key, 
                    // set the current node to the child node corresponding to that node

                    if (nodeToInsertTo.getKey(i) <= key) {
                        nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(i+1);
                        break;
                    }

                    // if the index reaches 0, means that the key is smaller than the smallest key in the node (at index 0)
                    // set the current node to the child node that corresponds to that node

                    else if (i == 0){
                        nodeToInsertTo = ((NonLeafNode) nodeToInsertTo).getChild(0);
                    }
                }

                if (nodeToInsertTo.getIsLeaf()){
                    break;
                }
            
        }

        
        keys = nodeToInsertTo.getKeys();

        System.out.print("Keys of found parent: ");
        System.out.println(keys);


        // Looping through the current node's indexes to find which of its leaf/child node to insert the key into, similar to above but this is to obtain the leaf node
        // return the child node once found
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (keys.get(i) <= key){   
                System.out.printf("Found key: %d\n", nodeToInsertTo.getKey(i));
                return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(i+1);
            }
        }

        // if the key is smaller than the smallest key in the current node, return the child corresponding to the smallest key
        return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(0); 
        }
       
    }


    

    public static void deleteNode(){


    }

    /**
     * Wrapper function on top of searchNode
     * @param key
     * @return ArrayList of Address in the database
     */
    public ArrayList<Address> keySearch(int key) {
        return(searchValue(this.rootNode, key));
    }

    public ArrayList<Address> searchValue(Node node, int key){
        // Find if key is within the rootNode
        if (node.getIsLeaf()){
            int ptrIdx = node.searchKey(key, false);
            if (ptrIdx >= 0 && ptrIdx < node.getKeySize() && key == node.getKeyAt(ptrIdx)) {
                return ((LeafNode) node).getAddressesForKey(key); // returns an ArrayList of addresses
            }
            return null;
        }
        // If it's an internal node, descend until we reach a leaf node to find the results
        else{
            int ptrIdx = node.searchKey(key, false); // looks for the upper bound of the key in the node
            NonLeafNode nonLeafNode = (NonLeafNode) node;  // descends into childnode at the corresponding ptr
            Node childNode = ((NonLeafNode) node).getChild(ptrIdx);
            return (searchValue(childNode, key));
        }
    }

    /**
     * Wrapper Function of rangeSearch
     * @param minKey min key of the range (inclusive)
     * @param maxKey max key of the range (inclusive)
     */
    public ArrayList<Address> rangeSearch(int minKey, int maxKey) {
        return searchValuesInRange(minKey, maxKey, this.rootNode);
    }

    public static ArrayList<Address> searchValuesInRange(int minKey, int maxKey, Node node) {
        int ptrIdx;
        ArrayList<Address> resultList = new ArrayList<>();
        if(node.getIsLeaf()){
            ptrIdx = node.searchKey(minKey, false); //if minKey is in key array, get key index
            LeafNode leaf = (LeafNode)node;
            while (true){
                if(ptrIdx == leaf.getKeySize()) {
                    // check if we have a next node to load.
                    // Assuming that next node return a null if there's no next node
                    if(leaf.getNext() == null)
                        break; // if not just break the loop
                    // Traverse to the next node and start searching from index 0 within the next node again
                    leaf = (LeafNode)(leaf.getNext());

                    ptrIdx = 0;
                    if(ptrIdx >= leaf.getKeySize())
                        throw new IllegalStateException("Range search found a node with 0 keys");
                }
                if(leaf.getKey(ptrIdx) > maxKey)
                    break;
                int key = leaf.getKey(ptrIdx);
                resultList.addAll(leaf.getAddressesForKey(key));
                ptrIdx++;
            }
            return (resultList.size()>0 ? resultList : null);
        }
        else {
            ptrIdx =  node.searchKey(minKey, true);
            // Descend into leaf node
            NonLeafNode nonLeafNode = (NonLeafNode) node;
            Node childNode = ((NonLeafNode) node).getChild(ptrIdx);
            return(searchValuesInRange(minKey, maxKey, childNode));
        }
    }

}