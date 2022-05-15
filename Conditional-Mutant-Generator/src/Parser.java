import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Parser {
	public static char lastChar = ' ';
	public static int lineNum = 1;
	public static int charPos = 1;
	public static int startPos = 1;
	public static String csym = "";
	public static boolean LineReturnNotInLeadingWhiteSpace = false;
	public static void main(String []args) throws IOException {
		lastChar = ' ';
		lineNum = 1;
		charPos = 1;
		startPos = 1;
		LineReturnNotInLeadingWhiteSpace = false;
		String lexeme = "";
		Charset encoding = Charset.defaultCharset();
		File file = new File("MainActivity.kt");
		try {
			InputStream in = new FileInputStream(file);
			Reader reader = new InputStreamReader(in, encoding);
			Reader buffer = new BufferedReader(reader);
			lexeme = next(buffer);
    
			String kindStr = kind(lexeme);
			while ( !(kindStr.equals("end-of-text"))) {
				System.out.println(lexeme+" - (Position: "+position()+" Kind: "+ kindStr+" Value: "+value(lexeme,kindStr) +")");
				lexeme = next(buffer);
				kindStr = kind(lexeme);
			}
			System.out.println(lexeme);
      
		} catch (FileNotFoundException e) {
			System.out.println("The entered file from input was not found on this machine, exiting now.");
			return;
		}
	}
  
	public static String next(Reader buffer) throws IOException {
		if(LineReturnNotInLeadingWhiteSpace == true) {
			lineNum++;
			charPos = 1;
			LineReturnNotInLeadingWhiteSpace = false;
		}
		startPos = charPos;
		int r;
		String lexeme = "";
		boolean leadws = true;
		boolean whiteSpace = false;
		boolean divOp = false;
		boolean comments = false;
		if(lastChar == ';') {
			lastChar = ' ';
			startPos--;
			return ";";
		}
		else if(lastChar == ')') {
			lastChar = ' ';
			startPos--;
			return ")";
		}
		else if(lastChar == ':') {
			lastChar = ' ';
			startPos--;
			return ":";
		}
		while (whiteSpace == false) {    
			buffer.mark(1);
			r = buffer.read();
			charPos++;
			if(r == -1) {
				return "end-of-text";
			}
			char ch = (char) r;
			if(ch == ' ' || ch == '\n' || ch == ';' || ch == ')' || ch == ':') {
				if(ch == ')') {
					if(lexeme.length() > 0)
						return lexeme + ")";
					else
						return ")";
				}
				else if(ch == ';') {
					if(lexeme.length() > 0)
						return lexeme + ";";
					else return ";";
				}
				else if( ch == '\n') {
					if(leadws == false) {
						LineReturnNotInLeadingWhiteSpace = true;
					}
					else {
						lineNum++;
						charPos = 1;
						startPos = 1;
					}
					if(lexeme.length() > 0)
						return lexeme + "\n";
					else
						return "\n";
				}
				if(comments == true) {
					if( ch == '\n') {
						divOp = false;
						comments = false;
					}
				}
				else if( divOp == true) {
					return "/";
				}
				else if((ch == ';' || ch == ')' || ch == ':') && leadws == true) {
					lexeme = lexeme +ch;
					startPos++;
					leadws = false;
				}
				else if (leadws == false) {
					whiteSpace = true;
					if(lexeme.equals(":=")) {
						startPos--;
					}
				}
				else {
					// With leading spaces increment start position
					startPos++;
				}
				lastChar = ch;
			}
			else if(ch == '{') {
				return "{";
			}
			else if(ch == '}') {
				return "}";
			}
			else if(ch == '(') {
				if(lexeme.length() > 0) {
					buffer.reset();
					return lexeme;
				}
				if(comments == false) {
					return "(";
				}
			}
			else if(ch == '/') {
				lexeme += ch;
				buffer.mark(1);
				r = buffer.read();
				ch = (char) r;
				if(ch == '/') {
					if(divOp = true) {
						comments = true;
						lexeme = lexeme.substring(0, lexeme.length()-1);
					}
					else {
						divOp = true;
					}
				}
				else {
					lexeme += ch;
				}
			}
			else {
				if(comments == false) {
					lexeme = lexeme +ch;
					leadws = false;
				}
			}
		}
		return lexeme;
	}
	public static String kind(String lexeme) {
		Character[] numarr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		Character[] alpharr = {'a','b','c','d','e','f','g','h','i','j','k',
				'l','m','n','o','p','q','u','r','s','t','u',
				'v','w','x','y','z',
				'A','B','C','D','E','F','G','H','I','J','K',
				'L','M','N','O','P','Q','U','R','S','T','U',
				'V','W','X','Y','Z'};
		String[] array = {"program", "int", ":","end","bool",";",":=",
				"if","then","else","fi","while","do","od","print","<","=","=<",
				"!=",">=",">","+","-","or","*","/","and","(",")",",","not","true","false", "end-of-text"};
		if(Arrays.asList(array).contains(lexeme)== true) {
			return lexeme;
		}
		Boolean id = true;
		Boolean num = true;
		for(int i=0; i<lexeme.length(); i++) {
			char ch = lexeme.charAt(i);
			if(Arrays.asList(numarr).contains(ch)== false) {
				num = false;
				if(Arrays.asList(alpharr).contains(ch)== false) {
					id = false;
					break;
				}
			}
		}
		if(num == true) {
			return "NUM";
		}
		else if(id == true) {
			return "ID";
		}
		return "ILLEGAL";
	}
	public static String position() {
		return "(" + lineNum +","+ startPos +")";
	}
	public static String value(String lexeme, String kind) {
		if((kind == "ID")||(kind=="NUM")) {
			return lexeme;
		}
		else return " ";
	}
}