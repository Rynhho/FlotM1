package optim.flow.domain;

public class Solution {
	private final int nbVertices;
	private final double[][] flowMatrix;

	/**
	 * Creates an empty solution.
	 * 
	 * @param nbVertices The number of vertices of the solution.
	 */
	public Solution(int nbVertices) {
		this.nbVertices = nbVertices;
		this.flowMatrix = new double[nbVertices][nbVertices];
	}

	/**
	 * Creates a solution from a flow matrix.
	 * 
	 * @param flowMatrix The flow matrix.
	 */
	public Solution(double[][] flowMatrix) {
		this.nbVertices = flowMatrix.length;
		this.flowMatrix = flowMatrix;
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
}
