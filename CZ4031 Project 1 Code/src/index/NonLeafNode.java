package index;

import java.util.ArrayList;

public class NonLeafNode extends Node{
    
    static Node parent;
    static ArrayList<Node> children;

    public NonLeafNode(){
        super();
        children = new ArrayList<Node>();
    }


    public ArrayList<Node> getChildren(){
        return children;
    }

    public Node getChild(int index) {
        return children.get(index);
    }

    public void setParent(NonLeafNode setparent){
        parent = setparent;
    }

    public Node getParent(){
        return parent;
    }

}
