package index;

import java.util.ArrayList;

/*
 * Class representing a nonLeafNode in a B+ tree
 */
public class NonLeafNode extends Node {

    ArrayList<Node> children;

    /**
     * Constructs a NonLeafNode object with an ArrayList of Node type.
     *
     * @param children The ArrayList of Node type, which contains the children nodes
     *                 of the non leaf node
     */
    public NonLeafNode() {
        super();
        children = new ArrayList<Node>();
        setIsLeaf(false);
    }

    /**
     * Get all children of current parent node.
     *
     * @return ArrayList<Node> the list of child nodes currently in parent.
     */
    public ArrayList<Node> getChildren() {
        return children;
    }

    /**
     * Get child node from argument index of parent node.
     *
     * @param index the pointer of the child node.
     * @return the child node pointed at by the index.
     */
    public Node getChild(int index) {
        return children.get(index);
    }

    /**
     * Add argument child node into parent node.
     *
     * @param child the child node that will be added into parent node.
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Remove argument child node from parent node.
     *
     * @param child the child node that will be removed from parent node.
     */
    public void removeChild(Node child) {
        this.children.remove(child);
    }

}
