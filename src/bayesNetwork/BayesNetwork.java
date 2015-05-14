package bayesNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;


public class BayesNetwork {
	
	//Data a Bayesian Network needs
	int numberOfNodes;
	int dataLength;
	Integer[][] data;
	int[] dataType; //The r_i of the data -> dataType = 2 if data = {0,1} (binary)
	
	//Nodes and Edges a BN holds
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	
	public BayesNetwork(int numberOfNodes){
		this.numberOfNodes = numberOfNodes;
		for(int i = 0; i < numberOfNodes; i++){
			Node node = new Node();
			this.nodeList.add(node);
		}
	}
		
	public BayesNetwork(int numberOfNodes, int dataLength, Integer[][] data, int[] dataType) {
		super();
		this.numberOfNodes = numberOfNodes;
		this.dataLength = dataLength;
		
		assert data.length == numberOfNodes;
		assert dataType.length == numberOfNodes;
		
		for(int i = 0; i < numberOfNodes; i++){
			assert data[i].length == dataLength;	
		}
		
		this.data = data;
		
		for(int i = 0; i < numberOfNodes; i++){
			Node node = new Node(dataType[i], data[i]);
			this.nodeList.add(node);
		}
		
	}
	
	boolean addEdge(Node parentNode, Node childNode){
		boolean edgeExists = false;
		
		//Check if Edge exists
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				edgeExists = true;
				break;
			}
		if(edgeExists == false){
			//Create a new Edge
			Edge edge = new Edge(parentNode, childNode);

			//Add Edge to nodes
			this.edgeList.add(edge);
			return true;
		}
		return false;
	}
	
	boolean removeEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				return true;
			}
		return false;
	}
	
	boolean flipEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				e.parentNode = childNode;
				e.childNode = parentNode;
				return true;
			}
		return false;
	}
	
	public boolean isDag(){
//		Check if network is DAG
		boolean isDag = true;
//		L ← Empty list that will contain the sorted nodes
		LinkedList<Node> unmarkedNodes = (LinkedList<Node>) this.nodeList.clone();
		LinkedList<Node> tempMarkedNodes = new LinkedList<Node>();
		
//		while there are unmarked nodes do
		while(unmarkedNodes.isEmpty() == false){
//		    select an unmarked node n
			
			Node selectedNode = unmarkedNodes.getFirst();
			
//		    visit(n)			
			isDag = visitNode(selectedNode, unmarkedNodes, tempMarkedNodes);
			
			if(isDag == false) break;
			
		}
		
		return isDag;	
	}

//	function visit(node n)
	private boolean visitNode(Node node, LinkedList<Node> unmarked, LinkedList<Node> tempMarked){
		boolean isDag = true;
		
//	    if n has a temporary mark then stop (not a DAG)
		if(tempMarked.contains(node) == true) return false;
		else{
//		    if n is not marked (i.e. has not been visited yet) then
//	        mark n temporarily
			tempMarked.add(node);
			
//	        for each node m with an edge from n to m do
			for(Edge e: edgeList){
				if(e.parentNode == node){
//		            visit(m)
					isDag = visitNode(e.childNode, unmarked, tempMarked);
				}
			}
//	        mark n permanently
			unmarked.remove(node);
//	        unmark n temporarily
			tempMarked.remove(node);
//	        add n to head of L
	
			
			return isDag;
			
		}
	}

	
	public void randomNet(){
		//Begin random object
		Random randomGenerator = new Random();
		boolean actionPerformed = false;
		
		do{
			
			int randomParentNode = randomGenerator.nextInt(this.numberOfNodes);
			int randomChildNode = 0;
			do{
			randomChildNode = randomGenerator.nextInt(this.numberOfNodes);
			}while(randomParentNode==randomChildNode);

			int randomAction = randomGenerator.nextInt(3)+1;
			System.out.println(randomAction);
			System.out.println(randomParentNode);
			System.out.println(randomChildNode);
			
			switch (randomAction) {
			case 1: actionPerformed = this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
					if(actionPerformed) System.out.println("Added Edge");
				break;
				
			case 2: actionPerformed = this.removeEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
					if(actionPerformed) System.out.println("Removed Edge");
				break;
				
			case 3: actionPerformed = this.flipEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
					if(actionPerformed) System.out.println("Flipped Edge");
				break;

			default: System.out.println("Action Random int is not right!");
				break;
			}
		}while(this.isDag() == false || actionPerformed == false);
		
		
	}


	public int calculateNijk(Node node, int parentConfig, int valueK){
		//Node - i
		//Parent Config - j
		//value K - k
		//return Nijk

		ArrayList<Node> parents = new ArrayList<Node>();

		int countNijk = 0;
		
		for(Edge e: this.edgeList){
			if(e.childNode == node){
				parents.add(e.parentNode);
			}
		}
		Integer[] parentValues = parentValues(parentConfig, parents);
		TreeSet<Integer> indexNijk = new TreeSet<Integer>();
		TreeSet<Integer> finalIndexNijk = new TreeSet<Integer>();
		
		boolean firstParent = true;
		boolean secondParent = false;
		int parentValuePointer = 0;

		if(parents.isEmpty()){
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
		
		

		for(int i: finalIndexNijk){
			if(node.data[i] == valueK) countNijk++;
		}


		return countNijk;
	}

	//Given j-config returns array of j_i value of each parent
	public Integer[] parentValues(int parentConfig, ArrayList<Node> parents){
		List<Integer> parentValues = new ArrayList<Integer>();
		
		int numberOfParents = parents.size();
		int aux = parents.size();
		int tempParentConfig = parentConfig;
		for(int i = numberOfParents; i>=2;i--){
			parentValues.add(0, tempParentConfig % parents.get(aux).dataType);
			
			tempParentConfig = (tempParentConfig - parentValues.get(0))/parents.get(aux).dataType;

			aux--;
		}
		parentValues.add(0, tempParentConfig);
		
		Integer[] finalArray=new Integer[parentValues.size()];
		finalArray=parentValues.toArray(finalArray);
		
		return finalArray;
	}

	public int netComplexity(){
		
		int maxParentConfigs = 1;
		int complexity = 0;
		
		for(Node n: this.nodeList){ //for each node
			for(Edge e: this.edgeList){ //for each edge
				if(e.childNode == n){ //if node is a child in that edge
					maxParentConfigs *= e.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
			complexity += (n.dataType - 1) *  maxParentConfigs; //sum this for all nodes - get complexity of net
		}
		
		return complexity;
	}
	
	
	public double logLike(){
		int Nij = 0;
		double loglike = 0;
		int maxParentConfigs = 1;
		for(Node n: this.nodeList){
			
			for(Edge e: this.edgeList){ //for each edge
				if(e.childNode == n){ //if node is a child in that edge
					maxParentConfigs *= e.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
			
			
			for(int i=0; i< maxParentConfigs; i++){
				Nij = 0;
				for(int k = 0; k < n.dataType; k++){
					Nij += calculateNijk(n, i, k);
				}
				
				for(int k = 0; k < n.dataType; k++){
					int auxNijk = calculateNijk(n, i, k);
					
					//System.out.println(auxNijk  + " * log( " + auxNijk + " / " + Nij + " )");
					
					if(auxNijk != 0 && Nij !=0){
						double auxDiv = (double) auxNijk / (double) Nij;
						loglike += (double) auxNijk * (Math.log(auxDiv) / Math.log(2));
					}
					else loglike += 0;
				}
				
			}
			maxParentConfigs = 1;
		}
		System.out.println("LL= " + loglike);
		return loglike;
	}
	
	public double mdl(){return 0;}
	

}
