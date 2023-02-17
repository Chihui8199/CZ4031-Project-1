
package index;

import java.util.ArrayList;
import storage.Address;

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
        newNode.setIsRoot(true);
        newNode.setIsLeaf(true);
        setRoot(newNode);
        return newNode;
    }

    public static Node createNode() {
        Node newNode = new Node();
        return newNode;
    }

    public static void setRoot(Node root) {
        rootNode = root;
        if (rootNode != null) {
            rootNode.setIsRoot(true);
        }
    }

    public static Node getRoot() {
        return rootNode;
    }

    // have to first search for the LeafNode to insert to, then add a record add
    // that LeafNode
    public void insertKey(int key, Address add) {
        System.out.printf(
                "\n\n\nInserting Key %d --------------------------------------------------------------------------------------------------------------------\n",
                key);
        System.out.printf("Current Root:");
        System.out.println(testBplusTree.getRoot().getKeys());
        nodeToInsertTo = searchNode(key);

        System.out.printf("Keys of node to insert to: ");
        System.out.print(nodeToInsertTo.getKeys());

        ((LeafNode) nodeToInsertTo).addRecord(key, add);
    }

    public LeafNode searchNode(int key) {
        ArrayList<Integer> keys;

        System.out.printf("Searching Node for Key %d\n", key);

        // If root is a leaf node, means its still at the first node, hence return the
        // rootNode
        if (testBplusTree.rootNode.getIsLeaf()) {
            setRoot(rootNode);
            System.out.printf("Found Node : Root\n");
            return (LeafNode) rootNode;
        }

        // else, it is not a leaf node
        else {
            Node nodeToInsertTo = (NonLeafNode) getRoot();

            // Starting from the rootnode, keep looping (going down) until the current
            // node's (nodeToInsertTo) child is a leaf node

            while (!((NonLeafNode) nodeToInsertTo).getChild(0).getIsLeaf()) {

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

                if (nodeToInsertTo.getIsLeaf()) {
                    break;
                }

            }

            keys = nodeToInsertTo.getKeys();

            System.out.print("Keys of nodeToInsertTo: ");
            System.out.println(keys);

            // Looping through the current node's indexes to find which of its leaf/child
            // node to insert the key into, similar to above but this is to obtain the leaf
            // node
            // return the child node once found
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (keys.get(i) <= key) {
                    System.out.printf("Found key: %d\n", nodeToInsertTo.getKey(i));
                    return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(i + 1);
                }
            }

            // if the key is smaller than the smallest key in the current node, return the
            // child corresponding to the smallest key

            return (LeafNode) ((NonLeafNode) nodeToInsertTo).getChild(0);
        }

    }

    public void deleteKey(int key) {
        System.out.printf(
                "\n\n\nDeleting Key %d --------------------------------------------------------------------------------------------------------------------\n",
                key);

        LeafNode leafNode = searchNode(key);

        // Invalid key- not found: nothing to delete
        if (leafNode == null) {
            System.out.println("The key is not found in the tree. No node is deleted.");
            return;
        }

        // Check if it is the only record in the node
        if (leafNode.getKeys().size() == 1) {
            if (leafNode.getIsRoot()) {
                setRoot(null);
                System.out.println("Root Node has been deleted. The entire B+ tree is no longer in memory");
            }
            // else{
            // If not leaf node, remove it from it's parent node
            // determine index of leaf notde from the parent's list of children
            // remove leaf node from list of children
            // need do some sibling thing
            // }
        }

        // Deletion on B+ Tree (Case 3):
        // 1. Neither adjacent sibling can be used
        // 2. Merge the two nodes, deleting one of them
        // 3. Adjust the parent
        // 4. If the parent is not full enough, recursively apply the deletion algorithm
        // in parent

        // Deletion on B+ Tree (Case 2):
        // 1. An adjacent sibling can be used

        // Deletion on B+ Tree (Case 1):
        // 1. No changes required

    }

    public static void searchValue() {
        // 1. Start from the root node
        // 2. While the node is not a leaf node
        // 1) We decide which pointer to follow based on the target key and the keys
        // maintained in the node
        // 2) Follow the pointer, arrive a new node
        // 3. Decide which pointer to follow
        // 4. Follow the pointer and retrieve the data block
    }

    public static void searchRange() {
        // 1. Start from the root node
        // 2. While the node is not a leaf node
        // 1) We decide which pointer to follow based on the target key (the lower bound
        // of the range) and the keys maintained in the node
        // 2) Follow the pointer, arrive a new node
        // 3. Decide which pointer to follow
        // 4. Follow the pointer and retrieve the data block
        // 5. Keep scanning the following leaf nodes and the data blocks pointed by the
        // pointers in the leaf nodes until we reach the upper bound of the range
    }

}