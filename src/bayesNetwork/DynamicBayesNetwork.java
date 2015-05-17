package bayesNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

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

		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
	}

	@Override
	public String toString() {
		return "DynamicBayesNetwork [nodeList=" + nodeList + ", edgeList="
				+ edgeList + "]";
	}
}
