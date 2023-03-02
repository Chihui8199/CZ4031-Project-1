package index;

import java.lang.reflect.Field;

public class PerformanceRecorder {

    private static int totalNode;
    private static int treeDegree;
    private static int totalNodeReads;
    private static int totalRangeNodeReads;


    public int getTotalNodes() {
        return totalNode;
    }

    static void addOneNode() {
        totalNode++;
    }

    private void deleteOneNode() {
        totalNode--;
    }


    public int getTreeDegree() {
        return treeDegree;
    }

    public static void addOneTreeDegree() {
        treeDegree++;
    }

    public static void deleteOneTreeDegree() {
        treeDegree--;
    }


    public int getNodeReads() {
        return totalNodeReads;
    }

    static void addOneNodeReads() {
        totalNodeReads++;
    }

    public int getRangeNodeReads() {
        return totalRangeNodeReads;
    }

    static void addOneRangeNodeReads() {
        totalRangeNodeReads++;
        addOneNodeReads();
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
