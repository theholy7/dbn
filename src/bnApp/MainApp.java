package bnApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import bayesNetwork.*;

public class MainApp {
	
	public static void main(String[] args) {
		
		//Check working directory
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		//READ FILE
		String fileName = "train-data-2.csv";
		
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
			System.out.println("Number of Nodes: " + numberOfNodes);
			
			//Calculate number of time slices
			int numOfCollums = collumnsOfLine[0].length;
			int timeSlices = Integer.parseInt(collumnsOfLine[0][numOfCollums-1].split("_")[1]);
			System.out.println("Time slices: " + timeSlices);
			
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
			
			System.out.println("Data types: " + Arrays.toString(dataTypes));
			
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
			for(int i = 0; i < numberOfNodes * 2; i++)
				System.out.println(Arrays.toString(tableTTp1[i].toArray()));
			
			
			DynamicBayesNetwork dbn = new DynamicBayesNetwork();
			
			for(int k = 0; k < 2; k++)
				for(int i = 0; i < numberOfNodes; i++){
				Integer[] data = new Integer[tableTTp1[i + k*numberOfNodes].size()];
				data = tableTTp1[i + k*numberOfNodes].toArray(data);
				
				Node node = new Node(dataTypes[i], data, (i + k*numberOfNodes+""));

				dbn.addNode(node);
				
			}
			System.out.println(dbn.logLike());
			System.out.println(dbn.mdl());
			dbn.randomNet();
			
			System.out.println(dbn.toString());
			
			DynamicBayesNetwork dbn2 = new DynamicBayesNetwork(dbn.bestNetwork());
			
			System.out.println(dbn2.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	//Function to read the file to a single string
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
