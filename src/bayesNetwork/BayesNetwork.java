package bayesNetwork;

import java.util.LinkedList;


public class BayesNetwork {
	
	//Data a Bayesian Network needs
	int numberOfNodes;
	int dataLength;
	Integer[][] data;
	int[] dataType; //The r_i of the data -> dataType = 2 if data = {0,1} (binary)
	
	//Nodes and Edges a BN holds
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	
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
	
	void addEdge(Node parentNode, Node childNode){
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
		}
	}
	
	void removeEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
			}
	}
	
	void flipEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				e.parentNode = childNode;
				e.childNode = parentNode;
			}
		
	}
	
	boolean isDag(){
		//Check if network is DAG
		return false;	
	}

}
