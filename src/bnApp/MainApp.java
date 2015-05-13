package bnApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MainApp {
	
	public static void main(String[] args) {
		
		//Check working directory
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		//READ FILE
		String fileName = "train-data.csv";
		
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
			
			System.out.println(numberOfNodes);
			
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
