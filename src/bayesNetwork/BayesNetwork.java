package bayesNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
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
	//Given j-config returns array of j_i value of each parent
	public Integer[] parentValues(int parentConfig, ArrayList<Node> parents){
		ArrayList<Integer> parentValues = new ArrayList<Integer>();
		
		int numberOfParents = parents.size();
		int aux = parents.size();
		int tempParentConfig = parentConfig;
		for(int i = numberOfParents; i>=2;i--){
			parentValues.add(0, tempParentConfig % parents.get(aux).dataType);
			
			tempParentConfig = (tempParentConfig - parentValues.get(0))/parents.get(aux).dataType;

			aux--;
		}
		parentValues.add(0, tempParentConfig);
		
		return (Integer[]) parentValues.toArray();
	}

}
