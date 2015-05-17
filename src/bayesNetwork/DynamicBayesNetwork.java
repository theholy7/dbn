package bayesNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import bnApp.Logger;

public class DynamicBayesNetwork {

	//DBN has nodes and Edges
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();

	public DynamicBayesNetwork(){
	}
	
	public void addNode(Node n){
		this.nodeList.add(n);
		
	}
	
	int getNumberOfNodes(){ return this.nodeList.size()/2;}
	
	boolean addEdge(Node parentNode, Node childNode){ // Function that connects father and son Nodes
		int parentCounter = 0;
		
		if(parentNode == childNode) return false;
		
		for(Edge e: this.edgeList){
			if(childNode == e.childNode)
				parentCounter++;
			if(parentCounter > 3)
				return false;
		}
		
		for(Edge e: this.edgeList){
			// Check if Edge exists
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				return false;
			}
			if(e.parentNode.equals(childNode) && e.childNode.equals(parentNode)){
				return false;
			}
		}
		
		// If edge not found
		//Create a new Edge
		Edge edge = new Edge(parentNode, childNode);

		// Add Edge to nodes
		this.edgeList.add(edge);
		return true;
	}
	
	boolean removeEdge(Node parentNode, Node childNode){// Function that removes connections
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				return true;
			}
		return false;
	}
	
	boolean flipEdge(Node parentNode, Node childNode){// Function to invert parent-child role
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				e.parentNode = childNode;
				e.childNode = parentNode;
				this.edgeList.addLast(e);
				return true;
			}
		return false;
	}
	
	
	
	/**
	 * LL Score 
	 */

	 // Function to calculate log-likelihood
	public double logLike(){

		double loglike = 0;
		
		Logger.log("Calculating logLike");
		
		for(Node node : nodeList) {
			
			int maxParentConfigs = 1;

			Logger.log("Node: " + node);
			
			// Calculate number of parent configurations
			for(Edge edge : edgeList){ //for each edge
				if(edge.childNode == node){ //if node is a child in that edge
					maxParentConfigs *= edge.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
	
			Logger.log("-- maxParentConfigs: " + maxParentConfigs);

			
			for(int parentConfig=0; parentConfig < maxParentConfigs; parentConfig++){//cycle to run all parent configurations
				int Nij = 0;
				for(int k = 0; k < node.dataType; k++){//cycle to run all the node's variables
					Logger.log("-- calculateNijk: " + "node:" + node + " / parentConfig: " +  parentConfig + "/ k" + k);
					Logger.log("-- calculateNijk: " + calculateNijk(node, parentConfig, k));
					Nij += calculateNijk(node, parentConfig, k);
				}

				Logger.log("-- calculateNij: " + Nij);
				
				for(int k = 0; k < node.dataType; k++){ // erhmmmm... what ? lol !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!???
					int auxNijk = calculateNijk(node, parentConfig, k);
	
					//System.out.println(auxNijk  + " * log( " + auxNijk + " / " + Nij + " )");
	
					if(auxNijk != 0 && Nij !=0){ // arithmetics
						double auxDiv = (double) auxNijk / (double) Nij;
						loglike += (double) auxNijk * (Math.log(auxDiv) / Math.log(2));
					}
					else loglike += 0;
				}
	
			}
		}

		Logger.log("LL= " + loglike);
		
		return loglike;
	}
	
	
	
	//Given j-config returns array of j_i value of each parent
	public Integer[] parentValues(int parentConfig, ArrayList<Node> parents){
		
		List<Integer> parentValues = new ArrayList<Integer>();
		
		int numberOfParents = parents.size();
		int aux = parents.size();
		int tempParentConfig = parentConfig;
		
		for(int i = numberOfParents; i>=2;i--){
			aux--;
			parentValues.add(0, tempParentConfig % parents.get(aux).dataType);
			
			tempParentConfig = (tempParentConfig - parentValues.get(0))/parents.get(aux).dataType;			
		}
		parentValues.add(0, tempParentConfig);
		
		Integer[] finalArray=new Integer[parentValues.size()];
		finalArray=parentValues.toArray(finalArray);
		
		return finalArray;
	}

	public int calculateNijk(Node node, int parentConfig, int valueK){// Function to calculate Nijk
		//Node - i
		//Parent Config - j
		//value K - k
		//return Nijk
		// Creating a vector to store the node's parent
		ArrayList<Node> parents = new ArrayList<Node>();

		int countNijk = 0;
		//Cycle to find node's parent, and add them to the array parents
		for(Edge e: this.edgeList){
			if(e.childNode == node){
				parents.add(e.parentNode);
			}
		}
		Integer[] parentValues = parentValues(parentConfig, parents);
		TreeSet<Integer> indexNijk = new TreeSet<Integer>(); // Set to save indexes of wanted variables( only used for nodes with more than 1 parent )
		TreeSet<Integer> finalIndexNijk = new TreeSet<Integer>();// Set to store indexes of variables wanted in several parents
		
		boolean firstParent = true;
		boolean secondParent = false;
		int parentValuePointer = 0;
		
		
		
		if(parents.isEmpty()){// if no parents
			for(int m=0; m<node.data.length; m++){
				if(node.data[m]==valueK){
					countNijk++;
				}
			}
		}
		else{
			for(Node n: parents){ //Check each parent Node
				for(int i = 0; i < n.data.length; i++){ //Check each data point
					if(firstParent){ //If its the first parent
						if(n.data[i]==parentValues[0]){ //If it matches the parent value of the config
							finalIndexNijk.add(i); //add the index of the data
							//System.out.println("finalIndexNijk" + finalIndexNijk);
						}
					}
					else{ //from the second parent on
						if(n.data[i]==parentValues[parentValuePointer]){ //if data matches value of parent config
							indexNijk.add(i); //add data index to set
							// System.out.println("IndexNijk" + indexNijk);
							secondParent = true;
						}
					}
				}
				firstParent = false; //no longer checking first parent
				if(secondParent) finalIndexNijk.retainAll(indexNijk); //match the sets and keep the entries that match

				parentValuePointer++; //point to the value that the next parent must have
			}
		}
		
		
		// Cycle to find the wanted variables in the Node's data
		for(int i: finalIndexNijk){
			if(node.data[i] == valueK) countNijk++;
		}


		return countNijk;
	}

	
	public void randomNet(){
		//Begin random object
		Random randomGenerator = new Random(); 
		
		int randomParentNode = randomGenerator.nextInt(getNumberOfNodes()*2);
		int randomChildNode = 0;
		
		// We only want transitions from (t -> t+1) and (t+1 -> t+1)
		
		do{ 
			// Generate random nodes only from the 2nd half of the set
			randomChildNode = randomGenerator.nextInt(getNumberOfNodes()) + getNumberOfNodes();
		}while(randomParentNode==randomChildNode);

		//this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
		// end here!!
		
		// TEMPORARY!!!!!!!!!!
		// AUHDAJFDSFLÇDASFJKLÇDASKAKDAJSFKLÇDJSAFKLÇADKS !!!!!!!
		randomParentNode = 3;
		randomChildNode = 4;

		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));

		randomParentNode = 1;
		randomChildNode = 5;

		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
	}

	@Override
	public String toString() {
		return "DynamicBayesNetwork [nodeList=" + nodeList + ", edgeList="
				+ edgeList + "]";
	}
	

}
