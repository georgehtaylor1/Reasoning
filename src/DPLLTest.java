import static org.junit.Assert.*;

import org.junit.Test;

public class DPLLTest {

	@Test
	public void test() {

		// Check I haven't set up the test wrong
		assertTrue(AllTests.sampleInputs.length == AllTests.sampleResults.length);

		for (int i = 0; i < AllTests.sampleInputs.length; i++) {
			Formula f = new Formula(AllTests.sampleInputs[i], false, AllTests.verbose, System.out);
			DPLLProof proof = new DPLLProof(f);
			proof.prove(AllTests.verbose);
			assertEquals(f.isSatisfiable(), AllTests.sampleResults[i]);
			if (AllTests.verbose)
				System.out.println("\n------------------------------------------------------------\n");
			else
				System.out.println(f.getProof().toString());
		}

	}

}
