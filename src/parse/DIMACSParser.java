package parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import formula.Formula;

public class DIMACSParser {

	static int currLine = -1;
	static int nbVar = 0;
	static int nbClause = 0;
	static int pLineLength = 4;

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

		try {

			currLine = -1;
			nbVar = 0;
			nbClause = 0;
			pLineLength = 4;

			processComments(fileLines);

			processPLine(fileLines);

			return processFormula(fileLines, verbose, out);

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
	 * Parse the formula in the DIMACS file
	 * 
	 * @param fileLines
	 *            The breakdown of lines in the file
	 * @param verbose
	 *            A boolean indicationg whether there should be a verbose output
	 * @param out
	 *            Where any verbose output should be directed
	 * @return The formula generated from the DIMACS file
	 */
	private static Formula processFormula(ArrayList<String> fileLines, boolean verbose, PrintStream out) {
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
	}

	/**
	 * Process the line of the file that contains the properties of the formula
	 * 
	 * @param fileLines
	 *            The individual lines in the file
	 * @throws IOException
	 */
	private static void processPLine(ArrayList<String> fileLines) throws IOException {
		if (fileLines.get(currLine).charAt(0) == 'p') {
			String[] splitLine = fileLines.get(currLine++).split(" ");

			if (splitLine.length != pLineLength)
				DIMACSFormatException();
			if (!splitLine[1].equals("cnf"))
				DIMACSFormatException();

			nbVar = Integer.parseInt(splitLine[2]);
			nbClause = Integer.parseInt(splitLine[3]);

		} else {
			DIMACSFormatException();
		}
	}

	/**
	 * Parse the file lines to ignore any comments at the start of the file
	 * 
	 * @param fileLines
	 *            The lines of the file to be checked
	 */
	private static void processComments(ArrayList<String> fileLines) {
		while (fileLines.get(++currLine).charAt(0) == 'c')
			;
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
