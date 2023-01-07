import java.io.IOException;

public class main_Interpreter 
{

	public static void main(String[] args) throws IOException 
	{
		
		
        Interpreter interp = new Interpreter();
        SymbolTable st;
        QuadTable qt;
        System.out.println("Ambar Basnet CS4100 Homework 3, Fall 2022");
        
        System.out.println("This program expects command-line parameters for filenames in this order:");
        System.out.println("traceFactorial SymbolFactorial QuadFactorial traceSum  SymbolSum QuadSum");
        
        // interpretation FACTORIAL
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        interp.initializeFactorialTest(st,qt);  
        interp.InterpretQuads(qt, st, true, args[0]);
        st.PrintSymbolTable(args[1]);
        qt.PrintQuadTable(args[2]);
        
        // interpretation SUMMATION
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        interp.initializeSummationTest(st,qt);  //Set up for SUMMATION
        interp.InterpretQuads(qt, st, true, args[3]);
        st.PrintSymbolTable(args[4]);
        qt.PrintQuadTable(args[5]);

	}// main method

}//main interpreter method
