package index;

public class PerformanceRecorder {

    private static int totalNode;
    private static int treeDegree;
    private static int totalNodeReads;
    private static int totalRangeNodeReads;

    /**
     * Returns total node counted by addOneNode.
     *
     * @return the total value of nodes counted.
     */
    public int getTotalNodes() {
        return totalNode;
    }

    /**
     * Increments totalNode by one with every call.
     */
    static void addOneNode() {
        totalNode++;
    }

    /**
     * Returns the degree of the B+ tree.
     * 
     * @return the degree of the B+ tree.
     */
    public int getTreeDegree() {
        return treeDegree;
    }

    /**
     * Increments treeDegree by one with every call.
     */
    public static void addOneTreeDegree() {
        treeDegree++;
    }

    /**
     * Decrements treeDegree by one with every call.
     */
    public static void deleteOneTreeDegree() {
        treeDegree--;
    }

    /**
     * Returns the total node reads counted by totalNodeReads
     * 
     * @return the total node reads counted by totalNodeReads
     */
    public int getNodeReads() {
        return totalNodeReads;
    }

    /**
     * Increments totalNodeReads by one with every call.
     */
    static void addOneNodeReads() {
        totalNodeReads++;
    }

    /**
     * Returns the total range nodes counted by addOneRangeNodeReads.
     * 
     * @return the total range nodes counted by addOneRangeNodeReads.
     */
    public int getRangeNodeReads() {
        return totalRangeNodeReads;
    }

    /**
     * Increments totalRangeNodeReads by one with every call.
     */
    static void addOneRangeNodeReads() {
        totalRangeNodeReads++;
        addOneNodeReads();
    }

}
