package optim.flow.domain;

public class Solution {
	private final String ID;
	private final String networkID;

	private final int nbVertices;
	private final double[][] flowMatrix;

	/**
	 * Creates an empty solution.
	 * 
	 * @param nbVertices The number of vertices of the solution.
	 */
	public Solution(String ID, String networkID, int nbVertices) {
		this.ID = ID;
		this.networkID = networkID;

		this.nbVertices = nbVertices;
		this.flowMatrix = new double[nbVertices][nbVertices];
	}

	/**
	 * Creates a solution from a flow matrix.
	 * 
	 * @param flowMatrix The flow matrix.
	 */
	public Solution(String ID, String networkID, double[][] flowMatrix) {
		this.ID = ID;
		this.networkID = networkID;

		this.nbVertices = flowMatrix.length;
		this.flowMatrix = flowMatrix;
	}

	/**
	 * @return Solution's ID
	 */
	public String getID() {
		return this.ID;
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
