package model;

import java.net.URI;

/**
 * Personal contact: Represented by a person wirh a (last) name and a first name
 */
public class PersonalContact extends Contact
{
	/**
	 * Firt name for this contact
	 */
	private String firstName;

	/**
	 * An eventual corporate contact for this personal contact
	 * Whe this corporate contact is present, phones, links can be provided
	 * by this corporate contact
	 */
	private CorporateContact corporation;

	/**
	 * Valued Constructor
	 * @param firstName First name of this contact
	 * @param lastName Last name of this contact
	 * @param imagePath an image URI for this contact (or null if thre is no image)
	 * @param number a phone number for this contact (or null ...)
	 * @param email an email for this contact (or null ..)
	 * @param link a link for this contact (or null ...)
	 * @param note a note for this contact (or null ...)
	 * @param corporation of this contact (or null...)
	 */
	public PersonalContact(String firstName,
	                       String lastName,
	                       URI imagePath,
	                       PhoneNumber number,
	                       URI email,
	                       URI link,
	                       Note note,
	                       CorporateContact corporation)
	{
		super(lastName, imagePath, number, email, link, note);
		this.firstName = new String(firstName);
		this.corporation = corporation;
	}

	/**
	 * Copy constructor
	 * @param contact The contact to copy
	 */
	public PersonalContact(PersonalContact contact)
	{
		super(contact);
		firstName = contact.firstName;
		corporation = contact.corporation;
	}

	/**
	 * First Name accessor
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * First name mutator
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		if (! (firstName == null) && ! (firstName.isEmpty())){
			this.firstName = firstName;
		}
	}

	/**
	 * Corporation accessor
	 * @return the corporation
	 */
	public CorporateContact getCorporation()
	{
		return corporation;
	}

	/**
	 * Corporation mutator
	 * @param corporation the corporation to set
	 * @implNote if provided corporation is null and this.corporation wasn't
	 * this contact should be removed from this.corporation before set to null
	 */
	public void setCorporation(CorporateContact corporation) {
		if ((corporation == null) && this.corporation != null) {
			this.corporation.remove(this) ; }
		this.corporation = corporation ;
		if (!(corporation == null)) {
			this.corporation.add(this) ; } }

	/**
	 * Type of contact : {@link Contact.Type#PERSONNAL}.
	 * Allow faster access than using introspection.
	 * @return the type of this contact
	 */
	@Override
	public Type getType()
	{
		return Contact.Type.PERSONNAL;
	}

	/**
	 * Compares with another contact.
	 * If other contact is also a {@link PersonalContact} then {@link Contact#name} and
	 * {@link #firstName} are compared, otherwise only {@link Contact#name} are compared.
	 * @param the other contact to compare
	 * @return negative number if "this" is considerred smaller to other contact,
	 * 0 if both contact are considered equal and positive number if "this" is
	 * considered greater thant other contact
	 */
	@Override
	public int compareTo(Contact other) {
		int superComparison = super.compareTo(other) ;
		if (superComparison == 0) {
			if (other.getName().compareTo(firstName) == 0) {
				return 0 ; }
			if (getClass() == other.getClass()) {
				PersonalContact p = (PersonalContact)other ;
				if (p.getFirstName().compareTo(firstName) == 0) {
					return 0 ; } } }
		return superComparison ; }

	/**
	 * HAshcode for this personal contact.
	 * Adds {@link #firstName} to hash code computation
	 * @return the hash code of this personal contact based on name and firstname
	 */
	@Override
	public int hashCode() {
		final int prime = 31 ;
		int result = super.hashCode() ;
		result = (prime * result) + ((firstName == null) ? 0 : firstName.hashCode()) ;
		return result ; }

	/**
	 * Comparison with another object
	 * @param obj the other obecjt to compare to
	 * @return true if other object is also a personnal contact with the same
	 * last name and first name.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false ; }
		if (obj == this) {
			return true ; }
		if (getClass() == obj.getClass()) {
			Contact c = (Contact)obj ;
			return super.equals(c) ; }
		return false ; }

	/**
	 * String reprenstation of this personal contact based on name and firstName
	 * @return firstName + name of this contact
	 */
	@Override
	public String toString()
	{
		return new String(firstName + " " +  super.toString());
	}

	/**
	 * Search for an element in {@link #name}, {@link #phoneNumbers} (in both
	 * keys and values), {@link #addresses} (in both keys and values),
	 * {@link #emails} (in both keys and values), {@link #links} (in both keys
	 * and values) and {@link #notes} (in both keys and values) and finally in
	 * {@link #firstName}. But NOT in {@link #corporation} since its a link to
	 * another {@link Contact}.
	 * @param element the element to search
	 * @return true if the provided element could be found in this object,
	 * false otherwise
	 */
	@Override
	public boolean contains(String element) {
		boolean previousResult = super.contains(element) ;
		if (!previousResult) {
			if (firstName.contains(element)) {
				previousResult = true ; } }
		return previousResult ; } }