package bayesNetwork;

public class Score {
	

	public Score() {}

	public double logLike(BayesNetwork bn){ // Function to calculate log-likelihood
		int Nij = 0;
		double loglike = 0;
		int maxParentConfigs = 1;
		
		for(Node n: bn.nodeList){
			for(Edge e: bn.edgeList){ //for each edge
				if(e.childNode == n){ //if node is a child in that edge
					maxParentConfigs *= e.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
	
			for(int i=0; i< maxParentConfigs; i++){//cycle to run all parent configurations
				Nij = 0;
				for(int k = 0; k < n.dataType; k++){//cycle to run all the node's variables
					Nij += bn.calculateNijk(n, i, k);
				}
	
				for(int k = 0; k < n.dataType; k++){ // erhmmmm... what ? lol !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!???
					int auxNijk = bn.calculateNijk(n, i, k);
	
					//System.out.println(auxNijk  + " * log( " + auxNijk + " / " + Nij + " )");
	
					if(auxNijk != 0 && Nij !=0){ // arithmetics
						double auxDiv = (double) auxNijk / (double) Nij;
						loglike += (double) auxNijk * (Math.log(auxDiv) / Math.log(2));
					}
					else loglike += 0;
				}
	
			}
			maxParentConfigs = 1;
		}
		//System.out.println("LL= " + loglike);
		return loglike;
	}

	public double mdl(BayesNetwork bn){ // Function that calculates MDL
		return this.logLike(bn) - 1/2 * Math.log(bn.nodeList.size())*(double) this.netComplexity(bn);
	}

	public int netComplexity(BayesNetwork bn){ // Function to calculate parameter B, network complexity
		
		int maxParentConfigs = 1;
		int complexity = 0;
		
		for(Node n: bn.nodeList){ //for each node
			for(Edge e: bn.edgeList){ //for each edge
				if(e.childNode == n){ //if node is a child in that edge
					maxParentConfigs *= e.parentNode.dataType; //get parent dataType and multiply = q_i
				}
			}
			complexity += (n.dataType - 1) *  maxParentConfigs; //sum this for all nodes - get complexity of net
		}
		
		return complexity;
	}

	public BayesNetwork argMax(BayesNetwork bn2){
		BayesNetwork bn = new BayesNetwork(bn2);
		double result = 0;
		double[] bestScore = new double[3];
		Edge lastEdge = bn.edgeList.getLast();
		Edge[] changedEdge = new Edge[3];
		boolean firstScore = true;
		
		//Simple greedy hill climb
		//Generate a simple random starting network
		//this.randomNet();
		
		
		
		//Check scores of all add-moves
		for(Node n: bn.nodeList){
			for(Node m: bn.nodeList){
				
				if(bn.addEdge(n, m)){ // adds new edge
					if(bn.isDag() != true){ // checks if net is a dag, if not, it is removed
						bn.edgeList.removeLast();
					}
					else{
						if(firstScore){// dag is confirmed
							bestScore[0] = this.mdl(bn); // mdl score is saved in array
							changedEdge[0] = bn.edgeList.getLast(); // stores the last changed edge
							firstScore = false;
						}
						else{// comparing new score with the previous
							double auxMDL = this.mdl(bn);
							if(auxMDL > bestScore[0]){// if better score, store - and store best add-action
								bestScore[0] = auxMDL;
								changedEdge[0] = bn.edgeList.getLast();
							}
						}
						bn.edgeList.removeLast();
					}
				}
			}
		}
		
//		System.out.println("EdgeList add: " + bn.edgeList);
		
		
		firstScore = true;
		//Check scores of all flip-moves
		for(int i=0; i< bn.edgeList.size(); i++){
			Edge e = bn.edgeList.get(0);
			if(e != lastEdge){ 
				bn.flipEdge(e.parentNode, e.childNode); // flips edge
//				System.out.println(bn.edgeList);
				
				if(bn.isDag() != true)// checks if net still a dag, if not, revert flip
					bn.flipEdge(e.parentNode, e.childNode);
				
				else{
					if(firstScore){// dag is confirmed
						bestScore[1] = this.mdl(bn);// mdl score is saved in array
						changedEdge[1] = e; // stores the last fliped edge
						firstScore = false;
					}
					else{// comparing new score with the previous
						double auxMDL = this.mdl(bn);
						if(auxMDL > bestScore[1]){// if better score, store
							bestScore[1] = auxMDL;
							changedEdge[1] = e; //overwrite last entry
						}
					}
					bn.flipEdge(e.parentNode, e.childNode);
				}
			}
			else{
				bn.edgeList.remove(e);
				bn.edgeList.addLast(e);
			}
		}
		
//		System.out.println("EdgeList flip: " + bn.edgeList);
		
		//Check scores of all remove-moves
		firstScore = true;
		
		for(int i = 0; i<bn.edgeList.size(); i++){
			Edge e = bn.edgeList.getFirst();
			
			if(e==lastEdge){
				bn.edgeList.remove(e);
				bn.edgeList.addLast(e);
			}
			else{
				bn.removeEdge(e.parentNode, e.childNode);
				// no need to check if it is still a dag
				if(firstScore){
					bestScore[2] = this.mdl(bn);// mdl score is saved in array
					changedEdge[2] = e;// stores the last removed edge
					firstScore = false;
				}
				else{
					double auxMDL = this.mdl(bn);
					if(auxMDL > bestScore[2]){// if better score, store
						bestScore[2] = auxMDL;
						changedEdge[2] = e; //overwrite last entry
					}
				}
	
				bn.edgeList.addLast(e);
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
		 
		 
		
//		System.out.println(bestScoreIndex);
		bn.edgeList.add(changedEdge[bestScoreIndex]); 
		return bn;
	}

}
