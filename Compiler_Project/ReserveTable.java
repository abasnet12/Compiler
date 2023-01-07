import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ReserveTable 
{
	private int elementsInUse ;
	private int index_num;
	private Element[] list_Of_Elements;
	
	ReserveTable(int maxSize)
	{
		list_Of_Elements = new Element[maxSize];
		elementsInUse = 0;
		index_num = 0;
	}// constructor
	
	int Add(String name, int code)
	{
		
		list_Of_Elements[index_num] = new Element(name,code);
		index_num++;
		elementsInUse++;
		
		return index_num;
	}// Add method
	
	int LookupName(String name)
	{
		int num = -1;
		for(int i = 0; i < elementsInUse ; i++)
		{
			if( name.compareToIgnoreCase(list_Of_Elements[i].getName()) == 0)
			{
				num = list_Of_Elements[i].getCode();
			}// if
		}// for loop
		return num;
	}// LookupName method
	
	String LookupCode(int code)
	{
		String name = "";
		for(int i = 0; i < elementsInUse ; i++)
		{
			if( code == list_Of_Elements[i].getCode())
			{
				name = list_Of_Elements[i].getName();
			}// if
		}// for loop
		
		return name;
	}//LookupCode method
	
	void PrintReserveTable(String filename)
	{
		 try {
	            FileOutputStream outputStream = new FileOutputStream(filename);
//	            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
	            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
	            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
	            String header = String.format("%1$s %2$12s %3$10s \n" , "Index" , "Name" ,"Code" );
	            bufferedWriter.write(header);
	           
	            for(int i = 0; i < elementsInUse ; i++)
	            {
	            	String line = String.format("%1$3d %2$-10s %3$-9s %4$3d \n" , i ," ", list_Of_Elements[i].getName().toUpperCase() ,list_Of_Elements[i].getCode() );
	            	bufferedWriter.write(line);
	            }// for loop
	            //String myNumberString = pad(pad(Integer.toString(index),3,true),8,false)
	            
	            bufferedWriter.newLine();
	            bufferedWriter.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }    
	}// PrintReserveTable method
	
	//internal class Elements
	class Element
	{
		private String name;
		private int code;
		
		Element(String name, int code)
		{
			this.name = name;
			this.code = code;
		}// constructor
		
		String getName()
		{
			return name;
		}// getName method
		
		int getCode()
		{
			return code;
		}// getCode method
		
	}// inner class Element
	
}// ReserveTable class
