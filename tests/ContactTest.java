package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import model.Address;
import model.Contact;
import model.Contact.Type;
import model.CorporateContact;
import model.Note;
import model.PersonalContact;
import model.PhoneNumber;

/**
 * A class to test {@link Contact}s such as {@link PersonalContact} and
 * {@link CorporateContact}
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Contact")
class ContactTest
{

	/**
	 * Different natures of contacts to test
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends Contact>[] contactTypes =
	    (Class<? extends Contact>[]) new Class<?>[] {
		PersonalContact.class,
		CorporateContact.class
	};

	/**
	 * Contact class provider used for parameterizes tests requiring the type of
	 * contact
	 * @return a stream of {@link Contact} classes to use in each Parameterized
	 * test
	 */
	private static Stream<Class<? extends Contact>> contactClassesProvider()
	{
		return Stream.of(contactTypes);
	}

	/**
	 * Contact class provider used for parameterizes tests requiring the type of
	 * {@link PersonalContact}
	 * @return a stream of {@link Contact} classes to use in each Parameterized
	 * test
	 */
	private static Stream<Class<? extends Contact>> personalContactClassProvider()
	{
		return contactClassesProvider().filter((Class<? extends Contact> c) -> {
			return (c == PersonalContact.class);
		});
	}

	/**
	 * Contact class provider used for parameterizes tests requiring the type of
	 * {@link CorporateContact}
	 * @return a stream of {@link Contact} classes to use in each Parameterized
	 * test
	 */
	private static Stream<Class<? extends Contact>> corporateContactClassProvider()
	{
		return contactClassesProvider().filter((Class<? extends Contact> c) -> {
			return (c == CorporateContact.class);
		});
	}

	/**
	 * The contact to test
	 */
	private Contact testContact;

	/**
	 * The name of the current test in all testXXX methods
	 */
	private String testName;

	/**
	 * Names for {@link PersonalContact}s
	 */
	private static final String[] lastNames = new String[] {
		"Durand",
		"Dupont",
	};

	/**
	 * First names for {@link PersonalContact}s
	 */
	private static final String[] firstNames = new String [] {
		"Pierre",
		"Paul",
	};

	/**
	 * Names for {@link CorporateContact}s
	 */
	private static final String[] names = new String[] {
		"ENSIIE",
		"Total",
	};

	/**
	 * Images for  {@link Contact}s
	 */
	private static final URI[] imageURIs = new URI[] {
		URI.create("icons/question_person-48.png"),
		URI.create("icons/question_person-48.png"),
		URI.create("icons/checked_person-48.png"),
		URI.create("icons/add_person-48.png"),
		URI.create("icons/remove_person-48.png"),
		URI.create("icons/contacts-48.png"),
		URI.create("icons/organization-48.png"),
	};

	/**
	 * Personal Phone number titles
	 */
	private static final String[] personalPhoneTitles = new String[] {
		"Bureau",
		"Office",
		"Nord Ouest",
		"Nord Est",
		"Sud Est",
		"Sud Ouest",
		"Mobile",
		"Cellulaire",
		"Ventes",
		"Privé",
	};

	/**
	 * Personal Phone numbers
	 */
	private static final PhoneNumber[] personalPhoneNumbers = new PhoneNumber[] {
		PhoneNumber.parse("01 69 36 74 62"),
		PhoneNumber.parse("+33 1 69 36 74 62"),
		PhoneNumber.parse("02 69 36 74 62"),
		PhoneNumber.parse("03 69 36 74 62"),
		PhoneNumber.parse("04 69 36 74 62"),
		PhoneNumber.parse("05 69 36 74 62"),
		PhoneNumber.parse("06 69 36 74 62"),
		PhoneNumber.parse("07 69 36 74 62"),
		PhoneNumber.parse("08 69 36 74 62"),
		PhoneNumber.parse("09 69 36 74 62"),
	};

	/**
	 * Corporate Phone number titles
	 */
	private static final String[] corporatePhoneTitles = new String[] {
		"Acceuil",
		"DGS",
		"CRI",
	};

	/**
	 * Personal Phone numbers
	 */
	private static final PhoneNumber[] corporatePhoneNumbers = new PhoneNumber[] {
		PhoneNumber.parse("01 69 36 73 50"),
		PhoneNumber.parse("01 69 36 74 48"),
		PhoneNumber.parse("01 69 36 73 62"),
	};

	/**
	 * Personal Addresses titles
	 */
	private static final String[] personalAddressTitles = new String[] {
		"Bureau",
		"Labo",
		"Maison",
		"Cabane",
	};

	/**
	 * Personal Addresses
	 */
	private static final Address[] personalAddresses = new Address[] {
		new Address(1, "Place de la Résistance", "Evry-Courcouronnes", "91025"),
		new Address(45, "Rue du Ventoux", "Evry Cedex", "91020"),
		new Address(21, "Rue Gustave Courbet", "Corbeil-Essonnes", "91100"),
		new Address(1443, "Chem. de la Lièvre S", "Mont-Laurier", "QC J9L 3G3", Locale.CANADA),
	};

	/**
	 * Corporate Addresses titles
	 */
	private static final String[] corporateAddressTitles = new String[] {
		"Siege",
		"Remise des Diplômes",
	};

	/**
	 * Corporate Addresses
	 */
	private static final Address[] corporateAddresses = new Address[] {
		new Address(1, "Place de la Résistance", "Evry-Courcouronnes", "91025"),
		new Address(1, "Rue de l'International", "Evry-Courcouronnes", "91000"),
	};

	/**
	 * Personal Emails titles
	 */
	private static final String[] personalEmailTitles = new String[] {
		"Bureau",
		"Labo",
		"Perso",
	};

	/**
	 * Personal Emails
	 */
	private static final URI[] personalEmailURIs = new URI[] {
		URI.create("mailto:pierre.durand@ensiie.fr"),
		URI.create("mailto:pierre.durand@labo-univ.fr"),
		URI.create("mailto:pierrot34@gmail.com"),
	};

	/**
	 * Corporate Emails titles
	 */
	private static final String[] corporateEmailTitles = new String[] {
		"Accueil",
		"Pedago",
		"CRI",
		"Stages",
	};

	/**
	 * Corporate Emails
	 */
	private static final URI[] corporateEmailURIs = new URI[] {
		URI.create("mailto:info@ensiie.fr"),
		URI.create("mailto:diretud@listes.ensiie.fr"),
		URI.create("mailto:cri@listes.ensiie.fr"),
		URI.create("mailto:stages1a2a@listes.ensiie.fr"),
	};

	/**
	 * Links Title
	 */
	private static final String[] linkTitles = new String[] {
		"web",
		"Aurion",
		"pydio",
		"pedago-etu",
		"exam",
		"Host file",
		"Ticket",
		"Stages",
	};

	/**
	 * Links
	 */
	private static final URI[] linkURIs = new URI[] {
		URI.create("https://www.ensiie.fr"),
		URI.create("https://aurionweb.ensiie.fr/"),
		URI.create("https://pydio.pedago.ensiie.fr"),
		URI.create("sftp://pedago-etu.ensiie.fr"),
		URI.create("http://exam.ensiie.fr/"),
		URI.create("file:///etc/hosts"),
		URI.create("https://helpdesk.ensiie.fr/index.php?do=tasklist&project=#"),
		URI.create("https://stages.ensiie.fr/"),
	};

	/**
	 * Notes titles
	 */
	private static final String[] noteTitles = new String[] {
		"Digicode",
		"Poubelles Jaunes",
		"Poubelles",
	};

	/**
	 * Notes
	 */
	private static final Note[] notes = new Note[] {
		new Note("1234"),
		new Note("Lundi soir"),
		new Note("Mercredi soir"),
	};

	/**
	 * Swing Frame to contain {@link #panel}
	 */
	private static JFrame frame = null;

	/**
	 * JFX Panel to initialize JavaFX runtime
	 */
	private static JFXPanel panel = null;

	/**
	 * Flag indicating {@link #frame} has been constructed
	 */
	private static Condition frameReady = new Condition(false);

	/**
	 * Flag indicating {@link #panel} has been constructed
	 */
	private static Condition panelReady = new Condition(false);

	/**
	 * Launch a UI in A Swing Frame
	 */
	private static void initSWINGUI()
	{
        // This method is invoked on Swing thread
        frame = new JFrame("FX");
        panel = new JFXPanel();
        frame.setBounds(0, 0, 200, 200);
        frame.add(panel);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initJavaFXUI(panel);
                synchronized (panelReady)
				{
                	panelReady.setValue(true);
                	panelReady.notify();
				}
            }
        });
		/*
		 * wait to be notified of JavaFX thread completed setup
		 */
        synchronized (panelReady)
		{
			while (!panelReady.getValue())
			{
				try
				{
					panelReady.wait();
				}
				catch (IllegalMonitorStateException e)
				{
					fail(e.getLocalizedMessage());
				}
				catch (InterruptedException e)
				{
					fail(e.getLocalizedMessage());
				}
			}
		}
		assertNotNull(panel, "null JFXPanel");
	}

	/**
	 * Builds the JavaFX Panel containg the UI
	 * @param panel the pane containing the UI
	 */
	private static void initJavaFXUI(JFXPanel panel)
	{
		VBox vbox = new VBox();
		Scene scene = new Scene(vbox, 200, 200, true, SceneAntialiasing.BALANCED);
		panel.setScene(scene);
	}


	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
            	initSWINGUI();
            	synchronized (frameReady)
				{
            		frameReady.setValue(true);
                	frameReady.notify();
				}
            }
        });

        /*
		 * wait to be notified of Swing thread complete setup
		 */
        synchronized (frameReady)
		{
        	while (!frameReady.getValue())
        	{
                try
        		{
                	frameReady.wait();
        		}
        		catch (IllegalMonitorStateException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        		catch (InterruptedException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        	}
		}
		assertNotNull(frame, "frame is null");
		System.out.println("-------------------------------------------------");
		System.out.println("Contact tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after al tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		if (frame != null)
		{
			frame.dispose();
		}
		System.out.println("-------------------------------------------------");
		System.out.println("Contact tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
	}

	/**
	 * Teardown after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
	}

	/**
	 * Build a contact based on type index in arrays and testName (to be used
	 * in other tests)
	 * @param type the type of contat to build (either {@link PersonalContact} or
	 * {@link CorporateContact}
	 * @param index the index in various arrays to use to build arguments
	 * @param useAllValues if true use names and all other values, otherwise
	 * use only names.
	 * @param testName the name of the test method using this method
	 * @return a new instance of contact
	 */
	private final Contact buildContact(Class<? extends Contact> type,
	                                   int index,
	                                   boolean useAllValues,
	                                   String testName)
	{
		Constructor<? extends Contact> valuedConstructor = null;
		Class<?>[] argumentsTypes = null;
		Object[] arguments = null;
		Object instance = null;

		if (type == CorporateContact.class)
		{
			argumentsTypes = new Class<?>[6];
			argumentsTypes[0] = String.class;
			argumentsTypes[1] = URI.class;
			argumentsTypes[2] = PhoneNumber.class;
			argumentsTypes[3] = URI.class;
			argumentsTypes[4] = URI.class;
			argumentsTypes[5] = Note.class;
			arguments = new Object[6];
			arguments[0] = names[index];
			arguments[1] = useAllValues ? imageURIs[index] : null;
			arguments[2] = useAllValues ? corporatePhoneNumbers[index] : null;
			arguments[3] = useAllValues ? corporateEmailURIs[index] : null;
			arguments[4] = useAllValues ? linkURIs[index] : null;
			arguments[5] = useAllValues ? notes[index] : null;
		}
		else if (type == PersonalContact.class)
		{
			argumentsTypes = new Class<?>[8];
			argumentsTypes[0] = String.class;
			argumentsTypes[1] = String.class;
			argumentsTypes[2] = URI.class;
			argumentsTypes[3] = PhoneNumber.class;
			argumentsTypes[4] = URI.class;
			argumentsTypes[5] = URI.class;
			argumentsTypes[6] = Note.class;
			argumentsTypes[7] = CorporateContact.class;
			arguments = new Object[8];
			arguments[0] = firstNames[index];
			arguments[1] = lastNames[index];
			arguments[2] = useAllValues ? imageURIs[index] : null;
			arguments[3] = useAllValues ? personalPhoneNumbers[index] : null;
			arguments[4] = useAllValues ? personalEmailURIs[index] : null;
			arguments[5] = useAllValues ? linkURIs[index] : null;
			arguments[6] = useAllValues ? notes[index] : null;
			arguments[7] = useAllValues ? buildContact(CorporateContact.class, 0, false, testName) : null;
		}
		else
		{
			fail(testName + " Unknown class " + type.getSimpleName());
		}

		try
		{
			valuedConstructor = type.getConstructor(argumentsTypes);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName  + " No such constructor, "
			    + e.getLocalizedMessage());
		}
		catch (SecurityException e)
		{
			fail(testName  + " security exception, "
			    + e.getLocalizedMessage());
		}

		if (valuedConstructor != null)
		{
			try
			{
				instance = valuedConstructor.newInstance(arguments);
			}
			catch (InstantiationException e)
			{
				fail(testName
				    + " valued instanciation exception : Abstract class, "
				    + e.getLocalizedMessage());
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " valued constructor is inaccessible, "
				    + e.getLocalizedMessage());
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " valued constructor illegal argument, "
				    + e.getLocalizedMessage());
			}
			catch (InvocationTargetException e)
			{
				fail(testName
				    + " invoked valued constructor throwed an exception, "
				    + e.getLocalizedMessage()  + ", cause: "
				    + e.getCause().getLocalizedMessage());
			}
			catch (Exception e)
			{
				fail(testName + " build instance failed, "
				    + e.getLocalizedMessage());
			}
		}

		return (Contact) instance;
	}

	/**
	 * Build a contact based on type index in arrays and testName (to be used
	 * in other tests)
	 * @param type the type of contat to build (either {@link PersonalContact} or
	 * {@link CorporateContact}
	 * @param contact the isntance to copy
	 * @return a new instance of contact copied from provided instance
	 */
	private final Contact copyContact(Class<? extends Contact> type, Contact contact)
	{
		Constructor<? extends Contact> copyConstructor = null;
		Class<?>[] argumentsTypes = new Class<?>[1];
		Object[] arguments = new Object[1];
		arguments[0] = contact;
		Object instance = null;

		if (type == CorporateContact.class)
		{
			argumentsTypes[0] = Contact.class;
		}
		else if (type == PersonalContact.class)
		{
			argumentsTypes[0] = PersonalContact.class;
			if (!(contact instanceof PersonalContact))
			{
				fail(testName + " " + contact.getClass().getSimpleName()
				    + " is not a " + type.getSimpleName());
			}
		}
		else
		{
			fail(testName + " Unknown class " + type.getSimpleName());
		}

		try
		{
			copyConstructor = type.getConstructor(argumentsTypes);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName  + " No such constructor, "
			    + e.getLocalizedMessage());
		}
		catch (SecurityException e)
		{
			fail(testName  + " security exception, "
			    + e.getLocalizedMessage());
		}

		if (copyConstructor != null)
		{
			try
			{
				instance = copyConstructor.newInstance(arguments);
			}
			catch (InstantiationException e)
			{
				fail(testName
				    + " valued instanciation exception : Abstract class, "
				    + e.getLocalizedMessage());
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " valued constructor is inaccessible, "
				    + e.getLocalizedMessage());
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " valued constructor illegal argument, "
				    + e.getLocalizedMessage());
			}
			catch (InvocationTargetException e)
			{
				fail(testName
				    + " invoked valued constructor throwed an exception, "
				    + e.getLocalizedMessage()  + ", cause: "
				    + e.getCause().getLocalizedMessage());
			}
			catch (Exception e)
			{
				fail(testName + " build instance failed, "
				    + e.getLocalizedMessage());
			}
		}

		return (Contact) instance;
	}

	/**
	 * Builds the word set of a contact : a set of words that can be found in this contact
	 * @param contact the contact to explore
	 * @return a set of unique words found in the provided contact
	 */
	private static final Set<String> buildContactWordSet(Contact contact)
	{
		Set<String> set = new TreeSet<>();

		String[] words = contact.getName().split(" ");
		for (String word : words)
		{
			if (!word.isEmpty())
			{
				set.add(word);
			}
		}
		for (String key : contact.getPhoneNumberKeySet())
		{
			set.add(key);
			words = contact.getPhoneNumber(key).toString().split(" ");
			for (String word : words)
			{
				if (!word.isEmpty())
				{
					set.add(word);
				}
			}
		}
		for (String key : contact.getAddressKeySet())
		{
			set.add(key);
			String[] lines = contact.getAddress(key).toString().split("\n");
			for (String line : lines)
			{
				words = line.split(" ");
				for (String word : words)
				{
					if (!word.isEmpty())
					{
						set.add(word);
					}
				}
			}
		}

		for (String key : contact.getEmailKeySet())
		{
			set.add(key);
			URI email = contact.getEmail(key);
			String[] parts = email.getSchemeSpecificPart().split("@");
			for (String part : parts)
			{
				words = part.split(".");
				for (String word : words)
				{
					if (!word.isEmpty())
					{
						set.add(word);
					}
				}
			}
		}

		for (String key : contact.getLinksKeySet())
		{
			set.add(key);
			URI link = contact.getLink(key);
			String host = link.getHost();
			if (host != null)
			{
				String[] hostParts = host.split(".");
				for (String part : hostParts)
				{
					if (!part.isEmpty())
					{
						set.add(part);
					}
				}
			}
			String path = link.getPath();
			if (path != null)
			{
				String[] pathParts = path.split("/");
				for (String part : pathParts)
				{
					if (!part.isEmpty())
					{
						set.add(part);
					}
				}
			}
		}

		for (String key : contact.getNotesKeySet())
		{
			set.add(key);
			Note note = contact.getNote(key);
			words = note.getContent().split(" ");
			for (String word : words)
			{
				if (!word.isEmpty())
				{
					set.add(word);
				}
			}
		}

		if (contact instanceof PersonalContact)
		{
			words = ((PersonalContact) contact).getFirstName().split(" ");
			for (String word : words)
			{
				if (!word.isEmpty())
				{
					set.add(word);
				}
			}

			CorporateContact corporation = ((PersonalContact) contact).getCorporation();
			if (corporation != null)
			{
				words = corporation.toString().split(" ");
				for (String word : words)
				{
					if (!word.isEmpty())
					{
						set.add(word);
					}
				}
			}
		}

		if (contact instanceof CorporateContact)
		{
			Set<PersonalContact> employees = ((CorporateContact)contact).getEmployees();
			for (PersonalContact person : employees)
			{
				set.addAll(buildContactWordSet(person));
			}
		}

		return set;
	}

	/**
	 * Test method for {@link model.Contact#Contact(java.lang.String, java.net.URI, model.PhoneNumber, java.net.URI, java.net.URI, model.Note)}.
	 * and also {@link PersonalContact#PersonalContact(String, String, java.net.URI, model.PhoneNumber, java.net.URI, java.net.URI, model.Note, CorporateContact)}
	 * and also {@link CorporateContact#CorporateContact(String, java.net.URI, model.PhoneNumber, java.net.URI, java.net.URI, model.Note)}
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("Contact(...)")
	@Order(1)
	final void testValuedConstructor(Class<? extends Contact> type)
	{
		String testName = new String(type.getSimpleName() + "(...)");
		System.out.println(testName);

		/*
		 * Minimal Contact with only names (other arguments are null)
		 */
		testContact = buildContact(type, 0, false, testName);
		assertNotNull(testContact, " unexpected null minimal instance");

		/*
		 * Maximal Contact with all values
		 */
		testContact = buildContact(type, 0, true, testName);
		assertNotNull(testContact, " unexpected null maximal instance");
	}

	/**
	 * Test method for {@link model.Contact#Contact(model.Contact)}
	 * and also {@link PersonalContact#PersonalContact(PersonalContact)}
	 * and also {@link CorporateContact#CorporateContact(Contact)}
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("Contact(Contact)")
	@Order(2)
	final void testCopyConstructor(Class<? extends Contact> type)
	{
		String testName = new String(type.getSimpleName() + "(Contact)");
		System.out.println(testName);

		/*
		 * Minimal Contact with only names (other arguments are null)
		 */
		testContact = buildContact(type, 0, false, testName);
		Contact otherContact = copyContact(type, testContact);
		assertNotNull(otherContact,
		              testName + " unexpected null copy minimal instance");
		assertNotSame(testContact,
		              otherContact,
		              testName + " unexpected shallow copy of minimal instance");
		assertEquals(testContact,
		             otherContact,
		             testName + " unexpected inequality oc copied minimal instance");

		/*
		 * Maximal Contact with all values
		 */
		testContact = buildContact(type, 0, true, testName);
		otherContact = copyContact(type, testContact);
		assertNotNull(otherContact,
		              testName + " unexpected null copy maximal instance");
		assertNotSame(testContact,
		              otherContact,
		              testName + " unexpected shallow copy of maximal instance");
		assertEquals(testContact,
		             otherContact,
		             testName + " unexpected inequality oc copied maximal instance");
	}

	/**
	 * Test method for {@link model.Contact#getName()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getName()")
	@Order(3)
	final void testGetName(Class<? extends Contact> type)
	{
		testName = new String("getName()");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			assertEquals(providedNames[i],
			             testContact.getName());
		}
	}

	/**
	 * Test method for {@link model.Contact#setName(String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("setName(String)")
	@Order(4)
	final void testSetName(Class<? extends Contact> type)
	{
		testName = new String("setName(String)");
		System.out.println(testName);
		final String empty = "";
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			String name = testContact.getName();

			/*
			 * Setting null name shall have no effect
			 */
			testContact.setName(null);
			assertEquals(name,
			             testContact.getName(),
			             testName + " unexpected name after setName(null)");

			/*
			 * Setting empty name shall have no effect
			 */
			testContact.setName(empty);
			assertEquals(name,
			             testContact.getName(),
			             testName + " unexpected name after setName(empty)");

			/*
			 * Setting a valid name should have an effect
			 */
			String newName = providedNames[(i+1)%providedNames.length];
			testContact.setName(newName);
			assertNotEquals(name,
			                testContact.getName(),
			                testName
			                    + " unexpected unchanged name after setName("
			                    + newName + ")");
			assertEquals(newName,
			             testContact.getName(),
			             testName + " unexpected name after setName(" + newName
			                 + ")");
		}
	}

	/**
	 * Test method for {@link model.PersonalContact#getFirstName()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("personalContactClassProvider")
	@DisplayName("getFirstName()")
	@Order(5)
	final void testGetFirstName(Class<? extends Contact> type)
	{
		testName = new String("getFirstName()");
		System.out.println(testName);

		for (int i = 0; i < firstNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			PersonalContact personalTestContact = (PersonalContact) testContact;
			assertEquals(firstNames[i],
			             personalTestContact.getFirstName(),
			             testName + " unexpected first name");
		}
	}

	/**
	 * Test method for {@link model.PersonalContact#setFirstName(String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("personalContactClassProvider")
	@DisplayName("setFirstName(String)")
	@Order(6)
	final void testSetFirstName(Class<? extends Contact> type)
	{
		testName = new String("setFirstName(String)");
		System.out.println(testName);
		final String empty = "";

		for (int i = 0; i < firstNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			PersonalContact personalTestContact = (PersonalContact) testContact;
			String firstName = personalTestContact.getFirstName();

			/*
			 * Setting null first name shall have no effect
			 */
			personalTestContact.setFirstName(null);
			assertEquals(firstName,
			             personalTestContact.getFirstName(),
			             testName + " unexpected name after setFirstName(null)");

			/*
			 * Setting empty first name shall have no effect
			 */
			personalTestContact.setFirstName(empty);
			assertEquals(firstName,
			             personalTestContact.getFirstName(),
			             testName + " unexpected name after setFirstName(empty)");

			/*
			 * Setting a valid first name should have an effect
			 */
			String newFirstName = firstNames[(i+1)%firstNames.length];
			personalTestContact.setFirstName(newFirstName);
			assertNotEquals(firstName,
			                personalTestContact.getFirstName(),
			                testName
			                    + " unexpected unchanged name after setFirstName("
			                    + newFirstName + ")");
			assertEquals(newFirstName,
			             personalTestContact.getFirstName(),
			             testName + " unexpected name after setFirstName(" + newFirstName
			                 + ")");
		}
	}

	/**
	 * Test method for {@link model.PersonalContact#getCorporation()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("personalContactClassProvider")
	@DisplayName("getCorporation())")
	@Order(7)
	final void testGetCorporation(Class<? extends Contact> type)
	{
		testName = new String("getCorporation()");
		System.out.println(testName);

		for (int i = 0; i < lastNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			PersonalContact personalTestContact = (PersonalContact) testContact;
			assertNull(personalTestContact.getCorporation(),
			           testName + " unexpected non null corporation");

			testContact = buildContact(type, i, true, testName);
			personalTestContact = (PersonalContact) testContact;
			assertNotNull(personalTestContact.getCorporation(),
			              testName + " unexpected null corporation");
		}
	}

	/**
	 * Test method for {@link model.PersonalContact#setCorporation(CorporateContact)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("personalContactClassProvider")
	@DisplayName("setCorporation(CorporateContact)")
	@Order(8)
	final void testSetCorporation(Class<? extends Contact> type)
	{
		testName = new String("setCorporation(CorporateContact)");
		System.out.println(testName);
		for (int i = 0; i < lastNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			CorporateContact corporation =
			    (CorporateContact) buildContact(CorporateContact.class,
			                                    0,
			                                    false,
			                                    testName);
			PersonalContact personalTestContact = (PersonalContact) testContact;
			assertFalse(corporation.contains(personalTestContact),
			            testName + " unexpected personal contact found in "
			            	+ "corporation before adding it !!!");

			CorporateContact previousCorporation = personalTestContact.getCorporation();
			assertNull(previousCorporation,
			           testName + " unexpected non null previous corporation");

			/*
			 * Setting non null corporation
			 */
			personalTestContact.setCorporation(corporation);
			assertSame(corporation,
			           personalTestContact.getCorporation(),
			           testName + " unexpected different corporation");
			assertTrue(corporation.contains(personalTestContact),
			           testName + " corporation doesn't contains contact");

			/*
			 * Setting null corporation
			 */
			personalTestContact.setCorporation(null);
			assertNull(personalTestContact.getCorporation(),
			           testName + " unexpected non null corporation");
			assertFalse(corporation.contains(personalTestContact),
			            testName + " corporation still contains personal contact");
		}
	}

	/**
	 * Test method for {@link model.CorporateContact#getEmployees()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("corporateContactClassProvider")
	@DisplayName("getEmployees()")
	@Order(9)
	final void testGetEmployees(Class<? extends Contact> type)
	{
		testName = new String("getEmployees()");
		System.out.println(testName);

		for (int i = 0; i < names.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			CorporateContact corporateTestContact = (CorporateContact) testContact;
			Set<PersonalContact> employees = corporateTestContact.getEmployees();
			assertNotNull(employees,
			              testName + " unexpected null employees set");
			assertTrue(employees.isEmpty(),
			           testName + " unexpected non empty initial employees set");
			/*
			 * Add employees
			 */
			for (int j = 0; j < lastNames.length; j++)
			{
				Contact contact = buildContact(PersonalContact.class, j, false, testName);
				PersonalContact personalContact = (PersonalContact) contact;

				corporateTestContact.add(personalContact);
				Set<PersonalContact> newEmployeeSet = corporateTestContact.getEmployees();

				assertSame(employees,
				           newEmployeeSet,
				           testName + " unexpected different employees set");
				assertEquals(j+1,
				             newEmployeeSet.size(),
				             testName + " unexpected employees set size after adding");
			}
		}
	}

	/**
	 * Test method for {@link model.CorporateContact#contains(PersonalContact)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("corporateContactClassProvider")
	@DisplayName("contains(PersonalContact)")
	@Order(10)
	final void testContainsPersonalContact(Class<? extends Contact> type)
	{
		testName = new String("contains(PersonalContact)");
		System.out.println(testName);

		for (int i = 0; i < names.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			CorporateContact corporateTestContact = (CorporateContact) testContact;
			Set<PersonalContact> employees = corporateTestContact.getEmployees();
			assertNotNull(employees,
			              testName + " unexpected null employees set");
			assertTrue(employees.isEmpty(),
			           testName + " unexpected non empty initial employees set");
			/*
			 * Add employees
			 */
			for (int j = 0; j < lastNames.length; j++)
			{
				Contact contact = buildContact(PersonalContact.class, j, false, testName);
				PersonalContact personalContact = (PersonalContact) contact;

				/*
				 * Employees does not contains personalContact yet
				 */
				assertFalse(corporateTestContact.contains(personalContact),
				            testName + " unexpected already contained employee");

				corporateTestContact.add(personalContact);

				/*
				 * Employees contains personalContact
				 */
				assertTrue(corporateTestContact.contains(personalContact),
				           testName + " unexpectedly uncontained employee");

				corporateTestContact.remove(personalContact);

				/*
				 * Employee should not be contained anymore
				 */
				assertFalse(corporateTestContact.contains(personalContact),
				            testName + " unexpected contained employee after remove");
			}
		}
	}

	/**
	 * Test method for {@link model.CorporateContact#add(PersonalContact)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("corporateContactClassProvider")
	@DisplayName("add(PersonalContact)")
	@Order(11)
	final void testAddPersonalContact(Class<? extends Contact> type)
	{
		testName = new String("add(PersonalContact)");
		System.out.println(testName);
		for (int i = 0; i < names.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			CorporateContact corporateTestContact = (CorporateContact) testContact;
			for (int j = 0; j < lastNames.length; j++)
			{
				Contact contact = buildContact(PersonalContact.class, j, false, testName);
				PersonalContact personalContact = (PersonalContact) contact;

				/*
				 * Add employees
				 */
				boolean added = corporateTestContact.add(personalContact);
				assertTrue(added,
				           testName + " unexpected result after adding new employee");

				Set<PersonalContact> employees = corporateTestContact.getEmployees();
				assertTrue(employees.contains(personalContact),
				           testName + " employee not added to employees set");
				assertEquals(j+1,
				             employees.size(),
				             testName + " unexpected employees set size after adding");
				assertSame(corporateTestContact,
				           personalContact.getCorporation(),
				           testName + " unexpected corporation in added employee");
				/*
				 * Adding the same employee twice should fail
				 */
				added = corporateTestContact.add(personalContact);
				assertFalse(added,
				            testName + " unexpected result after adding employee again");
				assertEquals(j+1,
				             employees.size(),
				             testName + " unexpected employees set size after adding twice");
			}
		}
	}

	/**
	 * Test method for {@link model.CorporateContact#remove(PersonalContact)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("corporateContactClassProvider")
	@DisplayName("remove(PersonalContact)")
	@Order(12)
	final void testRemovePersonalContact(Class<? extends Contact> type)
	{
		testName = new String("remove(PersonalContact)");
		System.out.println(testName);
		for (int i = 0; i < names.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			CorporateContact corporateTestContact = (CorporateContact) testContact;
			PersonalContact[] employees = new PersonalContact[lastNames.length];
			for (int j = 0; j < lastNames.length; j++)
			{
				Contact contact = buildContact(PersonalContact.class, j, false, testName);
				employees[j] = (PersonalContact) contact;
				/*
				 * Add employees
				 */
				corporateTestContact.add(employees[j]);
			}

			Set<PersonalContact> employeesSet = corporateTestContact.getEmployees();
			assertEquals(employees.length,
			             employeesSet.size(),
			             testName + " unexpected employees set size");

			/*
			 * remove employees
			 */
			int expectedSize = employeesSet.size() - 1;
			for (int j = 0; j < employees.length; j++)
			{
				boolean removed = corporateTestContact.remove(employees[j]);
				assertTrue(removed,
				           testName + " unexpected result after removing "
				           	+ "employee " + employees[j]);
				assertEquals(expectedSize--,
				             employeesSet.size(),
				             testName + " unexpected employees set size after remove");
				assertNull(employees[j].getCorporation(),
				           testName + " unexpected corporation in removed employee");

				/*
				 * removing the same employee twice shoudl fail
				 */
				removed = corporateTestContact.remove(employees[j]);
				assertFalse(removed,
				            testName + " unexpected result after removing "
				           	 + "employee " + employees[j] + " again");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getImage()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getImage()")
	@Order(13)
	final void testGetImage(Class<? extends Contact> type)
	{
		testName = new String("getImage()");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Check empty image
			 */
			Image image = testContact.getImage();
			assertNull(image,
			           testName +  " unexpected non null image");

			/*
			 * Check non empty image
			 */
			testContact = buildContact(type, i, true, testName);
			image = testContact.getImage();
			assertNotNull(image,
			              testName +  " unexpected non null image");
		}
	}

	/**
	 * Test method for {@link model.Contact#getImagePath()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getImagePath()")
	@Order(14)
	final void testGetImagePath(Class<? extends Contact> type)
	{
		testName = new String("getImagePath()");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Check empty image path
			 */
			URI imagePath = testContact.getImagePath();
			assertNull(imagePath,
			           testName +  " unexpected non null image path");

			/*
			 * Check non empty image path
			 */
			testContact = buildContact(type, i, true, testName);
			imagePath = testContact.getImagePath();
			assertNotNull(imagePath,
			              testName +  " unexpected non null image");
			assertEquals(imageURIs[i],
			             imagePath,
			             testName + " unexpected image path");
		}
	}

	/**
	 * Test method for {@link model.Contact#setImage(java.net.URI)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("setImage(java.net.URI)")
	@Order(15)
	final void testSetImage(Class<? extends Contact> type)
	{
		testName = new String("setImage(java.net.URI)");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Setting null image path should raise NullPointerException
			 */
			assertThrows(NullPointerException.class, () -> {
				Contact contact = copyContact(type, testContact);
				contact.setImage(null);
			});

			/*
			 * Setting invalid image path should raise IllegaArgumentException
			 */
			assertThrows(IllegalArgumentException.class, () -> {
				Contact contact = copyContact(type, testContact);
				contact.setImage(URI.create("NoWay"));
			});

			URI imagePath = imageURIs[i];
			try
			{
				testContact.setImage(imagePath);
				assertNotNull(testContact.getImage(),
				              testName + " unexpected null image");
				assertNotNull(testContact.getImagePath(),
				              testName + " unexpected null image path");
				assertEquals(imagePath,
				             testContact.getImagePath(),
				             testName + " unexpected image path");
			}
			catch (NullPointerException | IllegalArgumentException e)
			{
				fail(testName + " unexpected exception during setImage("
					+ imagePath + "), " + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getPhoneNumber(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getPhoneNumber(java.lang.String)")
	@Order(16)
	final void testGetPhoneNumber(Class<? extends Contact> type)
	{
		testName = new String("getPhoneNumber(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		PhoneNumber[] providedPhoneNumbers = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalPhoneTitles;
			providedPhoneNumbers = personalPhoneNumbers;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporatePhoneTitles;
			providedPhoneNumbers = corporatePhoneNumbers;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Getting PhoneNumbers on empty contact should only return
			 * null PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getPhoneNumber(providedTitles[j]),
				           testName + " unexpected non null PhoneNumber");
			}

			/*
			 * Adding PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addPhoneNumber(providedTitles[j], providedPhoneNumbers[j]);
			}

			/*
			 * Getting PhoneNumbers on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNotNull(testContact.getPhoneNumber(providedTitles[j]),
				              testName + " unexpected null PhoneNumber");
				assertSame(providedPhoneNumbers[j],
				           testContact.getPhoneNumber(providedTitles[j]),
				           testName + " unexpected PhoneNumber");
			}

			/*
			 * Removing PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removePhoneNumber(providedTitles[j]);
			}

			/*
			 * Getting PhoneNumbers on empty contact should only return
			 * null PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getPhoneNumber(providedTitles[j]),
				           testName + " unexpected non null PhoneNumber");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getPhoneNumberKeySet()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getPhoneNumberKeySet()")
	@Order(17)
	final void testGetPhoneNumberKeySet(Class<? extends Contact> type)
	{
		testName = new String("getPhoneNumberKeySet()");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		PhoneNumber[] providedPhoneNumbers = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalPhoneTitles;
			providedPhoneNumbers = personalPhoneNumbers;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporatePhoneTitles;
			providedPhoneNumbers = corporatePhoneNumbers;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			Set<String> keySet = testContact.getPhoneNumberKeySet();
			assertNotNull(keySet,
			              testName + " unexpected null key set");
			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty key set");

			/*
			 * Adding PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addPhoneNumber(providedTitles[j], providedPhoneNumbers[j]);
			}

			assertEquals(providedTitles.length,
			             keySet.size(),
			             testName + " unexpected key set size after adding PhoneNumbers");

			/*
			 * Getting PhoneNumbers on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertTrue(keySet.contains(providedTitles[j]),
				           testName + " key " + providedTitles[j]
				        	   + " could not be found in key set");
			}
			for (String key : keySet)
			{
				for (int k = 0; k < providedTitles.length; k++)
				{
					if (key.equals(providedTitles[k]))
					{
						assertSame(testContact.getPhoneNumber(key),
						           providedPhoneNumbers[k],
						           testName + " unexpected PhoneNumber");
					}
				}
			}

			/*
			 * Removing PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removePhoneNumber(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#addPhoneNumber(java.lang.String, model.PhoneNumber)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("addPhoneNumber(java.lang.String, model.PhoneNumber)")
	@Order(18)
	final void testAddPhoneNumber(Class<? extends Contact> type)
	{
		testName = new String("addPhoneNumber(java.lang.String, model.PhoneNumber)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		PhoneNumber[] providedPhoneNumbers = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalPhoneTitles;
			providedPhoneNumbers = personalPhoneNumbers;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporatePhoneTitles;
			providedPhoneNumbers = corporatePhoneNumbers;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getPhoneNumberKeySet();

			/*
			 * Adding PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean added = testContact.addPhoneNumber(providedTitles[j], providedPhoneNumbers[j]);
				assertTrue(added,
				           testName + " unexpected not added PhoneNumber");
				PhoneNumber element = testContact.getPhoneNumber(providedTitles[j]);
				assertNotNull(element,
				              testName + " unexpected null PhoneNumber");
				assertSame(providedPhoneNumbers[j],
				           element,
				           testName + " unexpected PhoneNumber");
				assertEquals(j+1,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Adding element twice should fail
				 */
				added = testContact.addPhoneNumber(providedTitles[j], providedPhoneNumbers[j]);
				assertFalse(added,
				            testName + " unexpectedly added element twice");
			}

			/*
			 * Removing PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removePhoneNumber(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#removePhoneNumber(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("removePhoneNumber(java.lang.String)")
	@Order(19)
	final void testRemovePhoneNumber(Class<? extends Contact> type)
	{
		testName = new String("removePhoneNumber(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		PhoneNumber[] providedPhoneNumbers = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalPhoneTitles;
			providedPhoneNumbers = personalPhoneNumbers;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporatePhoneTitles;
			providedPhoneNumbers = corporatePhoneNumbers;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getPhoneNumberKeySet();

			/*
			 * Adding PhoneNumbers
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addPhoneNumber(providedTitles[j], providedPhoneNumbers[j]);
			}

			/*
			 * Removing PhoneNumbers
			 */
			int expectedSize = keySet.size();
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean removed = testContact.removePhoneNumber(providedTitles[j]);
				assertTrue(removed,
				           testName + " unexpected not removed PhoneNumber");
				assertEquals(--expectedSize,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Removing twice should fail
				 */
				removed = testContact.removePhoneNumber(providedTitles[j]);
				assertFalse(removed,
				            testName + " unexpectedly removed element twice");
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#getAddress(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getAddress(java.lang.String)")
	@Order(20)
	final void testGetAddress(Class<? extends Contact> type)
	{
		testName = new String("getAddress(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		Address[] providedAdresses = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalAddressTitles;
			providedAdresses = personalAddresses;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateAddressTitles;
			providedAdresses = corporateAddresses;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Getting Addreses on empty contact should only return
			 * null Addreses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getAddress(providedTitles[j]),
				           testName + " unexpected non null Address");
			}

			/*
			 * Adding Addreses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addAddress(providedTitles[j], providedAdresses[j]);
			}

			/*
			 * Getting Addreses on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNotNull(testContact.getAddress(providedTitles[j]),
				              testName + " unexpected null Address");
				assertSame(providedAdresses[j],
				           testContact.getAddress(providedTitles[j]),
				           testName + " unexpected Address");
			}

			/*
			 * Removing Addreses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeAddress(providedTitles[j]);
			}

			/*
			 * Getting Addreses on empty contact should only return
			 * null Addreses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getAddress(providedTitles[j]),
				           testName + " unexpected non null Address");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getAddressKeySet()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getAddressKeySet()")
	@Order(21)
	final void testGetAddressKeySet(Class<? extends Contact> type)
	{
		testName = new String("getAddressKeySet()");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		Address[] providedAddresss = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalAddressTitles;
			providedAddresss = personalAddresses;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateAddressTitles;
			providedAddresss = corporateAddresses;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			Set<String> keySet = testContact.getAddressKeySet();
			assertNotNull(keySet,
			              testName + " unexpected null key set");
			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty key set");

			/*
			 * Adding Addresses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addAddress(providedTitles[j], providedAddresss[j]);
			}

			assertEquals(providedTitles.length,
			             keySet.size(),
			             testName + " unexpected key set size after adding Addresss");

			/*
			 * Getting Addresses on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertTrue(keySet.contains(providedTitles[j]),
				           testName + " key " + providedTitles[j]
				        	   + " could not be found in key set");
			}
			for (String key : keySet)
			{
				for (int k = 0; k < providedTitles.length; k++)
				{
					if (key.equals(providedTitles[k]))
					{
						assertSame(testContact.getAddress(key),
						           providedAddresss[k],
						           testName + " unexpected Address");
					}
				}
			}

			/*
			 * Removing Addresses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeAddress(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#addAddress(java.lang.String, model.Address)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("addAddress(java.lang.String, model.Address)")
	@Order(22)
	final void testAddAddress(Class<? extends Contact> type)
	{
		testName = new String("addAddress(java.lang.String, model.Address)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		Address[] providedAddresses = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalAddressTitles;
			providedAddresses = personalAddresses;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateAddressTitles;
			providedAddresses = corporateAddresses;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getAddressKeySet();

			/*
			 * Adding Addresses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean added = testContact.addAddress(providedTitles[j], providedAddresses[j]);
				assertTrue(added,
				           testName + " unexpected not added Address");
				Address element = testContact.getAddress(providedTitles[j]);
				assertNotNull(element,
				              testName + " unexpected null Address");
				assertSame(providedAddresses[j],
				           element,
				           testName + " unexpected Address");
				assertEquals(j+1,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Adding element twice should fail
				 */
				added = testContact.addAddress(providedTitles[j], providedAddresses[j]);
				assertFalse(added,
				            testName + " unexpectedly added element twice");
			}

			/*
			 * Removing Addresses
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeAddress(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#removeAddress(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("removeAddress(java.lang.String)")
	@Order(23)
	final void testRemoveAddress(Class<? extends Contact> type)
	{
		testName = new String("removeAddress(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		Address[] providedAddresses = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalAddressTitles;
			providedAddresses = personalAddresses;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateAddressTitles;
			providedAddresses = corporateAddresses;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getAddressKeySet();

			/*
			 * Adding Addresss
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addAddress(providedTitles[j], providedAddresses[j]);
			}

			/*
			 * Removing Addresss
			 */
			int expectedSize = keySet.size();
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean removed = testContact.removeAddress(providedTitles[j]);
				assertTrue(removed,
				           testName + " unexpected not removed Address");
				assertEquals(--expectedSize,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Removing twice should fail
				 */
				removed = testContact.removeAddress(providedTitles[j]);
				assertFalse(removed,
				            testName + " unexpectedly removed element twice");
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#getEmail(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getEmail(java.lang.String)")
	@Order(24)
	final void testGetEmail(Class<? extends Contact> type)
	{
		testName = new String("getEmail(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		URI[] providedEmails = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalEmailTitles;
			providedEmails = personalEmailURIs;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateEmailTitles;
			providedEmails = corporateEmailURIs;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Getting Emails on empty contact should only return
			 * null Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getEmail(providedTitles[j]),
				           testName + " unexpected non null Email");
			}

			/*
			 * Adding Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addEmail(providedTitles[j], providedEmails[j]);
			}

			/*
			 * Getting Emails on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNotNull(testContact.getEmail(providedTitles[j]),
				              testName + " unexpected null Email");
				assertSame(providedEmails[j],
				           testContact.getEmail(providedTitles[j]),
				           testName + " unexpected Email");
			}

			/*
			 * Removing Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeEmail(providedTitles[j]);
			}

			/*
			 * Getting Emails on empty contact should only return
			 * null Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getEmail(providedTitles[j]),
				           testName + " unexpected non null Email");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getEmailKeySet()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getEmailKeySet()")
	@Order(25)
	final void testGetEmailKeySet(Class<? extends Contact> type)
	{
		testName = new String("getEmailKeySet()");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		URI[] providedEmails = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalEmailTitles;
			providedEmails = personalEmailURIs;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateEmailTitles;
			providedEmails = corporateEmailURIs;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			Set<String> keySet = testContact.getEmailKeySet();
			assertNotNull(keySet,
			              testName + " unexpected null key set");
			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty key set");

			/*
			 * Adding Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addEmail(providedTitles[j], providedEmails[j]);
			}

			assertEquals(providedTitles.length,
			             keySet.size(),
			             testName + " unexpected key set size after adding Emails");

			/*
			 * Getting Emails on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertTrue(keySet.contains(providedTitles[j]),
				           testName + " key " + providedTitles[j]
				        	   + " could not be found in key set");
			}
			for (String key : keySet)
			{
				for (int k = 0; k < providedTitles.length; k++)
				{
					if (key.equals(providedTitles[k]))
					{
						assertSame(testContact.getEmail(key),
						           providedEmails[k],
						           testName + " unexpected Email");
					}
				}
			}

			/*
			 * Removing Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeEmail(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#addEmail(java.lang.String, java.net.URI)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("addEmail(java.lang.String, java.net.URI)")
	@Order(26)
	final void testAddEmail(Class<? extends Contact> type)
	{
		testName = new String("addEmail(java.lang.String, java.net.URI)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		URI[] providedEmails = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalEmailTitles;
			providedEmails = personalEmailURIs;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateEmailTitles;
			providedEmails = corporateEmailURIs;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getEmailKeySet();

			/*
			 * Adding Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean added = testContact.addEmail(providedTitles[j], providedEmails[j]);
				assertTrue(added,
				           testName + " unexpected not added Email");
				URI element = testContact.getEmail(providedTitles[j]);
				assertNotNull(element,
				              testName + " unexpected null Email");
				assertSame(providedEmails[j],
				           element,
				           testName + " unexpected Email");
				assertEquals(j+1,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Adding element twice should fail
				 */
				added = testContact.addEmail(providedTitles[j], providedEmails[j]);
				assertFalse(added,
				            testName + " unexpectedly added element twice");
			}

			/*
			 * Removing Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeEmail(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#removeEmail(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("removeEmail(java.lang.String)")
	@Order(27)
	final void testRemoveEmail(Class<? extends Contact> type)
	{
		testName = new String("removeEmail(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = null;
		URI[] providedEmails = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			providedTitles = personalEmailTitles;
			providedEmails = personalEmailURIs;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			providedTitles = corporateEmailTitles;
			providedEmails = corporateEmailURIs;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getEmailKeySet();

			/*
			 * Adding Emails
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addEmail(providedTitles[j], providedEmails[j]);
			}

			/*
			 * Removing Emails
			 */
			int expectedSize = keySet.size();
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean removed = testContact.removeEmail(providedTitles[j]);
				assertTrue(removed,
				           testName + " unexpected not removed Email");
				assertEquals(--expectedSize,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Removing twice should fail
				 */
				removed = testContact.removeEmail(providedTitles[j]);
				assertFalse(removed,
				            testName + " unexpectedly removed element twice");
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#getLink(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getLink(java.lang.String)")
	@Order(28)
	final void testGetLink(Class<? extends Contact> type)
	{
		testName = new String("getLink(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = linkTitles;
		URI[] providedLinks = linkURIs;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Getting Links on empty contact should only return
			 * null Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getLink(providedTitles[j]),
				           testName + " unexpected non null Link");
			}

			/*
			 * Adding Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addLink(providedTitles[j], providedLinks[j]);
			}

			/*
			 * Getting Links on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNotNull(testContact.getLink(providedTitles[j]),
				              testName + " unexpected null Link");
				assertSame(providedLinks[j],
				           testContact.getLink(providedTitles[j]),
				           testName + " unexpected Link");
			}

			/*
			 * Removing Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeLink(providedTitles[j]);
			}

			/*
			 * Getting Links on empty contact should only return
			 * null Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getLink(providedTitles[j]),
				           testName + " unexpected non null Link");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getLinksKeySet()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getLinksKeySet()")
	@Order(29)
	final void testGetLinksKeySet(Class<? extends Contact> type)
	{
		testName = new String("getLinksKeySet()");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = linkTitles;
		URI[] providedLinks = linkURIs;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			Set<String> keySet = testContact.getLinksKeySet();
			assertNotNull(keySet,
			              testName + " unexpected null key set");
			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty key set");

			/*
			 * Adding Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addLink(providedTitles[j], providedLinks[j]);
			}

			assertEquals(providedTitles.length,
			             keySet.size(),
			             testName + " unexpected key set size after adding Links");

			/*
			 * Getting Links on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertTrue(keySet.contains(providedTitles[j]),
				           testName + " key " + providedTitles[j]
				        	   + " could not be found in key set");
			}
			for (String key : keySet)
			{
				for (int k = 0; k < providedTitles.length; k++)
				{
					if (key.equals(providedTitles[k]))
					{
						assertSame(testContact.getLink(key),
						           providedLinks[k],
						           testName + " unexpected Link");
					}
				}
			}

			/*
			 * Removing Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeLink(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#addLink(java.lang.String, java.net.URI)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("addLink(java.lang.String, java.net.URI)")
	@Order(30)
	final void testAddLink(Class<? extends Contact> type)
	{
		testName = new String("addLink(java.lang.String, java.net.URI)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = linkTitles;
		URI[] providedLinks = linkURIs;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getLinksKeySet();

			/*
			 * Adding Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean added = testContact.addLink(providedTitles[j], providedLinks[j]);
				assertTrue(added,
				           testName + " unexpected not added Link");
				URI element = testContact.getLink(providedTitles[j]);
				assertNotNull(element,
				              testName + " unexpected null Link");
				assertSame(providedLinks[j],
				           element,
				           testName + " unexpected Link");
				assertEquals(j+1,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Adding element twice should fail
				 */
				added = testContact.addLink(providedTitles[j], providedLinks[j]);
				assertFalse(added,
				            testName + " unexpectedly added element twice");
			}

			/*
			 * Removing Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeLink(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#removeLink(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("removeLink(java.lang.String)")
	@Order(31)
	final void testRemoveLink(Class<? extends Contact> type)
	{
		testName = new String("removeLink(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = linkTitles;
		URI[] providedLinks = linkURIs;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getLinksKeySet();

			/*
			 * Adding Links
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addLink(providedTitles[j], providedLinks[j]);
			}

			/*
			 * Removing Links
			 */
			int expectedSize = keySet.size();
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean removed = testContact.removeLink(providedTitles[j]);
				assertTrue(removed,
				           testName + " unexpected not removed Link");
				assertEquals(--expectedSize,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Removing twice should fail
				 */
				removed = testContact.removeLink(providedTitles[j]);
				assertFalse(removed,
				            testName + " unexpectedly removed element twice");
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#getNote(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getNote(java.lang.String)")
	@Order(32)
	final void testGetNote(Class<? extends Contact> type)
	{
		testName = new String("getNote(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = noteTitles;
		Note[] providedNotes = notes;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			/*
			 * Getting Notes on empty contact should only return
			 * null Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getNote(providedTitles[j]),
				           testName + " unexpected non null Note");
			}

			/*
			 * Adding Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addNote(providedTitles[j], providedNotes[j]);
			}

			/*
			 * Getting Notes on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNotNull(testContact.getNote(providedTitles[j]),
				              testName + " unexpected null Note");
				assertSame(providedNotes[j],
				           testContact.getNote(providedTitles[j]),
				           testName + " unexpected Note");
			}

			/*
			 * Removing Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeNote(providedTitles[j]);
			}

			/*
			 * Getting Notes on empty contact should only return
			 * null Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertNull(testContact.getNote(providedTitles[j]),
				           testName + " unexpected non null Note");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#getNotesKeySet()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getNotesKeySet()")
	@Order(33)
	final void testGetNotesKeySet(Class<? extends Contact> type)
	{
		testName = new String("getNotesKeySet()");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = noteTitles;
		Note[] providedNotes = notes;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			Set<String> keySet = testContact.getNotesKeySet();
			assertNotNull(keySet,
			              testName + " unexpected null key set");
			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty key set");

			/*
			 * Adding Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addNote(providedTitles[j], providedNotes[j]);
			}

			assertEquals(providedTitles.length,
			             keySet.size(),
			             testName + " unexpected key set size after adding Notes");

			/*
			 * Getting Notes on non empty contact
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				assertTrue(keySet.contains(providedTitles[j]),
				           testName + " key " + providedTitles[j]
				        	   + " could not be found in key set");
			}
			for (String key : keySet)
			{
				for (int k = 0; k < providedTitles.length; k++)
				{
					if (key.equals(providedTitles[k]))
					{
						assertSame(testContact.getNote(key),
						           providedNotes[k],
						           testName + " unexpected Note");
					}
				}
			}

			/*
			 * Removing Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeNote(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#addNote(java.lang.String, model.Note)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("addNote(java.lang.String, model.Note)")
	@Order(34)
	final void testAddNote(Class<? extends Contact> type)
	{
		testName = new String("addNote(java.lang.String, model.Note)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = noteTitles;
		Note[] providedNotes = notes;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getNotesKeySet();

			/*
			 * Adding Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean added = testContact.addNote(providedTitles[j], providedNotes[j]);
				assertTrue(added,
				           testName + " unexpected not added Note");
				Note element = testContact.getNote(providedTitles[j]);
				assertNotNull(element,
				              testName + " unexpected null Note");
				assertSame(providedNotes[j],
				           element,
				           testName + " unexpected Note");
				assertEquals(j+1,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Adding element twice should fail
				 */
				added = testContact.addNote(providedTitles[j], providedNotes[j]);
				assertFalse(added,
				            testName + " unexpectedly added element twice");
			}

			/*
			 * Removing Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.removeNote(providedTitles[j]);
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#removeNote(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("removeNote(java.lang.String)")
	@Order(35)
	final void testRemoveNote(Class<? extends Contact> type)
	{
		testName = new String("removeNote(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] providedTitles = noteTitles;
		Note[] providedNotes = notes;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Set<String> keySet = testContact.getNotesKeySet();

			/*
			 * Adding Notes
			 */
			for (int j = 0; j < providedTitles.length; j++)
			{
				testContact.addNote(providedTitles[j], providedNotes[j]);
			}

			/*
			 * Removing Notes
			 */
			int expectedSize = keySet.size();
			for (int j = 0; j < providedTitles.length; j++)
			{
				boolean removed = testContact.removeNote(providedTitles[j]);
				assertTrue(removed,
				           testName + " unexpected not removed Note");
				assertEquals(--expectedSize,
				             keySet.size(),
				             testName + "unexpected key set size");
				/*
				 * Removing twice should fail
				 */
				removed = testContact.removeNote(providedTitles[j]);
				assertFalse(removed,
				            testName + " unexpectedly removed element twice");
			}

			assertTrue(keySet.isEmpty(),
			           testName + " unexpected non empty keyset");
		}
	}

	/**
	 * Test method for {@link model.Contact#getType()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("getType()")
	@Order(36)
	final void testGetType(Class<? extends Contact> type)
	{
		testName = new String("getType()");
		System.out.println(testName);
		String[] providedNames = null;
		Contact.Type expectedType = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			expectedType = Type.PERSONNAL;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			expectedType = Type.CORPORATE;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			assertEquals(expectedType,
			             testContact.getType(),
			             testName + " unexpected type");
		}
	}

	/**
	 * Test method for {@link model.Contact#compareTo(model.Contact)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("compareTo(model.Contact)")
	@Order(37)
	final void testCompareTo(Class<? extends Contact> type)
	{
		testName = new String("compareTo(model.Contact)");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Contact sameContact = buildContact(type, i, false, testName);

			assertEquals(0,
			             testContact.compareTo(sameContact),
			             testName + " unexpected comparison value");
			for (int j = 0; j < lastNames.length; j++)
			{
				PersonalContact pc = (PersonalContact) buildContact(PersonalContact.class, j, false, testName);
				int providedCompare = testContact.compareTo(pc);
				int nameCompare = testContact.getName().compareTo(pc.getName());
				if (nameCompare != 0)
				{
					assertEquals(nameCompare,
					             providedCompare,
					             testName + " unexpected comparison value");
				}
				else
				{
					if (testContact.getClass() == pc.getClass())
					{
						// testContact is PersonalContact ==> also compare firstName
						PersonalContact testPersonalContact = (PersonalContact) testContact;
						int firstNameCompare = testPersonalContact.getFirstName().compareTo(pc.getFirstName());
						assertEquals(firstNameCompare,
						             providedCompare,
						             testName +  " unexpected comparison value");
					}
					else
					{
						// testCompare is CorporateContact
						assertEquals(0,
						             providedCompare,
						             testName + " unexpected comparison value");
					}
				}
			}

			for (int j = 0; j < lastNames.length; j++)
			{
				CorporateContact cc = (CorporateContact) buildContact(CorporateContact.class, j, false, testName);
				int providedCompare = testContact.compareTo(cc);
				int nameCompare = testContact.getName().compareTo(cc.getName());
				assertEquals(nameCompare,
				             providedCompare,
				             testName + " unexpected comparison value");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#hashCode()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("hashCode()")
	@Order(38)
	final void testHashCode(Class<? extends Contact> type)
	{
		testName = new String("hashCode()");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);

			final int prime = 31;
			int expectedHash = 1;
			expectedHash = (prime * expectedHash) + testContact.getName().hashCode();
			if (type == PersonalContact.class)
			{
				PersonalContact testPersonalContact = (PersonalContact) testContact;
				expectedHash = (prime * expectedHash) + testPersonalContact.getFirstName().hashCode();
			}

			assertEquals(expectedHash,
			             testContact.hashCode(),
			             testName + " unexpected hash value");
		}
	}

	/**
	 * Test method for {@link model.Contact#equals(java.lang.Object)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("equals(java.lang.Object)")
	@Order(39)
	final void testEqualsObject(Class<? extends Contact> type)
	{
		testName = new String("equals(java.lang.Object)");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			Contact sameContact = buildContact(type, i, false, testName);

			assertEquals(testContact,
			             sameContact,
			             testName + " unexpected inequality");

			for (int j = 0; j < lastNames.length; j++)
			{
				PersonalContact pc = (PersonalContact) buildContact(PersonalContact.class, j, false, testName);
				if ((type == PersonalContact.class) && (i == j))
				{
					assertTrue(testContact.equals(pc),
					           testName + " unexpected inequality");
				}
				else // type == CorporateContact.class
				{
					boolean expectedEquality = testContact.getName().equals(pc.getName());
					assertEquals(expectedEquality,
					             testContact.equals(pc),
					             testName + " unexpected equality value");
				}
			}

			for (int j = 0; j < lastNames.length; j++)
			{
				CorporateContact cc = (CorporateContact) buildContact(CorporateContact.class, j, false, testName);
				boolean expectedEquality = testContact.getName().equals(cc.getName());
				assertEquals(expectedEquality,
				             testContact.equals(cc),
				             testName + " unexpected equality value");
			}
		}
	}

	/**
	 * Test method for {@link model.Contact#toString()}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("toString()")
	@Order(40)
	final void testToString(Class<? extends Contact> type)
	{
		testName = new String("toString()");
		System.out.println(testName);
		String[] providedNames = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, false, testName);
			StringBuilder sb = new StringBuilder();
			if (type == PersonalContact.class)
			{
				sb.append(((PersonalContact)testContact).getFirstName());
				sb.append(' ');
			}
			sb.append(testContact.getName());
			assertEquals(sb.toString(),
			             testContact.toString(),
			             testName + " unexpected toString value");
		}
	}

	/**
	 * Test method for {@link model.Contact#contains(java.lang.String)}.
	 * @param type The type of contact to instanciate
	 */
	@ParameterizedTest
	@MethodSource("contactClassesProvider")
	@DisplayName("contains(java.lang.String)")
	@Order(41)
	final void testContains(Class<? extends Contact> type)
	{
		testName = new String("contains(java.lang.String)");
		System.out.println(testName);
		String[] providedNames = null;
		String[] phoneTitles = null;
		PhoneNumber[] phoneNumbers = null;
		String[] addressTitles = null;
		Address[] addresses = null;
		String[] emailTitles = null;
		URI[] emails = null;

		if (type == PersonalContact.class)
		{
			providedNames = lastNames;
			phoneTitles = personalPhoneTitles;
			phoneNumbers = personalPhoneNumbers;
			addressTitles = personalAddressTitles;
			addresses = personalAddresses;
			emailTitles = personalEmailTitles;
			emails = personalEmailURIs;
		}
		else if (type == CorporateContact.class)
		{
			providedNames = names;
			phoneTitles = corporatePhoneTitles;
			phoneNumbers = corporatePhoneNumbers;
			addressTitles = corporateAddressTitles;
			addresses = corporateAddresses;
			emailTitles = corporateEmailTitles;
			emails = corporateEmailURIs;
		}
		else
		{
			fail(testName + " unexpected Contact type " + type.getSimpleName());
		}

		for (int i = 0; i < providedNames.length; i++)
		{
			testContact = buildContact(type, i, true, testName);

			/*
			 * Add all possible PhonNumbers
			 */
			for (int j = 0; j < phoneTitles.length; j++)
			{
				testContact.addPhoneNumber(phoneTitles[j], phoneNumbers[j]);
			}

			/*
			 * Add all possible Addresses
			 */
			for (int j = 0; j < addressTitles.length; j++)
			{
				testContact.addAddress(addressTitles[j], addresses[j]);
			}

			/*
			 * Add all possible Emails
			 */
			for (int j = 0; j < emailTitles.length; j++)
			{
				testContact.addEmail(emailTitles[j], emails[j]);
			}

			/*
			 * Add all possible Links
			 */
			for (int j = 0; j < linkTitles.length; j++)
			{
				testContact.addLink(linkTitles[j], linkURIs[j]);
			}

			/*
			 * Add all possible Notes
			 */
			for (int j = 0; j < noteTitles.length; j++)
			{
				testContact.addNote(noteTitles[j], notes[j]);
			}

			if (type == PersonalContact.class)
			{
				/*
				 * Add corporation
				 */
				CorporateContact corporation = (CorporateContact)buildContact(CorporateContact.class, 0, false, testName);
				PersonalContact personalTestContact = (PersonalContact) testContact;
				personalTestContact.setCorporation(corporation);
			}

			if (type == CorporateContact.class)
			{
				/*
				 * Add employees
				 */
				for (int j = 0; j < lastNames.length; j++)
				{
					PersonalContact employee = (PersonalContact)buildContact(PersonalContact.class, j, false, testName);
					CorporateContact corporateTestContact = (CorporateContact) testContact;
					corporateTestContact.add(employee);
				}
			}

			Set<String> contained = buildContactWordSet(testContact);
			for (String word : contained)
			{
				assertTrue(testContact.contains(word),
				           testName + " unexpected uncontained word " + word);
			}
		}
	}

	/**
	 * Internal condition used to wait and notify on either
	 * <ul>
	 * <li>{@link #frame} to be built on SWING thread</li>
	 * <li>{@link #panel} to be built on JavaFX thread</li>
	 * <li>Or any JavaFX operation that need to be performed on the JavaFX
	 * Thread</li>
	 * </ul>
	 * @author davidroussel
	 */
	private static class Condition
	{
		/**
		 * condition value
		 */
		private boolean value;

		/**
		 * Valued constructor
		 * @param value the initial value of the condition
		 */
		public Condition(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value setter
		 * @param value the value to set
		 */
		synchronized public void setValue(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value getter
		 * @return the current value
		 */
		public boolean getValue()
		{
			return value;
		}
	}
}
