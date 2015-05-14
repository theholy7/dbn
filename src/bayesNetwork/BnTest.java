package bayesNetwork;
import java.util.Arrays;

public class BnTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int nNodes=3;
		int dataSize=7;
		
		
		Integer[][] variables = {{0,0,1,1,0,1,1}, {0,1,2,1,1,2,0}, {0,1,0,1,0,0,1}};
		int[] dataTypes={2,3,2};
		
		BayesNetwork bn = new BayesNetwork(nNodes, dataSize, variables, dataTypes);
		for(int i=0; i<7; i++){
			System.out.println(bn.nodeList.getFirst().data[i]);
		}
	Node node = bn.nodeList.get(0);
	System.out.println(Arrays.toString(node.data));
	node = bn.nodeList.get(1);
	System.out.println(Arrays.toString(node.data));
	node = bn.nodeList.get(2);
	System.out.println(Arrays.toString(node.data));
	
	bn.addEdge(bn.nodeList.get(0), bn.nodeList.get(1));
	bn.addEdge(bn.nodeList.get(1), bn.nodeList.get(2));
	System.out.println(bn.isDag());
	
	System.out.println(bn.calculateNijk(bn.nodeList.get(0), 0, 0));
	bn.logLike();
	}
	
	
}
