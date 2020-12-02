package optim.flow.domain;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ResidualNetwork extends Network {
	private final Network network;

	private final double[][] flowMatrix;

	/*
	 * Auxiliary variables to speed up execution.
	 */
	// Todo: Implement
	private final double[] verticesFlowIn;
	private final double[] verticesFlowOut;
	private int solutionCost;

	private Map<Edge, Edge> oppositeEdgesTable = new HashMap<>();

	public ResidualNetwork(Network network) {
		super(network);

		this.network = network;
		this.flowMatrix = new double[this.nbVertices][this.nbVertices];

		this.verticesFlowIn = new double[this.nbVertices];
		this.verticesFlowOut = new double[this.nbVertices];

		// Todo: Parallelise
		for (int source = 0; source < this.network.nbVertices; ++source) {
			this.adjacencyList.clear();

			final int i = source;

			this.network.getOutEdges(source).stream().forEach(edge -> {
				Edge residualEdge = new Edge(edge.getDestination(), edge.getCost(), edge.getCapacity());
				Edge oppositeResidualEdge = new Edge(i, -edge.getCost(), 0);

				this.adjacencyList.get(i).add(residualEdge);
				this.adjacencyList.get(edge.getDestination()).add(oppositeResidualEdge);

				this.oppositeEdgesTable.put(residualEdge, oppositeResidualEdge);
				this.oppositeEdgesTable.put(oppositeResidualEdge, residualEdge);
			});
		}
	}

	public ResidualNetwork(Network network, double[][] flowMatrix) {
		super(network);

		this.network = network;
		this.flowMatrix = flowMatrix;

		this.verticesFlowIn = new double[this.nbVertices];
		this.verticesFlowOut = new double[this.nbVertices];
	}

	/**
	 * Returns the flow of a certain edge. Please verify it's a valid edge
	 * beforehand.
	 * 
	 * @param from Source vertex
	 * @param to   Destination vertex
	 * 
	 * @return The flow of the edge
	 */
	public double getEdgeFlow(int from, int to) {
		return this.flowMatrix[from][to];
	}

	/**
	 * Sets the flow of a certain edge. Please verify it's a valid edge beforehand.
	 * 
	 * @param from Source vertex
	 * @param to   Destination vertex
	 * @param flow The flow to be set
	 */
	public void setEdgeFlow(int from, int to, double flow) {
		this.flowMatrix[from][to] = flow;

		// Todo: Update verticesFlowIn and verticesFlowOut
	}

	/**
	 * @param vertex The vertex
	 * 
	 * @return The flow going into the vertex
	 */
	public double getVertexFlowIn(int vertexID) {
		// Todo: verticesFlowIn
		double flowIn = 0;
		for (int i = 0; i < this.nbVertices; ++i) {
			flowIn += this.flowMatrix[i][vertexID];
		}
		return flowIn;
	}

	/**
	 * @param vertex The vertex
	 * 
	 * @return The flow going out of the vertex
	 */
	public double getVertexFlowOut(int vertex) {
		// Todo: verticesFlowOut
		double flowOut = 0;
		for (int j = 0; j < this.nbVertices; ++j) {
			flowOut += this.flowMatrix[vertex][j];
		}
		return flowOut;
	}

	@Override
	public String toString() {
		String str = new String();

		str += "Number of vertices: " + this.nbVertices + "\n";

		str += "Flow matrix:\n";
		for (int i = 0; i < this.nbVertices; ++i) {
			str += "[";
			for (int j = 0; j < this.nbVertices - 1; ++j) {
				str += this.flowMatrix[i][j] + " ";
			}
			str += this.flowMatrix[i][this.nbVertices - 1];
			str += "]\n";
		}
		return str;
	}
}
