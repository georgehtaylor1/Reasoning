import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class DIMACSParser {

	/**
	 * Parse the given input file into a formula
	 * 
	 * @param file
	 *            The DIMACS file to parse
	 * @param verbose
	 *            Should there be a verbose output
	 * @param out
	 *            Where should the output be directed
	 * @return The formula generated from the DIMACS file
	 */
	public static Formula parse(String file, boolean verbose, PrintStream out) {

		ArrayList<String> fileLines = new ArrayList<String>();

		// Read the input file into the ArrayList
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String currLine = "";
			while ((currLine = reader.readLine()) != null)
				fileLines.add(currLine);
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("Specified input file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Specified input file could not be read.");
			e.printStackTrace();
		}

		int currLine = -1;

		int nbVar = 0;
		int nbClause = 0;

		try {

			while (fileLines.get(++currLine).charAt(0) == 'c')
				;

			if (fileLines.get(currLine).charAt(0) == 'p') {
				String[] splitLine = fileLines.get(currLine++).split(" ");

				if (splitLine.length != 4)
					DIMACSFormatException();
				if (!splitLine[1].equals("cnf"))
					DIMACSFormatException();

				nbVar = Integer.parseInt(splitLine[2]);
				nbClause = Integer.parseInt(splitLine[3]);

			} else {
				DIMACSFormatException();
			}

			if (nbClause == 1) {
				String[] splitLine = fileLines.get(currLine).split(" ");
				int[] intSplitLine = new int[splitLine.length];
				for (int i = 0; i < splitLine.length; i++)
					intSplitLine[i] = Integer.parseInt(splitLine[i]);
				String formula = clauseToString(intSplitLine);
				return new Formula(formula, verbose, out);
			}

			String formula = "";
			for (int i = 0; i < nbClause - 1; i++)
				formula = formula + "(";
			String[] splitLine = fileLines.get(currLine++).split(" ");
			int[] intSplitLine = new int[splitLine.length];
			for (int i = 0; i < splitLine.length; i++)
				intSplitLine[i] = Integer.parseInt(splitLine[i]);
			formula = formula + clauseToString(intSplitLine);

			while (currLine < fileLines.size()) {
				splitLine = fileLines.get(currLine).split(" ");
				intSplitLine = new int[splitLine.length];
				for (int i = 0; i < splitLine.length; i++)
					intSplitLine[i] = Integer.parseInt(splitLine[i]);
				formula = formula + "&" + clauseToString(intSplitLine) + ")";
				currLine++;
			}

			return new Formula(formula, verbose, out);

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.err.println("Expected integer inputs for number of clauses and variables");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Convert a DIMACS clause to a propositional string
	 * 
	 * @param input
	 *            The list of literals in the clause
	 * @return The generated clause
	 */
	private static String clauseToString(int[] input) {
		int numLiterals = input.length;
		if (numLiterals == 1)
			return numberToAtom(input[0]);
		String r = "";
		for (int i = 0; i < numLiterals - 1; i++)
			r = r + "(";
		r = r + numberToAtom(input[0]);
		for (int i = 1; i < numLiterals; i++) {
			r = r + "+" + numberToAtom(input[i]) + ")";
		}
		return r;
	}

	/**
	 * Convert a number to a symbol in the formula. A given number will be converted to base 26 and then converted into ASCII.
	 * 
	 * @param input
	 *            The number to produce an atom from
	 * @return the atom produced from the number
	 */
	public static String numberToAtom(int input) {
		String r = "";
		boolean neg = false;
		if (input < 0) {
			input *= -1;
			neg = true;
			r = "(-";
		}
		for (Integer i : numberToDigits(input + 1, 26))
			r = r + ((char) (i + 64));

		return neg ? r + ")" : r;
	}

	/**
	 * Convert a base 10 int into an array of another base
	 * 
	 * @param number
	 *            The number to be converted
	 * @param base
	 *            The base to convert into
	 * @return
	 */
	private static ArrayList<Integer> numberToDigits(int number, int base) {
		ArrayList<Integer> digits = new ArrayList<Integer>();
		while (number > 0) {
			digits.add(number % base);
			number = Math.floorDiv(number, base);
		}
		return digits;
	}

	/**
	 * Helper function simply throws a format exception
	 * 
	 * @throws IOException
	 */
	private static void DIMACSFormatException() throws IOException {
		throw new IOException("Invalid DIMACS format");
	}

}
