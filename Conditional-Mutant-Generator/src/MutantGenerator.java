
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MutantGenerator {

	public static BufferedReader br; //buffered reader instance
	
	//main function
	public static void main(String[] args) throws IOException {
		
		// After you have attached your desired file to the project, populate filename with the file name.
		String filename = "MainActivity.kt";
		
		// this array will keep track of all the lexemes that are read from the parser.
		// it will ultimately be used to reconstruct the file when generating the mutant file.
		ArrayList<String> headOfFile = new ArrayList<String>();
		
		// initializing the buffered reader with the given filename
		br = new BufferedReader(new FileReader(new File(filename)));
		
		// this variable will hold the number of mutants needed to be generated.
		int numOfOperators = 0;
		
		// this boolean variable will let the program know when to replace and operator and output the mutant file.
		boolean operatorFound = false;
		try {
			Parser t = new Parser(); // parser instance
			String lexeme = t.next(br); // recording the initial lexeme
			headOfFile.add(lexeme + " "); // adding the lexeme to the headOfFile list.
			lexeme = t.next(br); // recording the next lexeme
			
			// the analyzeFile function will take the parser and filename and return the number of conditional
			// operators found within the target file.
			numOfOperators = analyzeFile(t, filename);
			System.out.println("Generating " + numOfOperators + " mutations against " + filename + "...\n\n");
			// this loop will repeat for each operator in the target file.
			for(int i = 1; i <= numOfOperators; i++) {
				
				// while the lexeme returned from the parser does not equal 
				// "end-of-text" and an operator has not been found the next lexeme will be called
				// and added to the headOfFile list.
				while(!lexeme.equals("end-of-text") && !operatorFound) {
					
					// if a conditional operator is found, the operatorFound variable will be set
					// to true to break the while loop, the current lexeme will be added to the 
					// headOfFile list, and we will generate the mutant pertaining to this operator.
					if(conditionalMutations(lexeme) != null) {
						operatorFound = true;
						headOfFile.add(lexeme + " ");
						generateMutantFile(conditionalMutations(lexeme), headOfFile, i, filename);
					}
					
					// all lexemes that are not conditional operators will be added to the headOfFile list.
					else {
						headOfFile.add(lexeme + " ");
					}
					
					// getting the next lexeme
					lexeme = t.next(br);
				}
				
				// setting operatorFound back to false to search for the next conditional operator.
				operatorFound = false;
			}
			// catching IOException
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(numOfOperators + " mutations have been generated. "
				+ "See Conditional-Mutant-Generator directory for mutant files.");
		
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 																							   *
	 * The generateMutantFile function will take in a replacement conditional operator, the        *
	 * head of the original file, the current mutation number, and the name of the target file.    *
	 * A BufferedWriter will be used to first write out the head of the original file, next write  *
	 * the replacement operator, and then write the remainder of the original file after the       *
	 * mutation. The BufferedWriter is then closed.												   *
	 * 																							   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public static void generateMutantFile(String replacementOperator, ArrayList<String> headOfOriginal, 
			int mutationNumber, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("(" + mutationNumber + ")Mutant-" + filename));
		for(int i = 0; i < headOfOriginal.size()-1; i++) {
			writer.write(headOfOriginal.get(i));
		}
		writer.write(replacementOperator + " ");
		writeRemainderOfFile(writer);
		writer.close();
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 																							   *
	 * The writeRemainderOfFile function will take in a currently open BufferedReader and write    *
	 * the proceeding portion of the file after the mutation.
	 * 																							   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public static void writeRemainderOfFile(BufferedWriter w) {
		try {
			br.mark(1);
			while(br.ready()) {
				w.write(br.readLine() + "\n");
			}
			br.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 																							   *
	 * The analyzeFile function will take the parser instance and filename as input and scan the   *
	 * entire file for conditional operators. It will total this number and return it.			   *
	 * 																							   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public static int analyzeFile(Parser p, String filename) throws FileNotFoundException {
		int numOfOps = 0;
		BufferedReader fileScanner = new BufferedReader(new FileReader(filename));
		try {
			String lex = p.next(fileScanner);
			while(!lex.equals("end-of-text")) {
				if(conditionalMutations(lex) != null) {
					numOfOps++;
				}
				lex = p.next(fileScanner); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return numOfOps;
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * 																							   *
	 * The conditionalMutations function will take in a string as input and check whether it is a  *
	 * conditional operator. If it is, the function will return the inversed version of that       *
	 * operator. Otherwise it will return null.													   *
	 * 																							   *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public static String conditionalMutations(String operator) {
		String returnOperator = null;
		switch(operator) {
		case ">": returnOperator = "<=";
					break;
		case ">\n": returnOperator = "<=";
					break;
		case "<": returnOperator = ">=";
					break;
		case "<\n": returnOperator = ">=";
					break;
		case ">=": returnOperator = "<";
					break;
		case ">=\n": returnOperator = "<";
					break;
		case "<=": returnOperator = ">";
					break;
		case "<=\n": returnOperator = ">";
					break;
		case "==": returnOperator = "!=";
					break;
		case "==\n": returnOperator = "!=";
					break;
		case "!=": returnOperator = "==";
					break;
		case "!=\n": returnOperator = "==";
					break;
		case "is": returnOperator = "!is";
					break;
		case "!is": returnOperator = "is";
					break;
		case "is\n": returnOperator = "!is";
					break;
		case "!is\n": returnOperator = "is";
					break;
		case "in": returnOperator = "!in";
					break;
		case "!in": returnOperator = "in";
					break;
		case "in\n": returnOperator = "!in";
					break;
		case "!in\n": returnOperator = "in";
					break;
		}
		return returnOperator;
	}
	
}