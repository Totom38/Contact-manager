package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class holding a phone number (limited to french numbers)
 * Implements {@link Comparable} so {@link PhoneNumber}s can be sorted
 * @see <a href="https://en.wikipedia.org/wiki/National_conventions_for_writing_telephone_numbers#France">National conventions for writing telephone number</a>
 */
public class PhoneNumber implements Comparable<PhoneNumber>, Searchable<String>
{

	/**
	 * Default International prefix
	 */
	public final static String InternationalPrefix = "+33";

	/**
	 * Number prefix : Either "+33" or "0" for french numbers
	 */
	private String prefix;

	/**
	 * Part A: [1 digit] indicates number's region or nature:
	 * <ul>
	 * <li>[1-5] indicates regional number</li>
	 * <ul>
	 * 	<li>1 : Ile de France</li>
	 * 	<li>2 : Nord-Ouest</li>
	 * 	<li>3 : Nord-Est</li>
	 * 	<li>4 : Sud-Est</li>
	 * 	<li>5 : Sud-Ouest</li>
	 * </ul>
	 * <li>[6-7] indicates mobile number</li>
	 * <li>[8-9] indicates commercial number</li>
	 * <li>[9] indicates a digital subscriber number</li>
	 * </ul>
	 */
	private String partA;

	/**
	 * Numerical value of {@link #partA} for comparison purposes
	 */
	private int partAValue;

	/**
	 * Part B [8 digits] indicate subscriber unique number
	 */
	private String partB;

	/**
	 * Numerical value of {@link #partB} for comparison purposes
	 */
	private int partBValue;

	/**
	 * Regular expression used to parse a phone number.
	 * a Phone number is composed of
	 * <ul>
	 * 	<li>A prefix : either
	 * 		<ul>
	 * 			<li>"+33 " for international numbers</li>
	 * 			<li>"0" for national numbers</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>a part A composed of 1 digit indicating region</li>
	 * 	<li>a part B composed of 8 digits</li>
	 * </ul>
	 * Parsing a string containing a phone number consists in creating a
	 * {@link Matcher} from {@link #pattern} and querying this matcher
	 * to see if it matches and if so what are the matched groups.
	 */
	private static final Pattern pattern = Pattern.compile("^(\\+33|0)\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*(\\d{1})\\s*$");

	/**
	 * Matcher factory to create the matcher able to match provided number string
	 * with {@link #pattern} regular expression
	 * @param numberString the string containing the number to check
	 * @return a new matcher able to match provided string with {@link #pattern}.
	 */
	public static Matcher matcher(String numberString)
	{
		return pattern.matcher(numberString);
	}

	/**
	 * Parse phone number from string using regular expression
	 * @param numberString the string to parse
	 * @return a valid PhoneNumber parsed from string
	 * @throws IllegalArgumentException if number string couldn't be parsed
	 * @see #PhoneNumber(String, String, String)
	 */
	public static PhoneNumber parse(String numberString) throws IllegalArgumentException {
		Matcher matcher = matcher(numberString) ;
		if (!matcher.matches()) {
			throw new IllegalArgumentException(numberString + " is not a valid phone number") ; }
		String prefix = matcher.group(1) ;
		String part1 = matcher.group(2) ;
		int part1Number = 1 ;
		if ((part1.length() != 1) || (part1Number < 1)) {
			throw new IllegalArgumentException("Illegal part A : " + part1) ; }
		StringBuilder sb = new StringBuilder() ;
		for (int i = 3; i <= 10; i++) {
			sb.append(matcher.group(i)) ; }
		String part2 = sb.toString() ;
		if (part2.length() != 8) {
			throw new IllegalArgumentException("Illegal part B : " + part2) ; }
		return new PhoneNumber(prefix, part1, part2) ; }

	/**
	 * Private valued constructor to build phone number's parts without control.
	 * This constructor is used by {@link #parse(String)} only.
	 * @param prefix Phone Prefix (either 0 or "+33 ")
	 * @param partA regional part ([1..9]{1})
	 * @param partB subscriber number ([0..9]{8}) (unformatted)
	 * @throws NumberFormatException if partA or partB can't be parsed as ints
	 * @see #parse(String) use this constructor
	 */
	private PhoneNumber(String prefix, String partA, String partB)
	{
		this.prefix = prefix;
		this.partA = partA;
		partAValue = Integer.parseInt(partA);
		this.partB = partB;
		partBValue = Integer.parseInt(partB);

	}

	/**
	 * Copy constructor
	 * @param n the number to copy
	 */
	public PhoneNumber(PhoneNumber n)
	{
		prefix = n.prefix;
		partA = n.partA;
		partAValue = n.partAValue;
		partB = n.partB;
		partBValue = n.partBValue;
	}

	/**
	 * Valued constructor
	 * @param numberString the string to parse to get phone number
	 * @throws IllegalArgumentException if argument can't be parsed as a valid
	 * number)
	 * @see #parse(String)
	 * @see #PhoneNumber(PhoneNumber)
	 */
	public PhoneNumber(String numberString) throws IllegalArgumentException {
		this(parse(numberString)) ; }

	/**
	 * Set values from parsing provided number string.
	 * @param numberString the string to parse
	 * @return true if provided string could be parsed to obtain new values,
	 * false otherwise
	 * @see PhoneNumber#parse(String)
	 */
	public boolean parseFrom(String numberString) {
		PhoneNumber pn = parse(numberString) ;
        this.prefix = pn.prefix ;
        this.partA = pn.partA ;
        this.partAValue = pn.partAValue ;
        this.partB = pn.partB ;
        this.partBValue = pn.partBValue ;
        return true ; }

	/**
	 * Indicate if this number is international (starting with +33)
	 * @return true if has an international prefix, false otherwise
	 */
	public boolean isInternational()
	{
		return prefix.equals(InternationalPrefix);
	}

	/**
	 * Indicate if this number is a regional hardwired subscriber number
	 * @return true if part A is within [1..5]
	 */
	public boolean isRegional()
	{
		return partAIsWithin(1, 5);
	}

	/**
	 * Indicate if this number is a mobile subscriber number
	 * @return true if part A is within [6..7]
	 */
	public boolean isMobile()
	{
		return partAIsWithin(6, 7);
	}

	/**
	 * Indicate if this number is a commercial number
	 * @return true if part A is within [8..9]
	 */
	public boolean isCommercial()
	{
		return partAIsWithin(8, 9);
	}

	/**
	 * Indicate if this number is a digital subscriber number
	 * @return true if part A is 9
	 */
	public boolean isDigital()
	{
		return partAValue == 9;
	}

	/**
	 * Get region (part A) value
	 * @return the region value
	 */
	public int getRegion()
	{
		return partAValue;
	}

	/**
	 * Get subscriber (part B) value
	 * @return the subscriber value
	 */
	public int getSubscriber()
	{
		return partBValue;
	}

	/**
	 * Compare with other phone number
	 * @param other the other number to compare with
	 * @implSpec Uses {@link #partAValue} and {@link #partBValue}
	 * @return negative value if this number is considered less thand other, 0 if
	 * both numbers are equals and positive value if other is considered bigger
	 * than this. {@link #prefix} is ignored.
	 */
	@Override
	public int compareTo(PhoneNumber other) {
		int result = Integer.compare(this.partAValue, other.partAValue) ;
		if (result != 0) {
			return result ; }
		return Integer.compare(this.partBValue, other.partBValue) ; }

	/**
	 * Computes the hash code for this number based on {@link #partAValue} and
	 * {@link #partBValue}
	 * @return the hash code for this number
	 */
	@Override
	public int hashCode() {
		final int prime = 31 ;
		int hash = 1 ;
		hash = (prime * hash) + ((partA == null) ? 0 : partAValue) ;
		hash = (prime * hash) + ((partB == null) ? 0 : partBValue) ;
		return hash ; }

	/**
	 * @param obj the other object compare
	 * @return true if obj is a {@link PhoneNumber} with the same {@link #partA}
	 * and {@link #partB}. {@link #prefix} is ignored
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false ; }
		if (obj == this) {
			return true ; }
		if (getClass() == obj.getClass()) {
			PhoneNumber pn = (PhoneNumber)obj ;
			return (this.compareTo(pn)==0) ; }
		return false ; }

	/**
	 * String representation of this Phone number composed of
	 * "<prefix> <partA> <partB>" where <part B> is formated like so BB BB BB BB
	 * and space between <prefix> and <partA> is applied only on international
	 * numbers
	 * @return a String representation of this phone number
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder() ;
		sb.append(prefix) ;
		if (isInternational()) {
			sb.append(" ") ; }
		sb.append(partA).append(" ") ;
		for (int i = 0; i < partB.length(); i += 2) {
			sb.append(partB, i, i + 2).append(" ") ; }
		return sb.toString().trim() ; }

	/**
	 * Utility method indicating if partA is within [min..max] range
	 * @param min the min part of the range
	 * @param max the max part of the range
	 * @return true if {@link #partA} is within this range, false otherwise
	 */
	private boolean partAIsWithin(int min, int max)
	{
		int partAValue = Integer.parseInt(partA);
		return (partAValue >= min) && (partAValue <= max);
	}

	/**
	 * Search for an element in either {@link #prefix}, {@link #partA},
	 * {@link #partB} or {@link #toString()}
	 * @param element the element to search
	 * @return true if the provided element could be found in this object,
	 * false otherwise
	 */
	@Override
	public boolean contains(String element) {
		if (this.toString().contains(element)) {
			return true ; }
		return false ; } }