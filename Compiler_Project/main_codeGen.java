import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class main_codeGen {

	public static void main(String[] args) throws IOException {
	    //PrintStream out = new PrintStream(new FileOutputStream("BasicFileOutput_Ambar.txt"));
        //System.setOut(out);
		String filePath = args[0];
        System.out.println("Parsing "+filePath);
        boolean traceon = true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");

	}

}
