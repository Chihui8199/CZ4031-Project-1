package storage;

import java.util.LinkedHashMap;

/**
 * 1. The LRUCache class implements Least Recently Used cache, which stores a fixed number of Block objects.
 * <p>
 * 2. The cache is implemented using a LinkedHashMap, which ensures that the most recently accessed blocks are stored at the end of the map,
 * and the least recently accessed blocks are stored at the front of the map.
 * <p>
 * 3. When the cache is full, the least recently used block is evicted from the cache to make room for a new block.
 */
public class LRUCache {
    // LinkedHashMap used to store the blocks in the cache.
    private LinkedHashMap<Integer, Block> map;
    // maximum number of blocks that can be stored in the cache.
    private int SIZE;

    /**
     * Constructs an LRUCache with the specified capacity.
     *
     * @param capacity the maximum number of blocks that can be stored in the cache.
     */
    public LRUCache(int capacity) {
        map = new LinkedHashMap<>();
        SIZE = capacity;
    }

    /**
     * Returns the Block associated with the specified key, or null if the key is not present in the cache.
     * If the Block is present in the cache, it is moved to the end of the LinkedHashMap to indicate that it has been accessed.
     *
     * @param key the key used to retrieve the Block.
     * @return the Block associated with the specified key, or null if the key is not present in the cache.
     */
    public Block get(int key) {
        if (map.containsKey(key)) {
            Block value = map.remove(key);
            map.put(key, value);
            return value;
        }
        return null;
    }


    /**
     * Associates the specified Block with the specified key in the cache.
     * If the key is already present in the cache, the existing Block is replaced with the specified Block.
     * If the cache is full, the least recently used Block is evicted from the cache to make room for the new Block.
     *
     * @param key   the key used to associate the Block with.
     * @param value the Block to be associated with the key.
     */
    public void put(int key, Block value) {
        if (map.containsKey(key)) {
            map.remove(key);
        } else if (map.size() + 1 > SIZE) {
            map.remove(map.keySet().iterator().next());
        }
        map.put(key, value);
    }


}

