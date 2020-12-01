package optim.flow.domain;

public class Solution {
	private final String networkID;

	private final int nbVertices;
	private final double[][] flowMatrix;

	/*
	 * Auxiliary variables to speed up execution.
	 */
	private final double[] verticesFlowIn;
	private final double[] verticesFlowOut;

	/**
	 * Creates an empty solution.
	 * 
	 * @param nbVertices The number of vertices of the solution.
	 */
	public Solution(String networkID, int nbVertices) {
		this.networkID = networkID;

		this.nbVertices = nbVertices;
		this.flowMatrix = new double[nbVertices][nbVertices];

		this.verticesFlowIn = new double[nbVertices];
		this.verticesFlowOut = new double[nbVertices];
	}

	/**
	 * Creates a solution from a flow matrix.
	 * 
	 * @param flowMatrix The flow matrix.
	 */
	public Solution(String networkID, double[][] flowMatrix) {
		this.networkID = networkID;

		this.nbVertices = flowMatrix.length;
		this.flowMatrix = flowMatrix;

		this.verticesFlowIn = new double[nbVertices];
		this.verticesFlowOut = new double[nbVertices];

		for (int source = 0; source < nbVertices; ++source) {
			for (int destination = 0; destination < nbVertices; ++destination) {
				this.verticesFlowIn[destination] += this.flowMatrix[source][destination];
				this.verticesFlowIn[source] += this.flowMatrix[source][destination];
			}
		}
	}

	/**
	 * Returns the ID of the network linked with this solution.
	 * 
	 * @return The network ID.
	 */
	public String getNetworkID() {
		return networkID;
	}

	/**
	 * Returns size of the solution
	 */
	public int getNbVertices() {
		return this.nbVertices;
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
