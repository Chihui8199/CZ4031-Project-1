package storage;
import java.util.LinkedHashMap;


public class LRUCache {
    private LinkedHashMap<Integer, Block> map;
    private int SIZE;
    public LRUCache(int capacity) {
        map = new LinkedHashMap<>();
        SIZE = capacity;
    }

    public Block get(int key) {
        if(map.containsKey(key)) {
            Block value = map.remove(key);
            map.put(key, value);
            return value;
        }
        return null;
    }

    public void put(int key, Block value) {
        if(map.containsKey(key)) {
            map.remove(key);
        }else if(map.size() + 1 > SIZE) {
            map.remove(map.keySet().iterator().next());
        }
        map.put(key, value);
    }



}

