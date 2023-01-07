import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Interpreter 
{	
	// Reserve table with 16 fixed opcodes
	private static int sizeOfReserveTable = 16;
	ReserveTable reserve;
	// to calculate factorial or sum when InterpretQuads method is called (0 for sum, 1 for factorial)
	static int count = 0;
	
	public Interpreter()
	{
		//initialize Interpreter's reserve table
		reserve= new ReserveTable(sizeOfReserveTable);
		initReserve(reserve);
	}// constructor
	
	private void initReserve(ReserveTable optable) 
	{
		// add string name and code value to reserve table
		optable.Add("STOP", 0);
	    optable.Add("DIV", 1);
	    optable.Add("MUL", 2);
	    optable.Add("SUB", 3);
	    optable.Add("ADD", 4);
	    optable.Add("MOV", 5);
	    optable.Add("PRINT", 6);
	    optable.Add("READ", 7);
	    optable.Add("JMP", 8);
	    optable.Add("JZ", 9);
	    optable.Add("JP", 10);
	    optable.Add("JN", 11);
	    optable.Add("JNZ", 12);
	    optable.Add("JNP", 13);
	    optable.Add("JNN", 14);
	    optable.Add("JINDR", 15);
	}// initReserve method
	public int opcodeFor(String name)
	{	int num = -1;
		switch(name)
		{
			case "STOP":
				num = 0;
			break;
			case "DIV":
				num = 1;
			break;
			case "MUL":
				num = 2;
			break;
			case "SUB":
				num = 3;
			break;
			case "ADD":
				num = 4;
			break;
			case "MOV":
				num = 5;
			break;
			case "PRINT":
				num = 6;
			break;
			case "READ":
				num = 7;
			break;
			case "JMP":
				num = 8;
			break;
			case "JZ":
				num = 9;
			break;
			case "JP":
				num = 10;
			break;
			case "JN":
				num = 11;
			break;
			case "JNZ":
				num = 12;
			break;
			case "JNP":
				num = 13;
			break;
			case "JNN":
				num = 14;
			break;
			case "JINDR":
				num = 15;
			break;
			
					
		}
		return num;
		
	}
	public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable)
	{
		//to add value in symbol table for factorial
		initSTF(stable);
		//to add value in quad table for factorial
		initQTF(qtable);
		return true;
	}// initializeFactorialTest method
	
	public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable)
	{
		//to add value in symbol table for summation
		init_Summation_STF(stable);
		
		//to add value in quad table for summation
		init_Summation_QTF(qtable);
		return true;
	}// initializeSummationTest method
	
	// add symbol table data for factorial
	private void initSTF(SymbolTable stable) 
	{
		//adding symbol on each index of symbol table for factorial
		stable.AddSymbol("n", 'V', 10);
		stable.AddSymbol("i", 'V', 0);
		stable.AddSymbol("product", 'V', 0);
		stable.AddSymbol("1", 'C', 1);
		stable.AddSymbol("$temp", 'V', 0);
		
	}// initQTF method

	// add quad instruction for factorial
	private void initQTF(QuadTable qtable) 
	{
		// adding quad data on each index of quad table for factorial
		qtable.AddQuad(5, 3, 0, 2); //MOV
		qtable.AddQuad(5, 3, 0, 1); //MOV
		qtable.AddQuad(3, 1, 0, 4); //SUB
		qtable.AddQuad(10, 4, 0, 7);//JP
		qtable.AddQuad(2, 2, 1, 2); //MUL
		qtable.AddQuad(4, 1, 3, 1); //ADD
		qtable.AddQuad(8, 0, 0, 2); //JMP
		qtable.AddQuad(6, 0, 0, 2); //PRINT
		qtable.AddQuad(0, 0, 0, 0); //STOP
		
	}//initQTF method
	
	private void init_Summation_STF(SymbolTable stable) 
	{
		//adding symbol on each index of symbol table for summation
		stable.AddSymbol("n", 'V', 10);
		stable.AddSymbol("i", 'V', 0);
		stable.AddSymbol("sum", 'V', 0);
		stable.AddSymbol("1", 'C', 1);
		stable.AddSymbol("$temp", 'V', 0);
		stable.AddSymbol("0", 'C', 0);
	}// init_Summation_STF method
	
	private void init_Summation_QTF(QuadTable qtable) 
	{
		// adding quad data on each index of quad table for summation
		qtable.AddQuad(5, 5, 0, 2); //MOV
		qtable.AddQuad(5, 3, 0, 1); //MOV
		qtable.AddQuad(3, 1, 0, 4); //SUB
		qtable.AddQuad(10, 4, 0, 7);//JP
		qtable.AddQuad(4, 2, 1, 2); //ADD
		qtable.AddQuad(4, 1, 3, 1); //ADD
		qtable.AddQuad(8, 0, 0, 2); //JMP
		qtable.AddQuad(6, 0, 0, 2); //PRINT
		qtable.AddQuad(0, 0, 0, 0); //STOP
	}// init_Summation_QTF

	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) throws IOException
	{
		int pc = 0;
		boolean isTraceOn = TraceOn;
		
		//write the output to file
		FileOutputStream outputStream = new FileOutputStream(filename);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write("Ambar Basnet CS4100 Homework 3, Fall 2022\n");
        
		// Interpret the quad table until it reaches to last instruction or end of quad table
		while(pc < Q.NextQuad())
		{
			
			// returns quad data at index pc to variable array
			int [] array = Q.GetQuad(pc);
			int opcode = array[0];
			int op1 = array[1];
			int op2 = array[2];
			int op3 = array[3];
			
			//translate opcode value to instruction name
			String opCode_Name = reserve.LookupCode(opcode);
			
			//print out the quad instruction
			if(isTraceOn == true)
			{
				System.out.println(makeTraceString(pc, opCode_Name, op1, op2, op3 ));
				
				// write factorial portion to tracefactorial.txt
				if (count == 0)
				{
					bufferedWriter.write(makeTraceString(pc, opCode_Name, op1, op2, op3 ));
					bufferedWriter.write("\n");
				}//inner if
				else
				{
					bufferedWriter.write(makeTraceString(pc, opCode_Name, op1, op2, op3 ));
					bufferedWriter.write("\n");
				}// else
			}// outer if
			
			// assigning values to op1 and op2 variable from the symbol table value
			int op1_Value = S.GetInteger(op1);
			int op2_Value = S.GetInteger(op2);
			
			//interpret the individual opcode 
			switch(opCode_Name)
			{
				case "STOP":
					
					System.out.println("Execution terminated by program STOP.");
					//write to the file
					bufferedWriter.write("Execution terminated by program STOP.\n");
					
					pc = Q.NextQuad();
					
					break;
					
				case "DIV":
										
					//divide op1 value by op2 value
					int div_result = op1_Value / op2_Value;
					
					// assign the div_result to op3 value
					// keep the same usage char
					S.UpdateSymbol(op3,S.GetUsage(op3),div_result);
					pc++;
					
					break;
					
				case "MUL":
					
					// multiply op1 and op2 value
					int mul_result = op1_Value * op2_Value;
					
					// assign the mul_result to op3 value
					// keep the same usage char
					S.UpdateSymbol(op3,S.GetUsage(op3),mul_result);
					pc++;
					
					break;
					
				case "SUB":
					
					// subtract op2 from op1
					int subtractedValue = op1_Value - op2_Value;
					
					//assign subtracted result into op3 value
					// keep the same usage char
					S.UpdateSymbol(op3,S.GetUsage(op3),subtractedValue);
					pc++;
					
					break;
					
				case "ADD":
					
					// add op1 and op2 value
					int added_result = op1_Value + op2_Value;
					
					 
					// move added_result value into op3 value
					// keep the same usage char
					S.UpdateSymbol(op3,S.GetUsage(op3),added_result);
					pc++;
					
					break;
					
				case "MOV":
										
					// move value of op1 into op3
					// update value of ST at index op3
					// keep the same usage char
					S.UpdateSymbol(op3,S.GetUsage(op3),op1_Value);
					pc++;
					
					break;
					
				case "PRINT":
					
					String name = S.GetSymbol(op3);
					String value = S.GetString(op3);
					//bufferedWriter.write(name + "= " + value + "\n");
					System.out.println( value);
					S.UpdateSymbol(op3,S.GetUsage(op3),S.GetString(op3));
					pc++;
					break;
					
				case "READ":
					//read the input integer from keyboard
					Scanner scan = new Scanner(System.in);
					System.out.println("> ");
					
					//Read one integer only 
					int newOp3_value = scan.nextInt();
					
					// update value of ST at index op3
					S.UpdateSymbol(op3,S.GetUsage(op3),newOp3_value);
					scan = null;
					pc++;
					
					break;
					
				case "JMP":
					
					pc = op3;
					break;
					
				case "JZ":
					// if op1 == 0
					if(op1_Value == 0)
					{
						pc = op3;
					}// if
					
					else
					{
						pc++;
					}// else
					
					break;
					
				case "JP":
					
					if(op1_Value > 0)
					{
						pc = op3;
					}//if 
					else
					{
						pc++;
					}//else
					
					break;
					
				case "JN":
					if(op1_Value < 0)
					{
						pc = op3;
					}//if
					else
					{
						pc++;
					}//else
					break;
					
				case "JNZ":
					// if op1 != 0
					if(op1_Value != 0)
					{
						pc = op3;
					}// if
					
					else
					{
						pc++;
					}// else
					break;
					
				case "JNP":
					// if op1 <= 0
					if(op1_Value <= 0)
					{
						pc = op3;
					}// if
					
					else
					{
						pc++;
					}// else
					break;
					
				case "JNN":
					// if op1 <= 0
					if(op1_Value >= 0)
					{
						pc = op3;
					}// if
					
					else
					{
						pc++;
					}// else
					break;
					
				case "JINDR":
					pc = S.GetInteger(op3);
					break;
					
				default:
					System.out.println("Empty String opCODE Name");
			}// end of switch
			
		}// while loop
		bufferedWriter.close();
		
		//increment to keep track of summation or factorial function called
		count++;
		
	}// InterpretQuads method

	private String makeTraceString(int pc, String opCode_Name,int op1,int op2,int op3 )
	{
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(opCode_Name+"     ").substring(0,6)+String.format("%02d",op1)+
                                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }// makeTraceString method
}// Interpreter class
