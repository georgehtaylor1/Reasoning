import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResolutionTest {

	/**
	 * Test that the resolution prover works
	 */
	@Test
	public void test() {

		boolean verbose = true;

		String[] inputs = { "(X & (-X))", "(((A->B)&(-B))&A)", "(((A->B)&(B->C))->(-(A->C)))",
				"(((A->B)&(B->C))->(A->C))", "(((P -> Q) & P) -> Q)", };

		boolean[] results = { false, false, true, true, true };

		// Check I haven't set up the test wrong
		assertTrue(inputs.length == results.length);

		for (int i = 0; i < inputs.length; i++) {
			Formula f = new Formula(inputs[i], true, verbose, System.out);
			assertEquals(f.isSatisfiable(), results[i]);
			if (verbose)
				System.out.println("\n------------------------------------------------------------\n");
		}

	}

}
