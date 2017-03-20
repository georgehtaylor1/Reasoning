# Reasoning
This program takes string formulas or DIMACS files as an input and can produce parse trees, resolution proofs and DPLL proofs.
## The Program
### Parsing
Input can be given either as a string based formula, or as a DIMACS formatted file. These will then be parsed into an abstract syntax tree which is then reduced to conjunctive normal form.
### Resolution Proof
A resolution proof is completed by negating the given formula, this negation is then fully resolved to determine it's satisfiability and the original formulas validity is set.
### DPLL proof
A DPLL proof will determine whether or not a given formula is satisfiable or not, giving a model if it is satisfiable.
## Running
The program is test-driven, there are currently test files for DPLL proofs, Resolution Proofs, the DIMACS parser and clauses.
To run the tests, use the [test suite](https://github.com/georgehtaylor1/Reasoning/blob/master/src/AllTests.java).
## Notes
- The Formula object currently represents the clauses as a `HashSet<Clause>`. I would like to change this to use a `SortedSet<Clause>` as this would make selecting the smallest clauses for resolution more efficient.
- The abstract class [Proof.java](https://github.com/georgehtaylor1/Reasoning/blob/master/src/Proof.java) allows for different implementations of proofs.
