package bayesNetwork;


public class Node {
	
	//Data a Node in a BN has
	String name = null;
	int dataType;
	Integer[] data;
	
	//Fields to check if network is DAG
	//boolean tempMark = false;
	//boolean permMark = false;
	
	public Node(){}
	
	public Node(int dataType, Integer[] data, String name) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "Node " + name;
	}
	
	public Node clone() {
		return new Node(this.dataType, this.data, this.name);
	}
}
