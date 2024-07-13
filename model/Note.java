package model;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Time stamped note: {@link String} content timestamped by a {@link Date}.
 * Implements {@link Comparable} so {@link Note}s can be sorted
 */
public class Note implements Comparable<Note>, Searchable<String>
{
	/**
	 * Creation date of this note
	 * Each time content is updated date should be update to a new date
	 * obtained from {@link Instant#now()}
	 */
	private Date date;

	/**
	 * The content of this note
	 */
	private String content;

	/**
	 * Valued constructor
	 * @param content the content to set
	 */
	public Note(String content)
	{
		if (content == null){
			throw new NullPointerException();
		}
		if (content.isEmpty()){
			throw new IllegalArgumentException();
		}
		date = Date.from(Instant.now());
		this.content = content;
	}

	/**
	 * Note content's creation date
	 * @return the content's creation date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * Content accessor
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Content mutator
	 * @param content the content to set
	 * @implSpec changes the content's creation date
	 */
	public void setContent(String content)
	{
		if (!(content == null) && !(content.isEmpty())){
			date = Date.from(Instant.now());
			this.content = content;
		}

	}

	/**
	 * Hash code from date and content
	 * @return hasch value based on {@link #date} and {@link #content}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int hash = 1;
		hash = (hash * prime) + (date == null ? 0 : date.hashCode());
		hash = (hash * prime) + (content == null ? 0 : content.hashCode());
		return hash;
	}

	/**
	 * Comparison with another object
	 * @param obj the other object to compare
	 * @return true of other object is a {@link Note} with the same
	 * {@link #content}. {@link #date} is ignored
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false ; }
		if (obj == this) {
			return true ; }
		if (getClass() == obj.getClass()) {
			Note other = (Note)obj ;
			if (content.compareTo(other.getContent()) == 0) {
				return true ; } }
		return false ; }

	/**
	 * String representation of this note : "[<date>] <content>"
	 * @return A String representation of this note with {@link #date} and
	 * {@link #content}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(DateFormat.getDateInstance().format(date));
		sb.append("] ");
		sb.append(content);
		return sb.toString();
	}

	/**
	 * Compare to another Note
	 * @param other the other note to compare to
	 * @return negative number if this is considered lower than other note, ie
	 * this has a previous {@link #date} or at least a smaller {@link #content}.
	 * 0 if both notes have the same {@link #date} and {@link #content} and
	 * positive number otherwise.
	 */
	@Override
	public int compareTo(Note other)
	{
		int dateCompare = date.compareTo(other.date);
		if (dateCompare != 0)
		{
			return dateCompare;
		}
		return content.compareTo(other.content);
	}

	/**
	 * Search for an element (in {@link #content} only)
	 * @param element the element to search
	 * @return true if the provided element could be found in this object,
	 * false otherwise
	 */
	@Override
	public boolean contains(String element)
	{
		return content.contains(element);
	}
}