package bayesNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * @author Jose Miguel Filipe Antunes  ist167929
 * @author Pedro Miguel Nobre ist167693
 *
 */
/**
 * @author jose
 *
 */
public class DynamicBayesNetwork {

	//DBN has nodes and Edges
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	//The thetas of each var are a characteristic of the Network
	public double[][] arrayOfThetas;
	int scoreFunction;
	
	/** 
	 * No-arg Constructor
	 * 
	 */
	public DynamicBayesNetwork(){
	}
	
	/**
	 * Creates a copy of the original network
	 * 
	 * This copy is created so that the original Edges, edgeList are not changed
	 * 
	 * @param dbn object to be copied
	 */
	public DynamicBayesNetwork(DynamicBayesNetwork dbn){ //copy constructor
		this.scoreFunction = dbn.scoreFunction;
		this.nodeList = dbn.nodeList;
		for(Edge e: dbn.edgeList)
			this.edgeList.add(e.clone());
	}
	
	/**
	 * Method that adds a Node to the nodeList
	 * @param n node to be added to the nodeList
	 */
	public void addNode(Node n){
		this.nodeList.add(n);
		
	}
	
	/**
	 * Function that, when called, returns the size of the nodeList divided in half
	 * 
	 * @return int corresponding value to the number of nodes in the nodeList
	 */
	int getNumberOfNodes(){ return this.nodeList.size()/2;}
	
	/**
	 * Generates edges for the copied edgeList
	 * 
	 * Fuction that adds an Edge to the edgeList only if:
	 * <p>
	 * There is no Edge equal to the Edge intended to create (or inverted)
	 * <p>
	 * The childNode doesn't have more than 3 parents
	 * <p>
	 * There is no inter-temporal relations to the past
	 * 
	 * @param parentNode node to be connected as parent
	 * @param childNode	node to be connected as child
	 * @return boolean returns true if edge is created, or false if it doesn't meet the criteria 
	 */
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
		if(this.nodeList.indexOf(parentNode)>=getNumberOfNodes() && this.nodeList.indexOf(childNode)<getNumberOfNodes())
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
	
	
	/**
	 * Receives a string (MDL or LL) and sets a flag to use the correct function.
	 * 
	 * @param s
	 */
	public void setScore(String s){
		if(s.toLowerCase().equals("ll"))
			this.scoreFunction = 1;
		else if(s.toLowerCase().equals("mdl"))
			this.scoreFunction = 2;
		else System.out.println("Not setting Score");
		
	}
	
	/**
	 * Method to remove edges from the edgeList
	 * 
	 * @param parentNode node with father role to be removed from the relation with the childNode
	 * @param childNode node with child role to be removed from the relation with the fatherNode
	 * @return true if edge removed, otherwise return false
	 */
	boolean removeEdge(Node parentNode, Node childNode){// Function that removes connections
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
				return true;
			}
		return false;
	}
	
	/**
	 * Method that flips an existing edge
	 * 
	 * This method shall flip an edge, inverting the parent-child role if:
	 * <p>
	 * The edge exists
	 * <p>
	 * The parentNode is not posterior to the childNode
	 * 
	 * @param parentNode
	 * @param childNode
	 * @return true if edge is flipped, otherwise returns false
	 */
	boolean flipEdge(Node parentNode, Node childNode){// Function to invert parent-child role
		
		//Check if from t+1 to t
		if(this.nodeList.indexOf(parentNode)<getNumberOfNodes() && this.nodeList.indexOf(childNode)>=getNumberOfNodes())
			return false;
		
		for(Edge e: this.edgeList){
			
			if(e.parentNode == parentNode && e.childNode == childNode){
				if(this.edgeList.remove(e))//{//System.out.println("Removi o E");}
				e.parentNode = childNode;
				e.childNode = parentNode;
				if(this.edgeList.add(e))//{//System.out.println("Addicionei o E");}
				return true;
			}
		}
		return false;
	}
	
	 // Function to calculate log-likelihood
	/**
	 * Arithmetic function that calculates the log-likelihood
	 * 
	 * @return calculated score
	 */
	public double logLike(){

		double loglike = 0;
		
		
		
		for(Node node : nodeList) {
			
			int maxParentConfigs = 1;

			
			
			// Calculate number of parent configurations
			for(Edge edge : edgeList){ //for each edge
				if(edge.childNode == node){ //if node is a child in that edge
					maxParentConfigs *= edge.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
	
			

			
			for(int parentConfig=0; parentConfig < maxParentConfigs; parentConfig++){//cycle to run all parent configurations
				int Nij = 0;
				for(int k = 0; k < node.dataType; k++){//cycle to run all the node's variables
					
					
					Nij += calculateNijk(node, parentConfig, k);
				}

				
				
				for(int k = 0; k < node.dataType; k++){ // erhmmmm... what ? lol !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!???
					int auxNijk = calculateNijk(node, parentConfig, k);
	
					//System.out.println(auxNijk  + " * log( " + auxNijk + " / " + Nij + " )");
	
					if(auxNijk != 0 && Nij !=0){ // arithmetics
						double auxDiv = (double) auxNijk / (double) Nij;
						loglike += (double) auxNijk * (Math.log(auxDiv) / (double)Math.log(2));
					}
					else loglike += 0;
				}
	
			}
		}

		
		
		return loglike;
	}
	
	//Net Complexity
	/**
	 * Arithmetic function to calculate the net complexity
	 * 
	 * @return double with the net complexity
	 */
	public double netComplexity(){ // Function to calculate parameter B, network complexity

		double complexity = 0;

		for(Node n: nodeList){ //for each node
			double maxParentConfigs = 1;
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
	/**
	 * Arithmetic function that calculates the minimum description length
	 * 
	 * @return double with the mdl score
	 */
	public double mdl(){ // Function that calculates MDL
//		System.out.println("LL " + this.logLike());
//		System.out.println("NLS " + this.nodeList.size());
//		System.out.println("LOG " + ((0.5)*Math.log(this.nodeList.size())/Math.log(2)));
//		System.out.println("B " + this.netComplexity());
		
		return (this.logLike() - (0.5) * (Math.log(this.nodeList.getFirst().data.length)/((double) Math.log(2)))*this.netComplexity());
	}
	
	
	//Given j-config returns array of j_i value of each parent
	/**
	 * Given j parent config, returns an array of each parent
	 * 
	 * 
	 * @param parentConfig father configuration to be used
	 * @param parents ArrayList of parents
	 * @return Integer[] 
	 */
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

	/**
	 * Function that calculates Nijk for a given node, parentConfig and value
	 * 
	 * This function runs all the parent's values, to find the wanted values. Then, it saves a TreeSet with the respective indexes of those values, and uses the retain function with the other parents Tree.
	 * It proceeds to calculate the number of values wanted in the childNode that correspond to the indexes contained in the father's TreeSet  
	 * 
	 * @param node node to which is intended to calculte Nijk
	 * @param parentConfig father configuration to be used
	 * @param valueK value of the wanted feature
	 * @return int with the node's Nijk
	 */
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

	/**
	 * Method that generates a random initial network
	 * 
	 * Random events are created with the Random class, to retrieve a node number to operate on
	 * 
	 */
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
		
	}
	
	
	// isDag 	
	/**
	 * Method with an algorithm that tests if a network is acyclic
	 * 
	 * Creats two LinkedLists, one with all visited nodes, and one with unvisited nodes, being the latter a copy of the original nodeList. The function visitNode complements this method
	 * 
	 *
	 * 
	 * @return boolean true if dag, false otherwise
	 */
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
	/**
	 * Function that recursively visits nodes and checks if they have been previously visited.
	 * 
	 * 
	 * @param node
	 * @param unmarked
	 * @param tempMarked
	 * @return
	 */
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		output.append("=== inter-slice connectivity \n");
		for(int index=getNumberOfNodes(); index<getNumberOfNodes()*2; index++){
			output.append(this.nodeList.get(index).name);
			output.append("_at_time-slice_t+1 : ");
			for(Edge e: this.edgeList){
				if(e.childNode == this.nodeList.get(index) && this.nodeList.indexOf(e.parentNode)<getNumberOfNodes()){
					output.append(this.nodeList.get(this.nodeList.indexOf(e.parentNode)).name+"_at_time-slice_t,");
				}
			}
			
			
			output.append("\n");
		}
		
		output.append("=== intra-slice connectivity \n");
		for(int index=getNumberOfNodes(); index<getNumberOfNodes()*2; index++){
			output.append(this.nodeList.get(index).name);
			output.append("_at_time-slice_t+1 : ");
			for(Edge e: this.edgeList){
				if(e.childNode == this.nodeList.get(index) && this.nodeList.indexOf(e.parentNode)>=getNumberOfNodes()){
					output.append(this.nodeList.get(this.nodeList.indexOf(e.parentNode)).name+"_at_time-slice_t+1,");
				}
			}
			
			
			output.append("\n");
		}
		
		output.append("=== Scores\n");
		output.append("LL score : " + this.logLike() + "\n");
		output.append("MDL score : " + this.mdl() + "\n");
		
		return output.toString();
	}
	
	
	//Get net that maximizes the score
	/**
	 * Function that manipulates the network, using the add, flip and remove steps.
	 * 
	 * This function tries each step and compares the obtained score to the previous steps( of the same kind. i.e. if a better add is found, it is saved over the last add). If better, the step is saved and built in to the new DynamicBayesNetwork
	 * 
	 * 																																																															ZÉÉÉÉÉÉÉ´ILUMINA-ME NOS IFS FINAIS
	 * 
	 * @return DynamicBayesNetwork Network with best score
	 */
	public DynamicBayesNetwork argMax(){
		DynamicBayesNetwork dbn = new DynamicBayesNetwork(this);
		
		double result = 0;
		double[] bestScore = new double[3];
		Edge[] changedEdge = new Edge[3];
		boolean firstScore = true;
		
		
		//Check scores of all add-moves
		
		for(Node n: dbn.nodeList){
			for(Node m: dbn.nodeList){	
		
				if(dbn.addEdge(n, m)){ // adds new edge
					if(dbn.isDag() != true){ // checks if net is a dag, if not, it is removed
						dbn.edgeList.removeLast();
					}
					else{
						if(firstScore){// dag is confirmed
							if(scoreFunction == 1)
								bestScore[0] = dbn.logLike();
							if(scoreFunction == 2)
								bestScore[0] = dbn.mdl(); // mdl score is saved in array
							changedEdge[0] = dbn.edgeList.getLast(); // stores the last changed edge
							firstScore = false;
						}
						else{// comparing new score with the previous
							double auxScore = 0;
							if(scoreFunction == 1)
								auxScore = dbn.logLike();
							if(scoreFunction == 2)
								auxScore = dbn.mdl(); // mdl score is saved in array
							
							
							if(auxScore > bestScore[0]){// if better score, store - and store best add-action
								bestScore[0] = auxScore;
								changedEdge[0] = dbn.edgeList.getLast();
							}
						}
						dbn.edgeList.removeLast();
					}
				}
			}
			
		}
		
		
//		System.out.println("EdgeList add: " + dbn.edgeList);
		
		dbn = new DynamicBayesNetwork(this);
		firstScore = true;
		Edge lastEdge = dbn.edgeList.getLast();
		
		//Check scores of all flip-moves
		for(int i=0; i< dbn.edgeList.size(); i++){
			
		
			Edge e = dbn.edgeList.getFirst();
			if(e != lastEdge && dbn.flipEdge(e.parentNode, e.childNode)){ 
//				System.out.println(dbn.edgeList);
				
				if(dbn.isDag() != true)// checks if net still a dag, if not, revert flip
					dbn.flipEdge(e.parentNode, e.childNode);
				
				else{
					
					if(firstScore){// dag is confirmed
						if(scoreFunction == 1)
							bestScore[1] = dbn.logLike();// mdl score is saved in array
						if(scoreFunction == 2)
							bestScore[1] = dbn.mdl();
						changedEdge[1] = e; // stores the last fliped edge
						firstScore = false;
					}
					else{// comparing new score with the previous
						double auxScore = 0;
						if(scoreFunction == 1)
							auxScore = dbn.logLike();// mdl score is saved in array
						if(scoreFunction == 2)
							auxScore = dbn.mdl();
						
						if(auxScore > bestScore[1]){// if better score, store
							bestScore[1] = auxScore;
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
		dbn = new DynamicBayesNetwork(this);
		lastEdge = dbn.edgeList.getLast();
		
		//System.out.println(dbn.edgeList);
		for(int i = 0; i<dbn.edgeList.size(); i++){
			
			Edge e = dbn.edgeList.getFirst();
			
			if(e==lastEdge){
				dbn.edgeList.remove(e);
				dbn.edgeList.addLast(e);
			}
			else{
				
				dbn.removeEdge(e.parentNode, e.childNode);
				// no need to check if it is still a dag
				if(firstScore){
					if(scoreFunction == 1)
						bestScore[2] = dbn.logLike();// mdl score is saved in array
					if(scoreFunction == 2)
						bestScore[2] = dbn.mdl();
					changedEdge[2] = e;// stores the last removed edge
					firstScore = false;
				}
				else{
					double auxScore = 0;
					if(scoreFunction == 1)
						auxScore = dbn.logLike();// mdl score is saved in array
					if(scoreFunction == 2)
						auxScore = dbn.mdl();
					
					if(auxScore > bestScore[2]){// if better score, store
						bestScore[2] = auxScore;
						changedEdge[2] = e; //overwrite last entry
					}
				}
	
				dbn.edgeList.addLast(e);
			}
			//System.out.println(dbn.edgeList);
		}
		//System.out.println(dbn.edgeList);
		
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
		 
		 
		
		//System.out.println("Accao " + bestScoreIndex);
		if(bestScoreIndex==0)
			dbn.edgeList.add(changedEdge[bestScoreIndex]);
		if(bestScoreIndex==1)
			dbn.flipEdge(changedEdge[bestScoreIndex].parentNode, changedEdge[bestScoreIndex].childNode);
		if(bestScoreIndex==2)
			dbn.edgeList.remove(changedEdge[bestScoreIndex]);

		return dbn;
	}
	
	/**
	 * Function that builds returns the best DynamicBayesNetwork 
	 * 
	 * @return	DynamicBayesNetwork the best score network
	 */
	public DynamicBayesNetwork bestNetwork(){
		
		DynamicBayesNetwork dbnFinal = new DynamicBayesNetwork(this);
		DynamicBayesNetwork dbnPrime = new DynamicBayesNetwork(this);
		
		boolean increasingResult = true;
		
		//System.out.println(this.toString());
		//System.out.println(this.mdl());
		
		
		while(increasingResult){
			DynamicBayesNetwork dbnPrime2 = new DynamicBayesNetwork(dbnPrime.argMax());
			//System.out.println("Meio " + dbnPrime2.edgeList);
			//System.out.println("Meio " + dbnPrime2.mdl());
			//System.out.println(dbnPrime2.logLike() + " " + dbnFinal.logLike());
			//System.out.println(dbnPrime2.mdl() + " " + dbnFinal.mdl());
			if(scoreFunction == 1){
				if(dbnPrime2.logLike() > dbnFinal.logLike())
					dbnFinal = new DynamicBayesNetwork(dbnPrime2);
				else
					increasingResult = false;
			}
			if(scoreFunction == 2){
				if(dbnPrime2.mdl() > dbnFinal.mdl())
					dbnFinal = new DynamicBayesNetwork(dbnPrime2);
				else
					increasingResult = false;
			}
			
			
			dbnPrime = new DynamicBayesNetwork(dbnPrime2);

		}
		
//		System.out.println(dbnFinal.edgeList);
//		System.out.println(dbnFinal.mdl());
		
		
		return dbnFinal;
		
	}


	/**
	 * Function that returns the parameters theta for all values a node can have given its parent configuration
	 * 
	 */
	public void calculateTijk() {
		double nprime = 0.5;
		
		int numberOfNodes = this.getNumberOfNodes();
		this.arrayOfThetas = new double[numberOfNodes][];
		
		ArrayList<Node>[] parents = new ArrayList[numberOfNodes];
		//double[] sizeOfTheta = new double[numberOfNodes];
		
		for(int i = 0; i<numberOfNodes; i++)
			parents[i] = new ArrayList<Node>();

		
		for(int i=numberOfNodes; i<numberOfNodes*2;i++){ //Each node
			for(Edge e: this.edgeList){
				if(e.childNode == this.nodeList.get(i)){
					parents[i-numberOfNodes].add(e.parentNode); //get parents
				}
			}
			int auxConfigs = this.nodeList.get(i).dataType;
			int maxParentConfigs = 1;
			for(Node parent: parents[i-numberOfNodes]){
				auxConfigs *= parent.dataType; //maxparentconfigs * datatype of node
				maxParentConfigs *= parent.dataType;
			}
			
			this.arrayOfThetas[i-numberOfNodes] = new double[auxConfigs]; //build theta array for node
			
			//calc Nij and calc Nijk
			for(int parentConfig=0; parentConfig < maxParentConfigs; parentConfig++){//cycle to run all parent configurations
				int Nij = 0;
				for(int k = 0; k < this.nodeList.get(i).dataType; k++){//cycle to run all the node's variables
					Nij += calculateNijk(this.nodeList.get(i), parentConfig, k);
				}

				

				for(int k = 0; k < this.nodeList.get(i).dataType; k++){ 
					int auxNijk = calculateNijk(this.nodeList.get(i), parentConfig, k);

					//System.out.println(auxNijk  + " + " + nprime + " / " + Nij + " + " + this.nodeList.get(i).dataType + " * " + nprime);
					this.arrayOfThetas[i-numberOfNodes][parentConfig*this.nodeList.get(i).dataType + k]=((double) (auxNijk + nprime))/ ((double) Nij+this.nodeList.get(i).dataType*nprime);
					
				}

			}
		}
		
	}

}
