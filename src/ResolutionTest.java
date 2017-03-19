import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResolutionTest {

	/**
	 * Test that the resolution prover works
	 */
	@Test
	public void resolutionTest() {

		// Check I haven't set up the test wrong
		assertTrue(AllTests.sampleInputs.length == AllTests.sampleValidities.length);

		for (int i = 0; i < AllTests.sampleInputs.length; i++) {
			Formula f = new Formula(AllTests.sampleInputs[i], AllTests.verbose, System.out);
			f.parse(AllTests.verbose, System.out);
			f.prove(ProofType.RESOLUTION, AllTests.verbose, System.out);
			assertEquals(f.getConclusion(), AllTests.sampleValidities[i]);
			if (AllTests.verbose)
				System.out.println("\n------------------------------------------------------------\n");
			else
				System.out.println(f.getProof().toString());
		}

	}

}
