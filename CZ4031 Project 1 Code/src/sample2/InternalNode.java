package b_plus_tree;

import java.util.ArrayList;

class InternalNode extends Node{
    private final ArrayList<Integer> pointers;
    private final int childLevel;
    /**
     * Constructor which utilize super()
     * No modifiers to specify package accessibility
     * @param nodeType the node type parameter
     */
    InternalNode(NodeType nodeType, int childLevel) {
        super(nodeType);
        pointers = new ArrayList<>();
        this.childLevel = childLevel;
    }

    int getChildLevel(){
        return childLevel;
    }

    int getPointerAt(int index){
        if (index<=-1 || index >= getKeyCount()+1)
            return -1;
        return pointers.get(index);
    }

    void insertPointerAt(int index, int pointer){
        pointers.add(index, pointer);
    }
    void insertPointerAtLast(int pointer){insertPointerAt(pointers.size(), pointer);}
    int removePointerAt(int index){
        return pointers.remove(index);
    }

    int removePointerAtLast(){ return removePointerAt(pointers.size()-1);}

    @Override
    public String toString(){
        String str1 = super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(str1).append("\n");
        sb.append("[").append(getPointerAt(0)).append("]");
        for(int i = 0; i < this.getKeyCount(); i++) {
            sb.append("\t").append(this.getKeyAt(i)).append("\t");
            sb.append("[").append(this.getPointerAt(i+1)).append("]");
        }
        sb.append("\n----------------------------------------------\n");
        return sb.toString();
    }
}
