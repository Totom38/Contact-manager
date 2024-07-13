package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON Loader using Jackson's {@link ObjectMapper} to read or write
 * a {@link Set} of {@link Contact} from or to JSON {@link File}
 */
public class JSONLoader extends Loader
{

	/**
	 * Jackson's Object mapper to read or write JSON Nodes
	 */
	private ObjectMapper objectMapper;

	/**
	 * Date formatter used to parse or write dates
	 */
	private SimpleDateFormat format;

	/**
	 * Constructor with provided file
	 * @param file the file to use
	 */
	public JSONLoader(File file)
	{
		super(file);
		objectMapper = new ObjectMapper();
		format = new SimpleDateFormat("yyyy/MM/dd");
		objectMapper.setDateFormat(format);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	/**
	 * Default constructor
	 *
	 */
	public JSONLoader()
	{
		this(null);
	}

	/**
	 * Loads a {@link Set} of {@link Contact} from {@link #file}
	 * @return the set of contacts loaded from {@link #file}
	 * @throws FileNotFoundException if file can't be opened
	 * @throws ParseException whenever a parse error occurs
	 * @throws NullPointerException whenever an unexpected JsonNode is
	 * encountered or when internal {@link #file} is not set yet
	 * @throws IOException if {@link ObjectMapper} can't read file
	 */
	@Override
	public Set<Contact> load()
	    throws FileNotFoundException,
	    ParseException,
	    NullPointerException,
	    IOException
	{
		Objects.requireNonNull(file);
		JsonNode rootNode = null;
		try
		{
			rootNode = objectMapper.readTree(file);
		}
		catch (IOException e)
		{
			System.err.println("ObjectMapper error reading " + file);
			throw e;
		}

		Objects.requireNonNull(rootNode);

		/*
		 * Date
		 */
		JsonNode dateNode = rootNode.path("date");
		if (dateNode.isMissingNode())
		{
			throw new ParseException("missing date node", 0);
		}
		date = format.parse(dateNode.asText());

		/*
		 * Contacts [array]
		 */
		JsonNode contactsNode = rootNode.path("contacts");
		if (contactsNode.isMissingNode())
		{
			throw new ParseException("missing contacts node", 0);
		}

		if (!contactsNode.isArray())
		{
			throw new ParseException("contacts node is not an array", 0);
		}

		Set<Contact> result = new TreeSet<>();

		/*
		 * Map recording corporation (CorporateContact) name for each
		 * PersonalContact so it can be retrieved after parsing is completed
		 */
		Map<PersonalContact, String> corporationsMap = new TreeMap<>();

		/*
		 * Map recording Employee's name for each CorporateContact
		 * so employees (PersonalContact) can be retrieved after parsing
		 * is completed
		 */
		Map<String, CorporateContact> employeesMap = new TreeMap<>();

		for (JsonNode contactNode : contactsNode)
		{
			/*
			 * Name
			 */
			JsonNode contactNameNode = contactNode.get("name");
			Objects.requireNonNull(contactNameNode);
			String contactName = contactNameNode.asText();
			Contact contact = null;

			String firstName = null;
			if (contactNode.has("firstname"))
			{
				/*
				 * First Name (--> PersonalContact)
				 */
				firstName = contactNode.get("firstname").asText();
				PersonalContact pc = new PersonalContact(firstName,
				                                         contactName,
				                                         null,
				                                         null,
				                                         null,
				                                         null,
				                                         null,
				                                         null);
				/*
				 * Corporation
				 */
				if (contactNode.has("corporation"))
				{
					corporationsMap.put(pc, contactNode.get("corporation").asText());
				}
				contact = pc;
			}
			else
			{
				/*
				 * No First Name (--> CorporateContact)
				 */
				CorporateContact cc = new CorporateContact(contactName,
				                                           null,
				                                           null,
				                                           null,
				                                           null,
				                                           null);
				/*
				 * Employees [array]
				 */
				if (contactNode.has("employees"))
				{
					JsonNode employeesNode = contactNode.get("employees");
					if (!employeesNode.isArray())
					{
						throw new ParseException("employees are not an array", 0);
					}
					for (JsonNode employeeNode : employeesNode)
					{
						employeesMap.put(employeeNode.asText(), cc);
					}
				}
				contact = cc;
			}

			/*
			 * Image
			 */
			JsonNode imageNode = contactNode.get("image");
			if (imageNode != null)
			{
				// relative path
				String imagePath = imageNode.asText();
				URL urlValue = getClass().getResource(imagePath);
				String uriValue = null;
				if (urlValue != null) // valid relative path
				{
					uriValue = urlValue.getFile();
				}
				else // invalid relative path, trying absolute path
				{
					uriValue = imagePath;
				}
				try
				{
					URI imageURI = new URI("file:" + uriValue);
					// JSONLoader#load() : set Image in contact

				}
				catch (URISyntaxException e)
				{
					throw new ParseException("unable to parse " + uriValue
					    + " image path", 0);
				}
			}

			/*
			 * Phone numbers [array]
			 */
			JsonNode phonesNode = contactNode.get("phones");
			if (phonesNode != null)
			{
				if (!phonesNode.isArray())
				{
					throw new ParseException("phones are not an array", 0);
				}

				for (JsonNode phoneNode : phonesNode)
				{
					JsonNode nameNode = phoneNode.get("name");
					JsonNode numberNode = phoneNode.get("number");
					if ((nameNode == null) || (numberNode ==  null))
					{
						throw new ParseException("missing name or number node in phone", 0);
					}
					contact.addPhoneNumber(nameNode.asText(),
					                       PhoneNumber.parse(numberNode.asText()));
				}
			}

			/*
			 * Addresses [array]
			 */
			JsonNode addressesNode = contactNode.get("addresses");
			if (addressesNode != null)
			{
				if (!addressesNode.isArray())
				{
					throw new ParseException("addresses are not an array", 0);
				}

				for (JsonNode addressNode : addressesNode)
				{
					JsonNode nameNode = addressNode.get("name");
					JsonNode numberNode = addressNode.get("number");
					JsonNode wayNode = addressNode.get("way");
					JsonNode zipNode = addressNode.get("zipcode");
					JsonNode cityNode = addressNode.get("city");
					JsonNode countryNode = addressNode.get("country");

					if ((nameNode == null) || (wayNode == null) || (zipNode == null) || (cityNode == null))
					{
						throw new ParseException("missing name, way, zipcode or city node in address", 0);
					}

					Address address = null;
					Locale countryLocale = null;
					int number = -1;
					if (countryNode != null)
					{
						countryLocale = getLocaleFromCountry(countryNode.asText());
					}
					if (numberNode != null)
					{
						int potentialNumber = numberNode.asInt();
						if (potentialNumber > 0)
						{
							number = potentialNumber;
						}
					}

					// JSONLoader#load() : complete reading Addresses [array]

				}
			}

			/*
			 * Emails [array]
			 */
			JsonNode emailsNode = contactNode.get("emails");
			if (emailsNode != null)
			{
				if (!emailsNode.isArray())
				{
					throw new ParseException("emails are not an array", 0);
				}

				for (JsonNode emailNode : emailsNode)
				{
					// JSONLoader#load() : complete reading Emails [array]
				}
			}

			/*
			 * Links [array]
			 */
			JsonNode linksNode = contactNode.get("links");
			if (linksNode != null)
			{
				// JSONLoader#load() : complete reading Links [array]
			}

			/*
			 * Notes [array]
			 */
			JsonNode notesNode = contactNode.get("notes");
			if (notesNode != null)
			{
				//JSONLoader#load() : complete reading Notes [array]
			}

			result.add(contact);
		}

		// --------------------------------------------------------------------
		// Add employees to corporate contacts
		// Note: the "Add corporations" below does the exact same job
		// --------------------------------------------------------------------
		for (Map.Entry<String, CorporateContact> entry : employeesMap.entrySet())
		{
			String employeeName = entry.getKey();
			CorporateContact corporation = entry.getValue();
			// find employee
			for (Contact c : result)
			{
				if (c instanceof PersonalContact)
				{
					PersonalContact pc = (PersonalContact) c;
					if (pc.toString().equals(employeeName))
					{
						corporation.add(pc);
					}
				}
			}
		}

		// --------------------------------------------------------------------
		// Add corporations to personal contacts
		// Note: the "Add employees" above does the exact same job
		// --------------------------------------------------------------------
		for (Map.Entry<PersonalContact, String> entry : corporationsMap.entrySet())
		{
			PersonalContact employee = entry.getKey();
			String corporationName = entry.getValue();
			// find corporation
			for (Contact c : result)
			{
				if (c instanceof CorporateContact)
				{
					CorporateContact cc = (CorporateContact) c;
					if (cc.toString().equals(corporationName))
					{
						employee.setCorporation(cc);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Saves provided {@link Set} of {@link Contact} to {@link #file}
	 * @param set the set od contacts to save to {@link #file}
	 * @throws NullPointerException if internal {@link #file} is null
	 * @throws IOException if {@link ObjectMapper} write fails.
	 */
	@Override
	public void save(Set<Contact> set) throws NullPointerException, IOException
	{
		Objects.requireNonNull(file);
		ObjectNode rootNode = objectMapper.createObjectNode();
		/*
		 * Date
		 */
		rootNode.putPOJO("date", Date.from(Instant.now()));

		ArrayNode contactsArray = objectMapper.createArrayNode();
		for (Contact contact : set)
		{
			ObjectNode contactNode = objectMapper.createObjectNode();
			/*
			 * Name
			 */
			contactNode.put("name", contact.getName());
			if (contact instanceof PersonalContact)
			{
				PersonalContact pc = (PersonalContact) contact;
				/*
				 * First Name
				 */
				contactNode.put("firstname", pc.getFirstName());
				CorporateContact cc = pc.getCorporation();
				if (cc != null)
				{
					/*
					 * Corporation
					 */
					contactNode.put("corporation", cc.toString());
				}
			}
			if (contact.getImagePath() != null)
			{
				/*
				 * Image
				 */
				contactNode.put("image", contact.getImagePath().getSchemeSpecificPart());
			}
			if (contact instanceof CorporateContact)
			{
				CorporateContact cc = (CorporateContact) contact;
				Set<PersonalContact> employees = cc.getEmployees();
				if (!employees.isEmpty())
				{
					/*
					 * Employees [array]
					 */
					ArrayNode employeesArray = objectMapper.createArrayNode();
					for (PersonalContact pc : employees)
					{
						// JSONLoader#save(Set<Contact>) : add PersonalContact's name to employeesArray
					}

					contactNode.putPOJO("employees", employeesArray);
				}
			}

			/*
			 * Phone Numbers [array]
			 */
			Set<String> keys = contact.getPhoneNumberKeySet();
			if (!keys.isEmpty())
			{
				ArrayNode array = objectMapper.createArrayNode();
				for (String key : keys)
				{
					ObjectNode eltNode = objectMapper.createObjectNode();
					eltNode.put("name", key);
					eltNode.put("number", contact.getPhoneNumber(key).toString());
					array.add(eltNode);
				}
				contactNode.putPOJO("phones", array);
			}

			/*
			 * Emails [array]
			 */
			keys = contact.getEmailKeySet();
			if (!keys.isEmpty())
			{
				ArrayNode array = objectMapper.createArrayNode();
				for (String key : keys)
				{
					// JSONLoader#save(Set<Contact>) : add named email to emails array
				}
				contactNode.putPOJO("emails", array);
			}

			/*
			 * Addresses [array]
			 */
			keys = contact.getAddressKeySet();
			if (!keys.isEmpty())
			{
				ArrayNode array = objectMapper.createArrayNode();

				// JSONLoader#save(Set<Contact>) : add named addresses to addresses array

				contactNode.putPOJO("addresses", array);
			}

			/*
			 * Links [array]
			 */
			keys = contact.getLinksKeySet();
			if (!keys.isEmpty())
			{
				ArrayNode array = objectMapper.createArrayNode();

				// JSONLoader#save(Set<Contact>) : add named links to links array

				contactNode.putPOJO("links", array);
			}

			/*
			 * Notes [array]
			 */
			keys = contact.getNotesKeySet();
			if (!keys.isEmpty())
			{
				ArrayNode array = objectMapper.createArrayNode();

				//JSONLoader#save(Set<Contact>) : add named notes to notes array

				contactNode.putPOJO("notes", array);
			}

			contactsArray.add(contactNode);
		}

		/*
		 * Contacts [array]
		 */
		rootNode.putPOJO("contacts", contactsArray);
		objectMapper.writeValue(file, rootNode);
	}
}
