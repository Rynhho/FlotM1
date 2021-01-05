package optim.flow.domain;

import java.util.Map;
import java.util.HashMap;

public class ResidualNetwork extends Network {
	private final Network network;
	private final int nbEdges;

	private final Map<Edge, Edge> oppositeEdgesMap;

	private final double[] verticesFlowIn;
	private final double[] verticesFlowOut;

	private int solutionCost;

	public ResidualNetwork(Network network) {
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
		return this.oppositeEdgesMap.get(edge);
	}
	
	public boolean isInOriginalNet(Edge edge) {
		return this.network.hasEdgeBetween(edge.getSource(), edge.getDestination());
	}

	public double getFlow(Edge edge) {
//		if (this.network.hasEdgeBetween(edge.getSource(), edge.getDestination())) {
//			throw new IllegalArgumentException(
//					"ResidualNetwork::getFlow: edge parameter must be present in the principal network.\n");
//		}

		// Principal network's edge's capacity - this network's edges's capacity
		// may cause bug if edge is in residual (?)
		for(Edge e:this.network.getEdges(edge.getSource(), edge.getDestination())) {
			if(e.isResidual() == edge.isResidual()) {
				return e.getCapacity() - edge.getCapacity();
			}
		}
		return 0;
	}

	public Network getNetwork() {
		return this.network;
	}
	
	public void addFlow(Edge edge, double flow) {
		
		Edge newEdge = new Edge(edge.getSource(), edge.getDestination(), edge.getCapacity() - flow, edge.getCost(), edge.getReducedCost(), edge.isResidual());

		Edge oppositeEdge = oppositeEdgesMap.get(edge);
		Edge newOppositeEdge = new Edge(oppositeEdge.getSource(), oppositeEdge.getDestination(),
				oppositeEdge.getCapacity() + flow, oppositeEdge.getCost(), oppositeEdge.getReducedCost(), oppositeEdge.isResidual());

		this.adjacencyList.get(edge.getSource()).remove(edge);
		this.adjacencyList.get(oppositeEdge.getSource()).remove(oppositeEdge);

		this.adjacencyList.get(edge.getSource()).add(newEdge);
		this.adjacencyList.get(oppositeEdge.getSource()).add(newOppositeEdge);

		this.oppositeEdgesMap.remove(edge);
		this.oppositeEdgesMap.remove(oppositeEdge);

		this.oppositeEdgesMap.put(newEdge, newOppositeEdge);
		this.oppositeEdgesMap.put(newOppositeEdge, newEdge);

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
