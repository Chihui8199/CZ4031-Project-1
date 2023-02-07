import java.util.ArrayList;


class BPlusTreeNode {
    private int order; // represent n in lecture notes

    private ArrayList<Integer> keys;
    private ArrayList<BPlusTreeNode> children;
    private boolean isLeaf;

    BPlusTreeNode() { 
        // followed the lecture note definition for creating leaf node
        //LEct 7 slide 17-21
        int minimumNumberOfKeys = (int) Math.floor((this.order + 1) / 2.0);
        this.keys = new ArrayList<>(minimumNumberOfKeys);
        this.children = new ArrayList<>(minimumNumberOfKeys + 1);
        this.isLeaf = true;
    }
}
