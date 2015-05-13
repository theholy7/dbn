package bayesNetwork;

import java.util.LinkedList;


public class BayesNetwork {
	
	//Data a Bayesian Network needs
	int numberOfNodes;
	int dataLength;
	Integer[][] data;
	int[] dataType; //The r_i of the data -> dataType = 2 if data = {0,1} (binary)
	
	//Nodes and Edges a BN holds
	LinkedList<Node> nodeList = new LinkedList<Node>();
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	
	public BayesNetwork(int numberOfNodes, int dataLength, Integer[][] data, int[] dataType) {
		super();
		this.numberOfNodes = numberOfNodes;
		this.dataLength = dataLength;
		
		assert data.length == numberOfNodes;
		assert dataType.length == numberOfNodes;
		
		for(int i = 0; i < numberOfNodes; i++){
			assert data[i].length == dataLength;	
		}
		
		this.data = data;
		
		for(int i = 0; i < numberOfNodes; i++){
			Node node = new Node(dataType[i], data[i]);
			this.nodeList.add(node);
		}
		
	}
	
	void addEdge(Node parentNode, Node childNode){
		boolean edgeExists = false;
		
		//Check if Edge exists
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				edgeExists = true;
				break;
			}
		if(edgeExists == false){
			//Create a new Edge
			Edge edge = new Edge(parentNode, childNode);

			//Add Edge to nodes
			this.edgeList.add(edge);
		}
	}
	
	void removeEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				this.edgeList.remove(e);
			}
	}
	
	void flipEdge(Node parentNode, Node childNode){
		for(Edge e: this.edgeList)
			if(e.parentNode.equals(parentNode) && e.childNode.equals(childNode)){
				e.parentNode = childNode;
				e.childNode = parentNode;
			}
		
	}
	
	double private LLcalc(){
		int numOcorren=0;
		int nijk=0;
		int nij=0;
		int Nij=0;
		double Nijk=0; 
		double sumNijk=0; // uso double ou float ?
		
		for(Node n: this.nodeList){// percorre os nós
			for (Edge e: this.edgeList){//percorre os edges
				if(e.parentNode == n){
					int baseParent = n.dataType; // ficar a saber a quantidade de valores que xn toma
					int baseChild= e.childNode.dataType;
					for(int g=0; g<baseParent; g++){//percorre todos os valores possíveis para xn
						for(int j=0; j<=n.data.length;j++){//percorre vector de valores
							if(n.data[j]==g){// se encontrar valor no vector pai
								for(int h=0;h<baseChild;h++){ //percorre todos os valores possiveis do filho
									Nij=Nij+nij;
									for(int t=0;t<=e.childNode.data.length;t++){
										if(e.childNode.data[t]==h){
											nijk++;
											nij++;
											if(t==e.childNode.data.length){//ja viu todas as vezes que o filho toma determinado valor para determinado valor de pai
												sumNijk=sumNijk+Nijk*(Math.log(Nijk/Nij)/Math.log(2));
											}
										}
									}	
								}
							}
						}
						
					}
				}
				
			}
		}
		return sumNijk;
	}
	
	boolean isDag(){
		//Check if network is DAG
		return false;	
	}

}
