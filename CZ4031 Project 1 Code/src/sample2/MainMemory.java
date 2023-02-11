package b_plus_tree;

import java.util.ArrayList;

class MainMemory {
    final ArrayList<Node> blocks;
    final ArrayList<Integer> dirtyBlocks;

    MainMemory(){
        blocks = new ArrayList<>();
        dirtyBlocks = new ArrayList<>();
    }

    Node getBlockAt(int i){
        if(i <= -1 || i >= blocks.size())
            return null;
        if(i != blocks.get(i).getBlockIndex()){
            System.out.println(i+" Wrong Index "+blocks.get(i).getBlockIndex());
        }
        return blocks.get(i);
    }

    void removeBlockAt(int i){
        dirtyBlocks.add(i);         // Mark a block dirty without actual deletion
    }

    int addBlock(Node node){
        if (dirtyBlocks.isEmpty()){
            node.setBlockIndex(blocks.size());
            blocks.add(blocks.size(), node);
            return (blocks.size()-1);
        }
        else{
            node.setBlockIndex(dirtyBlocks.get(0));
            blocks.add(dirtyBlocks.get(0), node);
            return dirtyBlocks.remove(0);
        }
    }

    void overWriteNode(Node node){
        blocks.set(node.getBlockIndex(), node);
    }


    public int CountRecords(){
        int cnt = 0;
        for (Node node: blocks){
            if(node.isLeaf() && !dirtyBlocks.contains(node.getBlockIndex()))
                cnt += (node.getKeyCount() + ((LeafNode)node).getDuplicatesCount());
        }
        return cnt;
    }
}
