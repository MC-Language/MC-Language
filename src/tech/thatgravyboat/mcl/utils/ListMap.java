package tech.thatgravyboat.mcl.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ListMap<A, B> implements Map<A, B> {

    private final Map<A, B> map;
    private final Function<B, A> keyFunction;

    public ListMap(Function<B, A> keyFunction, Map<A, B> map) {
        super();
        this.keyFunction = keyFunction;
        this.map = map;
    }

    public static <A, B> ListMap<A, B> of(Function<B, A> keyFunction, Map<A, B> map) {
        return new ListMap<>(keyFunction, map);
    }

    public B put(B value) {
        A key = keyFunction.apply(value);
        return map.put(key, value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public B get(Object key) {
        return map.get(key);
    }

    @Nullable
    @Override
    public B put(A key, B value) {
        return map.put(key, value);
    }

    @Override
    public B remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends A, ? extends B> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<A> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<B> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<A, B>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
