package helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class IndexAwareSet<T> implements Set {
	private Set<T> set;

	public IndexAwareSet(Set<T> set) {
		this.set = set;
	}

	public int indexOf(T value) {
		int result = 0;
		for (T entry : set) {
			if (entry.equals(value)) return result;
			result++;
		}
		return -1;
	}

	@Override
	public int size() {
		return this.set.size();
	}

	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.set.contains(o);
	}

	@Override
	public Iterator iterator() {
		return this.set.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.set.toArray();
	}

	@Override
	public boolean add(Object o) {
		return this.set.add((T) o);
	}

	@Override
	public boolean remove(Object o) {
		return this.set.remove(o);
	}

	@Override
	public boolean addAll(Collection collection) {
		return this.set.addAll(collection);
	}

	@Override
	public void clear() {
		this.set.clear();
	}

	@Override
	public boolean removeAll(Collection collection) {
		return this.set.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection collection) {
		return this.set.retainAll(collection);
	}

	@Override
	public boolean containsAll(Collection collection) {
		return this.set.containsAll(collection);
	}

	@Override
	public Object[] toArray(Object[] objects) {
		return this.set.toArray(objects);
	}
}
