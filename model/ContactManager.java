package model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Contact Manager containing the contacts and all other related data.
 */
public class ContactManager implements Set<Contact>
{
	/**
	 * The observable list of all contacts
	 */
	private ObservableList<Contact> contacts;

	/**
	 * Filtered list of {@link Contact}s based on a filtering {@link Predicate}
	 * @implNote This list can be fed to a {@link javafx.scene.control.ListView}
	 */
	private FilteredList<Contact> filteredContacts;

	/**
	 * Property used to filter contacts based on {@link Contact.Type}
	 */
	private ObjectProperty<Contact.Type> typeFiltering;

	/**
	 * Property used to filter contact based on text
	 */
	private StringProperty searched;

	/**
	 * Predicate to apply on {@link #contacts} to obtain {@link #filteredContacts}
	 */
	private Predicate<Contact> predicate;

	/**
	 * Default constructor.
	 * Creates an empty Contact list.
	 */
	public ContactManager()
	{
		contacts = FXCollections.<Contact>observableArrayList();
		typeFiltering = new SimpleObjectProperty<Contact.Type>(Contact.Type.ALL);
		searched = new SimpleStringProperty("");
		/*
		 * Set default Predicate to filter nothing
		 * changed in #setPredicate
		 */
		predicate = new Predicate<Contact>()
		{
			@Override
			public boolean test(Contact t)
			{
				return true;
			}
		};
		filteredContacts = new FilteredList<Contact>(contacts, predicate);
	}

	/**
	 * Contact List access
	 * @return the contacts list
	 */
	public ObservableList<Contact> getContacts()
	{
		return contacts;
	}

	/**
	 * Filtered List access
	 * @return the filtered contacts list
	 */
	public FilteredList<Contact> getFilteredContacts()
	{
		return filteredContacts;
	}

	/**
	 * Get the current filtering predicate
	 * @return the predicate
	 */
	public Predicate<Contact> getPredicate()
	{
		return predicate;
	}

	/**
	 * Set {@link #predicate} and apply to {@link #filteredContacts}
	 * @param type the type of contacts to filter
	 * @param search the search string to search in contacts
	 */
	public void setPredicate(Contact.Type type, String search)
	{
		predicate = new Predicate<Contact>()
		{
			@Override
			public boolean test(Contact contact) {
				if (!(contact.getType() == type)) {
					return false ; }
				if (!contact.contains(search)) {
					return false ; }
				return true ; } } ;
		filteredContacts.setPredicate(predicate) ; }

	/**
	 * Type filtering property access
	 * @return the type filtering property
	 */
	public final ObjectProperty<Contact.Type> typeFilteringProperty()
	{
		return typeFiltering;
	}

	/**
	 * Type filtering value access
	 * @return type filtering value
	 */
	public final Contact.Type getTypeFiltering()
	{
		return typeFilteringProperty().get();
	}

	/**
	 * Set type filtering value
	 * @param typeFiltering the new type filtering value to set
	 */
	public final void setTypeFiltering(final Contact.Type typeFiltering)
	{
		typeFilteringProperty().set(typeFiltering);
	}

	/**
	 * Search property access
	 * @return the searche property
	 */
	public final StringProperty searchedProperty()
	{
		return searched;
	}

	/**
	 * Search property value
	 * @return the search value
	 */
	public final String getSearched()
	{
		return searchedProperty().get();
	}

	/**
	 * Search property set value
	 * @param searched the new search value to set
	 */
	public final void setSearched(final String searched)
	{
		searchedProperty().set(searched);
	}

	@Override
	public int size()
	{
		return contacts.size();
	}


	@Override
	public boolean isEmpty()
	{
		return contacts.isEmpty();
	}


	@Override
	public boolean contains(Object o)
	{
		return contacts.contains(o);
	}


	@Override
	public Iterator<Contact> iterator()
	{
		return contacts.iterator();
	}


	@Override
	public Object[] toArray()
	{
		return contacts.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a)
	{
		return contacts.toArray(a);
	}

	@Override
	public boolean add(Contact c) throws NullPointerException
	{
		Objects.requireNonNull(c);

		if (contains(c))
		{
			return false;
		}

		boolean added = contacts.add(c);
		Collections.sort(contacts);
		return added;
	}


	@Override
	public boolean remove(Object o)
	{
		return contacts.remove(o);
	}


	@Override
	public boolean containsAll(Collection<?> c)
	{
		return contacts.containsAll(c);
	}


	@Override
	public boolean addAll(Collection<? extends Contact> c)
	{
		return contacts.addAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c)
	{
		return contacts.retainAll(c);
	}


	@Override
	public boolean removeAll(Collection<?> c)
	{
		return contacts.removeAll(c);
	}


	@Override
	public void clear()
	{
		contacts.clear();
	}
}
		// Compare partAValues first
