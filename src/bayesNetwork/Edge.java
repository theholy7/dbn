package bayesNetwork;


public class Edge {
	
	//An Edge connects a Parent to a Child
	Node parentNode = null;
	Node childNode = null;

	public Edge(Node parentNode, Node childNode) {
		super();
		this.parentNode = parentNode;
		this.childNode = childNode;
	}

	@Override
	public String toString() {
		return "Edge [parentNode=" + parentNode + ", childNode=" + childNode
				+ "]";
	}
	
	public Edge clone() {
		return new Edge(this.parentNode, this.childNode);
	}
}
