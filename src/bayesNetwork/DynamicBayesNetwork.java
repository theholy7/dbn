package bayesNetwork;

import java.util.ArrayList;
import java.util.LinkedList;

public class DynamicBayesNetwork {

	//DBN has nodes and Edges
	LinkedList<Node> nodeList;
	LinkedList<Edge> edgeList;


	public DynamicBayesNetwork(){
	}
	
	public void addNode(Node n){
		this.nodeList.add(n);
		
	}
}
