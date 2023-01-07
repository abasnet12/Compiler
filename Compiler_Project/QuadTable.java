import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class QuadTable 
{
	private int opcode, op1, op2, op3;
	private static int nextAvailable;
	private int [][] num_in_array;
	
	public QuadTable(int maxSize)
	{
		num_in_array = new int[maxSize][4];
		nextAvailable = 0;
	}// constructor
	
	public int NextQuad()
	{
		int num = nextAvailable;
		
		return num;
	}// nextQuad method
	
	public void AddQuad(int opcode, int op1, int op2, int op3)
	{
		num_in_array[nextAvailable][0] = opcode;
		num_in_array[nextAvailable][1] = op1;
		num_in_array[nextAvailable][2] = op2;
		num_in_array[nextAvailable][3] = op3;
		nextAvailable++;
	}// AddQuad method
	
	public int[] GetQuad(int index)
	{
		return num_in_array[index];
	}// getQuad function
	
	public void UpdateJump( int index, int op3)
	{
		num_in_array[index][3] = op3;
	}// UpdateJump method
	
	void PrintQuadTable(String filename) 
	{
		try
		{
			FileOutputStream outputStream = new FileOutputStream(filename);
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
	        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
	        String header = String.format("%1$s %2$9s %3$6s %4$8s %5$8s\n" , "Index" , "Opcode" ,"Op1", "Op2" , "Op3" );
	        bufferedWriter.write(header);
	        
	        //run the for loop on 2D array
	        for(int i = 0; i < nextAvailable ; i++)
	        {
	        	String index  = new Integer(i).toString();
	        	bufferedWriter.write(index + "     |");
	        	
	        	String value = "";
	        	
	        	for(int j = 0; j < 4 ; j++)
	        	{
	        		String format= "";
	        		value = new Integer(num_in_array[i][j]).toString();
	        		format = String.format("%1$5s %2$3s" ,value, "|"  );
	            	bufferedWriter.write(format);
	        	}
	        	
	        	bufferedWriter.write("\n");
	        	
	        }// for loop
	        bufferedWriter.close();
		}catch (IOException e) {
            e.printStackTrace();
        }    		
	}// PrintQuadTable method
		
}// QuadTable  class
