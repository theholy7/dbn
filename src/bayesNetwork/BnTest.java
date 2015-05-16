package bayesNetwork;

public class BnTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int nNodes=6;
		int dataSize=3;


		Integer[][] variables = {{0,3,1}, {3,3,3}, {3,1,0}, {1,0,3}, {2,3,3}, {3,3,0}, {0,2,3}};
		int[] dataTypes={4,4,4,4,4,4};
		String[] names = {"a_0", "b_0", "c_0", "d_0", "e_0", "f_0", "g_0"};
		Score score = new Score();
		
		BayesNetwork bnInit = new BayesNetwork(nNodes, dataSize, variables, dataTypes, names);
		
		
		bnInit.addEdge(bnInit.nodeList.get(0), bnInit.nodeList.get(3));
		System.out.println(bnInit.isDag());
		bnInit.addEdge(bnInit.nodeList.get(0), bnInit.nodeList.get(2));
		System.out.println(bnInit.isDag());
		bnInit.addEdge(bnInit.nodeList.get(2), bnInit.nodeList.get(4));
		System.out.println(bnInit.isDag());
		bnInit.addEdge(bnInit.nodeList.get(2), bnInit.nodeList.get(5));
		System.out.println(bnInit.isDag());
		bnInit.addEdge(bnInit.nodeList.get(4), bnInit.nodeList.get(0));
		System.out.println(bnInit.isDag());
		
		
		
		
		
//		bnInit.randomNet();
//		
//		BayesNetwork bnFinal = new BayesNetwork(bnInit);
//		BayesNetwork bnPrime = new BayesNetwork(bnInit);
//		
//		boolean increasingResult = true;
//		
//		System.out.println(bnInit.edgeList);
//		System.out.println(score.mdl(bnInit));
//		
//		
//		while(increasingResult){
//			BayesNetwork bnPrime2 = new BayesNetwork(score.argMax(bnPrime));
//			System.out.println("Meio " + bnPrime2.edgeList);
//			System.out.println("Meio " + score.mdl(bnPrime2));
//			
//			if(score.mdl(bnPrime2) > score.mdl(bnFinal))
//				bnFinal = new BayesNetwork(bnPrime2);
//			else
//				increasingResult = false;
//			
//			
//			bnPrime = new BayesNetwork(bnPrime2);
//			System.out.println(bnPrime.isDag());
//		}
//		
//		System.out.println(bnFinal.edgeList);
//		System.out.println(score.mdl(bnFinal));
		
	}


}
