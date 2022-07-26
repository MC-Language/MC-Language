package tech.thatgravyboat.mcl.builder;

import java.util.Iterator;

public class PeekableIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    public PeekableIterator (Iterator<T> iterator) { this.iterator = iterator; }

    private boolean peeked = false;
    private T peeked_value = null;

    public boolean hasNext () { return iterator.hasNext () || peeked; }

    public T next() {
        T value;
        if (peeked) {
            peeked = false;
            value = peeked_value;
        } else {
            value = iterator.hasNext() ? iterator.next() : null;
        }
        return value;
    }

    public T peek() {
        if (!peeked) {
            peeked = true;
            peeked_value = iterator.hasNext() ? iterator.next() : null;
        }
        return peeked_value;
    }
}
