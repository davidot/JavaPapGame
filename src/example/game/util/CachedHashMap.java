package example.game.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A useful class to easily implement memoization in form of a cached {@link HashMap} <p> Make sure
 * you have implemented a version of {@link Object#hashCode()} else the internal HashMap could be
 * inconsistent. </p>
 * @param <K> The key Class
 * @param <V> The value Class
 *
 * @author davidot
 */
public abstract class CachedHashMap<K, V> {

    private Map<K, V> cache = new HashMap<K, V>();

    /**
     * Get the Value which can be calculated for the given Key
     * @param key the key to calculate with
     *
     * @return the value
     */
    public V get(K key) {
        if(!cache.containsKey(key)) {
            cache.put(key, calcValue(key));
        }
        return cache.get(key);
    }

    /**
     * Called to when it has to calculate a new Value from the given Key
     * @param key the key
     *
     * @return the value calculated
     */
    public abstract V calcValue(K key);

}
