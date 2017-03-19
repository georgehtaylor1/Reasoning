import static org.junit.Assert.*;

import org.junit.Test;

public class DPLLTest {

	@Test
	public void test() {

		// Check I haven't set up the test wrong
		assertTrue(AllTests.sampleInputs.length == AllTests.sampleSatisfiabilities.length);

		for (int i = 0; i < AllTests.sampleInputs.length; i++) {
			Formula f = new Formula(AllTests.sampleInputs[i], AllTests.verbose, System.out);
			f.parse(AllTests.verbose, System.out);
			f.prove(ProofType.DPLL, AllTests.verbose, System.out);
			assertEquals(f.getConclusion(), AllTests.sampleSatisfiabilities[i]);
			if (AllTests.verbose)
				System.out.println("\n------------------------------------------------------------\n");
			else
				System.out.println(f.getProof().toString());
		}

	}

}
