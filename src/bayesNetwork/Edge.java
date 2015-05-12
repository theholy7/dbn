package bayesNetwork;


public class Edge {
	
	//An Edge goes from a Parent to a Child
	Node parentNode = null;
	Node childNode = null;

	public Edge(Node parentNode, Node childNode) {
		super();
		this.parentNode = parentNode;
		this.childNode = childNode;
	}
}
