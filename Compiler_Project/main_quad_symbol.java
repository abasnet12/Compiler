import java.io.FileOutputStream;
import java.io.PrintStream;

public class main_quad_symbol 
{

	public static void main(String[] args) throws Exception 
	{
		// Create the tables
		SymbolTable symbols = new SymbolTable(25);
		QuadTable quads = new QuadTable(50);
		int index;
		int[] quadRow = new int[4];
		
		PrintStream out = new PrintStream(new FileOutputStream("Output1.txt"));
        System.setOut(out);
		//Required student name header
		System.out.println("Ambar Basnet CS4100 Homework 2, Fall 2022");
		System.out.println();
		//System.out.println("Testing the Quad Table\n");
		System.out.println("At the start, NextQuad is: "+ quads.NextQuad());
		
		quads.AddQuad(4,3,2,1);
		
		System.out.println("After one add, NextQuad is: "+ quads.NextQuad());
		quads.AddQuad(1,2,3,4);
		quads.AddQuad(2,2,2,2);
		quads.AddQuad(0,0,0,0);
		quads.AddQuad(1,3,5,9);
		quadRow = quads.GetQuad(4);
		
		System.out.println("Quad row at index 4 is: "+ quadRow[0]+", "+ quadRow[1]+", "+quadRow[2]+", "+quadRow[3]);
		quads.UpdateJump(4,17);
		quadRow = quads.GetQuad(4);
		
		System.out.println("Quad row at index 4 is: "+ quadRow[0]+", "+ quadRow[1]+", "+quadRow[2]+", "+quadRow[3]);
		System.out.println("Finally NextQuad is: "+ quads.NextQuad());
		
		String fileName = "Quad.txt";
		System.out.println("Printing QuadTable to file "+ fileName);
		quads.PrintQuadTable(fileName);
		System.out.println();
		
		System.out.println("Testing the Symbol Table\n");
		
		//Add stuff
		symbols.AddSymbol("TestInt", 'V', 27);
		symbols.AddSymbol("TestDouble", 'V', 42.25);
		symbols.AddSymbol("TestString", 'V', "Nevermind the furthermore...");
		symbols.AddSymbol("135",'C', 135);
		symbols.AddSymbol("3.1415",'C', 3.1415);
		symbols.AddSymbol("Please Enter A Value",'C', "Please Enter A Value");
		
		//Look for stuff
		index = symbols.LookupSymbol("testint");
		System.out.println("testint is located at "+index);
		index = symbols.LookupSymbol("3.1415");
		System.out.println("PI is located at "+index);
		System.out.println("  the KIND for PI is "+symbols.GetUsage(index));
		System.out.println("The KIND for slot 5 is "+symbols.GetUsage(5)+", data type is "+ symbols.GetDataType(5)+" and the value: "+symbols.GetString(5));
		index = symbols.LookupSymbol("BadVal");
		System.out.println("BadVal search returned "+index+'\n');
		
		String fileName1 = "SymbolTable.txt";
		System.out.println("Printing SymbolTable to file "+ fileName1);
		symbols.PrintSymbolTable(fileName1);
		
	}// main method

}// class
