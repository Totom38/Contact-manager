package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import model.PhoneNumber;

/**
 * Test Class for {@link PhoneNumber}s
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("PhoneNumber")
class PhoneNumberTest
{
	/**
	 * The number under test
	 */
	private PhoneNumber testNumber = null;

	/**
	 * Test strings for phone number
	 */
	private static final String[] testStrings = new String[] {
		"01 69 36 74 62",		// Valid national number
		"0169 367 462",			// Valid national number
		"0169 367 462 ",		// Valid national number (extra space at the end)
		"+33 169367462",		// valid international number
		"+33169367462",			// Valid international number (missing space after 33)
		"+33 1 69 36 74 62",	// Valid international number
		"+33 169 367 462",		// Valid international number
		"33 1 69367 462",		// Invalid international number (missing +)
		"+33 1 69367 46",		// Invalid international number (missing digit)
		"+33 1 69367 4620",		// Invalid international number (one too many digit)
		"0 269367462",			// Valid regional number
		"03 69367462",			// Valid regional number
		"046 9367462",			// Valid regional number
		"0569 367462",			// Valid regional number
		"06693 67462",			// Valid mobile number
		"076936 7462",			// Valid mobile number
		"+33 769 367462",		// Valid mobile number
		"0869367462",			// Valid commercial number
		"0969367462",			// Valid commercial number
		"0800 500 500",			// Valid commercial number
		"+33 800 500 500",		// Valid commercial number
		"11 69 36 74 61",		// Invalid prefix
		"01 69 36 74 61",		// Valid lower number
		"02 69 36 74 62",		// Valid bigger number
		"01 69 36 74 63",		// Valid bigger number
		"invalid test"			// Invalid anything
	};

	/**
	 * Expected matches for phone number regex
	 */
	private static final boolean[] expectedMatches = new boolean[] {
		true,	//"01 69 36 74 62",		// Valid national number
		true, 	//"0169 367 462",		// Valid national number
		true,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		true, 	//"+33 169367462",		// valid international number
		true, 	//"+33169367462",		// Valid international number (missing space after 33)
		true,	//"+33 1 69 36 74 62",	// Valid international number
		true,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		true,	//"0 269367462",		// Valid regional number
		true,	//"03 69367462",		// Valid regional number
		true,	//"046 9367462",		// Valid regional number
		true,	//"0569 367462",		// Valid regional number
		true,	//"06693 67462",		// Valid mobile number
		true,	//"076936 7462",		// Valid mobile number
		true,	//"+33 769 367462",		// Valid mobile number
		true,	//"0869367462",			// Valid commercial number
		true,	//"0969367462",			// Valid commercial number
		true,	//"0800 500 500",		// Valid commercial number
		true,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		true,	//"01 69 36 74 61",		// Valid lower number
		true,	//"02 69 36 74 62",		// Valid bigger number
		true,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected matches for international numbers
	 */
	private static final boolean[] expectedInternational = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		true, 	//"+33 169367462",		// valid international number
		true, 	//"+33169367462",		// Valid international number (missing space after 33)
		true,	//"+33 1 69 36 74 62",	// Valid international number
		true,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		true,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		false,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		true,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected matches for regional numbers
	 */
	private static final boolean[] expectedRegional = new boolean[] {
		true,	//"01 69 36 74 62",		// Valid national number
		true, 	//"0169 367 462",		// Valid national number
		true,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		true, 	//"+33 169367462",		// valid international number
		true, 	//"+33169367462",		// Valid international number (missing space after 33)
		true,	//"+33 1 69 36 74 62",	// Valid international number
		true,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		true,	//"0 269367462",		// Valid regional number
		true,	//"03 69367462",		// Valid regional number
		true,	//"046 9367462",		// Valid regional number
		true,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		false,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		false,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		false,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		true,	//"01 69 36 74 61",		// Valid lower number
		true,	//"02 69 36 74 62",		// Valid bigger number
		true,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected matches for mobile numbers
	 */
	private static final boolean[] expectedMobile = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		false, 	//"+33 169367462",		// valid international number
		false, 	//"+33169367462",		// Valid international number (missing space after 33)
		false,	//"+33 1 69 36 74 62",	// Valid international number
		false,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		true,	//"06693 67462",		// Valid mobile number
		true,	//"076936 7462",		// Valid mobile number
		true,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		false,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		false,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected matches for commercial numbers
	 */
	private static final boolean[] expectedCommercial = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		false, 	//"+33 169367462",		// valid international number
		false, 	//"+33169367462",		// Valid international number (missing space after 33)
		false,	//"+33 1 69 36 74 62",	// Valid international number
		false,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		false,	//"+33 769 367462",		// Valid mobile number
		true,	//"0869367462",			// Valid commercial number
		true,	//"0969367462",			// Valid commercial number
		true,	//"0800 500 500",		// Valid commercial number
		true,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected matches for digital numbers
	 */
	private static final boolean[] expectedDigital = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		false, 	//"+33 169367462",		// valid international number
		false, 	//"+33169367462",		// Valid international number (missing space after 33)
		false,	//"+33 1 69 36 74 62",	// Valid international number
		false,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		false,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		true,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		false,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Numbers expected to be equal to first number
	 */
	private static final boolean[] expectedEqual = new boolean[] {
		true,	//"01 69 36 74 62",		// Valid national number
		true, 	//"0169 367 462",		// Valid national number
		true,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		true, 	//"+33 169367462",		// valid international number
		true, 	//"+33169367462",		// Valid international number (missing space after 33)
		true,	//"+33 1 69 36 74 62",	// Valid international number
		true,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		false,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		false,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		false,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Numbers expected to be less than first number
	 */
	private static final boolean[] expectedLower = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		false, 	//"+33 169367462",		// valid international number
		false, 	//"+33169367462",		// Valid international number (missing space after 33)
		false,	//"+33 1 69 36 74 62",	// Valid international number
		false,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		false,	//"0 269367462",		// Valid regional number
		false,	//"03 69367462",		// Valid regional number
		false,	//"046 9367462",		// Valid regional number
		false,	//"0569 367462",		// Valid regional number
		false,	//"06693 67462",		// Valid mobile number
		false,	//"076936 7462",		// Valid mobile number
		false,	//"+33 769 367462",		// Valid mobile number
		false,	//"0869367462",			// Valid commercial number
		false,	//"0969367462",			// Valid commercial number
		false,	//"0800 500 500",		// Valid commercial number
		false,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		true,	//"01 69 36 74 61",		// Valid lower number
		false,	//"02 69 36 74 62",		// Valid bigger number
		false,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Numbers expected to be bigger than first number
	 */
	private static final boolean[] expectedBigger = new boolean[] {
		false,	//"01 69 36 74 62",		// Valid national number
		false, 	//"0169 367 462",		// Valid national number
		false,	//"0169 367 462 ",		// Valid national number (extra space at the end)
		false, 	//"+33 169367462",		// valid international number
		false, 	//"+33169367462",		// Valid international number (missing space after 33)
		false,	//"+33 1 69 36 74 62",	// Valid international number
		false,	//"+33 169 367 462",	// Valid international number
		false,	//"33 1 69367 462",		// Invalid international number (missing +)
		false,	//"+33 1 69367 46",		// Invalid international number (missing digit)
		false,	//"+33 1 69367 4620",	// Invalid international number (one too many digit)
		true,	//"0 269367462",		// Valid regional number
		true,	//"03 69367462",		// Valid regional number
		true,	//"046 9367462",		// Valid regional number
		true,	//"0569 367462",		// Valid regional number
		true,	//"06693 67462",		// Valid mobile number
		true,	//"076936 7462",		// Valid mobile number
		true,	//"+33 769 367462",		// Valid mobile number
		true,	//"0869367462",			// Valid commercial number
		true,	//"0969367462",			// Valid commercial number
		true,	//"0800 500 500",		// Valid commercial number
		true,	//"+33 800 500 500",	// Valid commercial number
		false,	//"11 69 36 74 61",		// Invalid prefix
		false,	//"01 69 36 74 61",		// Valid lower number
		true,	//"02 69 36 74 62",		// Valid bigger number
		true,	//"01 69 36 74 63",		// Valid bigger number
		false	//"invalid test"		// Invalid anything
	};

	/**
	 * Expected pattern for toString output.
	 * Either +33 X XX XX XX XX or 0X XX XX XX XX
	 */
	private static final Pattern outputPattern = Pattern.compile("^(\\+33\\s{1}|0)(\\d{1})\\s{1}(\\d{2})\\s{1}(\\d{2})\\s{1}(\\d{2})\\s{1}(\\d{2})$");

	/**
	 * Build a set of words we should find in ith address
	 * @param index the index of the address
	 * @return a set of words we should find in ith address
	 */
	private static Set<String> buildWordSet(int index)
	{
		Set<String> containedWords = new TreeSet<>();
		String string = testStrings[index];
		String[] words = string.split(" ");
		for (String word : words)
		{
			containedWords.add(word);
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

		for (int i = 0; i < testStrings.length; i++)
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
	 * @throws Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("PhoneNumber tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * @throws Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("PhoneNumber tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		// Nothing yet ...
	}

	/**
	 * @throws Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testNumber = null;
	}

	/**
	 * Test method for {@link model.PhoneNumber#parse(String)}.
	 */
	@Test
	@DisplayName("parse(String)")
	@Order(1)
	final void testParse()
	{
		String testName = new String("parse(String)");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				final int fi = i;
				assertThrows(IllegalArgumentException.class, () -> {
					PhoneNumber.parse(testStrings[fi]);
				},testName + " no exception was thrown parsing \""
				                 + testStrings[i] + "\"");
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception parsing \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#PhoneNumber(String)}.
	 */
	@Test
	@DisplayName("PhoneNumber(String)")
	@Order(2)
	final void testPhoneNumberString()
	{
		String testName = new String("PhoneNumber(String)");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				final int fi = i;
				assertThrows(IllegalArgumentException.class, () -> {
					@SuppressWarnings("unused")
					PhoneNumber unused = new PhoneNumber(testStrings[fi]);
				},testName + " no exception was thrown parsing \""
				                 + testStrings[i] + "\"");
			}
			else
			{
				try
				{
					testNumber = new PhoneNumber(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#PhoneNumber(model.PhoneNumber)}.
	 */
	@Test
	@DisplayName("PhoneNumber(model.PhoneNumber)")
	@Order(3)
	final void testPhoneNumberPhoneNumber()
	{
		String testName = new String("PhoneNumber(model.PhoneNumber)");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					PhoneNumber number = new PhoneNumber(testStrings[i]);
					testNumber = new PhoneNumber(number);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					assertNotSame(number,
					              testNumber,
					              testName
					                  + " unexpected same instance after copy");
					assertEquals(number,
					             testNumber,
					             testName
					                 + " unexpected inequality after copy");
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}


	/**
	 * Test method for {@link model.PhoneNumber#parse(String)}.
	 */
	@Test
	@DisplayName("parseFrom(String)")
	@Order(4)
	final void testParseFrom()
	{
		String testName = new String("parseFrom(String)");
		System.out.println(testName);

		testNumber = PhoneNumber.parse(testStrings[0]);

		for (int i = 0; i < testStrings.length; i++)
		{
			try
			{
				testNumber.parseFrom(testStrings[i]);
			}
			catch (IllegalArgumentException e)
			{
				if (expectedMatches[i])
				{
					fail(testName + " unexpected exception parsing \""
					    + testStrings[i] + "\"");
				}
			}
		}
	}


	/**
	 * Test method for {@link model.PhoneNumber#isInternational()}.
	 */
	@Test
	@DisplayName("isInternational()")
	@Order(5)
	final void testIsInternational()
	{
		String testName = new String("isInternational()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean isInternational = testNumber.isInternational();
					assertEquals(expectedInternational[i],
					             isInternational,
					             testName
					                 + " unexpected international status with \""
					                 + testStrings[i] + "\"");

				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#isRegional()}.
	 */
	@Test
	@DisplayName("isRegional()")
	@Order(6)
	final void testIsRegional()
	{
		String testName = new String("isRegional()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean isRegional = testNumber.isRegional();
					assertEquals(expectedRegional[i],
					             isRegional,
					             testName
					                 + " unexpected regional status with \""
					                 + testStrings[i] + "\"");

				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#isMobile()}.
	 */
	@Test
	@DisplayName("isMobile()")
	@Order(7)
	final void testIsMobile()
	{
		String testName = new String("isMobile()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean isMobile = testNumber.isMobile();
					assertEquals(expectedMobile[i],
					             isMobile,
					             testName
					                 + " unexpected mobile status with \""
					                 + testStrings[i] + "\"");

				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#isCommercial()}.
	 */
	@Test
	@DisplayName("isCommercial()")
	@Order(8)
	final void testIsCommercial()
	{
		String testName = new String("isCommercial()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean isCommercial = testNumber.isCommercial();
					assertEquals(expectedCommercial[i],
					             isCommercial,
					             testName
					                 + " unexpected commercial status with \""
					                 + testStrings[i] + "\"");

				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#isDigital()}.
	 */
	@Test
	@DisplayName("isDigital()")
	@Order(9)
	final void testIsDigital()
	{
		String testName = new String("isDigital()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean isDigital = testNumber.isDigital();
					assertEquals(expectedDigital[i],
					             isDigital,
					             testName
					                 + " unexpected digital status with \""
					                 + testStrings[i] + "\"");

				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#compareTo(model.PhoneNumber)}.
	 */
	@Test
	@DisplayName("compareTo(model.PhoneNumber)")
	@Order(10)
	final void testCompareTo()
	{
		String testName = new String("compareTo(model.PhoneNumber)");
		System.out.println(testName);

		PhoneNumber baseNumber = PhoneNumber.parse(testStrings[0]);
		assertNotNull(baseNumber,
		              testName +  " unexpected null base instance");

		for (int i = 1; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					int comparison = (int) Math.signum(testNumber.compareTo(baseNumber));
					boolean lower = comparison < 0;
					boolean bigger = comparison > 0;
					assertEquals(expectedLower[i],
					             lower,
					             testName + " unexpected lower status between "
					                 + baseNumber + " and " + testNumber);
					assertEquals(expectedBigger[i],
					             bigger,
					             testName + " unexpected bigger status between "
					                 + baseNumber + " and " + testNumber);
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#equals(Object)}.
	 */
	@Test
	@DisplayName("equals(Object)")
	@Order(11)
	final void testEqualsObject()
	{
		String testName = new String("equals(Object)");
		System.out.println(testName);

		PhoneNumber baseNumber = PhoneNumber.parse(testStrings[0]);
		assertNotNull(baseNumber,
		              testName +  " unexpected null base instance");

		for (int i = 1; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					boolean equal = baseNumber.equals(testNumber);
					assertEquals(expectedEqual[i],
					             equal,
					             testName + " unexpected equality status between "
					                 + baseNumber + " and " + testNumber);
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#hashCode()}.
	 */
	@Test
	@DisplayName("hashCode()")
	@Order(12)
	final void testHashCode()
	{
		String testName = new String("hashCode()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					final int prime = 31;
					int expectedHash = 1;
					expectedHash = (expectedHash * prime) + testNumber.getRegion();
					expectedHash = (expectedHash * prime) + testNumber.getSubscriber();
					int hash = testNumber.hashCode();
					assertEquals(expectedHash,
					             hash,
					             testName + " unexpected hash code for " + testNumber);
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(13)
	final void testToString()
	{
		String testName = new String("toString()");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					String output = testNumber.toString();
					Matcher matcher = outputPattern.matcher(output);
					assertTrue(matcher.matches(),
					           testName + " toString(): " + output
					               + " does not match expected output pattern");
					if (testNumber.isInternational())
					{
						assertTrue(output.startsWith(PhoneNumber.InternationalPrefix),
						           testName + " international number doesn't start with +33");
					}
					else
					{
						assertTrue(output.startsWith("0"),
						           testName + " national number doesn't start with 0");
					}
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link model.PhoneNumber#contains(String)}.
	 */
	@Test
	@DisplayName("contains(String)")
	@Order(14)
	final void testContains()
	{
		String testName = new String("contains(String)");
		System.out.println(testName);

		for (int i = 0; i < testStrings.length; i++)
		{
			if (!expectedMatches[i])
			{
				continue;
			}
			else
			{
				try
				{
					testNumber = PhoneNumber.parse(testStrings[i]);
					assertNotNull(testNumber,
					              testName +  " unexpected null instance");
					String output = testNumber.toString();
					String[] properWords = output.split(" ");
					for (String word : properWords)
					{
						assertTrue(testNumber.contains(word),
						           testName + " unexpected uncontained word: "
						               + word);
					}
					Set<String> containedWords = buildWordSet(i);
					for (int j = 0; j < properWords.length; j++)
					{
						for (Iterator<String> it = containedWords.iterator(); it.hasNext();)
						{
							if (it.next().contains(properWords[j]))
							{
								it.remove();
							}
						}
					}
					for (String word : containedWords)
					{
						assertTrue(testNumber.contains(word),
						           testName + " unexpected uncontained word: "
						               + word);
					}
					Set<String> uncontainedWords = buildForeignWordSet(i);
					for (int j = 0; j < properWords.length; j++)
					{
						for (Iterator<String> it = uncontainedWords.iterator(); it.hasNext();)
						{
							if (it.next().contains(properWords[j]))
							{
								it.remove();
							}
						}
					}
					for (String word : uncontainedWords)
					{
						assertFalse(testNumber.contains(word),
						            testName + " unexpected contained word: "
						                + word);
					}
				}
				catch (IllegalArgumentException e)
				{
					if (expectedMatches[i])
					{
						fail(testName + " unexpected exception creating from \""
						    + testStrings[i] + "\"");
					}
				}
			}
		}
	}

}
