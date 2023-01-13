package tech.thatgravyboat.mcl.utils;

import java.util.HashMap;
import java.util.Map;

public class NoDuplicateHashMap<A, B> extends HashMap<A, B> {

    public NoDuplicateHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public NoDuplicateHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public NoDuplicateHashMap() {
        super();
    }

    public NoDuplicateHashMap(Map<? extends A, ? extends B> m) {
        super(m);
    }

    @Override
    public B put(A key, B value) {
        if (containsKey(key)) {
            throw new RuntimeException("Duplicate key " + key);
        }
        return super.put(key, value);
    }
}
