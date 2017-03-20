package test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClauseTest {

	@Test
	public void test() {

		// Test the equals method
		assertEquals(AllTests.h4.equals(AllTests.h1), true);
		assertEquals(AllTests.h1.equals(AllTests.h4), true);
		assertEquals(AllTests.h1.equals(AllTests.h2), false);
		assertEquals(AllTests.h1.equals(AllTests.h3), false);
		assertEquals(AllTests.h1.equals(null), false);

		// Test the compareTo method
		assertEquals(AllTests.h1.compareTo(AllTests.h2), -1);
		assertEquals(AllTests.h1.compareTo(AllTests.h3), -1);
		assertEquals(AllTests.h3.compareTo(AllTests.h1), 1);
		assertEquals(AllTests.h1.compareTo(AllTests.h4), 0);
		assertEquals(AllTests.h4.compareTo(AllTests.h1), 0);
		assertEquals(AllTests.h1.compareTo(null), -1);
	}

}
