import java.io.IOException;

public class Syntactic {

	private String filein; // The full file path to input file
	private SymbolTable symbolList; // Symbol table storing ident/const
	private QuadTable quads;
	private Interpreter interp;
	private Lexical lex; // Lexical analyzer
	private Lexical.token token; // Next Token retrieved
	private boolean traceon; // Controls tracing mode
	private int level = 0; // Controls indent for trace mode
	private boolean anyErrors; // Set TRUE if an error happens
	private static String previousToken = ""; // if the prev token and curr token are identifier, no longer a simple
												// expression, so get out of simple expression loop

	private boolean handleAssign_For_Loop = false; // handle the assiggment and read the token if not in for loop
	private static int countParenthesis = 0; // keep track of parenthesis
	private final int symbolSize = 250;
	private final int quadSize = 1000;
	private int Minus1Index;
	private int Plus1Index;
	private static int tempVarCount = 0;

	public Syntactic(String filename, boolean traceOn) {
		filein = filename;
		traceon = traceOn;
		symbolList = new SymbolTable(symbolSize);
		Minus1Index = symbolList.AddSymbol("-1", 'C', -1);
		Plus1Index = symbolList.AddSymbol("1", 'C', 1);

		quads = new QuadTable(quadSize);
		interp = new Interpreter();

		lex = new Lexical(filein, symbolList, true);
		lex.setPrintToken(traceOn);
		anyErrors = false;
	}

	// The interface to the syntax analyzer, initiates parsing
	// Uses variable RECUR to get return values throughout the non-terminal methods
	public void parse() throws IOException {
		// Use source filename as pattern for symbol table and quad table output later
		String filenameBase = filein.substring(0, filein.length() - 4);
		System.out.println(filenameBase);

		int recur = 0;
		// prime the pump to get the first token to process
		token = lex.GetNextToken();
		// call PROGRAM
		recur = Program();
		// Done with recursion, so add the final STOP quad
		quads.AddQuad(interp.opcodeFor("STOP"), 0, 0, 0);

		// Print SymbolTable, QuadTable before execute
		symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
		quads.PrintQuadTable(filenameBase + "QUADS.txt");
		// interpret
		if (!anyErrors) {
			interp.InterpretQuads(quads, symbolList, false, filenameBase + "TRACE.txt");
		} else {
			System.out.println("Errors, unable to run program.");
		}
		symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
	}

	// Non Terminal PROGIDENTIFIER is fully implemented here, leave it as-is.
	private int ProgIdentifier() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		// This non-term is used to uniquely mark the program identifier
		if (token.code == lex.codeFor("IDENT")) {
			// Because this is the progIdentifier, it will get a 'P' type to prevent re-use
			// as a var
			symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
			// move on
			token = lex.GetNextToken();
		}
		return recur;
	}

	// Non Terminal PROGRAM is fully implemented here.
	private int Program() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Program", true);
		if (token.code == lex.codeFor("UNIT_")) {
			token = lex.GetNextToken();
			recur = ProgIdentifier();
			if (token.code == lex.codeFor("SEMCO")) {
				token = lex.GetNextToken();
				recur = Block();

				if (token.code == lex.codeFor("PERIO")) {

					if (!anyErrors) {

						System.out.println("Success.");
					} else {
						System.out.println("Compilation failed.");
					}
				} else {
					error(lex.reserveFor("PERIO"), token.lexeme);
				}
			} else {
				error(lex.reserveFor("SEMCO"), token.lexeme);
			}
		} else {
			error(lex.reserveFor("UNIT_"), token.lexeme);
		}
		trace("Program", false);
		return recur;
	}

	// Non Terminal BLOCK is fully implemented here.
	private int Block() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Block", true);

		// contains optional label declaration || 0 or more variable declaration
		while (token.code == lex.codeFor("VAR_1") || token.code == lex.codeFor("LABEL")) {
			recur = declaration();

		} // while loop

		recur = blockBody();

		trace("Block", false);
		return recur;
	}// Block

	// Non Terminal <block-body> is defined by $BEGIN <statement> {$SCOLN
	// <statement>} $END
	private int blockBody() {
		int recur = 0;

		if (anyErrors) {
			return -1;
		}

		trace("block-Body", true);
		if (token.code == lex.codeFor("BEGIN")) {

			token = lex.GetNextToken();

			recur = Statement();

			while ((token.code == lex.codeFor("SEMCO")) && (!lex.EOF()) && (!anyErrors)) {
				token = lex.GetNextToken();

				recur = Statement();

			}
			// recur = Statement();

			if (token.code == lex.codeFor("END_1")) {
				token = lex.GetNextToken();
				anyErrors = false;

			} else {
				error(lex.reserveFor("END_1"), token.lexeme);
			}
			// if anyErrors is true then handle errors and resynch
			if ((!lex.EOF()) && (anyErrors)) {

				// when there is anyerrors in the statement, handle the errors and resynch to
				// the next valid statements
				recur = handleErrors();

			} // if

		} else {
			error(lex.reserveFor("BEGIN"), token.lexeme);
		}

		trace("block-Body", false);
		return recur;
	}// blockBody() method

	// contains optional label declaration || 0 or more variable declaration
	private int declaration() {
		int recur = 0;

		if (anyErrors) {
			return -1;
		}

		trace("Declaration", true);

		// non terminal <variable-dec-sec> defined by $VAR <variable-declaration>
		if (token.code == lex.codeFor("VAR_1")) {
			token = lex.GetNextToken();
			recur = var_Dec();

		} // if

		// non terminal <variable-dec-sec> defined by $LABEL declaration
		// <variable-declaration>
		else if (token.code == lex.codeFor("LABEL")) {
			handleErrors();
		} else {
			error("VAR_1 or LABEL", token.lexeme);
		}

		trace("Declaration", false);
		return recur;
	}// declaration function

	private int var_Dec() {
		int recur = 0;

		if (anyErrors) {
			return -1;
		}

		trace("Variable Declaration", true);
		// not terminal <variable-declaration> is defined by {<identifier> {$COMMA
		// <identifier>}*
		// $COLON <simple type> $SEMICOLON}+
		if (token.code == lex.codeFor("IDENT")) {

			// add the unique identifier into symbol list
			recur = addToSymbolList();
			if (token.code == lex.codeFor("COLON")) {
				token = lex.GetNextToken();
				recur = simpleType();
			} //
			else {
				error("COLON for var-declaration", token.lexeme);
			} // else

		} // outer if
		else {
			error("IDENT for var-declaration", token.lexeme);
		} // outer else

		trace("Variable Declaration", false);
		return recur;

	}// var_Dec method

	//// not terminal <simpleType> is defined by $INTEGER | $FLOAT | $STRING
	private int simpleType() {
		int recur = 0;

		if (anyErrors) {
			return -1;
		}
		trace("Simple Type", true);
		// if token is INteger,float or String
		if (token.code == lex.codeFor("INTEG") || token.code == lex.codeFor("FLOAT")
				|| token.code == lex.codeFor("STING")) {
			token = lex.GetNextToken();

			// if the var declaration ends with semi colon
			if (token.code == lex.codeFor("SEMCO")) {
				token = lex.GetNextToken();
			} else
				error("SEMCO at end of var-declaration", token.lexeme);
		} // if
		else {
			error("Integer,float or string for var-declaration", token.lexeme);
		} /// else

		trace("Simple Type", false);
		return recur;
	}

	// add unique identifier to symbol list
	private int addToSymbolList() {
		int recur = 0;
		recur = Variable();
		// {<identifier> {$COMMA <identifier>}*
		while (token.code == lex.codeFor("COMMA")) {
			token = lex.GetNextToken();
			recur = Variable();

		} // if

		return recur;
	}// addToSymbolList

	// if there is any errors in the statement, handle errors and resynch
	private int handleErrors() {

		int recur = 0;
		// set it to false, so
		anyErrors = false;

		// to resynch the statement
		// unless valid token for the statement is found, continue reading the token
		while (!(is_token_a_statement_start(token)) && (!lex.EOF())) {

			token = lex.GetNextToken();

		} // while

		// if the start of statement is valid IF/ELSE, FOR ...... then resysnch and
		// continue
		while ((!lex.EOF()) && (!anyErrors)) {

			recur = Statement();
			while (!(is_token_a_statement_start(token)) && (!lex.EOF())) {

				// read the token until period token is read.
				if (token.code != lex.codeFor("PERIO")) {
					token = lex.GetNextToken();
				}
				// when handling error and at the end with token '.'
				else {
					anyErrors = true;
					return -1;
				}

			} // while
		} // while

		return recur;
	}// handleErrors() method

	// check whether the token is If/ELSE, FOR, DOWHILE, READLN, REPEAT/UNTIL,
	// WRITELN
	private boolean is_token_a_statement_start(Lexical.token token) {
		boolean isValid = false;
		String currentToken = "";
		if ((!lex.EOF())) {
			currentToken = token.mnemonic;
		}

		// if valid token, then start of statment is valid
		switch (currentToken) {
		case "IF_12":
			isValid = true;
			break;
		case "FOR_1":
			isValid = true;
			break;
		case "WRTLN":
			isValid = true;
			break;
		case "REDLN":
			isValid = true;
			break;
		case "DOWHL":
			isValid = true;
			break;
		case "REPET":
			isValid = true;
			break;
		default:
			isValid = false;
		}// switch statement
		return isValid;
	}// is_token_a_statement_start method

	// Not a NT, but used to shorten Statement code body for readability.
	// <variable> $COLON-EQUALS <simple expression>
	private int handleAssignment() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("handleAssignment", true);
		// handleAssign_For_Loop = false;
		// have ident already in order to get to here, handle as Variable
		recur = Variable(); // Variable moves ahead, next token ready

		if (token.code == lex.codeFor("ASSIG")) {
			previousToken = "ASSIG";
			token = lex.GetNextToken();
			recur = SimpleExpression();
		} else {
			error(lex.reserveFor("ASSIG"), token.lexeme);
		}

		trace("handleAssignment", false);
		return recur;
	}

	// NT This is dummied in to only work for an identifier.
	// It will work with the SyntaxAMiniTest file having ASSIGNMENT statements
	// containing only IDENTIFIERS. TERM and FACTOR and numbers will be
	// needed to complete Part A.
	// SimpleExpression MUST BE
	// COMPLETED TO IMPLEMENT CFG for <simple expression>
	private int SimpleExpression() {
		int left, right, temp, opcode;
		int signval = 0;
		if (anyErrors) {
			return -1;
		}
		trace("SimpleExpression", true);

		// if simple exp starts with optional + or -
		if (previousToken.equals("ASSIG")
				&& (token.code == lex.codeFor("ADDTN") || token.code == lex.codeFor("SUBTN"))) {
			// returns -1 if neg, otherwise 1
			signval = sign();
		} // if

		// index of left term in the simple expression
		left = term();

		// Add a negation quad
		if (signval == -1) { // Add a negation quad
			quads.AddQuad(interp.opcodeFor("MUL"), left, Minus1Index, left);
		} // if

		// if <simle exp> have {<add> <term> }*
		while (token.code == lex.codeFor("ADDTN") || token.code == lex.codeFor("SUBTN")) {
			// if +
			if (token.code == lex.codeFor("ADDTN")) {
				opcode = interp.opcodeFor("ADD");

			} else {
				opcode = interp.opcodeFor("SUB");
			}

			token = lex.GetNextToken();
			// get the index for right side of expression
			right = term();

			temp = genSymbol(); // index of new temp variable
			quads.AddQuad(opcode, left, right, temp);
			left = temp; // new leftmost term is last result
			// recur = term();
		} // if
		if (token.code == lex.codeFor("CPARN")) {

			trace("SimpleExpression", false);
			token = lex.GetNextToken();
			return left;
		}

		if ((token.code == lex.codeFor("SEMCO") && countParenthesis == 0) || token.code == lex.codeFor("END_1")) {
			trace("SimpleExpression", false);
			return left;
		} else if (token.code == lex.codeFor("SEMCO") && countParenthesis != 0) {
			anyErrors = true;

		} // else if

		else if (anyErrors == false && (!lex.EOF()) && handleAssign_For_Loop == false) {
			// token = lex.GetNextToken();

		}
		trace("SimpleExpression", false);
		return left;
	}// SimpleExpression()

	// return index of specialy-named temp variable
	private int genSymbol() {
		int num = -1;

		// convertinginteger to string to match the output @1, @0, @2 etc
		String count = Integer.toString(tempVarCount);
		String tempVar = "@" + count;

		symbolList.AddSymbol(tempVar, 'V', 0);

		// return the index of new named temp vriable
		num = symbolList.LookupSymbol(tempVar);
		tempVarCount++;
		return num;
	}

	// when the simple expression starts with + or -
	private int sign() {
		int recur = 1;
		if (anyErrors) {
			return -1;
		}

		if (token.code == lex.codeFor("SUBTN")) {
			recur = -1;
			token = lex.GetNextToken();
		}

		else if (token.code == lex.codeFor("ADDTN")) {
			token = lex.GetNextToken();
		}
		return recur;
	}// sign function

	// non terminal <term> defined by <factor> {<mulop> <factor> }*
	private int term() {
		int opcode, left, right, temp;
		if (anyErrors) {
			return -1;
		}
		trace("Term", true);
		// index of left factor in the term
		left = factor();
		;

		// if term have {{<mulop> <factor>}*
		while (token.code == lex.codeFor("ASTRK") || token.code == lex.codeFor("FSLAS")) {
			// recur = mulOp();

			// if *
			if (token.code == lex.codeFor("ASTRK")) {
				opcode = interp.opcodeFor("MUL");

			} else {
				opcode = interp.opcodeFor("DIV");
			}
			token = lex.GetNextToken();
			// get the index for right side of expression
			right = factor();
			temp = genSymbol();
			quads.AddQuad(opcode, left, right, temp);
			left = temp;

		} // if
		trace("Term", false);
		return left;
	}// term method

	// non terminal <factor> defined by <unsigned constant> <variable> | $LPAR
	// <simple expression> $RPAR
	private int factor() {
		int recur = 0;

		trace("Factor", true);

		if (anyErrors) {

			trace("Factor", false);
			return -1;
		}

		// unisgned constant
		if (token.code == lex.codeFor("INCON") || token.code == lex.codeFor("FTCON")) {
			recur = unsignedConstant();
		}

		// idetifier
		else if (token.code == lex.codeFor("IDENT")) {
			recur = Variable();
		} // else if

		// if left parenthesis '(' , there is simple expression inside
		else if (token.code == lex.codeFor("OPARN")) {
			countParenthesis++;
			token = lex.GetNextToken();
			recur = SimpleExpression();
		} else {
			error("Number, Variable or 'C' ", token.lexeme);
		}

		trace("Factor", false);

		// if expression ends with parenthesis
		if (token.code == lex.codeFor("CPARN")) {

			// keep track of parenthesis
			countParenthesis--;

			// statement starts with ')' or invalid '('
			if (countParenthesis < 0) {
				anyErrors = true;
			} // if

			return recur;
		} // else if

		return recur;
	}// factor method

	// non terminal <Unsigned Constant> defined by <unsigned number>
	private int unsignedConstant() {
		trace("Unsigned Constant", true);
		int result = -1;
		result = unsignedNumber();
		if (anyErrors) {
			return -1;
		}
		trace("Unsigned Constant", false);
		return result;
	}// unsignedConstant method

	// non terminal <Unsigned Number> defined by $FLOAT | $INTEGER
	private int unsignedNumber() {

		trace("UnsignedNumber", true);
		int result = symbolList.LookupSymbol(token.lexeme);
		token = lex.GetNextToken();
		trace("UnsignedNumber", false);
		return result;

	}// unsignedNumber method

	// Eventually this will handle all possible statement starts in
	// a nested if/else structure. Only ASSIGNMENT is implemented now.
	private int Statement() {

		int left, right; // indices returned for left and right
							// side vars of the assignment stmt
		int saveTop, branchQuad;
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Statement", true);

		// for assignment
		if (token.code == lex.codeFor("IDENT")) { // must be an ASSIGNMENT
			left = Variable(); // returns var’s symbol table index
			// Handle Assignment
			if (token.code == lex.codeFor("ASSIG")) {
				token = lex.GetNextToken();
				right = SimpleExpression(); // get result index
				quads.AddQuad(interp.opcodeFor("MOV"), right, 0, left);
			} else {
				error("ASSIGN", token.lexeme);
			}
		} // if

		// if statments begins with if
		else if (token.code == lex.codeFor("IF_12")) {

			token = lex.GetNextToken();
			recur = relExpression();
			// look for Then reserve word when if statement is invoked
			if (token.code == lex.codeFor("THEN_")) {
				token = lex.GetNextToken();
				recur = Statement();
				// token = lex.GetNextToken();
				if (token.code == lex.codeFor("ELSE_")) {
					token = lex.GetNextToken();

					recur = Statement();
				}
			} // if
				// verify if statement that has else;

			else {
				error("THEN", token.lexeme);
			} // error if token is not 'THEN'

		} // else if

		// if statments begins with for
		else if (token.code == lex.codeFor("FOR_1")) {

			handleAssign_For_Loop = true;
			token = lex.GetNextToken();

			// if variable is undeclared and not in symbol table, Print and move on
			if (symbolList.LookupSymbol(token.lexeme) != -1) {
				System.out.println("UNDECLARED Variable " + token.lexeme + " (NOT DECLARED AT THE TOP)");
			}
			recur = Variable();

			if (token.code == lex.codeFor("ASSIG")) {
				token = lex.GetNextToken();
				recur = SimpleExpression();
			}

			// token 'TO' expecter in for loop
			if (token.code == lex.codeFor("TO_12")) {
				token = lex.GetNextToken();
				recur = SimpleExpression();

				// expected token 'DO' in for loop
				if (token.code == lex.codeFor("DO_12")) {
					token = lex.GetNextToken();
					recur = Statement();
				} // if
				else
					error("DO", token.lexeme);
			} // if
			else
				error("TO", token.lexeme);

		} // else if

		// block-body
		else if (token.code == lex.codeFor("BEGIN") || token.code == lex.codeFor("END_1")) {

			if (token.code == lex.codeFor("END_1")) {

				anyErrors = false;
			} else {
				recur = blockBody();
			}
		} // else if

		// DOWHILE
		else if (token.code == lex.codeFor("DOWHL")) {
			trace("DOWHILE", true);

			token = lex.GetNextToken();
			saveTop = quads.NextQuad(); // Before generating code, save top of loop
										// where unconditional branch will jump
			branchQuad = relExpression(); // tells where branchTarget to be set
			recur = Statement(); // the loop body is processed
			// quads.AddQuad(interp.opcodeFor("JMP"), 0, 0, saveTop);
			// quads.UpdateJump(branchQuad, quads.NextQuad());
			trace("DOWHILE", false);
		} // else if

		// WriteLn
		else if (token.code == lex.codeFor("WRTLN")) {
			recur = handleWriteln();
		} // else if

		// Readln
		else if (token.code == lex.codeFor("REDLN")) {
			recur = handleReadln();
		}

		// repeat and until
		else if (token.code == lex.codeFor("REPET")) {
			handleAssign_For_Loop = true;
			token = lex.GetNextToken();
			recur = Statement();
			if (token.code == lex.codeFor("UNTIL")) {
				token = lex.GetNextToken();
				recur = relExpression();
			} else
				error("UNTIL", token.lexeme);
		} else {
			error("Statement start", token.lexeme);
			anyErrors = true;
			while (!(is_token_a_statement_start(token)) && (!lex.EOF())) {

				token = lex.GetNextToken();
			} // while

			// recur = Statement();
		} // else

		trace("Statement", false);
		return recur;
	}

	// read it from the console
	private int handleReadln() {
		int recur = 0;
		int toprint = 0;
		if (anyErrors) {
			return -1;
		}
		trace("handleReadln", true);
		token = lex.GetNextToken();
		// token must be '(' after WRITELN
		if (token.code == lex.codeFor("OPARN")) {
			token = lex.GetNextToken();

			// return the index of symbol table
			toprint = symbolList.LookupSymbol(token.lexeme);
			// add to quad table
			quads.AddQuad(interp.opcodeFor("READ"), 0, 0, toprint);
			recur = Variable();
			token = lex.GetNextToken();

		} // if
		else {
			error("(", token.lexeme);
		}
		trace("handleReadln", false);
		return recur;
	}// handleReadln function

	// write it to the console
	private int handleWriteln() {
		int recur = 0;
		int toprint = 0;
		if (anyErrors) {
			return -1;
		}

		trace("handleWriteln", true);
		// got here from a WRITELN token, move past it...
		token = lex.GetNextToken();
		// look for ( stringconst, ident, or simpleexp )
		if (token.code == lex.codeFor("OPARN")) {
			// move on
			token = lex.GetNextToken();
			if ((token.code == lex.codeFor("ST_12")) || (token.code == lex.codeFor("IDENT"))) {
				// save index for string literal or identifier
				toprint = symbolList.LookupSymbol(token.lexeme);
				// move on
				token = lex.GetNextToken();
			} else {
				toprint = SimpleExpression();
			}

			// add to quad table
			quads.AddQuad(interp.opcodeFor("PRINT"), 0, 0, toprint);
			// now need right ")"
			if (token.code == lex.codeFor("CPARN")) {
				// move on
				token = lex.GetNextToken();
			} else {
				error(lex.reserveFor("CPARN"), token.lexeme);
			}
		} else {
			error(lex.reserveFor("OPARN"), token.lexeme);
		}
		// end lpar group

		trace("handleWriteln", false);
		return recur;

	}

	private int relExpression() {
		int left, right, saveRelop, result, temp;
		if (anyErrors) {
			return -1;
		}
		trace("Rel Expression", true);
		// token = lex.GetNextToken();
		// token = lex.GetNextToken();
		left = SimpleExpression(); // get the left operand, our ‘A’

		// if the token is valid relop
		if ((is_relop(token))) {
			saveRelop = relop(); // returns tokenCode of rel operator
			right = SimpleExpression(); // right operand, our ‘B’
			temp = genSymbol(); // Create temp var in symbol table
			quads.AddQuad(interp.opcodeFor("SUB"), left, right, temp); // compare
			result = quads.NextQuad(); // Save Q index where branch will be
			quads.AddQuad(relopToOpcode(saveRelop), temp, 0, 0); // target set later

		} else
			error("relop", token.lexeme);

		trace("Rel Expression", false);
		return left;
	}

	// return the Opcode for relop
	private int relop() {
		int result = -1;
		// return the Opcode for relop
		result = lex.codeFor(token.mnemonic);
		token = lex.GetNextToken();
		return result;
	}// relop

	// verify if its valid relop in rel expression
	private boolean is_relop(Lexical.token token2) {
		boolean isValid = false;
		String currentToken = "";
		currentToken = token2.mnemonic;

		// if valid token, then relop is valid
		switch (currentToken) {
		case "EQUAL":
			isValid = true;
			break;
		case "LSTHN":
			isValid = true;
			break;
		case "GRTHN":
			isValid = true;
			break;
		case "NTEQL":
			isValid = true;
			break;
		case "LTEQL":
			isValid = true;
			break;
		case "GTEQL":
			isValid = true;
			break;
		default:
			isValid = false;
		}// switch statement

		return isValid;
	}

	// Non-terminal VARIABLE just looks for an IDENTIFIER. Later, a
	// type-check can verify compatible math ops, or if casting is required.
	private int Variable() {
		int result = -1;
		result = identifier(); // NOTE: identifier() does not move past
								// the token, returns S.T. index, so
								// that token could be type checked here
		if (anyErrors) {
			return -1;
		}

		trace("Variable", true);
		if ((token.code == lex.codeFor("IDENT"))) {
			// bookkeeping and move on
			token = lex.GetNextToken();
			previousToken = token.mnemonic;
		} else
			error("Variable", token.lexeme);

		trace("Variable", false);
		return result;

	}

	// return the false branch
	int relopToOpcode(int relop) {
		int result = -1;
		switch (relop) {
		// Greater Than
		case 38:
			result = interp.opcodeFor("JNP");
			// less than
		case 39:
			result = interp.opcodeFor("JNN");
			// Greater than or Equal
		case 40:
			result = interp.opcodeFor("JN");
			// Less Than or Equal
		case 41:
			result = interp.opcodeFor("JP");
			// equal
		case 42:
			result = interp.opcodeFor("JNZ");
			// Not equal
		case 43:
			result = interp.opcodeFor("JZ");
		}// switch statement
		return result;
	}// relopToOpcode

	// return the index on symbol table
	private int identifier() {
		int result = symbolList.LookupSymbol(token.lexeme);
		return result;
	}// identifier

	/**
	 * *************************************************
	 */
	/* UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
	// error provides a simple way to print an error statement to standard output
	// and avoid reduncancy
	private void error(String wanted, String got) {
		anyErrors = true;
		System.out.println("ERROR: Expected " + wanted + " but found " + got);
	}

	// trace simply RETURNs if traceon is false; otherwise, it prints an
	// ENTERING or EXITING message using the proc string
	private void trace(String proc, boolean enter) {
		String tabs = "";

		if (!traceon) {
			return;
		}

		if (enter) {
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("--> Entering " + proc);
			level++;
		} else {
			if (level > 0) {
				level--;
			}
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("<-- Exiting " + proc);

		}
	}

	// repeatChar returns a string containing x repetitions of string s;
	// nice for making a varying indent format
	private String repeatChar(String s, int x) {
		int i;
		String result = "";
		for (i = 1; i <= x; i++) {
			result = result + s;
		}
		return result;
	}

	/*
	 * Template for all the non-terminal method bodies // ALL OF THEM SHOULD LOOK
	 * LIKE THE FOLLOWING AT THE ENTRY/EXIT POINTS private int exampleNonTerminal(){
	 * int recur = 0; //Return value used later if (anyErrors) { // Error check for
	 * fast exit, error status -1 return -1; }
	 * 
	 * trace("NameOfThisMethod", true);
	 * 
	 * // The unique non-terminal stuff goes here, assigning to "recur" based // on
	 * recursive calls that were made
	 * 
	 * trace("NameOfThisMethod", false); // Final result of assigning to "recur" in
	 * the body is returned return recur;
	 * 
	 * }
	 * 
	 */
}// Syntactic class
