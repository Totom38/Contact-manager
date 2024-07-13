package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import model.Address;

/**
 * Test class for {@link Address}
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Address")
class AddressTest
{
	/**
	 * The tested address
	 */
	private Address testAddress = null;
//
//	/**
//	 * The index in the following arrays chosen to construct an address
//	 * @see #testCities
//	 * @see #testZipCodes
//	 * @see #testLocales
//	 * @see #testWays
//	 * @see #testNumbers
//	 */
//	private int index;

	/**
	 * Test cities names
	 */
	private static final String[] testCities = new String[] {
		"Evry",				// 91000
		"Courcouronnes",	// 91100
		"Massy",			// 91300
		"Grigny",			// 91350
		"Orléans",			// 45000
		"Orléans",			// 45100
		"Hull"				// HU6 7RX
	};

	/**
	 * First city in alphabetical order
	 */
	private static final String FirstCity = "Artenay";
	/**
	 * Last city in alphabetical order
	 */
	private static final String LastCity = "Zimmerbach";

	/**
	 * Test cities zipcodes
	 */
	private static final String[] testZipCodes = new String[] {
		"91000",	// Evry
		"91100",	// Courcouronnes
		"91300",	// Massy
		"91350",	// Grigny
		"45000",	// Orléans
		"45100",	// Orléans
		"HU6 7RX"	// Hull
	};

	/**
	 * First Zipcode in alphabetical order
	 */
	private final static String FirstZipCode = "01012"; // Bourg en Bresse
	/**
	 * Last Zipcode in alphabetical order
	 */
	private final static String LastZipCode = "HU7 0AB"; // Still in Hull

	/**
	 * Locales for test cities
	 */
	private final static Locale[] testLocales = new Locale[]{
		Locale.FRANCE,
		Locale.FRANCE,
		Locale.FRANCE,
		Locale.FRANCE,
		Locale.FRANCE,
		Locale.FRANCE,
		Locale.UK
	};
	/**
	 * First Locale in alphabetical order (through toString())
	 */
	private final static Locale FirstLocale = Locale.GERMANY;
	/**
	 * Last Locale in alphabetical order (through toString())
	 */
	private final static Locale LastLocale = Locale.KOREA;

	/**
	 * Test ways
	 */
	private static final String[] testWays = new String[] {
		"Rue Louis Pasteur",
		"Place Charles de Gaulle",
		"Boulevard Victor Hugo",
		"Avenue du Général Leclerc",
		"Rue Jean Jaurès",
		"Cours Léon Gambetta",
		"Cottingham Road"
	};

	/**
	 * First way in alphabetical order
	 */
	private final static String FirstWay = "Aire de repos";
	/**
	 * Last way in alphabetical order
	 */
	private final static String LastWay = "Ruelle des Saules";

	/**
	 * Test numbers in ways
	 * Negative numbers mean no number
	 */
	private static final int[] testNumbers = new int[] {
		13,
		-1,
		17,
		19,
		23,
		29,
		-1
	};

	/**
	 * First number
	 */
	private final static int FirstNumber = 1;
	/**
	 * Last number
	 */
	private final static int LastNumber = 99;

	/**
	 * Number of test addresses to build with arrays above
	 */
	private final static int size = testCities.length;

	/**
	 * Random number generator
	 */
	private final static Random rand = new Random();

	/**
	 * Get ith address
	 * @param testName the name of the test to use this method
	 * @param index the index in {@link #testCities} to use
	 * @return a new Address
	 * @throws IndexOutOfBoundsException if index is invalid
	 */
	private static Address constructAddress(String testName, int index)
	    throws IndexOutOfBoundsException
	{
		if ((index < 0) || (index >= size))
		{
			throw new IndexOutOfBoundsException("Invalid index");
		}
		Address result = null;
		Locale testLocale = testLocales[index];
		String testZipCode = testZipCodes[index];
		String city = testCities[index];
		String way = testWays[index];
		int number = testNumbers[index];
		try
		{
			if (number > 0) // use this number
			{
				result = new Address(number, way, city, testZipCode, testLocale);
			}
			else // don't use number
			{
				result = new Address(way, city, testZipCode, testLocale);
			}
		}
		catch (Exception e)
		{
			fail(testName + " unexpected exception " + e.getLocalizedMessage());
		}
		return result;
	}

//	/**
//	 * Get randomly picked address
//	 * @param testName the name of the test to use this method
//	 * @return a new Address
//	 */
//	private static Address shuffledAddress(String testName)
//	{
//		return constructAddress(testName, rand.nextInt(size));
//	}

	/**
	 * Build a set of words we should find in ith address
	 * @param index the index of the address
	 * @return a set of words we should find in ith address
	 */
	private static Set<String> buildWordSet(int index)
	{
		Set<String> containedWords = new TreeSet<>();
		String city = testCities[index];
		String[] words = city.split(" ");
		for (String word : words)
		{
			containedWords.add(word);
		}
		String zipCode = testZipCodes[index];
		words = zipCode.split(" ");
		for (String word : words)
		{
			containedWords.add(word);
		}
		Locale locale = testLocales[index];
		words = locale.getDisplayCountry(locale).split(" ");
		for (String word : words)
		{
			containedWords.add(word);
		}
		String way = testWays[index];
		words = way.split(" ");
		for (String word : words)
		{
			containedWords.add(word);
		}
		int number = testNumbers[index];
		if (number != -1)
		{
			containedWords.add(String.valueOf(number));
		}
		return containedWords;
	}

	/**
	 * Build a set of words we should NOT find in ith address
	 * @param index the index of the address
	 * @return a set of words we should NOT find in ith address
	 * @see #buildWordSet(int)
	 */
	private static Set<String> buildForeignWordSet(int index)
	{
		Set<String> uncontainedWords = new TreeSet<>();
		Set<String> containedWords = buildWordSet(index);

		for (int i = 0; i < size; i++)
		{
			if (i != index)
			{
				Set<String> currentWords = buildWordSet(i);
				for (String word : currentWords)
				{
					boolean uncontained = !containedWords.contains(word);
					if (uncontained)
					{
						for (String testWord : containedWords)
						{
							uncontained &= !testWord.contains(word);
							if (!uncontained)
							{
								break;
							}
						}
						if (uncontained)
						{
							uncontainedWords.add(word);
						}
					}
				}
			}
		}
		return uncontainedWords;
	}

	/**
	 * Setup before all tests
	 * @throws Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		/*
		 * assert FirstXXX and LastXXX values
		 */
		for (int i = 0; i < testCities.length; i++)
		{
			assertTrue(FirstCity.compareTo(testCities[i]) < 0);
			assertTrue(LastCity.compareTo(testCities[i]) > 0);
		}
		for (int i = 0; i < testZipCodes.length; i++)
		{
			assertTrue(FirstZipCode.compareTo(testZipCodes[i]) < 0);
			assertTrue(LastZipCode.compareTo(testZipCodes[i]) > 0);
		}
		for (int i = 0; i < testLocales.length; i++)
		{
			assertTrue(FirstLocale.toString().compareTo(testLocales[i].toString()) < 0);
			assertTrue(LastLocale.toString().compareTo(testLocales[i].toString()) > 0);
		}
		for (int i = 0; i < testWays.length; i++)
		{
			assertTrue(FirstWay.compareTo(testWays[i]) < 0);
			assertTrue(LastWay.compareTo(testWays[i]) > 0);
		}
		for (int i = 0; i < testNumbers.length; i++)
		{
			if (testNumbers[i] > 0)
			{
				assertTrue(FirstNumber < testNumbers[i]);
				assertTrue(LastNumber > testNumbers[i]);
			}
		}
		System.out.println("-------------------------------------------------");
		System.out.println("Address tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 * @throws Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("Address tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		// Generate a random address
//		index = rand.nextInt(size);
	}

	/**
	 * Teardown after each test
	 * @throws Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testAddress = null;
	}

	/**
	 * Test method for {@link model.Address#Address(int, String, String, String, Locale)}.
	 */
	@Test
	@DisplayName("Address(int, String, String, String, Locale)")
	@Order(1)
	final void testAddressIntStringStringStringLocale()
	{
		String testName = new String("Address(int, String, String, String, Locale)");
		System.out.println(testName);
		int index = 0; // because testNumbers[0] has a valid number
		int invalidNumber = -rand.nextInt(testCities.length);
		String empty = new String();

		/*
		 * Illegal number (<= 0)
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(invalidNumber,
			                        testWays[index],
			                        testCities[index],
			                        testZipCodes[index],
			                        testLocales[index]);
		});

		/*
		 * null arguments
		 */
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        null,
			                        testCities[index],
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        null,
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        testCities[index],
			                        null,
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        testCities[index],
			                        testZipCodes[index],
			                        null);
		});

		/*
		 * empty arguments
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        empty,
			                        testCities[index],
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        empty,
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        testCities[index],
			                        empty,
			                        testLocales[index]);
		});

		try
		{
			testAddress = new Address(testNumbers[index],
			                          testWays[index],
			                          testCities[index],
			                          testZipCodes[index],
			                          testLocales[index]);
			assertNotNull(testAddress, testName + " unexpected null instance");
		}
		catch (Exception e)
		{
			fail(testName + " unexpected exception " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link model.Address#Address(String, String, String, Locale)}.
	 */
	@Test
	@DisplayName("Address(String, String, String, Locale)")
	@Order(2)
	final void testAddressStringStringStringLocale()
	{
		String testName = new String("Address(String, String, String, Locale)");
		System.out.println(testName);
		int index = 1; // because testNumbers has a no valid number
		String empty = new String();

		/*
		 * null arguments
		 */
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(null,
			                        testCities[index],
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        null,
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        testCities[index],
			                        null,
			                        testLocales[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        testCities[index],
			                        testZipCodes[index],
			                        null);
		});

		/*
		 * empty arguments
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(empty,
			                        testCities[index],
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        empty,
			                        testZipCodes[index],
			                        testLocales[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        testCities[index],
			                        empty,
			                        testLocales[index]);
		});

		try
		{
			testAddress = new Address(testWays[index],
			                          testCities[index],
			                          testZipCodes[index],
			                          testLocales[index]);
			assertNotNull(testAddress, testName + " unexpected null instance");
		}
		catch (Exception e)
		{
			fail(testName + " unexpected exception " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link model.Address#Address(int, String, String, String)}.
	 */
	@Test
	@DisplayName("Address(int, String, String, String)")
	@Order(3)
	final void testAddressIntStringStringString()
	{
		String testName = new String("Address(int, String, String, String)");
		System.out.println(testName);
		int index = 0; // because testNumbers[0] has a valid number
		int invalidNumber = -rand.nextInt(testCities.length);
		String empty = new String();

		/*
		 * Illegal number (<= 0)
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(invalidNumber,
			                        testWays[index],
			                        testCities[index],
			                        testZipCodes[index]);
		});

		/*
		 * null arguments
		 */
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        null,
			                        testCities[index],
			                        testZipCodes[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        null,
			                        testZipCodes[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        testCities[index],
			                        null);
		});

		/*
		 * empty arguments
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        empty,
			                        testCities[index],
			                        testZipCodes[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        empty,
			                        testZipCodes[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testNumbers[index],
			                        testWays[index],
			                        testCities[index],
			                        empty);
		});

		try
		{
			testAddress = new Address(testNumbers[index],
			                          testWays[index],
			                          testCities[index],
			                          testZipCodes[index]);
			assertNotNull(testAddress, testName + " unexpected null instance");
		}
		catch (Exception e)
		{
			fail(testName + " unexpected exception " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link model.Address#Address(String, String, String)}.
	 */
	@Test
	@DisplayName("Address(String, String, String)")
	@Order(4)
	final void testAddressStringStringString()
	{
		String testName = new String("Address(String, String, String)");
		System.out.println(testName);
		int index = 1; // because testNumbers has a no valid number
		String empty = new String();

		/*
		 * null arguments
		 */
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(null,
			                        testCities[index],
			                        testZipCodes[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        null,
			                        testZipCodes[index]);
		});
		assertThrows(NullPointerException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        testCities[index],
			                        null);
		});

		/*
		 * empty arguments
		 */
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(empty,
			                        testCities[index],
			                        testZipCodes[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        empty,
			                        testZipCodes[index]);
		});
		assertThrows(IllegalArgumentException.class, () ->{
			@SuppressWarnings("unused")
			Address a = new Address(testWays[index],
			                        testCities[index],
			                        empty);
		});

		try
		{
			testAddress = new Address(testWays[index],
			                          testCities[index],
			                          testZipCodes[index]);
			assertNotNull(testAddress, testName + " unexpected null instance");
		}
		catch (Exception e)
		{
			fail(testName + " unexpected exception " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link model.Address#getNumber()}.
	 */
	@Test
	@DisplayName("getNumber()")
	@Order(5)
	final void testGetNumber()
	{
		String testName = new String("getNumber()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			Optional<Integer> number = testAddress.getNumber();
			if (testNumbers[i] > 0)
			{
				assertTrue(number.isPresent(),
				           testName + " unexpected non present number");
				assertTrue(number.get() > 0,
				           testName + " unexpected null or negative number");
				assertEquals(testNumbers[i],
				             number.get(),
				             testName + " unexpected number value");
			}
			else
			{
				assertTrue(number.isEmpty(),
				           testName + " unexpected non empty number");
			}
		}
	}

	/**
	 * Test method for {@link model.Address#getWay()}.
	 */
	@Test
	@DisplayName("getWay()")
	@Order(6)
	final void testGetWay()
	{
		String testName = new String("getWay()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			assertTrue(!testAddress.getWay().isEmpty(),
			           testName +  " unexpected empty way");
			assertEquals(testWays[i],
			             testAddress.getWay(),
			             testName + " unexpected way");
		}
	}

	/**
	 * Test method for {@link model.Address#getCity()}.
	 */
	@Test
	@DisplayName("getCity()")
	@Order(7)
	final void testGetCity()
	{
		String testName = new String("getCity()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			assertTrue(!testAddress.getCity().isEmpty(),
			           testName +  " unexpected empty city");
			assertEquals(testCities[i],
			             testAddress.getCity(),
			             testName + " unexpected city");
		}
	}

	/**
	 * Test method for {@link model.Address#getZipCode()}.
	 */
	@Test
	@DisplayName("getZipCode()")
	@Order(8)
	final void testGetZipCode()
	{
		String testName = new String("getZipCode()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			assertTrue(!testAddress.getZipCode().isEmpty(),
			           testName +  " unexpected empty zip code");
			assertEquals(testZipCodes[i],
			             testAddress.getZipCode(),
			             testName + " unexpected zip code");
		}
	}

	/**
	 * Test method for {@link model.Address#getLocale()}.
	 */
	@Test
	@DisplayName("getLocale()")
	@Order(9)
	final void testGetLocale()
	{
		String testName = new String("getLocale()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			assertEquals(testLocales[i],
			             testAddress.getLocale(),
			             testName + " unexpected locale");
		}
	}

	/**
	 * Test method for {@link model.Address#setNumber(int)}.
	 */
	@Test
	@DisplayName("setNumber(int)")
	@Order(10)
	final void testSetNumber()
	{
		String testName = new String("setNumber(int)");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			Optional<Integer> initialNumber = testAddress.getNumber();
			boolean initialPresent = initialNumber.isPresent();
			int expectedNumber = 1;
			if (initialPresent)
			{
				expectedNumber += initialNumber.get();
			}

			/*
			 * Set Invalid number (<= 0) : no value should be set
			 */
			final int[] invalidValues = new int [] {0, -1};
			for (int j = 0; j < invalidValues[j]; j++)
			{
				testAddress.setNumber(invalidValues[j]);

				assertEquals(initialPresent,
				             testAddress.getNumber().isPresent(),
				             testName + " unexpected value presence after "
				             	+ "setting invalid value");
				assertEquals(initialNumber,
				             testAddress.getNumber(),
				             testName + " unexpected value after setting "
				             	+ "invalid  value");
			}

			/*
			 * Set Valid Number
			 */
			testAddress.setNumber(expectedNumber);
			Optional<Integer> providedNumber = testAddress.getNumber();
			assertTrue(providedNumber.isPresent());
			assertEquals(expectedNumber,
			             providedNumber.get(),
			             testName + " unexpected value after setting number");
		}
	}

	/**
	 * Test method for {@link model.Address#setWay(String)}.
	 */
	@Test
	@DisplayName("setWay(String)")
	@Order(11)
	final void testSetWay()
	{
		String testName = new String("setWay(String)");
		System.out.println(testName);
		final String empty = new String();
		final String expectedWay = LastWay;
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			String intitialWay = testAddress.getWay();

			/*
			 * Set Invalid way (null or empty) : no value should be set
			 */
			testAddress.setWay(null);
			assertEquals(intitialWay,
			             testAddress.getWay(),
			             testName + " unexpected value after setting null "
			             	+ "way");

			testAddress.setWay(empty);
			assertEquals(intitialWay,
			             testAddress.getWay(),
			             testName + " unexpected value after setting empty "
			             	+ "way");

			/*
			 * Set Valid way
			 */
			testAddress.setWay(expectedWay);
			assertEquals(expectedWay,
			             testAddress.getWay(),
			             testName + " unexpected value after setting way");
		}
	}

	/**
	 * Test method for {@link model.Address#setCity(String)}.
	 */
	@Test
	@DisplayName("setCity(String)")
	@Order(12)
	final void testSetCity()
	{
		String testName = new String("setCity(String)");
		System.out.println(testName);
		final String empty = new String();
		final String expectedCity = LastCity;
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			String intitialCity = testAddress.getCity();

			/*
			 * Set Invalid city (null or empty) : no value should be set
			 */
			testAddress.setCity(null);

			assertEquals(intitialCity,
			             testAddress.getCity(),
			             testName + " unexpected value after setting null "
			             	+ "city");

			testAddress.setCity(empty);
			assertEquals(intitialCity,
			             testAddress.getCity(),
			             testName + " unexpected value after setting empty "
			             	+ "city");

			/*
			 * Set Valid city
			 */
			testAddress.setCity(expectedCity);
			assertEquals(expectedCity,
			             testAddress.getCity(),
			             testName + " unexpected value after setting city");
		}
	}

	/**
	 * Test method for {@link model.Address#setZipCode(String)}.
	 */
	@Test
	@DisplayName("setZipCode(String)")
	@Order(13)
	final void testSetZipCode()
	{
		String testName = new String("setZipCode(String)");
		System.out.println(testName);
		final String empty = new String();
		final String expectedZipCode = LastZipCode;
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			String intitialZipCode = testAddress.getZipCode();

			/*
			 * Set Invalid zipcpde (null or empty) : no value should be set
			 */
			testAddress.setZipCode(null);

			assertEquals(intitialZipCode,
			             testAddress.getZipCode(),
			             testName + " unexpected value after setting null "
			             	+ "zipcode");

			testAddress.setZipCode(empty);
			assertEquals(intitialZipCode,
			             testAddress.getZipCode(),
			             testName + " unexpected value after setting empty "
			             	+ "zipcode");

			/*
			 * Set Valid zipcode
			 */
			testAddress.setZipCode(expectedZipCode);
			assertEquals(expectedZipCode,
			             testAddress.getZipCode(),
			             testName + " unexpected value after setting zipcode");
		}
	}

	/**
	 * Test method for {@link model.Address#setLocale(Locale)}.
	 */
	@Test
	@DisplayName("setLocale(Locale)")
	@Order(14)
	final void testSetLocale()
	{
		String testName = new String("setLocale(Locale)");
		System.out.println(testName);
		final Locale expectedLocale = LastLocale;
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");
			Locale intitialLocale = testAddress.getLocale();

			/*
			 * Set Invalid zipcpde (null or empty) : no value should be set
			 */
			testAddress.setLocale(null);

			assertEquals(intitialLocale,
			             testAddress.getLocale(),
			             testName + " unexpected value after setting null "
			             	+ "locale");

			/*
			 * Set Valid zipcode
			 */
			testAddress.setLocale(expectedLocale);
			assertEquals(expectedLocale,
			             testAddress.getLocale(),
			             testName + " unexpected value after setting locale");
		}
	}

	/**
	 * Test method for {@link model.Address#hashCode()}.
	 */
	@Test
	@DisplayName("hashCode()")
	@Order(15)
	final void testHashCode()
	{
		String testName = new String("hashCode()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);
			assertNotNull(testAddress, testName +  " unexpected null instance");

			/*
			 * Compute expected hash
			 */
			final int prime = 31;
			int expectedHash = 1;
			Locale locale = testAddress.getLocale();
			expectedHash = (prime * expectedHash) + (locale == null ? 0 : locale.hashCode());
			String zipCode = testAddress.getZipCode();
			expectedHash = (prime * expectedHash) + (zipCode == null ? 0 : zipCode.hashCode());
			String city = testAddress.getCity();
			expectedHash = (prime * expectedHash) + (city == null ? 0 : city.hashCode());
			String way = testAddress.getWay();
			expectedHash = (prime * expectedHash) + (way == null ? 0 : way.hashCode());
			Optional<Integer> number = testAddress.getNumber();
			expectedHash = (prime * expectedHash) + (number.isEmpty() ? 0 : number.get());

			/*
			 * assert expected hash
			 */
			assertEquals(expectedHash,
			             testAddress.hashCode(),
			             testName + " unexpected hash value");
		}
	}

	/**
	 * Test method for {@link model.Address#equals(Object)}.
	 */
	@Test
	@DisplayName("equals(Object)")
	@Order(16)
	final void testEqualsObject()
	{
		String testName = new String("equals(Object)");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);

			/*
			 * testAddress is alway different from null
			 */
			assertFalse(testAddress.equals(null),
			            testName + " unexpected equality with null");

			/*
			 * testAddress is alway equal to self
			 */
			long selfTestStart = System.nanoTime();
			boolean equalSelf = testAddress.equals(testAddress);
			long selfTestDuration = System.nanoTime() - selfTestStart;
			assertTrue(equalSelf,
			           testName + " unexpected inequality with self");

			/*
			 * testAddress is equal to clone (but slower)
			 */
			Address cloneAddress = constructAddress(testName, i);
			long cloneTestStart = System.nanoTime();
			boolean equalClone = testAddress.equals(cloneAddress);
			long cloneTestDuration = System.nanoTime() - cloneTestStart;
			assertTrue(equalClone,
			           testName + " unexpected inequality with clone");

			/*
			 * CAUTION : During initial run, this assertion might fail.
			 * However, repeating the test several times should not fail anymore
			 */
			assertTrue(selfTestDuration < cloneTestDuration,
			           testName + "Testing self equality was as long as testing "
			           	+ "clone equality: use if (obj == this) clause in "
			           	+ "equals method");

			for (int j = 0; j < size; j++)
			{
				Address otherAddress = constructAddress(testName, j);
				if (i == j)
				{
					assertTrue(testAddress.equals(otherAddress),
					           testName + " unexpected inequality with clone");
				}
				else
				{
					assertFalse(testAddress.equals(otherAddress),
					            testName + " unexpected equality with other");
				}
			}
		}
	}

	/**
	 * Test method for {@link model.Address#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(17)
	final void testToString()
	{
		String testName = new String("toString()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);

			/*
			 * Compute expected String :
			 * [<number>] <way>\n
			 * <zipcode> <city>
			 * [\n<Country (iff not France)>]
			 */
			Optional<Integer> optional = testAddress.getNumber();
			boolean hasNumber = optional.isPresent();
			final String number = hasNumber ? optional.get().toString() : "";
			final String way = testAddress.getWay();
			final String zipCode = testAddress.getZipCode();
			final String city = testAddress.getCity();
			Locale locale = testAddress.getLocale();
			boolean isLocal = locale.equals(Locale.FRANCE);
			final String country = isLocal ? "" : locale.getDisplayCountry(locale);

			/*
			 * Assert expected String
			 */
			String providedString = testAddress.toString();
			String[] lines = providedString.split("\n");
			final int expectedLines = isLocal ? 2 : 3;
			assertEquals(expectedLines,
			             lines.length,
			             testName + " unexpected number of lines in toString()");

			/*
			 * Test 1st line :  [<number>] <way>
			 */
			StringBuilder sb = new StringBuilder();
			if (hasNumber)
			{
				sb.append(number);
				sb.append(' ');
			}
			sb.append(way);
			assertEquals(sb.toString(),
			             lines[0],
			             testName  + " unexpected 1st line of toString()");

			/*
			 * Test 2nd line : <zipcode> <city>
			 */
			sb = new StringBuilder();
			sb.append(zipCode);
			sb.append(' ');
			sb.append(city);
			assertEquals(sb.toString(),
			             lines[1],
			             testName  + " unexpected 2nd line of toString()");

			/*
			 * Test 3rd line : <country> from locale
			 */
			if (!isLocal)
			{
				assertEquals(country,
				             lines[2],
				             testName + " unexpected 3rd line of toString()");
			}
		}
	}

	/**
	 * Test method for {@link model.Address#compareTo(model.Address)}.
	 */
	@Test
	@DisplayName("compareTo(model.Address)")
	@Order(18)
	final void testCompareTo()
	{
		String testName = new String("compareTo(model.Address)");
		System.out.println(testName);

		Address firstAddress = new Address(FirstNumber, FirstWay, FirstCity, FirstZipCode, FirstLocale);
		Address lastAddress = new Address(LastNumber, LastWay, LastCity, LastZipCode, LastLocale);

		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);

			assertTrue(testAddress.compareTo(firstAddress) > 0,
			           testName + " unexpected comparison result with first address");
			assertTrue(testAddress.compareTo(lastAddress) < 0,
			           testName + " unexpected comparison result with last address");

			Address otherAddress = constructAddress(testName, i);
			assertEquals(0,
			             testAddress.compareTo(otherAddress),
			             testName + " unexpected comparison result with same "
			             	+ "address");

			/*
			 * Compare by modifying locale
			 */
			Locale locale = testAddress.getLocale();
			testAddress.setLocale(FirstLocale);
			assertTrue(testAddress.compareTo(otherAddress) < 0,
			           testName + " unexpected comparison result with bigger "
			           	+ "address based on Locale");
			testAddress.setLocale(LastLocale);
			assertTrue(testAddress.compareTo(otherAddress) > 0,
			           testName + " unexpected comparison result with smaller "
			           	+ "address based on Locale");
			testAddress.setLocale(locale);

			/*
			 * Compare by modifying ZipCode
			 */
			String zipCode = testAddress.getZipCode();
			testAddress.setZipCode(FirstZipCode);
			assertTrue(testAddress.compareTo(otherAddress) < 0,
			           testName + " unexpected comparison result with bigger "
			           	+ "address based on ZipCode");
			testAddress.setZipCode(LastZipCode);
			assertTrue(testAddress.compareTo(otherAddress) > 0,
			           testName + " unexpected comparison result with smaller "
			           	+ "address based on ZipCode");
			testAddress.setZipCode(zipCode);

			/*
			 * Compare by modifying City
			 */
			String city = testAddress.getCity();
			testAddress.setCity(FirstCity);
			assertTrue(testAddress.compareTo(otherAddress) < 0,
			           testName + " unexpected comparison result with bigger "
			           	+ "address based on City");
			testAddress.setCity(LastCity);
			assertTrue(testAddress.compareTo(otherAddress) > 0,
			           testName + " unexpected comparison result with smaller "
			           	+ "address based on City");
			testAddress.setCity(city);

			/*
			 * Compare by modifying Way
			 */
			String way = testAddress.getWay();
			testAddress.setWay(FirstWay);
			assertTrue(testAddress.compareTo(otherAddress) < 0,
			           testName + " unexpected comparison result with bigger "
			           	+ "address based on Way");
			testAddress.setWay(LastWay);
			assertTrue(testAddress.compareTo(otherAddress) > 0,
			           testName + " unexpected comparison result with smaller "
			           	+ "address based on Way");
			testAddress.setWay(way);

			/*
			 * Compare by modifying number
			 */
			Optional<Integer> number = testAddress.getNumber();
			if (number.isPresent())
			{
				testAddress.setNumber(FirstNumber);
				assertTrue(testAddress.compareTo(otherAddress) < 0,
				           testName + " unexpected comparison result with smaller "
					           	+ "address based on Number");
				testAddress.setNumber(LastNumber);
				assertTrue(testAddress.compareTo(otherAddress) > 0,
				           testName + " unexpected comparison result with smaller "
					           	+ "address based on Number");
			}
			else // number is not present ==> setting one will always be greater
			{
				testAddress.setNumber(FirstNumber);
				assertTrue(testAddress.compareTo(otherAddress) > 0,
				           testName + " unexpected comparison result between "
				           	+ "having number and not having number");
				testAddress.setNumber(LastNumber);
				assertTrue(testAddress.compareTo(otherAddress) > 0,
				           testName + " unexpected comparison result between "
				           	+ "having number and not having number");
			}
		}
	}

	/**
	 * Test method for {@link model.Address#contains(String)}.
	 */
	@Test
	@DisplayName("contains(String)")
	@Order(19)
	final void testContains()
	{
		String testName = new String("contains(String)");
		System.out.println(testName);

		for (int i = 0; i < size; i++)
		{
			testAddress = constructAddress(testName, i);

			/*
			 * Build words to search for
			 */
			Set<String> containedWords = buildWordSet(i);
			// System.out.println("Contained words : " + containedWords);
			Set<String> uncontainedWords = buildForeignWordSet(i);
			// System.out.println("Uncontained words : " + uncontainedWords);

			/*
			 * Assert contained words are found
			 */
			for (String word : containedWords)
			{
				boolean found = testAddress.contains(word);
				assertTrue(found,
				           testName + " unexpected uncontained word " + word
				               + " in " + testAddress);
			}

			/*
			 * Assert uncontained words are NOT found
			 */
			for (String word : uncontainedWords)
			{
				boolean found = testAddress.contains(word);
				assertFalse(found,
				            testName + " unexpected contained word " + word +
					           " in " + testAddress);
			}
		}
	}
}
