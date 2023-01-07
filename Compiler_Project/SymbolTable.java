import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SymbolTable 
{
	private static int totalRow ;
	private DiffElements[] symbol_Table;
	
	SymbolTable(int maxSize)
	{
		symbol_Table = new DiffElements[maxSize];
		totalRow = 0;
	}// Constructor
	
	public int AddSymbol(String symbol , char usage , int value)
	{
		symbol_Table[totalRow] = new DiffElements(symbol,usage,value);
		totalRow++;
		
		return value;
	}// AddSymbol method for int
	
	public int AddSymbol(String symbol , char usage , double value)
	{
		symbol_Table[totalRow] = new DiffElements(symbol,usage,value);
		totalRow++;
		return 0;
	}// AddSymbol method for double
	
	public int AddSymbol(String symbol , char usage , String value)
	{
		symbol_Table[totalRow] = new DiffElements(symbol,usage,value);
		totalRow++;
		return 0;
	}// AddSymbol method for double
	
	public int LookupSymbol( String symbol)
	{
		int index = -1;
		
		for (int i = 0; i < totalRow ; i++)
		{
			if (symbol.equalsIgnoreCase(symbol_Table[i].getSymbol()))
			{
				index = i;
				i = totalRow;
			}
		}// for loop
		
		return index;
	}// Lookup Symbol
	
	public String GetSymbol(int index)
	{
		String symbol = symbol_Table[index].getSymbol();
		return symbol;
	}// Get Symbol method
	
	public char GetUsage(int index)
	{
		char usage = symbol_Table[index].getUsage();
		return usage;
		
	}// get usage method 
	
	public char GetDataType(int index)
	{
		char dataType = symbol_Table[index].GetDataType();
		return dataType;
		
	}// get usage method
	
	public String GetString( int index)
	{
		String value = symbol_Table[index].GetValue();
		
		return value;
	}// Get string method
	
	public int GetInteger( int index)
	{
		int value = Integer.parseInt(symbol_Table[index].GetValue());
		return value;
	}// Get Integer method
	
	public double GetFloat( int index)
	{
		double value = Float.parseFloat(symbol_Table[index].GetValue())   ;
		return value;
	}// Get Float method
	
	public void UpdateSymbol(int index, char usage, int value)
	{
		symbol_Table[index].UpdateChar(usage);
		symbol_Table[index].UpdateValue(value);
	}// update symbol for usage or int value 
	
	public void UpdateSymbol(int index, char usage, double value)
	{
		symbol_Table[index].UpdateChar(usage);
		symbol_Table[index].UpdateValue(value);
	}// update symbol for usage or double value
	
	public void UpdateSymbol(int index, char usage, String value)
	{
		symbol_Table[index].UpdateChar(usage);
		symbol_Table[index].UpdateValue(value);
	}// update symbol for usage or String value
	
	// internal class
	class DiffElements
	{
		private String symbol;
		private char usage;
		private char dataType;
		private String value;
		
		DiffElements(String symbol , char usage , int value)
		{
			this.symbol = symbol;
			this.usage = usage;
			dataType = 'I';
			this.value = String.valueOf(value);
		}// constructor
		
		DiffElements(String symbol , char usage , double value)
		{
			this.symbol = symbol;
			this.usage = usage;
			dataType = 'F';
			this.value = String.valueOf(value);;
		}// constructor
		
		DiffElements(String symbol , char usage , String value)
		{
			this.symbol = symbol;
			this.usage = usage;
			dataType = 'S';
			this.value = value;
		}// constructor
		 
		String getSymbol()
		{			
			return symbol;
		}// get symbol method
		
		char getUsage()
		{
			return usage;
		}// get usage method
		
		char GetDataType()
		{
			return dataType;
		}// get Data type  method
		
		String GetValue()
		{
			return value ;
		}// Get string method
		
		
		void UpdateChar(char usage)
		{
			this.usage = usage;
		}// updateSymbol
		
		void UpdateValue(int value)
		{
			this.value = String.valueOf(value);
		}// update integer value
		
		void UpdateValue(double value)
		{
			this.value = String.valueOf(value);
		}// update float value
		
		void UpdateValue(String value)
		{
			this.value = value;
		}// update String value
		
	}// internal class
	
	void PrintSymbolTable(String filename)
	{
		try
		{
			FileOutputStream outputStream = new FileOutputStream(filename);
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
	        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
	        String header = String.format("%1$s %2$7s %3$18s %4$9s %5$9s\n" , "Index" , "Name" ,"Use", "Typ" , "Value" );
	        bufferedWriter.write(header);
	        
	        for (int i = 0 ; i < totalRow ; i++)
	        {
	        	String line = String.format("%1$3d %2$3s %3$-21s %4$-9s %5$-7s %6$-1s\n" , i ," ", symbol_Table[i].getSymbol() ,symbol_Table[i].getUsage(),symbol_Table[i].GetDataType(), symbol_Table[i].GetValue());
	        	bufferedWriter.write(line);
	        }// for loop
	        
	        bufferedWriter.close();
		}catch (IOException e) {
            e.printStackTrace();
        } // catch
	}// PrintSymbolTable  method
	
}// Symbol table class
