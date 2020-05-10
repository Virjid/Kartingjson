package me.virjid.karting.json.model;

import me.virjid.karting.json.exception.JSONTypeException;
import me.virjid.karting.json.util.JSON;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 * @author Virjid
 */
public class JSONArray implements List<Object>, Serializable {
    private static final long serialVersionUID = 7444159755983846597L;

    private List<Object> list = new ArrayList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<?> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<?> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @NotNull
    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public JSONObject getJSONObject(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JSONObject)) {
            throw new JSONTypeException("Type of value is not JsonObject");
        }

        return (JSONObject) obj;
    }

    public JSONArray getJSONArray(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JSONArray)) {
            throw new JSONTypeException("Type of value is not JsonArray");
        }

        return (JSONArray) obj;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toString(int indent) {
        return JSON.toJSONString(this, indent);
    }
}
