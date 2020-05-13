package me.virjid.karting.json.parser;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Virjid
 */
public class TokenList implements List<Token> {
    private List<Token> tokens = new ArrayList<>();

    private int pos = 0;

    public Token peek() {
        return hasNext() ? tokens.get(pos) : null;
    }

    public Token peekPrevious() {
        return pos - 1 < 0 ? null : tokens.get(pos - 2);
    }

    public Token next() {
        return tokens.get(pos++);
    }

    public void back() {
        pos = Math.max(0, --pos);
    }

    public boolean hasNext() {
        return pos < tokens.size();
    }

    @Override
    public int size() {
        return tokens.size();
    }

    @Override
    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return tokens.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return tokens.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(T[] a) {
        return tokens.toArray(a);
    }

    @Override
    public boolean add(Token token) {
        return tokens.add(token);
    }

    @Override
    public boolean remove(Object o) {
        return tokens.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return tokens.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Token> c) {
        return tokens.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Token> c) {
        return tokens.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return tokens.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return tokens.retainAll(c);
    }

    @Override
    public void clear() {
        tokens.clear();
    }

    @Override
    public Token get(int index) {
        return tokens.get(index);
    }

    @Override
    public Token set(int index, Token element) {
        return tokens.set(index, element);
    }

    @Override
    public void add(int index, Token element) {
        tokens.add(index, element);
    }

    @Override
    public Token remove(int index) {
        return tokens.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return tokens.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return tokens.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Token> listIterator() {
        return tokens.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Token> listIterator(int index) {
        return tokens.listIterator(index);
    }

    @NotNull
    @Override
    public List<Token> subList(int fromIndex, int toIndex) {
        return tokens.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        tokens.forEach(token -> sb.append(token).append("\n"));

        return sb.toString();
    }
}
