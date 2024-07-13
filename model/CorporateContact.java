/**
 *
 */
package model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Contact} dedicated to companies with {@link PersonalContact}
 * embedded.
 */
public class CorporateContact extends Contact
{
	/**
	 * Set of Persons linked to this company
	 */
	private Set<PersonalContact> employees;

	/**
	 * Valued Constructor
	 * @param name the name of this contact
	 * @param imagePath an image URI for this contact (or null if thre is no image)
	 * @param number a phone number for this contact (or null ...)
	 * @param email an email for this contact (or null ..)
	 * @param link a link for this contact (or null ...)
	 * @param note a note for this contact (or null ...)
	 */
	public CorporateContact(String name,
	                        URI imagePath,
	                        PhoneNumber number,
	                        URI email,
	                        URI link,
	                        Note note)
	{
		super(name, imagePath, number, email, link, note);
		employees = new HashSet<>();
	}

	/**
	 * Copy constructor
	 * @param contact the contact to copy
	 */
	public CorporateContact(Contact contact)
	{
		super(contact);
		employees = new HashSet<>();
	}

	/**
	 * Employees accessor
	 * @return the employees
	 */
	public Set<PersonalContact> getEmployees()
	{
		return employees;
	}

	/**
	 * Check if provided contact is part of {@link #employees}
	 * @param contact the contact to check
	 * @return true if the provided contact is part of {@link #employees}
	 */
	public boolean contains(PersonalContact contact)
	{
		return employees.contains(contact);
	}

	public boolean contains(String element)
{
	boolean contained = super.contains(element);
	if (!contained)
	{
		for (PersonalContact employee : employees)
		{
			if (employee.contains(element))
			{
				return true;
			}
		}
	}
	return contained;
}
	

	/**
	 * Add a personal contact to {@link #employees}
	 * @param contact the contact to add
	 * @return true if the provided contact was not already part of
	 * {@link #employees} and was added, false otherwise
	 * @implNote provided contact's corporation should also be changed to this
	 */
	public boolean add(PersonalContact contact) {
		if (employees.contains(contact)){
			return false ; }
		employees.add(contact) ;
		contact.setCorporation(this) ;
		return true ; }

	/**
	 * Remove provdided contact from {@link #employees}
	 * @param contact the contact to remove
	 * @return true if the provided contact was part of {@link #employees} and
	 * removed, false otherwise
	 * @implNote provided contact's corporation should also be changed to null
	 */
	public boolean remove(PersonalContact contact) {
		if (!employees.contains(contact)) {
			return false ; }
		employees.remove(contact) ;		
		contact.setCorporation(null) ;
		return true ; }

	/**
	 * Type of contact : {@link Contact.Type#CORPORATE}.
	 * Allow faster access than using introspection.
	 * @return the type of this contact
	 */
	@Override
	public Type getType()
	{
		return Contact.Type.CORPORATE;
	}
}
