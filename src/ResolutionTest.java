import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResolutionTest {

	/**
	 * Test that the resolution prover works
	 */
	@Test
	public void test() {

		// Check I haven't set up the test wrong
		assertTrue(AllTests.sampleInputs.length == AllTests.sampleResults.length);

		for (int i = 0; i < AllTests.sampleInputs.length; i++) {
			Formula f = new Formula(AllTests.sampleInputs[i], true, AllTests.verbose, System.out);
			assertEquals(f.isSatisfiable(), AllTests.sampleResults[i]);
			if (AllTests.verbose)
				System.out.println("\n------------------------------------------------------------\n");
		}

	}

}
