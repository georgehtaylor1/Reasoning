package reasoning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import formula.Formula;
import parse.DIMACSParser;
import proof.ProofType;

public class Reasoning {

	static HashMap<String, ArrayList<String>> params;

	public static void main(String[] args) {

		String dir = System.getProperty("user.dir");

		// Create a map of specified program parameters
		params = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				int j = i + 1;
				ArrayList<String> vals = new ArrayList<String>();
				while (j < args.length && !args[j].startsWith("-")) {
					vals.add(args[j]);
					j++;
				}
				params.put(args[i], vals);
				i = j - 1;
			}
		}

		if (params.isEmpty())
			fail("No program parameters specified.");

		boolean verbose = params.containsKey("-v");

		/**
		 * Get the location to output to
		 */
		PrintStream out = System.out;
		if (params.containsKey("-o")) {
			ArrayList<String> vals = params.get("-o");
			if (vals.size() != 1)
				fail("Incorrect usage of '-o'.");
			try {
				out = new PrintStream(new File(dir + vals.get(0)));
			} catch (FileNotFoundException e) {
				fail("The specified output file could not be found.");
			}
		}

		if(params.containsKey("-h")){
			help();
		}
		
		Formula formula = null;
		if (params.containsKey("-f")) {
			ArrayList<String> vals = params.get("-f");
			if (vals.size() != 1)
				fail("Invalid input for the '-f' flag.");
			formula = new Formula(vals.get(0), verbose, out);
		} else if (params.containsKey("-d")) {
			ArrayList<String> vals = params.get("-d");
			if (vals.size() != 1)
				fail("Invalid input for the '-d' flag.");
			formula = DIMACSParser.parse(dir + vals.get(0), verbose, out);
		} else {
			fail("No formula specified. Hint: use the '-f' or '-d' flags.");
		}

		if (formula != null) {
			process(formula, verbose, out);
		}
	}

	/**
	 * Process the given formula
	 * 
	 * @param formula
	 *            The formula to be processed
	 * @param verbose
	 *            Should there be a verbose output produced by the program
	 * @param out
	 *            Where should the output of the program be directed
	 */
	private static void process(Formula formula, boolean verbose, PrintStream out) {
		formula.parse(verbose, out);
		formula.prove(ProofType.RESOLUTION, verbose, out);
		System.out.println(formula.getProof());
		formula.prove(ProofType.DPLL, verbose, out);
		System.out.println(formula.getProof());
	}

	/**
	 * Display the help file
	 */
	private static void help() {
		try (BufferedReader br = new BufferedReader(
				new FileReader("/home/george/workspace/Reasoning/resource/help.txt"))) {
			String line = null;
			while ((line = br.readLine()) != null)
				System.out.println(line);
		} catch (FileNotFoundException e) {
			System.err.println("Help file not found");
		} catch (IOException e) {
			System.err.println("Help file could not be read");
		}
		System.exit(0);
	}

	/**
	 * Print a failure in the program with an optional message
	 * 
	 * @param message
	 *            The optional message to be printed
	 */
	private static void fail(String message) {
		if (message != null)
			System.out.println(message);
		System.out.println("Invalid usage. use 'java Reasoning -h' for help.");
		System.exit(0);
	}

}
