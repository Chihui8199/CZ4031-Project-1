package b_plus_tree;

import java.lang.reflect.Field;

public class PerformanceRecorder {

    private int totalNode;
    private int totalInternalNode;
    private int totalLeafNode;

    private int totalNodeReads;
    private int totalInternalNodeReads;
    private int totalLeafNodeReads;

    private int totalNodeWrites;
    private int totalInternalNodeWrites;
    private int totalLeafNodeWrites;
    private int totalInsertionReads;
    private int totalDeletionReads;
    private int totalSearchReads;
    private int totalRangeQueryReads;
    private int totalInsertionWrites;
    private int totalDeletionWrites;

    private int totalInsertions;
    private int totalDeletions;
    private int totalSearches;
    private int totalRangeQueries;

    private int totalSplits;
    private int totalRootSplits;
    private int totalInternalNodeSplits;
    private int totalLeafSplits;

    private int totalMerges;
    private int totalInternalNodeMerges;
    private int totalLeafMerges;

    

    private int totalRedistributes;
    private int totalInternalNodeRedistributes;
    private int totalLeafRedistributes;
    private int treeDegree;


    private void addOneNode() {
        totalNode++;
    }

    public void addOneInternalNode() {

        totalInternalNode++;
        addOneNode();
        addOneInternalNodeWrites();
    }

    public void addOneLeafNode() {

        totalLeafNode++;
        addOneNode();
        addOneLeafNodeWrites();
    }

    private void addOneNodeReads() {
        totalNodeReads++;
    }

    public void addOneInternalNodeReads() {
        totalInternalNodeReads++;
        addOneNodeReads();
    }

    public void addOneLeafNodeReads() {
        totalLeafNodeReads++;
        addOneNodeReads();
    }

    private void addOneNodeWrites() {
        totalNodeWrites++;
    }

    private void addToNodeWrites(int times) {
        totalNodeWrites += times;
    }

    public void addOneInternalNodeWrites() {
        totalInternalNodeWrites++;
        addOneNodeWrites();
    }
    public void addToInternalNodeWrites(int times) {
        totalInternalNodeWrites+=times;
        addToNodeWrites(times);
    }

    public void addOneLeafNodeWrites() {
        totalLeafNodeWrites++;
        addOneNodeWrites();
    }
    public void addToLeafNodeWrites(int times) {
        totalLeafNodeWrites+=times;
        addToNodeWrites(times);
    }


    public void addOneInsertionWrites() {
        totalInsertionWrites++;
    }
    public void addToInsertionNodeWrites(int times){totalInsertionWrites+=times;}
    public void addToDeletionWrites(int times) {
        totalDeletionWrites+=times;
    }


    public void addOneInsertions() {
        totalInsertions++;
    }

    public void addOneDeletions() {
        totalDeletions++;
    }
    public void addOneSearches() {
        totalSearches++;
    }
    public void addOneRangeQueries() {
        totalRangeQueries++;
    }

    public void addOneInsertionReads() {
        totalInsertionReads++;
    }
    public void addOneDeletionReads() {
        totalDeletionReads++;
    }
    public void addOneSearchReads() {
        totalSearchReads++;
    }

    public void addOneRangeQueryReads() {
        totalRangeQueryReads++;
    }

    private void addOneSplits() {
        totalSplits++;
    }

    public void addOneRootSplits() {
        totalRootSplits++;
        addOneSplits();
    }

    public void addOneInternalNodeSplits() {
        totalInternalNodeSplits++;
        addOneSplits();
    }
    public void addOneLeafSplits() {
        totalLeafSplits++;
        addOneSplits();
    }
    private void addOneMerges() {
        totalMerges++;
    }

    public void addOneInternalNodeMerges() {
        totalInternalNodeMerges++;
        addOneMerges();
    }

    public void addOneLeafMerges() {
        totalLeafMerges++;
        addOneMerges();
    }

    private void addOneRedistributes() {
        totalRedistributes++;
    }

    public void addOneInternalNodeRedistributes() {
        totalInternalNodeRedistributes++;
        addOneRedistributes();
    }

    public void addOneLeafRedistributes() {
        totalLeafRedistributes++;
        addOneRedistributes();
    }
    public void addOneTreeDegree(){
        treeDegree++;
    }

    private void deleteOneNode(){totalNode--;}

    public void deleteOneInternalNode() {

        totalInternalNode++;
        deleteOneNode();
    }
    public void deleteOneLeafNode() {

        totalLeafNode++;
        deleteOneNode();
    }
    public void deleteOneTreeDegree(){
        treeDegree--;
    }


    public int getTotalNodes() {
        return totalNode;
    }
    public int getInternalNode() {
        return totalInternalNode;
    }

    public int getLeafNode() {
        return totalLeafNode;
    }

    public int getNodeReads() {
        return totalNodeReads;
    }

    public int getInternalNodeReads() {
        return totalInternalNodeReads;
    }

    public int getLeafNodeReads() {
        return totalLeafNodeReads;
    }

    public int getNodeWrites() {
        return totalNodeWrites;
    }
    public int getInternalNodeWrites() {
        return totalInternalNodeWrites;
    }
    public int getLeafNodeWrites() {
        return totalLeafNodeWrites;
    }

    public int getInsertionReads() {
        return totalInsertionReads;
    }


    public int getDeletionReads() {
        return totalDeletionReads;
    }


    public int getSearchReads() {
        return totalSearchReads;
    }


    public int getRangeQueryReads() {
        return totalRangeQueryReads;
    }

    public int getInsertionWrites() {
        return totalInsertionWrites;
    }

    public int getDeletionWrites() {
        return totalDeletionWrites;
    }

    public int getInsertions() {
        return totalInsertions;
    }

    public int getDeletions() {
        return totalDeletions;
    }

    public int getSearches() {
        return totalSearches;
    }


    public int getRangeQueries() {
        return totalRangeQueries;
    }

    public int getSplits() {
        return totalSplits;
    }


    public int getRootSplits() {
        return totalRootSplits;
    }

    public int getInternalNodeSplits() {
        return totalInternalNodeSplits;
    }

    public int getLeafSplits() {
        return totalLeafSplits;
    }

    public int getTotalMerges() {
        return totalMerges;
    }

    public int getTotalInternalNodeMerges() {
        return totalInternalNodeMerges;
    }

    public int getTotalLeafMerges() {
        return totalLeafMerges;
    }

    public int getTotalRedistributes() {
        return totalRedistributes;
    }

    public int getTotalInternalNodeRedistributes() {
        return totalInternalNodeRedistributes;
    }

    public int getTotalLeafRedistributes() {
        return totalLeafRedistributes;
    }


    public int getTreeDegree(){
        return treeDegree;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                sb.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
