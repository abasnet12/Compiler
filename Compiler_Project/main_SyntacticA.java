import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class main_SyntacticA {

	public static void main(String[] args) throws IOException {
		String filePath = args[0];
        boolean traceon = true;
        //PrintStream out = new PrintStream(new FileOutputStream("BadOutputSyntax_Ambar.txt"));
        //System.setOut(out);
		
		System.out.println("Syntactic for " + filePath);
        System.out.println("Ambar Basnet, 959336, CS4100/5100, FALL 2022");
        System.out.println("INPUT FILE TO PROCESS IS: "+filePath);
    
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");

	}// main

}//main_SyntacticA class 
