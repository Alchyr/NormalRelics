package normalrelics.util;

import normalrelics.patches.DeepDreamPatch;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class DreamHandList<E> extends ArrayList<E>
        implements List<E>, RandomAccess, Cloneable {
    private final ArrayList<E> parent;

    //the sub array list represents up to limit objects of the parent arraylist starting from index 0
    public DreamHandList(ArrayList<E> parent) {
        this.parent = parent;
    }

    //non-modifying or accessing methods simply refer straight to parent
    @Override
    public void trimToSize() {
        parent.trimToSize();
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        parent.ensureCapacity(minCapacity);
    }

    @Override
    public int size() {
        return Math.min(DeepDreamPatch.getDreamLimit(), parent.size());
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    private int off() {
        return parent.size() - size();
    }
    private List<E> sub() {
        return parent.subList(off(), parent.size());
    }

    @Override
    public boolean contains(Object o) {
        return sub().contains(o);
    }

    @Override
    public int indexOf(Object o) {
        int index = super.indexOf(o) - off();
        if (index < 0) return -1;
        return index;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = parent.size()-1; i >= off(); i--)
                if (parent.get(i)==null)
                    return i;
        } else {
            for (int i = parent.size()-1; i >= off(); i--)
                if (o.equals(parent.get(i)))
                    return i;
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        ArrayList<E> parentClone = (ArrayList<E>) parent.clone();

        return new DreamHandList<>(parentClone);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOfRange(parent.toArray(), off(), parent.size());
    }
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] src = (T[]) Arrays.copyOfRange(parent.toArray(), off(), parent.size(), a.getClass());
        if (a.length < src.length)
            // Make a new array of a's runtime type, but my contents:
            return src;

        System.arraycopy(src, 0, a, 0, src.length);

        if (a.length > src.length)
            a[src.length] = null;
        return a;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return parent.get(off() + index);
    }

    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        return parent.set(off() + index, element);
    }

    @Override
    public boolean add(E e) {
        parent.add(e);
        return true;
    }

    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        parent.add(off() + index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return parent.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        return parent.addAll(off() + index, c);
    }

    @Override
    public E remove(int index) {
        return sub().remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return sub().remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return sub().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return sub().retainAll(c);
    }

    @Override
    public void clear() {
        if (size() > 0) {
            sub().clear();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return sub().listIterator();
    }

    public ListIterator<E> listIterator(final int index) {
        return sub().listIterator(index);
    }

    private void rangeCheck(int index) {
        if (index >= size())
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > this.size())
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+this.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size()); //from and to are <= size, off() + size = parent.size
        return parent.subList(off() + fromIndex, off() + toIndex);
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return sub().removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        for (int i=off(); i < parent.size(); ++i) {
            parent.set(i, operator.apply(parent.get(i)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        E[] elements = (E[]) toArray();
        Arrays.sort(elements, c);

        for (int i = 0; i < elements.length; ++i) {
            parent.set(off() + i, elements[i]);
        }
    }
}
