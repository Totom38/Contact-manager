package model;

/**
 * A simple interface for all classe in which we can search elements
 * of type T
 * @param <T> The type of element to search
 */
public interface Searchable<T>
{
	/**
	 * Search for an element
	 * @param element the element to search
	 * @return true if the provided element could be found in this object,
	 * false otherwise
	 */
	public abstract boolean contains(T element);
}
