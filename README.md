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
The program can be built from source using the build script:
```{r, engine='bash', count_lines}
./build
```
Once built the program can be run using the running script:
```{r, engine='bash', count_lines}
./run [<flag> [args]*]*
```
Examples can be found below.
The program is also test-driven, there are currently test files for DPLL proofs, Resolution Proofs, the DIMACS parser and clauses.
To run the tests, use the [test suite](https://github.com/georgehtaylor1/Reasoning/blob/master/src/AllTests.java).
## Examples
```{r, engine='bash', count_lines}
./run -f "(((A->B)&(B->C))->(A->C))"
```
Will output resolution and DPLL proofs of transitivity.
```{r, engine='bash', count_lines}
./run -f "((-(A+B))->((-A)&(-B)))" -o "out.txt" -v
```
Will output resolution and DPLL proofs of transitivity whilst providing a verbose output in "out.txt".
```{r, engine='bash', count_lines}
./run -d "DIMACSTestFiles/dimacs1.txt" -o "out.txt" -v
```
Will output resolution and DPLL proofs from the contents of "DIMACSTestFiles/dimacs1.txt" whilst providing verbose output in "out.txt".
