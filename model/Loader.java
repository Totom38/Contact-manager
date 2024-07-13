package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Abstract class handling file input/output for {@link ContactManager}
 */
public abstract class Loader
{
	/**
	 * The file to read or write
	 */
	protected File file;

	/**
	 * Date of last loaded or saved file.
	 * @implNote updated during {@link #load()} or {@link #save(Set)} operations
	 */
	protected Date date;

	/**
	 * Country to Locale map to convert country name to {@link Locale}
	 */
	private static Map<String, Locale> countryLocale = null;

	/**
	 * Constructor with provided file
	 * @param file the file to use for reading and/or writing
	 */
	protected Loader(File file)
	{
		this.file = file;
		date = null;
	}

	/**
	 * Default constructor.
	 * Sets internal {@link #file} to null
	 */
	protected Loader()
	{
		this(null);
	}

	/**
	 * Get the working file
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Set the working file
	 * @param file the file to set
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * Get the last update date
	 * @return the update date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * Loads a {@link Set} of {@link Contact} from {@link #file}
	 * @return the set of contacts loaded from {@link #file}
	 * @throws FileNotFoundException if file can't be opened
	 * @throws ParseException whenever a parse error occurs
	 * @throws NullPointerException when internal {@link #file} is not set yet
	 * or when parsing leads to unexpected null elements.
	 * @throws IOException if internal tools can't read file
	 */
	public abstract Set<Contact> load()
	    throws FileNotFoundException,
	    ParseException,
	    NullPointerException,
	    IOException;

	/**
	 * Saves provided {@link Set} of {@link Contact} to {@link #file}
	 * @param set the set od contacts to save to {@link #file}
	 * @throws NullPointerException if internal {@link #file} is null
	 * @throws IOException if internal tools write fails.
	 */
	public abstract void save(Set<Contact> set)
		throws NullPointerException,
		IOException;

	/**
	 * Get Locale from country name
	 * @param countryName the name of the country to search
	 * @return the {@link Locale} corresponding to country or null if no
	 * such Local could be found in {@link #countryLocale}
	 */
	public static Locale getLocaleFromCountry(String countryName)
	{
		if (countryLocale == null) // build it first
		{
			countryLocale = new TreeMap<>();
			countryLocale.put("France", Locale.FRANCE);
			countryLocale.put("Deutschland", Locale.GERMANY);
			countryLocale.put("Italia", Locale.ITALY);
			countryLocale.put("日本", Locale.CHINA);
			countryLocale.put("대한민국", Locale.KOREA);
			countryLocale.put("United Kingdom", Locale.UK);
			countryLocale.put("United States", Locale.US);
			countryLocale.put("Canada", Locale.CANADA);
		}

		return countryLocale.get(countryName);
	}

	/**
	 * Validate the scheme specific part (e.g. david.roussel@ensiie.fr) of an
	 * e-mail URI (e.g. mailto:david.roussel@ensiie.fr)
	 * @param emailString the email address string to parse
	 * @return true if emailString corresponds to an valid email address structure.
	 */
	public static boolean checkEmailAddress(String emailString)
	{
		if (emailString != null)
		{
			return emailString.matches("^[\\w-\\.]+@[\\w-]+\\.[\\w-]{2,4}$");
		}
		return false;
	}

	/**
	 * Check provided URI contains a well structured e-mail URI
	 * @param emailURI the e-mail URI to check
	 * @return true of provided URI corresponds to a valid e-mail URI.
	 */
	public static boolean checkEmailURI(URI emailURI)
	{
		if (emailURI != null)
		{
			return emailURI.getScheme().equals("mailto")
			    && checkEmailAddress(emailURI.getSchemeSpecificPart());
		}
		return false;
	}

	/**
	 * Check provided string contains a well structured URL
	 * (e.g. https://www.ensiie.fr or file:/home)
	 * @param urlString the url string to parse
	 * @return true if provided url string contains a well structured url
	 */
	public static boolean checkLink(String urlString)
	{
		if (urlString != null)
		{
			return urlString.matches("^(file|ftp|http|https|sftp):///*[-a-zA-Z0-9@:%._\\+~#?&/=]*$");
		}
		return false;
	}

	/**
	 * Check provided URI contains a well structured URL
	 * @param linkURI the URL URI to check
	 * @return true if the provided URI corresponds to a valid URL structure
	 */
	public static boolean checkLinkURI(URI linkURI)
	{
		if (linkURI != null)
		{
			return checkLink(linkURI.toString());
		}
		return false;
	}
}
