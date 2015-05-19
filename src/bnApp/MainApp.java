package bnApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import bayesNetwork.*;

/**
 * Represents the app that deals with the parsing of the file and creation of DBN
 * 
 * @author Jose Miguel Filipe Antunes  ist167929
 * @author Pedro Miguel Nobre ist167693
 *
 */
public class MainApp {
	
	public static void main(String[] args) {
		
		//Check working directory
		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		File f=new File(args[0]);
		File f2=new File(args[1]);
		
		boolean inputTest=inputParam(args);
		if(inputTest!=true){
			System.out.println("Invalid input parameters");
			System.exit(1);
		}
			
		if(!f.exists()){
			System.out.println("file not found");
			System.exit(1);
		}
		if(!f2.exists()){
			System.out.println("file not found");
			System.exit(1);
		}
		
		if(args.length == 4)
			System.out.println("Parameters: " + args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
		if(args.length == 5)
			System.out.println("Parameters: " + args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4]);
		
		//READ FILE
		String fileName = args[0];
		
		try {
			//String with the file
			String readFile = readFile(fileName);
			
			//Split by lines
			String[] linesOfFile = readFile.split("\n");
			
			//Array number of lines by number of collumns
			String[][] collumnsOfLine = new String[linesOfFile.length][];
			
			//For each line of text, fill cells of 2D-array
			for(int i=0; i<linesOfFile.length; i++){
				collumnsOfLine[i] = linesOfFile[i].split(",");
			}

			
			//Calculate number of Nodes
			int numberOfNodes = 0;
			for(int i = 0; i < collumnsOfLine[0].length; i++){
				if(collumnsOfLine[0][i].contains("_0")==true)
					numberOfNodes++;
				else{
					break;
				}
			}
			//System.out.println("Number of Nodes: " + numberOfNodes);
			
			//Calculate number of time slices
			int numOfCollums = collumnsOfLine[0].length;
			int timeSlices = Integer.parseInt(collumnsOfLine[0][numOfCollums-1].split("_")[1]);
			//System.out.println("Time slices: " + timeSlices);
			
			//Calculate r_i of each node
			int[] dataTypes = new int[numberOfNodes];
			
			for(int n=0; n < numberOfNodes; n++){
				dataTypes[n] = 1;
				for(int i=0; i < timeSlices; i++){
					for(int linha=1; linha < linesOfFile.length; linha++){
						try{
//							System.out.println(n + " " + i + " " + linha + "-"+collumnsOfLine[linha][numberOfNodes * i + n]+"-");
							if(Integer.parseInt(collumnsOfLine[linha][numberOfNodes * i + n].trim())==dataTypes[n])
								dataTypes[n]++;
						}
						catch(ArrayIndexOutOfBoundsException e){
							
						}
						
					}
				}
			}
			
			//System.out.println("Data types: " + Arrays.toString(dataTypes));
			
			//Create t and t+1 table
			ArrayList<Integer>[] tableTTp1 = new ArrayList[2*numberOfNodes];
			
			for(int i = 0; i < 2*numberOfNodes; i++)
				tableTTp1[i] = new ArrayList<Integer>();
			
			
			for(int linha=1; linha < linesOfFile.length; linha++)
				for(int i=1; i <= timeSlices; i++){
					try{
						for(int n=0; n < numberOfNodes; n++){
							tableTTp1[numberOfNodes+n].add(Integer.parseInt(collumnsOfLine[linha][numberOfNodes * (i) + n].trim()));
						}
						for(int n=0; n < numberOfNodes; n++){
							try{
								tableTTp1[n].add(Integer.parseInt(collumnsOfLine[linha][numberOfNodes * (i-1) + n].trim()));
							}
							catch(IndexOutOfBoundsException e){
								
							}
						}
					}
					catch(IndexOutOfBoundsException e){
						
					}
						
				}
//			for(int i = 0; i < numberOfNodes * 2; i++)
//				System.out.println(Arrays.toString(tableTTp1[i].toArray()));
			
			
			DynamicBayesNetwork dbn = new DynamicBayesNetwork();
			dbn.setScore(args[2]);
			
			for(int k = 0; k < 2; k++)
				for(int i = 0; i < numberOfNodes; i++){
				Integer[] data = new Integer[tableTTp1[i + k*numberOfNodes].size()];
				data = tableTTp1[i + k*numberOfNodes].toArray(data);
				
				Node node = new Node(dataTypes[i], data, (i + k*numberOfNodes+""));

				dbn.addNode(node);
				
			}
			
			dbn.randomNet();
//			System.out.println("====== AFTER RAND ======");
//			System.out.println(dbn.logLike());
//			System.out.println(dbn.netComplexity());
//			System.out.println(dbn.mdl());
//			
			//System.out.println(dbn.argMax());
			
			//System.out.println(dbn.toString());
			
			long startTime = System.nanoTime();
			
			DynamicBayesNetwork dbn2 = new DynamicBayesNetwork(dbn.bestNetwork());
			
			long endTime = System.nanoTime();
			double durationTime = (endTime-startTime)/1000000000.0; //Time in seconds
			System.out.println("Building DBN: " + durationTime + " time");
			System.out.println("Transition network:");
			
			System.out.println(dbn2.toString());
			
			dbn2.calculateTijk();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * Function that verifies the validity of the input parameters.
	 * 
	 * @param args These are the arguments the user passes to the program
	 * @return Returns true if all params are valid
	 */
	public static boolean inputParam(String[] args){


		String extension="";
		String extension2="";

		if(args.length < 4 || args.length >5)return false; // VER QUANTIDADE DE ARGUMENTOS DE ENTRADA
		
		if(!args[2].equals("MDL") && !args[2].equals("LL"))return false; // check validity of parameter score

		int i = args[0].lastIndexOf('.');
		int j = args[1].lastIndexOf('.');
		if(i>0 && j>0){
			extension=args[0].substring(i+1);
			extension2=args[1].substring(j+1);
		}
		if(!extension.equals("csv") || !extension2.equals("csv"))return false; // checks file extensions
		

		
		
		
		for(int k=3; k<args.length; k++)
			if(!isInteger(args[k]))
				return false;

		return true;
	}
	
	/**
	 * Function to check if an input parameter is a number
	 * 
	 * @param s String that needs to be parsed to int
	 * @return returns true if string is a valid number
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	//Function to read the file to a single string
	/**
	 * Function that reads the input file and builds the string with the whole file
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}

}
