package index;

import java.util.ArrayList;

public class NonLeafNode extends Node{
    
    ArrayList<Node> children;

    public NonLeafNode(){
        super();
        children = new ArrayList<Node>();
        setIsLeaf(false);
    }


    public ArrayList<Node> getChildren(){
        return children;
    }

    public Node getChild(int index) {
        return children.get(index);
    }

    public void addChild(Node child) {
        this.children.add(child);
    }


	public void removeChild(Node child) {
        this.children.remove(child);
	}



}
