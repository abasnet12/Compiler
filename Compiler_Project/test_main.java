import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class test_main {

	public static void main(String[] args) throws FileNotFoundException {
		// Create the table
		
		// Printing the MAIN output into txt file
		//PrintStream out = new PrintStream(new FileOutputStream("Output.txt"));
        //System.setOut(out);
        System.out.println("Ambar Basnet CS4100 Homework 1, Fall 2022");
        ReserveTable reserve = new ReserveTable(25);
        // Add to the table
        reserve.Add("cat", 15);
        reserve.Add("APPLE", 11);
        reserve.Add("Dog", 5);
        reserve.Add("DOnE", 21);
        reserve.Add("Over", 8);
        // Search the table
        
        System.out.println("The Code for 'over' is "+reserve.LookupName("over"));
        System.out.println("The Code for 'DOG' is "+reserve.LookupName("DOG"));
        System.out.println("The Code for 'Cat' is "+reserve.LookupName("Cat"));
        System.out.println("The Code for 'gone' is "+reserve.LookupName("gone"));
        System.out.println();
        System.out.println("The Name for 11 is "+reserve.LookupCode(11));
        System.out.println("The Name for 5 is "+reserve.LookupCode(5));
        System.out.println("The Name for 8 is "+reserve.LookupCode(8));
        System.out.println("The Name for 28 is "+reserve.LookupCode(28));
        
        // Print table to file
        String fileName = "Reserve.txt";
        System.out.println("Saving Printed Table to "+ fileName);
        reserve.PrintReserveTable(fileName);
	}

}
