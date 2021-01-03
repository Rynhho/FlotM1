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
				Edge oppositeEdge = new Edge(edge.getDestination(), s, 0, -edge.getCost());

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

		for (int source = 0; source < this.nbVertices; ++source) {
			this.adjacencyList.get(source).forEach(edge -> {
				addFlow(edge, flowMatrix[edge.getSource()][edge.getDestination()]);
			});
		}
	}

	public double getFlow(Edge edge) {
		if (this.network.hasEdgeBetween(edge.getSource(), edge.getDestination())) {
			throw new IllegalArgumentException(
					"ResidualNetwork::getFlow: edge parameter must be present in the principal network.\n");
		}

		// Principal network's edge's capacity - this network's edges's capacity
		return this.network.getEdges(edge.getSource(), edge.getDestination()).get(0).getCapacity() - edge.getCapacity();
	}

	public void addFlow(Edge edge, double flow) {
		Edge newEdge = new Edge(edge.getSource(), edge.getDestination(), edge.getCapacity() - flow, edge.getCost());

		Edge oppositeEdge = oppositeEdgesMap.get(edge);
		Edge newOppositeEdge = new Edge(oppositeEdge.getSource(), oppositeEdge.getDestination(),
				oppositeEdge.getCapacity() + flow, oppositeEdge.getCost());

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