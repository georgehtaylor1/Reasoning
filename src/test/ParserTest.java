package test;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import parse.DIMACSParser;


public class ParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * First test that atoms are generated correctly from numbers then test that full files are parsed correctly.
	 */
	@Test
	public void test() {

		assertEquals(DIMACSParser.numberToAtom(0), "A");
		assertEquals(DIMACSParser.numberToAtom(26), "AA");
		assertEquals(DIMACSParser.numberToAtom(53), "BB");

		assertEquals(DIMACSParser
				.parse("/home/george/workspace/Reasoning/DIMACSTestFiles/dimacs1.txt", AllTests.verbose, System.out)
				.getOriginalFormula(), "(((((B+D)+A)&(((B+E)+(-F))+C))&(A+B))&((E+(-D))+C))");
	}

}
