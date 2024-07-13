package model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;

/**
 * Base class of all contacts.
 * @implSpec this class is declared abstract for the sole purpose of forcing the
 * instantiation of its sub-classes
 */
public abstract class Contact implements Comparable<Contact>, Searchable<String>
{

	/**
	 * The name of this contact
	 */
	protected String name;

	/**
	 * Logo or Icon for this contact
	 * (Using JavaFx images)
	 */
	protected Image image;

	/**
	 * URI path to file used to create {@link #image}
	 */
	protected URI imagePath;

	/**
	 * Map of all phone numbers: The key represents the name of this phone
	 * number, such as "Office", "Home", "Mobile", etc.
	 * Common names can be found in ...
	 */
	protected Map<String, PhoneNumber> phoneNumbers;

	/**
	 * Map of all addresses of this contact: The Key represents the name of this
	 * address.
	 */
	protected Map<String, Address> addresses;

	/**
	 * Map of all e-mails of this contact: each email is keyed by its name
	 * (office, home, etc).
	 */
	protected Map<String, URI> emails;

	/**
	 * Map of links for this contact: each link is keyed by its name (website,
	 * intranet, etc).
	 */
	protected Map<String, URI> links;

	/**
	 * Map of notes of this contact: each note is keyed by its title (digicode,
	 * etc).
	 */
	protected Map<String, Note> notes;

	/**
	 * Valued Constructor
	 * @param name the name of this contact
	 * @param imagePath an image for this contact (or null if thre is no image)
	 * @param number a phone number for this contact (or null ...)
	 * @param email an email for this contact (or null ..)
	 * @param link a link for this contact (or null ...)
	 * @param note a note for this contact (or null ...)
	 * @throws IllegalArgumentException if imagePath is invalid
	 */
	protected Contact(String name,
	                  URI imagePath,
	                  PhoneNumber number,
	                  URI email,
	                  URI link,
	                  Note note)
	{
		this.name = new String(name);
		this.imagePath = imagePath;
		if (imagePath != null)
		{
			image = new Image(this.imagePath.toString());
		}
		phoneNumbers = new HashMap<>();
		addresses = new HashMap<>();
		emails = new HashMap<>();
		links = new HashMap<>();
		notes = new HashMap<>();
		if (number != null)
		{
			String phoneTitle = (number.isMobile() ? "mobile" : (number
			    .isCommercial() ? "bureau" : "maison"));
			phoneNumbers.put(phoneTitle, number);
		}
		if (email != null)
		{
			emails.put("email", email);
		}
		if (link != null)
		{
			links.put("site web", link);
		}
		if (note != null)
		{
			notes.put("note", note);
		}
	}

	/**
	 * Copy constructor
	 * @param c the contact to copy
	 */
	protected Contact(Contact c) {
		this(c.name, c.imagePath, null, null, null, null) ; 
		for (Map.Entry<String, PhoneNumber> entry : c.phoneNumbers.entrySet()) {
			phoneNumbers.put(entry.getKey(), entry.getValue()) ; }
		for (Map.Entry<String, Address> entry : c.addresses.entrySet()) {
			addresses.put(entry.getKey(), entry.getValue()) ; }
		for (Map.Entry<String, URI> entry : c.emails.entrySet()) {
			emails.put(entry.getKey(), entry.getValue()) ; }
		for (Map.Entry<String, URI> entry : c.links.entrySet()) {
			links.put(entry.getKey(), entry.getValue()) ; }
		for (Map.Entry<String, Note> entry : c.notes.entrySet()) {
			notes.put(entry.getKey(), entry.getValue()) ; } }

	/**
	 * Accessor to contact name
	 * @return the contact's name
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name){
		if (!(name == null) && !(name.isEmpty())){
			this.name = name;
		}
	}

	/**
	 * Image accessor
	 * @return the image
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * Get image path
	 * @return the image path
	 */
	public URI getImagePath()
	{
		return imagePath;
	}

	/**
	 * Image mutator
	 * @param imageURI the URI of the image to set
	 * @throws NullPointerException if provided {@link URI} is null
	 * @throws IllegalArgumentException if provided {@link URI} is invalid
	 */
	public void setImage(URI imageURI) throws NullPointerException, IllegalArgumentException {
		if (imageURI == null) {
			throw new NullPointerException() ; }
		this.imagePath = imageURI ;
		this.image = new Image(imagePath.toString()) ; }

	/**
	 * Get the phone number designated by the provided key (iff it exist)
	 * @param key the title of the searched phone number
	 * @return the phone number corresponding to this key
	 */
	public PhoneNumber getPhoneNumber(String key)
	{
		return phoneNumbers.get(key);
	}

	/**
	 * Phone numbers titles set.
	 * Iterating over this set will obtain all phone numbers titles
	 * @return the set of phone numbers titles
	 */
	public Set<String> getPhoneNumberKeySet()
	{
		return phoneNumbers.keySet();
	}

	/**
	 * Adds a new phone number entitled "title" among phone numbers
	 * @param title the title of the phone number
	 * @param number the number to add
	 * @return true if phone numbers didn't contained a phone number entitled
	 * "title" and the new phone number has been added
	 */
	public boolean addPhoneNumber(String title, PhoneNumber number)
	{
		if (phoneNumbers.containsKey(title))
		{
			System.err
			    .println("Key " + title + " already exist in phone numbers");
			return false;
		}

		phoneNumbers.put(title, number);
		return true;
	}

	/**
	 * Remove the phone number corresponding to the provided key (iff it
	 * existed)
	 * @param key the title of the phone number to remove
	 * @return true if such a phone number existed and has been removed
	 */
	public boolean removePhoneNumber(String key)
	{
		if (!phoneNumbers.containsKey(key))
		{
			return false;
		}

		phoneNumbers.remove(key);
		return true;
	}

	/**
	 * Get the addres corresponding to this key
	 * @param title the title of the address
	 * @return the corresponding address or null if there is no such address
	 * among {@link #addresses}
	 */
	public Address getAddress(String title)
	{
		return addresses.get(title);
	}

	/**
	 * Addresses titles set.
	 * Iterating over this set will obtain all addresses titles
	 * @return the set of addresses titles
	 */
	public Set<String> getAddressKeySet()
	{
		return addresses.keySet();
	}

	/**
	 * Adds a new address entitled "title" to the addresses
	 * @param title the title of the address to add
	 * @param address the address to add
	 * @return true if there was no address called "title" among
	 * {@link #addresses} and this new address has been added
	 */
	public boolean addAddress(String title, Address address) {
		if (addresses.containsKey(title)) {
			System.err
			    .println("Key " + title + " already exist in phone numbers") ;
			return false ; }
		addresses.put(title, address) ;
		return true ; }

	/**
	 * Remove the address entitled "title" among {@link #addresses}
	 * @param title the title of the address to remove
	 * @return true if the corresponding addres has been removed, false
	 * if there was no such address.
	 */
	public boolean removeAddress(String title) {
		if (!addresses.containsKey((title))) {
			System.err.println("Key" + title + " doesn't exist in addresses") ;
			return false ; }
		addresses.remove(title) ;
		return true ; }

	/**
	 * Get the email corresponding to this key
	 * @param title the title of the emmail
	 * @return the corresponding email or null if there is no such email
	 * among {@link #emails}
	 */
	public URI getEmail(String title)
	{
		return emails.get(title);
	}

	/**
	 * emails titles set.
	 * Iterating over this set will obtain all email titles
	 * @return the set of email titles
	 */
	public Set<String> getEmailKeySet()
	{
		return emails.keySet();
	}

	/**
	 * Adds a new email entitled "title" to the {@link #emails}
	 * @param title the title of the email to add
	 * @param email the email to add
	 * @return true if there was no email called "title" among
	 * {@link #emails} and this new email has been added
	 */
	public boolean addEmail(String title, URI email) {
		if (emails.containsKey(title)) {
			System.err
			    .println("Key " + title + " already exist in emails") ;
			return false ; }
		emails.put(title, email) ;
		return true ; }

	/**
	 * Remove the email entitled "title" among {@link #emails}
	 * @param title the title of the email to remove
	 * @return true if the corresponding email has been removed, false
	 * if there was no such email.
	 */
	public boolean removeEmail(String title) {
		if (!emails.containsKey((title))) {
			System.err.println("Key" + title + " doesn't exist in emails") ;
			return false ; }
		emails.remove(title) ;
		return true ; }

	/**
	 * Get the link corresponding to this key
	 * @param title the title of the link
	 * @return the corresponding link or null if there is no such link
	 * among {@link #links}
	 */
	public URI getLink(String title)
	{
		return links.get(title);
	}

	/**
	 * links titles set.
	 * Iterating over this set will obtain all links titles
	 * @return the set of links titles
	 */
	public Set<String> getLinksKeySet()
	{
		return links.keySet();
	}

	/**
	 * Adds a new link entitled "title" to the {@link #links}
	 * @param title the title of the link to add
	 * @param link the link to add
	 * @return true if there was no link called "title" among
	 * {@link #links} and this new link has been added
	 */
	public boolean addLink(String title, URI link) {
		if (links.containsKey(title)) {
			System.err
			    .println("Key " + title + " already exist in links") ;
			return false ; }
		links.put(title, link) ;
		return true ; }

	/**
	 * Remove the link entitled "title" among {@link #links}
	 * @param title the title of the link to remove
	 * @return true if the corresponding link has been removed, false
	 * if there was no such link.
	 */
	public boolean removeLink(String title) {
		if (!links.containsKey((title))) {
			System.err.println("Key" + title + " doesn't exist in links") ;
			return false ; }
		links.remove(title) ;
		return true ; }

	/**
	 * Get the note corresponding to this key
	 * @param title the title of the note
	 * @return the corresponding note or null if there is no such note
	 * among {@link #notes}
	 */
	public Note getNote(String title)
	{
		return notes.get(title);
	}

	/**
	 * notes titles set.
	 * Iterating over this set will obtain all notes titles
	 * @return the set of notes titles
	 */
	public Set<String> getNotesKeySet()
	{
		return notes.keySet();
	}

	/**
	 * Adds a new note entitled "title" to the notes
	 * @param title the title of the note to add
	 * @param note the note to add
	 * @return true if there was no note called "title" among
	 * {@link #notes} and this new note has been added
	 */
	public boolean addNote(String title, Note note) {
		if (notes.containsKey(title)) {
			System.err
			    .println("Key " + title + " already exist in notes") ;
			return false ; }
		notes.put(title, note) ;
		return true ; }

	/**
	 * Remove the note entitled "title" among {@link #notes}
	 * @param title the title of the note to remove
	 * @return true if the corresponding addres has been removed, false
	 * if there was no such note.
	 */
	public boolean removeNote(String title) {
		if (!notes.containsKey((title))) {
			System.err.println("Key" + title + " doesn't exist in links") ;
			return false ; }
		notes.remove(title) ;
		return true ; }

	/**
	 * Type of contact (either personnal or corporate).
	 * Allow faster access than using introspection.
	 * Subclasses will implement this method.
	 * @return the type of this contact
	 */
	public abstract Type getType();

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * @apiNote
	 * It is strongly recommended, but <i>not</i> strictly required that
	 * {@code (x.compareTo(y)==0) == (x.equals(y))}. Generally speaking, any
	 * class that implements the {@code Comparable} interface and violates
	 * this condition should clearly indicate this fact. The recommended
	 * language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 * @param other the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException if the specified object's type prevents it
	 * from being compared to this object.
	 */
	@Override
	public int compareTo(Contact other)
	{
		return name.compareTo(other.name);
	}

	/**
	 * Hash code for this contact.
	 * We only consider the name of this contact
	 * @return the hash code of this contact based only on name
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Comparison with another object
	 * @param obj the other object to compare
	 * @return true if the other object is a contact with the same name
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false ; }
		if (obj == this) {
			return true ; }
		if (getClass() == obj.getClass()) {
			Contact other = (Contact)obj ;
			return (name.compareTo(other.name) == 0) ; }
		return false ; }

	/**
	 * String representation of this contact solely based on name
	 * @return the name of this contact
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Type of contact (either personal or corpodate)
	 * @see PersonalContact
	 * @see CorporateContact
	 */
	public static enum Type
	{
		/**
		 * All kinds of contact
		 */
		ALL,
		/**
		 * Personnal contact type
		 */
		PERSONNAL,
		/**
		 * Corporate contact type
		 */
		CORPORATE;

		/**
		 * String representation for this enum
		 * @return a string represenation of this enum
		 */
		@Override
		public String toString() throws AssertionError
		{
			switch(this)
			{
				case ALL:
					return new String("Tous");
				case PERSONNAL:
					return new String("Personne");
				case CORPORATE:
					return new String("Companie");
			}

			throw new AssertionError(getClass().getSimpleName()
			             		    + ".toString() unknown assertion: " + this);

		}

		/**
		 * List of all values.
		 * Can be used to fill a {@link javafx.scene.control.ComboBox}
		 * @return a collection of all possible Contact types
		 * @see application.Controller#initialize(java.net.URL, java.util.ResourceBundle)
		 */
		public static Collection<Type> all()
		{
			Collection<Type> list = new ArrayList<>();
			list.add(ALL);
			list.add(PERSONNAL);
			list.add(CORPORATE);
			return list;
		}
	}

	/**
	 * Search for an element in {@link #name}, {@link #phoneNumbers} (in both
	 * keys and values), {@link #addresses} (in both keys and values),
	 * {@link #emails} (in both keys and values), {@link #links} (in both keys
	 * and values) and {@link #notes} (in both keys and values)
	 * @param element the element to search
	 * @return true if the provided element could be found in this object,
	 * false otherwise
	 */
	@Override
	public boolean contains(String element) {
		if (element == null) {
			return false; // Ou lancez une IllegalArgumentException si vous préférez.
		}
		
		if (name != null && name.contains(element)) {
			return true;
		}
		
		if (phoneNumbers != null) {
			for (Map.Entry<String, PhoneNumber> entry : phoneNumbers.entrySet()) {
				if (entry.getKey().contains(element) || entry.getValue().toString().contains(element)) {
					return true;
				}
			}
		}
		
		if (addresses != null) {
			for (Map.Entry<String, Address> entry : addresses.entrySet()) {
				if (entry.getKey().contains(element) || entry.getValue().toString().contains(element)) {
					return true;
				}
			}
		}
		
		if (emails != null) {
			for (Map.Entry<String, URI> entry : emails.entrySet()) {
				if (entry.getKey().contains(element) || entry.getValue().toString().contains(element)) {
					return true;
				}
			}
		}
		
		if (links != null) {
			for (Map.Entry<String, URI> entry : links.entrySet()) {
				if (entry.getKey().contains(element) || entry.getValue().toString().contains(element)) {
					return true;
				}
			}
		}
		
		if (notes != null) {
			for (Map.Entry<String, Note> entry : notes.entrySet()) {
				if (entry.getKey().contains(element) || entry.getValue().toString().contains(element)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
