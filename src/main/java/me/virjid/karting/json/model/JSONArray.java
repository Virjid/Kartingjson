package me.virjid.karting.json.model;

import me.virjid.karting.json.exception.JSONTypeException;
import me.virjid.karting.json.util.JSON;
import me.virjid.karting.json.util.ReflectUtil;
import me.virjid.karting.json.util.StringUtil;
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


    public String getString(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof String)) {
            throw new JSONTypeException("Type of value is not String");
        }

        return (String) obj;
    }

    public Long getLong(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Long)) {
            throw new JSONTypeException("Type of value is not Long");
        }

        return (Long) obj;
    }

    public Integer getInteger(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Integer)) {
            throw new JSONTypeException("Type of value is not Integer");
        }

        return (Integer) obj;
    }

    public Short getShort(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Short)) {
            throw new JSONTypeException("Type of value is not Short");
        }

        return (Short) obj;
    }

    public Byte getByte(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Byte)) {
            throw new JSONTypeException("Type of value is not Byte");
        }

        return (Byte) obj;
    }

    public Character getCharacter(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Character)) {
            throw new JSONTypeException("Type of value is not Character");
        }

        return (Character) obj;
    }

    public Double getDouble(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Double)) {
            throw new JSONTypeException("Type of value is not Double");
        }

        return (Double) obj;
    }

    public Float getFloat(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Float)) {
            throw new JSONTypeException("Type of value is not Float");
        }

        return (Float) obj;
    }

    public Boolean getBoolean(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof Boolean)) {
            throw new JSONTypeException("Type of value is not Boolean");
        }

        return (Boolean) obj;
    }

    public JSONObject getJSONObject(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JSONObject)) {
            throw new JSONTypeException("Type of value is not JSONObject");
        }

        return (JSONObject) obj;
    }

    public JSONArray getJSONArray(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JSONArray)) {
            throw new JSONTypeException("Type of value is not JSONArray");
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

    public void toList(List<Object> list, Class<?> itemType) throws Exception {
        for (Object item : this) {
            if (ReflectUtil.isNotJSONObject(itemType)) {
                list.add(item);
            }
            else if (ReflectUtil.isDateTime(itemType)) {
                list.add(StringUtil.stringToDateTime((String) item, itemType));
            }
            else {
                Object model = item.getClass().newInstance();
                ((JSONObject) item).toObject(model);
            }
        }
    }
}
