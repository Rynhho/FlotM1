package optim.flow.domain;

import java.util.Map;
import java.util.HashMap;

public class ResidualNetwork extends Network {
	private final Network network;
	private final int nbEdges;

//	private final Map<Edge, Edge> oppositeEdgesMap;

	private final double[] verticesFlowIn;
	private final double[] verticesFlowOut;

	private int solutionCost;

	public ResidualNetwork(Network network) {
<<<<<<< HEAD
		super(network, true);
		this.network = network;
		this.nbEdges = 2 * network.nbEdges;

//		this.oppositeEdgesMap = new HashMap<Edge, Edge>();

//		for (int source = 0; source < network.nbVertices; ++source) {
//			final int s = source;
//			network.getOutEdges(source).stream().forEach(edge -> {
//				Edge oppositeEdge = new Edge(edge.getDestination(), s, 0, 0, 0, true);
//
//				this.adjacencyList.get(edge.getDestination()).add(oppositeEdge);
//
//				this.oppositeEdgesMap.put(edge, oppositeEdge);
//				this.oppositeEdgesMap.put(oppositeEdge, edge);
//			});
//		}
=======
		
		super(network);
		
		this.network = network;
		this.nbEdges = 2 * network.nbEdges;

		this.oppositeEdgesMap = new HashMap<Edge, Edge>();

		for (int source = 0; source < network.nbVertices; ++source) {
			final int s = source;
			network.getOutEdges(source).stream().forEach(edge -> {
				Edge oppositeEdge = new Edge(edge.getDestination(), s, 0, 0, 0, true);

				this.adjacencyList.get(edge.getDestination()).add(oppositeEdge);

				this.oppositeEdgesMap.put(edge, oppositeEdge);
				this.oppositeEdgesMap.put(oppositeEdge, edge);
				
			});
		}
>>>>>>> lucas

		this.verticesFlowIn = new double[this.nbVertices];
		this.verticesFlowOut = new double[this.nbVertices];

		this.solutionCost = 0;
	}

	public ResidualNetwork(Network network, double[][] flowMatrix) {
		
		this(network);

		if (flowMatrix.length != this.nbVertices) {
			throw new IllegalArgumentException("Network and flow matrix don't have the same number of vertices.\n");
		}
		// for (int source = 0; source < this.nbVertices; ++source) {
		// 	this.adjacencyList.get(source).forEach(edge -> {
		// 		addFlow(edge, flowMatrix[edge.getSource()][edge.getDestination()]);
		// 	});
		// }
		// dans ce nouvel opus de : "les iterator, cette belle invension", "supprimer et rajouter des elements de la liste que tu parcour, c'est pas une bonne idee :)"

		for (int source = 0; source < this.nbVertices; source++) {
			for (int dst = 0; dst < this.nbVertices; dst++) {
				for (int edge=0 ; edge < adjacencyList.get(source).size(); edge++){
					if (adjacencyList.get(source).get(edge).getDestination()==dst){
						addFlow(adjacencyList.get(source).get(edge), flowMatrix[source][dst]);
					}
				}
			}
		}
		//comme on crée et suprime des aretes avec addFlow, les indices se melangent ce qui gene le parcour :) donc 3 boucles for pour 3 x + de fun
	}
	
	public Edge getOppositeEdge(Edge edge) {
		return edge.getOppositeEdge();
	}
	
	public boolean isInOriginalNet(Edge edge) {
		return !edge.isResidual();
	}

	public double getFlow(Edge edge) {
		return edge.getFlow();
	}

	public Network getNetwork() {
		return this.network;
	}
	
	public double getCost() {
		double cost=0;
		for (int i = 0; i < this.getNbVertices(); i++) {
			for (Edge edge: this.getOutEdges(i)) {
//				System.out.println(edge);
//				edge.updateReducedCost(bellman.getDist().get(edge.getSource()) - bellman.getDist().get(edge.getDestination()));
//				System.out.println(edge+"\n");
//				if(sol.getFlow(edge) != 0)
//					if(sol.isInOriginalNet(edge))
						cost+= edge.getCost()*edge.getFlow();
			}
		}
		return cost;
	}
	
	public boolean isFeasible() {
		boolean isFeasible = true;
		for (int i = 0; i < this.getNbVertices(); i++) {
			double flowRemaing = -this.getVertexDemand(i);
			for(Edge edge:this.getInEdges(i) ){
				if(!edge.isResidual())
					flowRemaing+=edge.getFlow();
			}
			for(Edge edge:this.getOutEdges(i)) {
				if(!edge.isResidual())
					flowRemaing-=edge.getFlow();
			}
			if(flowRemaing!=0) {
//				System.out.println(flowRemaing +" remaining in vertex "+i+" should be 0.");
				return false;
			}
		}
		return isFeasible;
	}
	
	public void addFlow(Edge edge, double flow) {
		edge.addFlow(flow);
		this.verticesFlowOut[edge.getSource()] += flow;
		this.verticesFlowIn[edge.getDestination()] += flow;
	}

	public double getVertexFlowIn(int vertex) {
		return this.verticesFlowIn[vertex];
	}

	public double getVertexFlowOut(int vertex) {
		return this.verticesFlowOut[vertex];
	}
	
	@Override
	public String toString() {
		String str = new String();

		str += "Number of vertices: " + this.nbVertices + "\n";

		str += "Flow matrix:\n";
		for (int source = 0; source < this.nbVertices; ++source) {
			str += "[";
			int currentDestination = 0;
			for (Edge edge : this.getOutEdges(source)) {
				for (int i = currentDestination; i < edge.getDestination(); ++i) {
					str += "0.0, ";
				}

				str += String.format("%.2f", this.getFlow(edge)) + ", ";
			}

			for (int i = currentDestination; i < this.nbVertices; ++i) {
				str += "0.00, ";
			}

			str = str.substring(0, str.length() - 2) + "]\n";
		}

		return str;
	}
}
