/*
 The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100/5100.

FALL 2022 version

STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:

1) Initialize the 2 reserve tables, which are fields in the Lexical class,
    named reserveWords and mnemonics.  Create the following functions.
    These calls are in the lexical constructor:
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);
    One-line examples are provided below

2) getIdentifier, getNumber, getString, and getOtherToken. getOtherToken recognizes
   one- and two-character tokesn in the language. 



PROVIDED UTILITY FUNCTIONS THAT STUDENT MAY NEED TO CALL-
1) YOU MUST NOT USE MAGIC NUMBERS, that is, numeric constants anywhere in the code,
   like "if tokencode == 50".  Instead, use the following:
// To get an integer for a given mnemonic, use
public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
// To get the full reserve word for a given mnemonic, use:
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }

Other methods:
    private void consoleShowError(String message)
    private boolean isLetter(char ch)
    private boolean isDigit(char ch)
    private boolean isStringStart(char ch)
    private boolean isWhitespace(char ch)
    public char GetNextChar()
To check numeric formats of strings to see if they are valid, use:

    public boolean doubleOK(String stin) 
    public boolean integerOK(String stin)
    

CALLING OTHER FUNCTIONS LIKE getNextLine COULD BREAK THE EXISTING CODE!

 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

//import sun.security.util.Length;

/**
 *
 * @author abrouill
 */
import java.io.*;

public class Lexical {

	private File file; // File to be read for input
	private FileReader filereader; // Reader, Java reqd
	private BufferedReader bufferedreader; // Buffered, Java reqd
	private String line; // Current line of input from file
	private int linePos; // Current character position
	// in the current line
	private SymbolTable saveSymbols; // SymbolTable used in Lexical
	// sent as parameter to construct
	private boolean EOF; // End Of File indicator
	private boolean echo; // true means echo each input line
	private boolean printToken; // true to print found tokens here
	private int lineCount; // line #in file, for echo-ing
	private boolean needLine; // track when to read a new line

	// Tables to hold the reserve words and the mnemonics for token codes
	private final int sizeReserveTable = 50;
	private ReserveTable reserveWords = new ReserveTable(sizeReserveTable); // a few more than # reserves
	private ReserveTable mnemonics = new ReserveTable(sizeReserveTable); // a few more than # reserves

	// constructor
	public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
		saveSymbols = symbols; // map the initialized parameter to the local ST
		echo = echoOn; // store echo status
		lineCount = 0; // start the line number count
		line = ""; // line starts empty
		needLine = true; // need to read a line
		printToken = false; // default OFF, do not print tokesn here
		// within GetNextToken; call setPrintToken to
		// change it publicly.
		linePos = -1; // no chars read yet
		// call initializations of tables
		initReserveWords(reserveWords);
		initMnemonics(mnemonics);

		// set up the file access, get first character, line retrieved 1st time
		try {
			file = new File(filename); // creates a new file instance
			filereader = new FileReader(file); // reads the file
			bufferedreader = new BufferedReader(filereader); // creates a buffering character input stream
			EOF = false;
			currCh = GetNextChar();
		} catch (IOException e) {
			EOF = true;
			e.printStackTrace();
		}
	}// Lexical constructor

	// inner class "token" is declared here, no accessors needed
	public class token {
		public String lexeme;
		public int code;
		public String mnemonic;

		token() {
			lexeme = "";
			code = 0;
			mnemonic = "";
		}// constructor
	}// inner class token

	// ******************* PUBLIC USEFUL METHODS
	// These are nice for syntax to call later
	// given a mnemonic, find its token code value
	public int codeFor(String mnemonic) {
		return mnemonics.LookupName(mnemonic);
	}// reserveFor method

	// given a mnemonic, return its reserve word
	public String reserveFor(String mnemonic) {
		return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
	}// reserveFor method

	// Public access to the current End Of File status
	public boolean EOF() {
		return EOF;
	}// EOF method

	
	// DEBUG enabler, turns on/OFF token printing inside of GetNextToken
	public void setPrintToken(boolean on) {
		printToken = on;
	}// setPrintToken method

	/* Added reserve words to reserve table */
	private void initReserveWords(ReserveTable reserveWords) {
		reserveWords.Add("GOTO", 0);
		reserveWords.Add("INTEGER", 1);
		reserveWords.Add("TO", 2);
		reserveWords.Add("DO", 3);
		reserveWords.Add("IF", 4);
		reserveWords.Add("THEN", 5);
		reserveWords.Add("ELSE", 6);
		reserveWords.Add("FOR", 7);
		reserveWords.Add("OF", 8);
		reserveWords.Add("WRITELN", 9);
		reserveWords.Add("READLN", 10);
		reserveWords.Add("BEGIN", 11);
		reserveWords.Add("END", 12);
		reserveWords.Add("VAR", 13);
		reserveWords.Add("DOWHILE", 14);
		reserveWords.Add("UNIT", 15);
		reserveWords.Add("LABEL", 16);
		reserveWords.Add("REPEAT", 17);
		reserveWords.Add("UNTIL", 18);
		reserveWords.Add("PROCEDURE", 19);
		reserveWords.Add("DOWNTO", 20);
		reserveWords.Add("FUNCTION", 21);
		reserveWords.Add("RETURN", 22);
		reserveWords.Add("FLOAT", 23);
		reserveWords.Add("STRING", 24);
		reserveWords.Add("ARRAY", 25);

		// 1 and 2-char
		reserveWords.Add("/", 30);
		reserveWords.Add("*", 31);
		reserveWords.Add("+", 32);
		reserveWords.Add("-", 33);
		reserveWords.Add("(", 34);
		reserveWords.Add(")", 35);
		reserveWords.Add(";", 36);
		reserveWords.Add(":=", 37);
		reserveWords.Add(">", 38);
		reserveWords.Add("<", 39);
		reserveWords.Add(">=", 40);
		reserveWords.Add("<=", 41);
		reserveWords.Add("=", 42);
		reserveWords.Add("<>", 43);
		reserveWords.Add(",", 44);
		reserveWords.Add("[", 45);
		reserveWords.Add("]", 46);
		reserveWords.Add(":", 47);
		reserveWords.Add(".", 48);
		//reserveWords.Add("ID", 50);
		//reserveWords.Add("IC", 51);
		//reserveWords.Add("FC", 52);
		//reserveWords.Add("ST", 53);
		reserveWords.Add("UN", 99);
	}// initReserveWords method

	/* add the student created ID to represent code integer */
	private void initMnemonics(ReserveTable mnemonics) {

		mnemonics.Add("GO_TO", 0);
		mnemonics.Add("INTEG", 1);
		mnemonics.Add("TO_12", 2);
		mnemonics.Add("DO_12", 3);
		mnemonics.Add("IF_12", 4);
		mnemonics.Add("THEN_", 5);
		mnemonics.Add("ELSE_", 6);
		mnemonics.Add("FOR_1", 7);
		mnemonics.Add("OF_12", 8);
		mnemonics.Add("WRTLN", 9);
		mnemonics.Add("REDLN", 10);
		mnemonics.Add("BEGIN", 11);
		mnemonics.Add("END_1", 12);
		mnemonics.Add("VAR_1", 13);
		mnemonics.Add("DOWHL", 14);
		mnemonics.Add("UNIT_", 15);
		mnemonics.Add("LABEL", 16);
		mnemonics.Add("REPET", 17);
		mnemonics.Add("UNTIL", 18);
		mnemonics.Add("PRCDR", 19);
		mnemonics.Add("DOWN2", 20);
		mnemonics.Add("FNCTN", 21);
		mnemonics.Add("RTURN", 22);
		mnemonics.Add("FLOAT", 23);
		mnemonics.Add("STING", 24);
		mnemonics.Add("ARRAY", 25);

		// 1 and 2-char
		mnemonics.Add("FSLAS", 30);
		mnemonics.Add("ASTRK", 31);
		mnemonics.Add("ADDTN", 32);
		mnemonics.Add("SUBTN", 33);
		mnemonics.Add("OPARN", 34);
		mnemonics.Add("CPARN", 35);
		mnemonics.Add("SEMCO", 36);
		mnemonics.Add("ASSIG", 37);
		mnemonics.Add("GRTHN", 38);
		mnemonics.Add("LSTHN", 39);
		mnemonics.Add("GTEQL", 40);
		mnemonics.Add("LTEQL", 41);
		mnemonics.Add("EQUAL", 42);
		mnemonics.Add("NTEQL", 43);
		mnemonics.Add("COMMA", 44);
		mnemonics.Add("OSQBR", 45);
		mnemonics.Add("CSQBR", 46);
		mnemonics.Add("COLON", 47);
		mnemonics.Add("PERIO", 48);
		mnemonics.Add("IDENT", 50);
		mnemonics.Add("INCON", 51);
		mnemonics.Add("FTCON", 52);
		mnemonics.Add("ST_12", 53);
		mnemonics.Add("UNDEF", 99);
	}// initMnemonics method

	// ********************** UTILITY FUNCTIONS
	private void consoleShowError(String message) {
		System.out.println("**** ERROR FOUND: " + message);
	}// consoleShowError method

	// Character category for alphabetic chars
	private boolean isLetter(char ch) {
		return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
	}// isLetter method

	// Character category for 0..9
	private boolean isDigit(char ch) {
		return ((ch >= '0') && (ch <= '9'));
	}// isDigit method

	// Category for any whitespace to be skipped over
	private boolean isWhitespace(char ch) {
		// SPACE, TAB, NEWLINE are white space
		return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
	}// isWhitespace method

	// Returns the VALUE of the next character without removing it from the
	// input line. Useful for checking 2-character tokens that start with
	// a 1-character token.
	private char PeekNextChar() {
		char result = ' ';
		if ((needLine) || (EOF)) {
			result = ' '; // at end of line, so nothing
		} else //
		{
			if ((linePos + 1) < line.length()) { // have a char to peek
				result = line.charAt(linePos + 1);
			}
		}
		return result;
	}// PeekNextChar method

	// Called by GetNextChar when the cahracters in the current line are used up.
	// STUDENT CODE SHOULD NOT EVER CALL THIS!
	private void GetNextLine() {
		try {
			line = bufferedreader.readLine();
			if ((line != null) && (echo)) {
				lineCount++;
				System.out.println(String.format("%04d", lineCount) + " " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line == null) { // The readLine returns null at EOF, set flag
			EOF = true;
		}
		linePos = -1; // reset vars for new line if we have one
		needLine = false; // we have one, no need
		// the line is ready for the next call to get a character
	}

	// Called to get the next character from file, automatically gets a new
	// line when needed. CALL THIS TO GET CHARACTERS FOR GETIDENT etc.
	public char GetNextChar() {
		char result;
		if (needLine) // ran out last time we got a char, so get a new line
		{
			GetNextLine();
		}
		// try to get char from line buff
		if (EOF) {
			result = '\n';
			needLine = false;
		} else {
			if ((linePos < line.length() - 1)) { // have a character available
				linePos++;
				result = line.charAt(linePos);
			} else { // need a new line, but want to return eoln on this call first
				result = '\n';
				needLine = true; // will read a new line on next GetNextChar call
			}
		}
		return result;
	}// GetNextChar method

	// The constants below allow flexible comment start/end characters
	final char commentStart_1 = '{';
	final char commentEnd_1 = '}';
	final char commentStart_2 = '(';
	final char commentPairChar = '*';
	final char commentEnd_2 = ')';

	// Skips past single and multi-line comments, and outputs UNTERMINATED
	// COMMENT when end of line is reached before terminating
	String unterminatedComment = "Comment not terminated before End Of File";

	public char skipComment(char curr) {
		if (curr == commentStart_1) {
			curr = GetNextChar();
			while ((curr != commentEnd_1) && (!EOF)) {
				curr = GetNextChar();
			}
			if (EOF) {
				consoleShowError(unterminatedComment);
			} // inner if
			else {
				curr = GetNextChar();
			} // inner else
		} // outer if
		else {
			if ((curr == commentStart_2) && (PeekNextChar() == commentPairChar)) {
				curr = GetNextChar(); // get the second
				curr = GetNextChar(); // into comment or end of comment

				while ((!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2))) && (!EOF)) {
					// if (lineCount >=4) {
					// System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());}
					curr = GetNextChar();
				}
				if (EOF) {
					consoleShowError(unterminatedComment);
				} // inner if
				else {
					curr = GetNextChar(); // must move past close
					curr = GetNextChar(); // must get following
				} // inner else
			} // middle level if

		} // outer else
		return (curr);
	}// skipComment method

	// Reads past all whitespace as defined by isWhiteSpace
	// NOTE THAT COMMENTS ARE SKIPPED AS WHITESPACE AS WELL!
	public char skipWhiteSpace() {

		do {
			while ((isWhitespace(currCh)) && (!EOF)) {
				currCh = GetNextChar();
			} // inner while loop
			currCh = skipComment(currCh);
		} while (isWhitespace(currCh) && (!EOF));
		return currCh;
	}// skipWhiteSpace() method

	private boolean isPrefix(char ch) {
		return ((ch == ':') || (ch == '<') || (ch == '>'));
	}// isPrefix method

	private boolean isStringStart(char ch) {
		return ch == '"';
	}// isStringStart method

	private boolean isUnderScore(char ch) {
		return ch == '_';
	}// isUnderScore method

	private boolean isDollarSign(char ch) {
		return ch == '$';
	}// isUnderScore method
	
	// global char
	char currCh;

	private token getIdentifier() {
		return returnIdentifier();
	}// getIdentifier method

	// return the lexeme portion of token
	private token returnIdentifier() {
		token result = new token();
		result.lexeme = result.lexeme + currCh; // have the first char

		currCh = GetNextChar();

		// while the char is letter, digit or underscore include that on identifier
		// token
		while (isLetter(currCh) || isDigit(currCh) || isUnderScore(currCh) || isDollarSign(currCh)) {
			result.lexeme = result.lexeme + currCh;
			currCh = GetNextChar();
		} // while

		// once the lexeme is concatenate , get the code and mnemonic from reserve table
		// and mnemonic table
		String longText = result.lexeme;

		// truncated string
		String lengthOf20 = "";
		// if identifier is longer than 20 character, TRUNCATE
		if (longText.length() >= 20) {

			int i = 0;
			while (i < 20) {
				lengthOf20 = lengthOf20 + longText.charAt(i);
				i++;
			}
			longText = lengthOf20;
			System.out.println("Identifier length > 20, truncated " + result.lexeme + " to " + lengthOf20 + "\n\n");
		} // if

		// get the code from the reserve word
		int code = getCode(longText);

		// if the lexeme is not reserve word then its identifier
		if (code == -1) {
			result.mnemonic = "IDENT";
			result.code = mnemonics.LookupName(result.mnemonic);
			// verify if the string is in symbol table
			int index = saveSymbols.LookupSymbol(longText);

			// if string is not in symbol table
			if (index == -1) {
				saveSymbols.AddSymbol(longText, 'v', 0);
			} // inner if
		} // if

		// else the lexeme is on reserve table
		else {
			result.code = code;
			result.mnemonic = mnemonics.LookupCode(result.code);
		} // else
		return result;
	}// returnIdentifier method

	// return the code for reserve word
	private int getCode(String longText) {
		int num = -1;
		num = reserveWords.LookupName(longText);
		return num;
	}// getCode method

	private token getNumber() {

		return returnNumber();

	}// getNUmber method

	// return token type if integer or float
	private token returnNumber() {
		token result = new token();
		result.lexeme = result.lexeme + currCh; // have the first char
		currCh = GetNextChar();
		// if the number is interger or float
		boolean isInteger = true;
		boolean isFloat = false;
		int decimalPtCount = 0;
		int numOfE = 0;
		// while the char is number,decimal point and E
		while (isDigit(currCh) || (currCh == '.' && decimalPtCount == 0) || (currCh == 'E' && numOfE == 0)) {
			
			if (isDigit(currCh)) {
				result.lexeme = result.lexeme + currCh;
			} // if
			// count the number of decimal point in a number
			if (currCh == '.') {
				result.lexeme = result.lexeme + currCh;
				decimalPtCount++;
				isInteger = false;
				isFloat = true;
			} //  else if
			
			if (currCh == 'E') {
				if(decimalPtCount == 1 && numOfE == 0)
				{
					result.lexeme = result.lexeme + currCh;
					isInteger = false;
					isFloat = true;
					numOfE++;
				}//if
							
				else {					
					result.mnemonic = "UNDEF";
					
					result.code = mnemonics.LookupName(result.mnemonic);
					result.mnemonic = mnemonics.LookupCode(result.code);
				}//else
			} // else if
			
			currCh = GetNextChar();
		}// while
		
		String numString = result.lexeme;
		
		if(!(numString.charAt(numString.length()-1 ) == 'E'))
		{
			String reducedLen = "";
			//length of the lexeme
			int numLen = result.lexeme.length();
			
			// verify if the string is in symbol table
			int index = saveSymbols.LookupSymbol(numString);
			
			//if the number is interger
			if(isInteger == true)
			{
				int intNum = Integer.parseInt(numString);
				if(numLen >= 6)
				{
					int i = 0;
					while (i < 6) {
						reducedLen = reducedLen + numString.charAt(i);
						i++;
					}
					System.out.println("Integer length > 6, truncated " + numString + " to " + reducedLen );
					numString = reducedLen;
				}//inner if
				result.mnemonic = "INCON";
				result.code = mnemonics.LookupName(result.mnemonic);
				if (index == -1) {
					saveSymbols.AddSymbol(numString, 'c', intNum);
				}// inner if
			}//if
			//else if the number is float
			else if(isFloat == true)
			{
				
				float floatNum = Float.parseFloat(numString);
				if(numLen >= 12)
				{
					int i = 0;
					while (i < 12) {
						reducedLen = reducedLen + numString.charAt(i);
						
						i++;
					}//while
					
					System.out.println("Float length > 12, truncated " + numString + " to " + reducedLen );
					numString = reducedLen;
				}//inner if
				result.mnemonic = "FTCON";
				result.code = mnemonics.LookupName(result.mnemonic);
				if (index == -1) {
					saveSymbols.AddSymbol(numString, 'c', floatNum);
				}
			}//else if
		}//if
		else
		{
			result.mnemonic = "UNDEF";
			result.code = mnemonics.LookupName(result.mnemonic);
		}
		return result;
	}// returnNUmber method

	private token getString() {
		return returnString();
		
	}// get String method

	private token returnString() 
	{
		token result = new token();
		result.lexeme = ""; 
		currCh = GetNextChar();
		
		while(!((currCh == '"') || (currCh == '\n')))
		{
			result.lexeme = result.lexeme + currCh;
			currCh = GetNextChar();
		}// while
		
		// end the string with close quotation then its valis string
		if (currCh == '"')
		{
			result.mnemonic = "ST_12";
			result.code = mnemonics.LookupName(result.mnemonic);
			currCh = GetNextChar();
			saveSymbols.AddSymbol(result.lexeme, 'c', result.lexeme);
		}// if
		
		//unterminated string
		else if(currCh == '\n')
		{
			System.out.println("Unterminated String");
			result.mnemonic = "UNDEF";
			result.code = mnemonics.LookupName(result.mnemonic);
		}//else if
		
		return result;
	}// returnString method

	private token getOtherToken() {
		return returnOtherToken();
		//return dummyGet();
	}// get Other token method

	// return get other token method
	private token returnOtherToken() {
		token result = new token();
		result.lexeme = result.lexeme + currCh; // have the first char
		//System.out.println("currCh " + currCh);
		char nextChar = PeekNextChar();
		
		
		// assignment
		if(currCh == ':' && nextChar == '=')
		{
			currCh = GetNextChar();
			result.lexeme = result.lexeme + currCh;
			result.code = getCode(result.lexeme);
			result.mnemonic = mnemonics.LookupCode(result.code);
			currCh = GetNextChar();
		}//if
		
		else if(currCh == '>' && nextChar == '=')
		{
			currCh = GetNextChar();
			result.lexeme = result.lexeme + currCh;
			result.code = getCode(result.lexeme);
			result.mnemonic = mnemonics.LookupCode(result.code);
			currCh = GetNextChar();
		}//else if
		
		else if(currCh == '<' && nextChar == '=')
		{
			currCh = GetNextChar();
			result.lexeme = result.lexeme + currCh;
			result.code = getCode(result.lexeme);
			result.mnemonic = mnemonics.LookupCode(result.code);
			currCh = GetNextChar();
		}//else if
		else if(currCh == '<' && nextChar == '>')
		{
			currCh = GetNextChar();
			result.lexeme = result.lexeme + currCh;
			result.code = getCode(result.lexeme);
			result.mnemonic = mnemonics.LookupCode(result.code);
			currCh = GetNextChar();
		}//else if
		
		else
		{
			int code;
			switch(currCh)
			{
			//Colon
				case ':':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '.':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case ']':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '[':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case ',':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '>':
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '<':
										
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '=':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '-':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case ';':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '(':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case ')':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				
				case '+':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '*':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				case '/':
					
					result.code = getCode(result.lexeme);
					result.mnemonic = mnemonics.LookupCode(result.code);
					
					break;
				
				// UNdefined
				default:
					result.code = getCode(result.lexeme);
					code = result.code;
					if(code == -1)
					{
						
						result.mnemonic = "UNDEF";
						result.code =  mnemonics.LookupName(result.mnemonic);
					}
					else
					{
						result.mnemonic = mnemonics.LookupCode(result.code);
					}// else
					break;
					
				
			}// switch
			currCh = GetNextChar();
		}//else
		
		return result;
	}// returnOtherToken() method

	

	// Checks to see if a string contains a valid DOUBLE
	public boolean doubleOK(String stin) {
		boolean result;
		Double x;
		try {
			x = Double.parseDouble(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}// doubleOk method

	// Checks the input string for a valid INTEGER
	public boolean integerOK(String stin) {
		boolean result;
		int x;
		try {
			x = Integer.parseInt(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}// integerOk method

	public token GetNextToken() {
		token result = new token();
		currCh = skipWhiteSpace();

		if (isLetter(currCh)) { // is identifier
			result = getIdentifier();
		} // if
		else if (isDigit(currCh)) { // is numeric
			result = getNumber();
		} else if (isStringStart(currCh)) { // string literal
			result = getString();
		} // else if
		else // default char checks
		{
			result = getOtherToken();
		} // else

		if ((result.lexeme.equals("")) || (EOF)) {
			result = null;
		} // if
			// set the mnemonic
		if (result != null) {
			// THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP
			// result.mnemonic = mnemonics.LookupCode(result.code);
			if (printToken) {
				System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t"
						+ result.lexeme);
			} // inner if
		} // outer if
		return result;

	}// GetNextToken() method

}// Lexical class
