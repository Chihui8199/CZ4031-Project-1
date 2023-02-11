package b_plus_tree;

import physical_storage.Pointer;

import java.util.ArrayList;
import java.util.HashMap;

class LeafNode extends Node {
    private int nextNodePointer;      //blockIndex of the next linked node
    private int prevNodePointer;      //blockIndex of previous linked node

    final ArrayList<Pointer> recordPointers = new ArrayList<>();
    final HashMap<Integer, ArrayList<Pointer>> duplicates = new HashMap<>();

    /**
     * Constructor which utilize super()
     * No modifiers to specify package accessibility
     *
     * @param nodeType        the actual node type
     * @param nextNodePointer block index of the next linking node
     * @param prevNodePointer block index of the previous linking node
     */

    LeafNode(NodeType nodeType,
             int prevNodePointer, int nextNodePointer) {
        super(nodeType);
        this.nextNodePointer = nextNodePointer;
        this.prevNodePointer = prevNodePointer;
    }

    //---------- Below are getters and setters for the class
    int getNextNodePointer() {
        return nextNodePointer;
    }

    void setNextNodePointer(int nextNodePointer) {
        this.nextNodePointer = nextNodePointer;
    }

    int getPrevNodePointer() {
        return prevNodePointer;
    }

    void setPrevNodePointer(int prevNodePointer) {
        this.prevNodePointer = prevNodePointer;
    }

    //--------- Below are ArrayList Operations for recordPointers
    @SuppressWarnings("unused")
    Pointer getPointerAt(int index) {
        return recordPointers.get(index);
    }

    @SuppressWarnings("unused")
    void replacePointerAt(int index, Pointer pointer) {
        recordPointers.set(index, pointer);
    }

    void insertPointerAt(int index, Pointer pointer) {
        recordPointers.add(index, pointer);
    }

    void insertPointerAtLast(Pointer pointer) {
        insertPointerAt(recordPointers.size(), pointer);
    }

    Pointer removePointerAt(int index) {
        return recordPointers.remove(index);
    }

    Pointer removePointerAtLast() {
        return removePointerAt(recordPointers.size() - 1);
    }

    boolean hasDuplicates(int key){
        return !(duplicates.get(key) == null);
    }

    void insertOneDuplicate(int key, Pointer record) {
        ArrayList<Pointer> keyDuplicates = duplicates.computeIfAbsent(key, k -> new ArrayList<>());
        keyDuplicates.add(record);
    }

    Pointer removeFirstDuplicate(int key){
        ArrayList<Pointer> keyDuplicates = duplicates.get(key);
        Pointer tmpPtr = keyDuplicates.remove(0);
        if(keyDuplicates.isEmpty())
            duplicates.remove(key);
        return tmpPtr;
    }

    ArrayList<Pointer> getAllDuplicates(int key){
        return duplicates.get(key);
    }

    ArrayList<Pointer> removeAllDuplicates(int key){
        return duplicates.remove(key);
    }

    void insertAllDuplicates(int key, ArrayList<Pointer> pointers){
        duplicates.put(key, pointers);
    }

    int getDuplicatesCount(){
        int cnt = 0;
        for (HashMap.Entry<Integer, ArrayList<Pointer>> set :
                duplicates.entrySet()) {
            cnt += set.getValue().size();
        }
        return cnt;
    }

    @Override
    public String toString(){
        String str1 = super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(str1).append("\n");
        sb.append("prev_index=").append(this.getPrevNodePointer())
                .append("; next_index=").append(this.getNextNodePointer()).append("\n");
        sb.append("Keys stored in current leaf:\n");
        for(int i = 0; i < this.getKeyCount(); i++) {
            sb.append(this.getKeyAt(i)).append(":").append(this.getPointerAt(i)).append("\n");
            if (hasDuplicates(this.getKeyAt(i))){
                ArrayList<Pointer>dup = getAllDuplicates(this.getKeyAt(i));
                sb.append("In total,").append(dup.size()+1).append(" copies found for key ")
                        .append(this.getKeyAt(i)).append("\n");
                /*
                        .append(this.getKeyAt(i)).append(", Printing first 5 copies:\n");
                for (int j=0; j<5 && j<dup.size(); j++)
                    sb.append(dup.get(j)).append("\n");*/
            }
        }
        sb.append("\n----------------------------------------------\n");
        return sb.toString();
    }
}
