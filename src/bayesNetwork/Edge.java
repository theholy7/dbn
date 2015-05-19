package bayesNetwork;

/**
 * @author Jose Miguel Filipe Antunes  ist167929
 * @author Pedro Miguel Nobre ist167693
 *
 */
public class Edge {
	
	//An Edge connects a Parent to a Child
	Node parentNode = null;
	Node childNode = null;

	/**
	 * Edge's constructor
	 * 
	 * @param parentNode Node to be related to other node with a parent role
	 * @param childNode Node to be related to other node with a child role
	 */
	public Edge(Node parentNode, Node childNode) {
		super();
		this.parentNode = parentNode;
		this.childNode = childNode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Edge [parentNode=" + parentNode + ", childNode=" + childNode
				+ "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Edge clone() {
		return new Edge(this.parentNode, this.childNode);
	}
}
