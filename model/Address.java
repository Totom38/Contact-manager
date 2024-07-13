package model;

import java.util.Locale;
import java.util.Optional;

/**
 * Class holding an address.
 * Implements {@link Comparable} so addresses can be sorted based on
 * {@link #locale}, {@link #zipCode}, {@link #city}, {@link #way} and
 * {@link #number}
 */
public class Address implements Comparable<Address>, Searchable<String>
{
	/**
	 * [Optional] Number in the way.
	 * Some addresses don't have numbers
	 */
	private Optional<Integer> number;
	/**
	 * Way, Street, Road, etc.
	 */
	private String way;

	/**
	 * City, village,etc.
	 */
	private String city;

	/**
	 * Zip code
	 */
	private String zipCode;

	/**
	 * Local for this address (used to define Country)
	 */
	private Locale locale;

	/**
	 * Complete Valued constructor
	 * @param number the number in the way
	 * @param way the way
	 * @param city city
	 * @param zipCode city zipcode
	 * @param locale locale indicating country
	 * @see Optional#of(Object)
	 */
	public Address(int number,
	               String way,
	               String city,
	               String zipCode,
	               Locale locale)
	{
		super();
		if (way == null || city == null || zipCode == null || locale == null){
			throw new NullPointerException();
		}
		if ( way.isEmpty() || city.isEmpty() || zipCode.isEmpty() || number <=0 ){
			throw new IllegalArgumentException();
		}
		this.number = Optional.of(number);
		this.way = way;
		this.city = city;
		this.zipCode = zipCode;
		this.locale = locale;
	}

	/**
	 * Valued constructor with no number
	 * @param way the way
	 * @param city city
	 * @param zipCode city zipcode
	 * @param locale locale indicating country
	 * @see Optional#empty()
	 */
	public Address(String way,
	               String city,
	               String zipCode,
	               Locale locale)
	{
		super();
		if (way == null || city == null || zipCode == null || locale == null){
			throw new NullPointerException();
		}
		if ( way.isEmpty() || city.isEmpty() || zipCode.isEmpty()){
			throw new IllegalArgumentException();
		}
		number = Optional.empty();
		this.way = way;
		this.city = city;
		this.zipCode = zipCode;
		this.locale = locale;
	}

	/**
	 * Valued constructor (with locale set to {@link Locale#FRANCE})
	 * @param number the number in the way
	 * @param way the way
	 * @param city city
	 * @param zipCode city zipcode
	 */
	public Address(int number,
	               String way,
	               String city,
	               String zipCode)
	{
		this(number, way, city, zipCode, Locale.FRANCE);
	}


	/**
	 * Valued constructor (with no number and locale set to {@link Locale#FRANCE})
	 * @param way the way
	 * @param city city
	 * @param zipCode city zipcode
	 */
	public Address(String way,
	               String city,
	               String zipCode)
	{
		this(way, city, zipCode, Locale.FRANCE);
	}

	/**
	 * Number accessor
	 * @return the optional number
	 */
	public Optional<Integer> getNumber()
	{
		return number;
	}

	/**
	 * Way accessor
	 * @return the way
	 */
	public String getWay()
	{
		return way;
	}

	/**
	 * City accessor
	 * @return the city
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Zip Code accessor
	 * @return the zipCode
	 */
	public String getZipCode()
	{
		return zipCode;
	}

	/**
	 * Locale accessor
	 * @return the locale
	 */
	public Locale getLocale()
	{
		return this.locale;
	}

	/**
	 * Number mutator
	 * @param number the number to set
	 * @see Optional#of(Object)
	 */
	public void setNumber(int number)
	{
		this.number = Optional.of(number);
	}

	/**
	 * Way mutator
	 * @param way the way to set
	 */
	public void setWay(String way)
	{
		if (!(way == null) && !(way.isEmpty())){
			this.way = way;
		}
	}

	/**
	 * City mutator
	 * @param city the city to set
	 */
	public void setCity(String city)
	{
		if (!(city == null) && !(city.isEmpty())){
			this.city = city;
		}	
	}

	/**
	 * Zip Code mutator
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode)
	{
		if (!(zipCode == null) && !(zipCode.isEmpty())){
			this.zipCode = zipCode;
		}
	}

	/**
	 * Locale Mutator
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale)
	{
		if (!(locale == null) && !(locale.toString().isEmpty())){
			this.locale = locale;
		}	
	}

	/**
	 * Hash code for this address, based on {@link #locale}, {@link #city},
	 * {@link #zipCode}, {@link #way} and finally {@link #number} (if any)
	 * @return the hash value of this address
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int hash = 1;
		hash = (prime * hash) + (locale == null ? 0 : locale.hashCode());
		hash = (prime * hash) + (zipCode == null ? 0 : zipCode.hashCode());
		hash = (prime * hash) + (city == null ? 0 : city.hashCode());
		hash = (prime * hash) + (way == null ? 0 : way.hashCode());
		hash = (prime * hash) + (number == null ? 0 : (number.isEmpty() ? 0 : number.get()));
		return hash;
	}

	/**
	 * Comparison with another object
	 * @obj the other object to compare with
	 * @return true if other object is an {@link Address} with the exact same
	 * values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false ; }
		if (obj == this) {
			return true ; }
		if (getClass() == obj.getClass()) {
			Address a = (Address)obj;
			return (city.equals(a.getCity()) && locale.equals(a.getLocale()) && number.equals(a.getNumber()) && zipCode.equals(a.getZipCode()) && way.equals(a.getWay())) ; }
		return false ; }

	/**
	 * String representation of this address:
	 * [<number>] <way>\n<zipCode> <city>[\n <country from locale iff not France>]
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (number.isPresent())
		{
			sb.append(String.valueOf(number.get()));
			sb.append(' ');
		}
		sb.append(way);
		sb.append('\n');
		sb.append(zipCode);
		sb.append(' ');
		sb.append(city);
		if (!locale.equals(Locale.FRANCE))
		{
			sb.append('\n');
			sb.append(locale.getDisplayCountry(locale));
		}

		return sb.toString();
	}

	/**
	 * Compare with another address.
	 * @param other the other Address to compare to.
	 * @return negative number if other address is considered greater, 0 if
	 * both address are equals. Positive number otherwise
	 * @implSpec comparison should start with {@link #locale},
	 * then {@link #zipCode}, then {@link #city}, {@link #way} and
	 * evt {@link #number}
	 * @implNote If field is not comparable, use its String representation to
	 * compare.
	 */
	@Override
	public int compareTo(Address other) {
		int result = locale.toString().compareTo(other.locale.toString()) ;
		if (result != 0) {
			return result ; }
		result = zipCode.compareTo(other.zipCode) ;
		if (result != 0) {
			return result ; }
		result = city.compareTo(other.city) ;
		if (result != 0) {
			return result ; }
		result = way.compareTo(other.way) ;
		if (result != 0) {
			return result ; }
		if (number.isPresent() && other.number.isPresent()) {
			result = number.get().compareTo(other.number.get()) ;
			if (result != 0) {
				return result ; } } 
		else {
			if (number.isPresent()) {
				return 1 ; } 
			else if (other.number.isPresent()) {
				return -1 ; } }
	return 0 ; }

	/**
	 * Search for provided elements in any of the fields
	 * @param element the element to search
	 * @return true if the provided element could be found in this object
	 */
	@Override
	public boolean contains(String element) {
		if (number.isPresent()) {
			Integer value = null ;
			try {
				value = Integer.valueOf(element) ; } 
			catch ( NumberFormatException e ) {}
			if (number.get().equals(value)) {
				return true ; } }
		if (way.contains(element)) {
			return true ; }
		if (city.contains(element)) {
			return true ; }
		if (zipCode.contains(element)) {
			return true ; }
		if (locale.getDisplayCountry(locale).contains(element)) {
			return true ; }
		return false ; } }