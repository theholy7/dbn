package bayesNetwork;

public class Node {
	
	//Data a Node in a BN has
	int dataType;
	Integer[] data;
	
	//Fields to check if network is DAG
	//boolean tempMark = false;
	//boolean permMark = false;
	
	public Node(){}
	
	public Node(int dataType, Integer[] data) {
		super();
		this.dataType = dataType;
		this.data = data;
	}
	

}
