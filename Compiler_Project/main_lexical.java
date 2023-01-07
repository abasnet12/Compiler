import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class main_lexical 
{

	public static void main(String[] args) throws FileNotFoundException 
	{
		
		String inFileAndPath = args[0];
		String outFileAndPath = args[1];
		PrintStream out = new PrintStream(new FileOutputStream("OutputLexical_Ambar.txt"));
        System.setOut(out);
		System.out.println("Lexical for " + inFileAndPath);
		boolean traceOn = true;
		
		// Create a symbol table to store appropriate3 symbols found
		SymbolTable symbolList;
		symbolList = new SymbolTable(150);
		Lexical myLexer = new Lexical(inFileAndPath, symbolList, traceOn);
		Lexical.token currToken;
		//LexicalReserve myLexer = new LexicalReserve(inFileAndPath, symbolList, traceOn);
		//LexicalReserve.token currToken;
		currToken = myLexer.GetNextToken();
		
		while (currToken != null) 
		{
			System.out.println("\t" + currToken.mnemonic + " | \t" + String.format("%04d", currToken.code) + " | \t" + currToken.lexeme);
			currToken = myLexer.GetNextToken();
		}// while loop
		symbolList.PrintSymbolTable(outFileAndPath);
		System.out.println("Done.");
		
	}// main method

}// main_lexical class
