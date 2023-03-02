package tech.thatgravyboat.mcl.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PairList<A, B> implements Iterable<Pair<A, B>> {

    private final List<Pair<A, B>> entries = new ArrayList<>();

    public void add(A a, B b) {
        entries.add(new Pair<>(a, b));
    }

    public void add(Pair<A, B> pair) {
        entries.add(pair);
    }

    public int size() {
        return entries.size();
    }

    @NotNull
    @Override
    public Iterator<Pair<A, B>> iterator() {
        return entries.iterator();
    }
}
