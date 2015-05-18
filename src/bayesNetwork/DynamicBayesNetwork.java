package bayesNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.apple.laf.AquaButtonBorder.Dynamic;

import bnApp.Logger;

public class DynamicBayesNetwork {

	//DBN has nodes and Edges
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();

	public DynamicBayesNetwork(){
	}
	
	public DynamicBayesNetwork(DynamicBayesNetwork dbn){ //copy constructor
		
		this.nodeList = dbn.nodeList;
		for(Edge e: dbn.edgeList)
			this.edgeList.add(e.clone());
	}
	
	public void addNode(Node n){
		this.nodeList.add(n);
		
	}
	
	int getNumberOfNodes(){ return this.nodeList.size()/2;}
	
	boolean addEdge(Node parentNode, Node childNode){ // Function that connects father and son Nodes
		int parentCounter = 0;
		
		if(parentNode == childNode) return false;
		
		for(Edge e: this.edgeList){
			if(childNode == e.childNode)
				parentCounter++;
			if(parentCounter > 3)
				return false;
		}
		
		//Check if from t+1 to t
		if(this.nodeList.indexOf(parentNode)>getNumberOfNodes() && this.nodeList.indexOf(childNode)<getNumberOfNodes())
			return false;
		
		//Check if from t to t
		if(this.nodeList.indexOf(parentNode)<getNumberOfNodes() && this.nodeList.indexOf(childNode)<getNumberOfNodes())
			return false;
		
		for(Edge e: this.edgeList){
			// Check if Edge exists
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				return false;
			}
			if(e.parentNode.equals(childNode) && e.childNode.equals(parentNode)){
				return false;
			}
		}
		
		// If edge not found
		//Create a new Edge
		Edge edge = new Edge(parentNode, childNode);

		// Add Edge to nodes
		this.edgeList.add(edge);
		return true;
	}
	
	boolean removeEdge(Node parentNode, Node childNode){// Function that removes connections
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				return true;
			}
		return false;
	}
	
	boolean flipEdge(Node parentNode, Node childNode){// Function to invert parent-child role
		//Check if from t+1 to t
		if(this.nodeList.indexOf(parentNode)<getNumberOfNodes() && this.nodeList.indexOf(childNode)>getNumberOfNodes())
			return false;
		
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				e.parentNode = childNode;
				e.childNode = parentNode;
				this.edgeList.addLast(e);
				return true;
			}
		return false;
	}
	
	
	
	/**
	 * LL Score 
	 */

	 // Function to calculate log-likelihood
	public double logLike(){

		double loglike = 0;
		
		Logger.log("Calculating logLike");
		
		for(Node node : nodeList) {
			
			int maxParentConfigs = 1;

			Logger.log("Node: " + node);
			
			// Calculate number of parent configurations
			for(Edge edge : edgeList){ //for each edge
				if(edge.childNode == node){ //if node is a child in that edge
					maxParentConfigs *= edge.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
	
			Logger.log("-- maxParentConfigs: " + maxParentConfigs);

			
			for(int parentConfig=0; parentConfig < maxParentConfigs; parentConfig++){//cycle to run all parent configurations
				int Nij = 0;
				for(int k = 0; k < node.dataType; k++){//cycle to run all the node's variables
					Logger.log("-- calculateNijk: " + "node:" + node + " / parentConfig: " +  parentConfig + "/ k" + k);
					Logger.log("-- calculateNijk: " + calculateNijk(node, parentConfig, k));
					Nij += calculateNijk(node, parentConfig, k);
				}

				Logger.log("-- calculateNij: " + Nij);
				
				for(int k = 0; k < node.dataType; k++){ // erhmmmm... what ? lol !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!???
					int auxNijk = calculateNijk(node, parentConfig, k);
	
					//System.out.println(auxNijk  + " * log( " + auxNijk + " / " + Nij + " )");
	
					if(auxNijk != 0 && Nij !=0){ // arithmetics
						double auxDiv = (double) auxNijk / (double) Nij;
						loglike += (double) auxNijk * (Math.log(auxDiv) / Math.log(2));
					}
					else loglike += 0;
				}
	
			}
		}

		Logger.log("LL= " + loglike);
		
		return loglike;
	}
	
	//Net Complexity
	public double netComplexity(){ // Function to calculate parameter B, network complexity

		double maxParentConfigs = 1;
		double complexity = 0;

		for(Node n: nodeList){ //for each node
			for(Edge e: edgeList){ //for each edge
				if(e.childNode == n){ //if node is a child in that edge
					maxParentConfigs *= e.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
			complexity += (n.dataType - 1) *  maxParentConfigs; //sum this for all nodes - get complexity of net
		}

		return complexity;
	}
	
	//Net MDL
	public double mdl(){ // Function that calculates MDL
//		System.out.println("LL " + this.logLike());
//		System.out.println("NLS " + this.nodeList.size());
//		System.out.println("LOG " + ((0.5)*Math.log(this.nodeList.size())/Math.log(2)));
//		System.out.println("B " + this.netComplexity());
		
		return (this.logLike() - (0.5) * Math.log(this.nodeList.size())*this.netComplexity());
	}
	
	
	//Given j-config returns array of j_i value of each parent
	public Integer[] parentValues(int parentConfig, ArrayList<Node> parents){
		
		List<Integer> parentValues = new ArrayList<Integer>();
		
		int numberOfParents = parents.size();
		int aux = parents.size();
		int tempParentConfig = parentConfig;
		
		for(int i = numberOfParents; i>=2;i--){
			aux--;
			parentValues.add(0, tempParentConfig % parents.get(aux).dataType);
			
			tempParentConfig = (tempParentConfig - parentValues.get(0))/parents.get(aux).dataType;			
		}
		parentValues.add(0, tempParentConfig);
		
		Integer[] finalArray=new Integer[parentValues.size()];
		finalArray=parentValues.toArray(finalArray);
		
		return finalArray;
	}

	public int calculateNijk(Node node, int parentConfig, int valueK){// Function to calculate Nijk
		//Node - i
		//Parent Config - j
		//value K - k
		//return Nijk
		// Creating a vector to store the node's parent
		ArrayList<Node> parents = new ArrayList<Node>();

		int countNijk = 0;
		//Cycle to find node's parent, and add them to the array parents
		for(Edge e: this.edgeList){
			if(e.childNode == node){
				parents.add(e.parentNode);
			}
		}
		Integer[] parentValues = parentValues(parentConfig, parents);
		TreeSet<Integer> indexNijk = new TreeSet<Integer>(); // Set to save indexes of wanted variables( only used for nodes with more than 1 parent )
		TreeSet<Integer> finalIndexNijk = new TreeSet<Integer>();// Set to store indexes of variables wanted in several parents
		
		boolean firstParent = true;
		boolean secondParent = false;
		int parentValuePointer = 0;
		
		
		
		if(parents.isEmpty()){// if no parents
			for(int m=0; m<node.data.length; m++){
				if(node.data[m]==valueK){
					countNijk++;
				}
			}
		}
		else{
			for(Node n: parents){ //Check each parent Node
				for(int i = 0; i < n.data.length; i++){ //Check each data point
					if(firstParent){ //If its the first parent
						if(n.data[i]==parentValues[0]){ //If it matches the parent value of the config
							finalIndexNijk.add(i); //add the index of the data
							//System.out.println("finalIndexNijk" + finalIndexNijk);
						}
					}
					else{ //from the second parent on
						if(n.data[i]==parentValues[parentValuePointer]){ //if data matches value of parent config
							indexNijk.add(i); //add data index to set
							// System.out.println("IndexNijk" + indexNijk);
							secondParent = true;
						}
					}
				}
				firstParent = false; //no longer checking first parent
				if(secondParent) finalIndexNijk.retainAll(indexNijk); //match the sets and keep the entries that match

				parentValuePointer++; //point to the value that the next parent must have
			}
		}
		
		
		// Cycle to find the wanted variables in the Node's data
		for(int i: finalIndexNijk){
			if(node.data[i] == valueK) countNijk++;
		}


		return countNijk;
	}

	
	public void randomNet(){
		//Begin random object
		Random randomGenerator = new Random(); 
		
		int randomParentNode = randomGenerator.nextInt(getNumberOfNodes()*2);
		int randomChildNode = 0;
		
		// We only want transitions from (t -> t+1) and (t+1 -> t+1)
		
		do{ 
			// Generate random nodes only from the 2nd half of the set
			randomChildNode = randomGenerator.nextInt(getNumberOfNodes()) + getNumberOfNodes();
		}while(randomParentNode==randomChildNode);

		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
		// end here!!
		
		// TEMPORARY!!!!!!!!!!
		// AUHDAJFDSFLÇDASFJKLÇDASKAKDAJSFKLÇDJSAFKLÇADKS !!!!!!!
//		Logger.log("isDag?" + this.isDag());
//		randomParentNode = 0;
//		randomChildNode = 3;
//
//		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
//		Logger.log("isDag?" + this.isDag());
//		randomParentNode = 4;
//		randomChildNode = 5;
//
//		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
//		Logger.log("isDag?" + this.isDag());
//
//		randomParentNode = 5;
//		randomChildNode = 3;
//
//		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
//		Logger.log("isDag?" + this.isDag());
//		randomParentNode = 3;
//		randomChildNode = 4;
//
//		this.addEdge(this.nodeList.get(randomParentNode), this.nodeList.get(randomChildNode));
//		Logger.log("isDag?" + this.isDag());
	}
	
	
	// isDag 	
	
	public boolean isDag(){
//		Check if network is DAG
		boolean isDag = false;
//		L ← Empty list that will contain the sorted nodes
		LinkedList<Node> unmarkedNodes = new LinkedList<Node>(this.nodeList);
		LinkedList<Node> tempMarkedNodes = new LinkedList<Node>();
		
		//System.out.println(Arrays.toString(unmarkedNodes.toArray()));
		
//		while there are unmarked nodes do
		while(unmarkedNodes.isEmpty() == false){
//		    select an unmarked node n
			//System.out.println(unmarkedNodes.getFirst().name);
			Node selectedNode = unmarkedNodes.getFirst();
			
//		   Call function visit(n)			
			isDag = visitNode(selectedNode, unmarkedNodes, tempMarkedNodes);
			
			if(isDag == false) return isDag;
			
		}
		
		return isDag;	
	}

//	function visit(node n), that marks visited nodes
	private boolean visitNode(Node node, LinkedList<Node> unmarked, LinkedList<Node> tempMarked){
		
//	    if n has a temporary mark then stop (not a DAG) ISTO NAO É REDUNDANTE TENDO EM CONTA QUE SO RECEBERÁS NÓS NAO MARCADOS?
		if(tempMarked.contains(node) == true)
			return false;
		else{
			boolean isDag = true;
//		    if n is not marked (i.e. has not been visited yet) then
//	        mark n temporarily
			tempMarked.add(node);
			
//	        for each node m with an edge from n to m do
			for(Edge e: edgeList){
				if(e.parentNode == node){
//		            visit(m)
					isDag = visitNode(e.childNode, unmarked, tempMarked);
				}
			}
//	        mark n permanently
			unmarked.remove(node);
//	        unmark n temporarily
			tempMarked.remove(node);
//	        add n to head of L
	
			
			return isDag;
			
		}
	}

	
	

	@Override
	public String toString() {
		return "DynamicBayesNetwork [nodeList=" + nodeList + ", edgeList="
				+ edgeList + "]";
	}
	
	
	//Get net that maximizes the score
	public DynamicBayesNetwork argMax(){
		DynamicBayesNetwork dbn = new DynamicBayesNetwork(this);
		
		double result = 0;
		double[] bestScore = new double[3];
		Edge lastEdge = dbn.edgeList.getLast();
		Edge[] changedEdge = new Edge[3];
		boolean firstScore = true;
		
		
		//Check scores of all add-moves
		int p = 0;
		for(Node n: dbn.nodeList){
			for(Node m: dbn.nodeList){
				p++;
				System.out.println(p);
				if(dbn.addEdge(n, m)){ // adds new edge
					if(dbn.isDag() != true){ // checks if net is a dag, if not, it is removed
						dbn.edgeList.removeLast();
					}
					else{
						if(firstScore){// dag is confirmed
							bestScore[0] = dbn.mdl(); // mdl score is saved in array
							changedEdge[0] = dbn.edgeList.getLast(); // stores the last changed edge
							firstScore = false;
						}
						else{// comparing new score with the previous
							double auxMDL = dbn.mdl();
							if(auxMDL > bestScore[0]){// if better score, store - and store best add-action
								bestScore[0] = auxMDL;
								changedEdge[0] = dbn.edgeList.getLast();
							}
						}
						dbn.edgeList.removeLast();
					}
				}
			}
		}
		
//		System.out.println("EdgeList add: " + dbn.edgeList);
		
		
		firstScore = true;
		p=0;
		//Check scores of all flip-moves
		for(int i=0; i< dbn.edgeList.size(); i++){
			p++;
			System.out.println(p);
			Edge e = dbn.edgeList.get(0);
			if(e != lastEdge){ 
				dbn.flipEdge(e.parentNode, e.childNode); // flips edge
//				System.out.println(dbn.edgeList);
				
				if(dbn.isDag() != true)// checks if net still a dag, if not, revert flip
					dbn.flipEdge(e.parentNode, e.childNode);
				
				else{
					if(firstScore){// dag is confirmed
						bestScore[1] = dbn.mdl();// mdl score is saved in array
						changedEdge[1] = e; // stores the last fliped edge
						firstScore = false;
					}
					else{// comparing new score with the previous
						double auxMDL = dbn.mdl();
						if(auxMDL > bestScore[1]){// if better score, store
							bestScore[1] = auxMDL;
							changedEdge[1] = e; //overwrite last entry
						}
					}
					dbn.flipEdge(e.parentNode, e.childNode);
				}
			}
			else{
				dbn.edgeList.remove(e);
				dbn.edgeList.addLast(e);
			}
		}
		
//		System.out.println("EdgeList flip: " + bn.edgeList);
		
		//Check scores of all remove-moves
		firstScore = true;
		p=0;
		for(int i = 0; i<dbn.edgeList.size(); i++){
			p++;
			System.out.println(p);
			Edge e = dbn.edgeList.getFirst();
			
			if(e==lastEdge){
				dbn.edgeList.remove(e);
				dbn.edgeList.addLast(e);
			}
			else{
				dbn.removeEdge(e.parentNode, e.childNode);
				// no need to check if it is still a dag
				if(firstScore){
					bestScore[2] = dbn.mdl();// mdl score is saved in array
					changedEdge[2] = e;// stores the last removed edge
					firstScore = false;
				}
				else{
					double auxMDL = dbn.mdl();
					if(auxMDL > bestScore[2]){// if better score, store
						bestScore[2] = auxMDL;
						changedEdge[2] = e; //overwrite last entry
					}
				}
	
				dbn.edgeList.addLast(e);
			}
		}
		
//		System.out.println("EdgeList remove: " + bn.edgeList);
//		
//		System.out.println("BS-add " + bestScore[0]);
//		System.out.println("BS-flip " + bestScore[1]);
//		System.out.println("BS-remove " + bestScore[2]);
		
		
		int bestScoreIndex = 0;
		for(int i = 0; i < 3; i++)
			if(bestScore[i] < 0){
				result = bestScore[i];
				bestScoreIndex = i;
				break;
			}
				
	
		 for(int k = bestScoreIndex; k<3; k++)
			 if(bestScore[k]>result && bestScore[k]!=0){
				 result = bestScore[k];
				 bestScoreIndex = k;
			 }
		 
		 
		
		System.out.println("Accao " + bestScoreIndex);
		dbn.edgeList.add(changedEdge[bestScoreIndex]); 
		return dbn;
	}
	
	public DynamicBayesNetwork bestNetwork(){
		
		DynamicBayesNetwork dbnFinal = new DynamicBayesNetwork(this);
		DynamicBayesNetwork dbnPrime = new DynamicBayesNetwork(this);
		
		boolean increasingResult = true;
		
		//System.out.println(this.toString());
		//System.out.println(this.mdl());
		
		int i=0;
		while(increasingResult){
			DynamicBayesNetwork dbnPrime2 = new DynamicBayesNetwork(dbnPrime.argMax());
			//System.out.println("Meio " + dbnPrime2.edgeList);
			//System.out.println("Meio " + dbnPrime2.mdl());
			
			if(dbnPrime2.mdl() > dbnFinal.mdl())
				dbnFinal = new DynamicBayesNetwork(dbnPrime2);
			else
				increasingResult = false;
			
			
			dbnPrime = new DynamicBayesNetwork(dbnPrime2);
			System.out.println(dbnPrime.isDag());
			i++;
			System.out.println("ITER: " + i);
		}
		
//		System.out.println(dbnFinal.edgeList);
//		System.out.println(dbnFinal.mdl());
		
		
		return dbnFinal;
		
	}

}
