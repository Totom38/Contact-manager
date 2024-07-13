/**
 *
 */
package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import model.Note;

/**
 * Test class for {@link Note}
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Note")
class NoteTest
{
	/**
	 * Note to test
	 */
	private Note testNote;

	/**
	 * Content to use in {@link Note}s
	 */
	private static final String[] contents = new String[] {
		"Lorem ipsum dolor sit amet, consectetur; adipiscing! elit, sed do eiusmod tempor incididunt? ut labore et dolore magna aliqua.",
		"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
		"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
	};

	/**
	 * Number of contents to set
	 */
	private static final int size = contents.length;

	/**
	 * Build a set of words we should find in ith content
	 * @param index the index of the content
	 * @return a set of words we should find in ith content
	 */
	private static Set<String> buildWordSet(int index)
	{
		Set<String> containedWords = new TreeSet<>();
		String content = contents[index];
		String[] words = content.split(";|,|\\.|!|\\?|\\s");
		for (String word : words)
		{
			if (!word.isEmpty())
			{
				containedWords.add(word);
			}
		}
		return containedWords;
	}

	/**
	 * Build a set of words we should NOT find in ith content
	 * @param index the index of the content
	 * @return a set of words we should NOT find in ith content
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
		System.out.println("-------------------------------------------------");
		System.out.println("Note tests");
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
		System.out.println("Note tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
	}

	/**
	 * Teardown after each test
	 * @throws Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testNote = null;
	}

	/**
	 * Test method for {@link model.Note#Note(java.lang.String)}.
	 */
	@Test
	@DisplayName("Note(String)")
	@Order(1)
	final void testNote()
	{
		String testName = new String("Note(String)");
		System.out.println(testName);

		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");

			/*
			 * No null content
			 */
			assertThrows(NullPointerException.class, () -> {
				@SuppressWarnings("unused")
				Note note = new Note(null);
			});

			/*
			 * No empty content
			 */
			assertThrows(IllegalArgumentException.class, () -> {
				@SuppressWarnings("unused")
				Note note = new Note("");
			});
		}
	}

	/**
	 * Test method for {@link model.Note#getDate()}.
	 */
	@Test
	@DisplayName("getDate()")
	@Order(2)
	final void testGetDate()
	{
		String testName = new String("getDate()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			Date now = Date.from(Instant.now());
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");
			Date date = testNote.getDate();
			assertNotNull(date,
			              testName + " unexpected null date instance");
			/*
			 * In rare cases "now" can be created at the end of a second
			 * and testNote can be created on the next second.
			 * Re-running the test should fix the issue
			 */
			assertEquals(now,
			             date,
			             testName + " unexpected date");
		}
	}

	/**
	 * Test method for {@link model.Note#getContent()}.
	 */
	@Test
	@DisplayName("getContent()")
	@Order(3)
	final void testGetContent()
	{
		String testName = new String("getContent()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");
			assertEquals(contents[i],
			             testNote.getContent(),
			             testName + " unexpected content");
		}
	}

	/**
	 * Test method for {@link model.Note#setContent(java.lang.String)}.
	 */
	@Test
	@DisplayName("setContent(String)")
	@Order(4)
	final void testSetContent()
	{
		String testName = new String("setContent(String)");
		System.out.println(testName);
		final String empty = "";
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");
			String previousContent = testNote.getContent();

			/*
			 * Setting null content should have no effect
			 */
			testNote.setContent(null);
			String content = testNote.getContent();
			assertNotNull(content,
			              testName + " unexpected null content");
			assertEquals(previousContent,
			             content,
			             testName + " unexpected content after setting null");

			/*
			 * Setting empty content should have no effect
			 */
			testNote.setContent(empty);
			content = testNote.getContent();
			assertNotEquals(empty,
			                content,
			                testName + " unexpected empty content");
			assertEquals(previousContent,
			             content,
			             testName + " unexpected content after setting null");

			Date date = testNote.getDate();
			/*
			 * Wait one second before changing content so date will be
			 * different
			 */
			try
			{
				TimeUnit.SECONDS.sleep(1);
			}
			catch (InterruptedException e)
			{
				fail(testName + " interrupted wait " + e.getLocalizedMessage());
			}
			String newContent = contents[(i+1)%size];
			testNote.setContent(newContent);
			assertEquals(newContent,
			             testNote.getContent(),
			             testName + " unexpected content");
			assertNotEquals(date,
			                testNote.getDate(),
			                testName +  "unexpected unmodified date after "
			                	+ "setting new content");
			assertTrue(testNote.getDate().after(date),
			           testName + " unexpected anterior date after setting "
			           	+ "new content");
		}
	}

	/**
	 * Test method for {@link model.Note#hashCode()}.
	 */
	@Test
	@DisplayName("hashCode()")
	@Order(5)
	final void testHashCode()
	{
		String testName = new String("hashCode()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");

			final int prime = 31;
			int expectedHash = 1;
			expectedHash = (expectedHash * prime) + testNote.getDate().hashCode();
			expectedHash = (expectedHash * prime) + testNote.getContent().hashCode();

			assertEquals(expectedHash,
			             testNote.hashCode(),
			             testName + " unexpected has value");
		}
	}

	/**
	 * Test method for {@link model.Note#equals(java.lang.Object)}.
	 */
	@Test
	@DisplayName("equals(Object)")
	@Order(6)
	final void testEqualsObject()
	{
		String testName = new String("equals(Object)");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");

			/*
			 * testNote is always different from null
			 */
			assertNotEquals(null,
			                testNote,
			                testName + " unexpected equality to null");

			/*
			 * testNote is alway equal to self
			 */
			long selfTestStart = System.nanoTime();
			boolean equalSelf = testNote.equals(testNote);
			long selfTestDuration = System.nanoTime() - selfTestStart;
			assertTrue(equalSelf,
			           testName + " unexpected inequality with self");

			/*
			 * Wait one second before creating other so date will be
			 * different : date should NOT be used to compare Notes
			 */
			try
			{
				TimeUnit.SECONDS.sleep(1);
			}
			catch (InterruptedException e)
			{
				fail(testName + " interrupted wait " + e.getLocalizedMessage());
			}

			/*
			 * testNote is equal to clone (but slower)
			 */
			Note otherNote = new Note(contents[i]);
			long cloneTestStart = System.nanoTime();
			boolean equalClone = testNote.equals(otherNote);
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
		}
	}

	/**
	 * Test method for {@link model.Note#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(7)
	final void testToString()
	{
		String testName = new String("toString()");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");
			/*
			 * toString format: "[date] content"
			 */
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(DateFormat.getDateInstance().format(testNote.getDate()));
			sb.append("] ");
			sb.append(testNote.getContent());
			String expected = sb.toString();
			String provided = testNote.toString();
			assertEquals(expected,
			             provided,
			             testName +  " unexpected toString value");
		}
	}

	/**
	 * Test method for {@link model.Note#compareTo(model.Note)}.
	 */
	@Test
	@DisplayName("compareTo(Note)")
	@Order(8)
	final void testCompareTo()
	{
		String testName = new String("compareTo(Note)");
		System.out.println(testName);

		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");

			/*
			 * Wait one second before creating other so date will be
			 * different : date should NOT be used to compare Notes
			 */
			try
			{
				TimeUnit.SECONDS.sleep(1);
			}
			catch (InterruptedException e)
			{
				fail(testName + " interrupted wait " + e.getLocalizedMessage());
			}

			Note otherNote = new Note(contents[i]);
			Note otherOtherNote = new Note(contents[i]);

			assertTrue(testNote.compareTo(otherNote) < 0,
			           testName +  " unexpected comparison with posterior note");
			assertTrue(otherNote.compareTo(testNote) > 0,
			           testName +  " unexpected comparison with anterior note");
			assertTrue(otherNote.compareTo(otherOtherNote) == 0,
			           testName +  " unexpected comparison with same time note");

			/*
			 * Whatever content is, posterior notes will always be greater
			 */
			for (int j = 0; j < size; j++)
			{
				otherNote = new Note(contents[j]);
				assertTrue(testNote.compareTo(otherNote) < 0,
				           testName + " unexpected comparison with anterior "
				           	+ "note");
			}

			/*
			 * But Simultaneous notes will be compared based on content
			 */
			testNote = new Note(contents[i]);
			for (int j = 0; j < size; j++)
			{
				otherNote = new Note(contents[j]);
				int contentCompare = contents[i].compareTo(contents[j]);
				assertEquals(contentCompare,
				             testNote.compareTo(otherNote),
				             testName + " unexpected comparison with "
				             	+ "simultaneous note");
			}
		}
	}

	/**
	 * Test method for {@link model.Note#contains(java.lang.String)}.
	 */
	@Test
	@DisplayName("contains(String)")
	@Order(9)
	final void testContains()
	{
		String testName = new String("contains(String)");
		System.out.println(testName);
		for (int i = 0; i < size; i++)
		{
			testNote = new Note(contents[i]);
			assertNotNull(testNote,
			              testName + " unexpected null instance");
			/*
			 * Contains should only search into content, not date
			 */
			Set<String> contained = buildWordSet(i);
			for (String word : contained)
			{
				assertTrue(testNote.contains(word),
				           testName + " unexpected not contained word " + word);
			}
			Set<String> uncontained = buildForeignWordSet(i);
			for (String word : uncontained)
			{
				assertFalse(testNote.contains(word),
				            testName + " unexpected contained word " + word);
			}
		}
	}
}
