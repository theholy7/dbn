package bayesNetwork;

/**
 * Creates a node that stores a name, data and type of data
 * 
 * @author Jose Miguel Filipe Antunes  ist167929
 * @author Pedro Miguel Nobre ist167693
 *
 */
public class Node {
	
	//Data a Node in a BN has
	String name = null;
	int dataType;
	Integer[] data;
	
	//Fields to check if network is DAG
	//boolean tempMark = false;
	//boolean permMark = false;
	
	/**
	 * No Arg Constructor
	 * 
	 */
	public Node(){}
	
	/**
	 * Node's constructor
	 * 
	 * @param dataType	indicates the amount of values a given node can have
	 * @param data	Integer[] string with the variable features
	 * @param name	variable ID
	 */
	public Node(int dataType, Integer[] data, String name) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Node " + name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Node clone() {
		return new Node(this.dataType, this.data, this.name);
	}
}
