# Reasoning
This program takes string formulas as an input and can produce parse trees and resolution proofs.
## Running
The program is test-driven, there are currently two test files [ClauseTest.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/ClauseTest.java) and [ResolutionTest.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/ResolutionTest.java). The first tests the equals and compareTo methods in the [Clause.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/Clause.java) class. The second tests that the proof for a set of formulas returns the correct satisfiability.
To run the tests, use the [test suite](https://github.com/georgehtaylor1/Reasoning/blob/master/src/AllTests.java).
## Notes
- The Formula object currently represents the clauses as a `HashSet<Clause>`. I would like to change this to use a `SortedSet<Clause>` as this would make selecting the smallest clauses for resolution more efficient.
- The abstract class [Proof.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/Proof.java) allows for different implementations of proofs. The only implementation so far is in [ResolutionProof.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/ResolutionProof.java) which implements a full resolution prover.
