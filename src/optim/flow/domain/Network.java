package optim.flow.domain;

import java.io.FileWriter;
import java.io.IOException;

public class Network {
	private final int nbVertices;
	private final int nbEdges;

	private final double[][] capacityMatrix;
	private final double[][] costMatrix;

	private final double[] verticesDemand;

	private final double maxCapacity;
	private final double maxCost;
	private final double maxDemand;

	/**
	 * Constructs a network with the parameters characteristics. The underlying data
	 * is generated randomly.
	 * 
	 * @param nbVertices
	 * @param nbEdges
	 * @param maxCapacity
	 * @param maxCost
	 * @param maxDemand
	 */
	public Network(int nbVertices, int nbEdges, double maxCapacity, double maxCost, double maxDemand) {
		this.nbVertices = nbVertices;
		this.nbEdges = nbEdges;

		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxDemand = maxDemand;

		this.capacityMatrix = new double[nbVertices][nbVertices];
		this.costMatrix = new double[nbVertices][nbVertices];
		this.verticesDemand = new double[nbVertices];

		setRandomData();
	}

	/**
	 * Creates a flow network from its corresponding data matrices.
	 * 
	 * @param capacityMatrix Capacity matrix
	 * @param costMatrix     Cost matrix
	 * @param verticesDemand Vertices demand
	 */
	public Network(double[][] capacityMatrix, double[][] costMatrix, double[] verticesDemand) {
		this.capacityMatrix = capacityMatrix;
		this.costMatrix = costMatrix;
		this.verticesDemand = verticesDemand;

		this.nbVertices = capacityMatrix.length;
		this.nbEdges = getNbEdgesFromCapacity();

		double maxCapacity = this.capacityMatrix[0][0];
		double maxCost = this.costMatrix[0][0];
		double maxDemand = this.verticesDemand[0];

		for (int i = 0; i < this.nbVertices; i++) {
			maxDemand = Math.max(maxDemand, this.verticesDemand[i]);

			for (int j = 0; j < this.nbVertices; j++) {
				maxCapacity = Math.max(maxCapacity, this.capacityMatrix[i][j]);
				maxCost = Math.max(maxCost, this.costMatrix[i][j]);
			}
		}

		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxDemand = maxDemand;
	}

	/**
	 * Verifies if an edge is valid or not. Note that if the edge doesn't exist but
	 * the parameters are valid, then the edge is valid but with a cost of zero.
	 * 
	 * @param from Source vertex
	 * @param to   Destination vertex
	 * 
	 * @return true if it's valid, else false.
	 */
	public boolean isEdgeValid(int from, int to) {
		return from >= 0 && from < this.nbVertices && to >= 0 && from < this.nbVertices;
	}

	/**
	 * Verifies if a vertex ID is within the network's vertices range.
	 * 
	 * @param vertexID The ID to verify.
	 * 
	 * @return true if the vertex is valid, else false.
	 */
	public boolean isVertexIDValid(int vertexID) {
		return vertexID >= 0 && vertexID < this.nbVertices;
	}

	/**
	 * @return The number of vertices in the network.
	 */
	public int getNbVertices() {
		return this.nbVertices;
	}

	/**
	 * @param vertexID The vertex ID.
	 * @return The vertex demand.
	 */
	public double getVertexDemand(int vertexID) {
		return this.verticesDemand[vertexID];
	}

	/**
	 * @return The number of edges in the network.
	 */
	public int getNbEdges() {
		return this.nbEdges;
	}

	/**
	 * @param from Source vertex
	 * @param to   Destination vertex
	 * 
	 * @return The capacity of the corresponding edge.
	 */
	public double getEdgeCapacity(int from, int to) {
		return capacityMatrix[from][to];
	}

	/**
	 * @param from Source vertex
	 * @param to   Destination vertex
	 * 
	 * @return The cost of the corresponding edge.
	 */
	public double getEdgeCost(int from, int to) {
		return costMatrix[from][to];
	}

	/**
	 * Checks if the network is valid or not.
	 * 
	 * @return true if the network is valid, false otherwise.
	 */
	public boolean checkValidity() {
		return verifyPositivity() && verifyMatricesDimensions() && verifyZeroCapacityEdges() && verifyVerticesDemands();
	}

	/**
	 * Verifies if the network caracteristics are valid.
	 * 
	 * @return true if they are, else false.
	 */
	private boolean verifyPositivity() {
		if (this.nbVertices <= 0) {
			System.out.println("Error: Vertex Number <= 0");
			return false;
		}
		if (this.nbEdges <= 0) {
			System.out.println("Error: Edges Number <= 0");
			return false;
		}
		if (this.maxCapacity <= 0) {
			System.out.println("Error: Maximum Capacity <= 0");
			return false;
		}
		if (this.maxCost <= 0) {
			System.out.println("Error: Maximum Cost <= 0");
			return false;
		}
		if (this.maxDemand <= 0) {
			System.out.println("Error: Maximum Consumption <= 0");
			return false;
		}
		if (this.nbEdges > this.nbVertices * this.nbVertices) {
			System.out.println("Error: Edges Number > Vertex_Number^2");
			return false;
		}

		return true;
	}

	/**
	 * Verify data matrices dimensions.
	 * 
	 * @return true if it's valid, else false.
	 */
	private boolean verifyMatricesDimensions() {
		int nbVertex = this.capacityMatrix.length;

		if (nbVertex != this.costMatrix.length) {
			System.out.println("Error: Capacity and cost matrices don't have the same dimensions.\n");
			return false;
		}

		if (nbVertex != this.verticesDemand.length) {
			System.out.println("Error: Capacity matrix and Demand vector don't have the same length.\n");
			return false;
		}

		for (int i = 0; i < nbVertex; ++i) {
			if (nbVertex != this.capacityMatrix[i].length) {
				System.out.println("Error: Capacity matrix isn't a square matrix.\n");
				return false;
			}

			if (nbVertex != this.costMatrix[i].length) {
				System.out.println("Error: Cost matrix isn't a square matrix.\n");
				return false;
			}
		}

		return true;
	}

	/**
	 * Not unvaliding but having a cost on a zero-capacity edge should issue a
	 * warning.
	 * 
	 * @return Always true
	 */
	private boolean verifyZeroCapacityEdges() {
		for (int i = 0; i < this.costMatrix.length; ++i) {
			for (int j = 0; j < this.costMatrix[i].length; ++j) {
				if (this.capacityMatrix[i][j] == 0 && this.costMatrix[i][j] != 0)
					System.out.println("Warning: Edge from " + i + " to " + j
							+ " has zero capacity but non-zero cost, potentially increasing non-polynomials algorithms complexity.\n");
			}
		}

		return true;
	}

	/**
	 * Verify that all vertices costs are low enough for its entering edge's
	 * capacities.
	 * 
	 * @return true if all demands are valid, else false.
	 */
	private boolean verifyVerticesDemands() {
		for (int vertexID = 0; vertexID < verticesDemand.length; ++vertexID) {
			int maxFlowIn = 0;
			for (int i = 0; i < capacityMatrix.length; ++i) {
				maxFlowIn += capacityMatrix[i][vertexID];
			}

			if (maxFlowIn < verticesDemand[vertexID]) {
				System.out.println("Error: Vertex #" + vertexID
						+ " cannot be satisfied. Its entering edges' capacities sum are lower than its demand.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Generate random data set based on the network's caracteristics.
	 */
	private void setRandomData() {
		/* Demand vector */
		for (int i = 0; i < this.nbVertices; i++) {
			this.verticesDemand[i] = Math.random() * 2 * this.maxDemand - this.maxDemand;
		}

		/* Capacity and cost matrices */
		for (int i = 0; i < nbEdges; i++) {
			int[] Coordinates = getEmptyCapacityMatrixCell();

			this.capacityMatrix[Coordinates[0]][Coordinates[1]] = Math.random() * maxCapacity;
			this.costMatrix[Coordinates[0]][Coordinates[1]] = Math.random() * 2 * maxCost - maxCost;
		}
	}

	/**
	 * @return A cell from the capacity matrix that's empty.
	 */
	private int[] getEmptyCapacityMatrixCell() {
		int x, y;
		do {
			x = (int) Math.round(Math.random() * (nbVertices - 1));
			y = (int) Math.round(Math.random() * (nbVertices - 1));
		} while (this.capacityMatrix[x][y] != 0.0);
		return new int[] { x, y };
	}

	/**
	 * @return The number of edges based on capacity matrix.
	 */
	private int getNbEdgesFromCapacity() {
		int nbEdges = 0;
		for (int i = 0; i < this.nbVertices; i++) {
			for (int j = 0; j < this.nbVertices; j++) {
				if (capacityMatrix[i][j] != 0)
					nbEdges += 1;
			}
		}
		return nbEdges;
	}

	public void save(String filename) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(this.toString());
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a human readable string of the network. Can also be used to save
	 * into a file.
	 * 
	 * @return The string to be displayed
	 */
	@Override
	public String toString() {
		String str = "Number of vertices: " + this.nbVertices + "\n";
		str += "Number of edges: " + this.nbVertices + "\n";

		str += "\nCapacity matrix:\n";
		for (int i = 0; i < this.nbVertices; ++i) {
			str += "[";
			for (int j = 0; j < this.nbVertices; ++j) {
				str += String.format("%.2f", this.capacityMatrix[i][j]) + ", ";
			}
			str = str.substring(0, str.length() - 2) + "]\n";
		}

		str += "\nCost matrix:\n";
		for (int i = 0; i < this.nbVertices; ++i) {
			str += "[";
			for (int j = 0; j < this.nbVertices; ++j) {
				str += String.format("%.2f", this.costMatrix[i][j]) + ", ";
			}
			str = str.substring(0, str.length() - 2) + "]\n";
		}

		str += "\nDemand vector:\n";
		str += "[";
		for (int i = 0; i < this.nbVertices; i++) {
			str += String.format("%.2f", this.verticesDemand[i]) + ", ";
		}
		str = str.substring(0, str.length() - 2) + "]\n";

		return str;
	}

	// public boolean Checker(Solution sol) {
	// if (sol.getnbVertices() != this.nbVertices) {
	// System.out.println("wrong size in Checker");
	// return false;
	// }

	// for (int i = 0; i < sol.getnbVertices(); i++) {

	// if (sol.getValueOut(i) < sol.getValueIn(i) + this.verticesDemand[i])
	// return false;

	// for (int j = 0; j < sol.getnbVertices(); j++) {
	// if (sol.getEdgesFlowsAt(i, j) > this.capacityMatrix[i][j])
	// return false;
	// }
	// }
	// return true;
	// }

	// public double SolutionCost(double[][] solution) {
	// double cost = 0;
	// for (int i = 0; i < solution.length; i++) {
	// for (int j = 0; j < solution.length; j++) {
	// cost += solution[i][j] * this.costMatrix[i][j];
	// }
	// }
	// return cost;
	// }
}
