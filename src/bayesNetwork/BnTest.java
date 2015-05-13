package bayesNetwork;

public class BnTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BayesNetwork bn = new BayesNetwork(6);
		
		System.out.println(bn.nodeList.size());
		
		bn.randomNet();
		System.out.println(bn.edgeList);
		System.out.println(bn.isDag());
		bn.randomNet();
		System.out.println(bn.edgeList);
		System.out.println(bn.isDag());
		bn.randomNet();
		System.out.println(bn.edgeList);
		System.out.println(bn.isDag());
		
//		bn.addEdge(bn.nodeList.getFirst(), bn.nodeList.getLast());
//		bn.addEdge(bn.nodeList.getFirst(), bn.nodeList.get(1));
//		bn.addEdge(bn.nodeList.get(1), bn.nodeList.get(2));
//		bn.addEdge(bn.nodeList.get(2), bn.nodeList.get(0));
		
		System.out.println(bn.edgeList);
		
		System.out.println(bn.isDag());
		
		
	}

}
