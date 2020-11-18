package optim.flow.domain;

import java.util.List;
import java.util.ArrayList;

public class Network {
	private final int nbVertices;
	private final int nbEdges;

	private final double maxCapacity;
	private final double maxCost;
	private final double maxDemand;

	private final List<List<Edge>> adjacencyList;
	private final double[] verticesDemands;

	/**
	 * Constructs a network with the parameters characteristics. The underlying data
	 * is generated randomly.
	 * 
	 * Condition 1: nbEdges <= 2 * nbVertices
	 * 
	 * @param nbVertices
	 * @param nbEdges
	 * @param maxCapacity
	 * @param maxCost
	 * @param maxDemand
	 */
	public Network(int nbVertices, int nbEdges, double maxCapacity, double maxCost, double maxDemand) {
		if (nbVertices <= 0 || nbEdges <= 0 || maxCapacity <= 0 || maxCost <= 0 || maxDemand <= 0) {
			throw new IllegalArgumentException("Network parameters must be positive.\n");
		}

		if (nbEdges > 2 * nbVertices) {
			throw new IllegalArgumentException("Condition 1 (nbEdges <= 2 * nbVertices) is not satisfied.\n");
		}

		this.nbVertices = nbVertices;
		this.nbEdges = nbEdges;

		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxDemand = maxDemand;

		this.adjacencyList = new ArrayList<List<Edge>>(nbVertices);
		for (int i = 0; i < nbVertices; ++i) {
			this.adjacencyList.add(null);
		}

		this.verticesDemands = new double[nbVertices];

		generateRandomData();
	}

	/**
	 * Creates a flow network from its corresponding adjacency list and vertices
	 * demands.
	 * 
	 * The adjacency list must be constructed before calling this constructor.
	 * 
	 * @param adjacencyList   Adjacency list
	 * @param verticesDemands Vertices demands
	 */
	public Network(List<List<Edge>> adjacencyList, double[] verticesDemands) {
		if (adjacencyList == null || verticesDemands == null) {
			throw new IllegalArgumentException("Network parameters must not be null.\n");
		}

		if (adjacencyList.size() != verticesDemands.length) {
			throw new IllegalArgumentException(
					"Network adjacency list and vertices demands array must have the same number of vertices.\n");
		}

		this.adjacencyList = adjacencyList;
		this.verticesDemands = verticesDemands;

		this.nbVertices = this.adjacencyList.size();

		double maxCapacity = this.adjacencyList.get(0).get(0).getCapacity();
		double maxCost = this.adjacencyList.get(0).get(0).getCost();
		double maxDemand = this.verticesDemands[0];

		int nbEdges = 0;
		for (int source = 0; source < this.nbVertices; ++source) {
			maxDemand = Math.max(maxDemand, this.verticesDemands[source]);

			for (Edge edge : getOutEdges(source)) {
				++nbEdges;

				maxCapacity = Math.max(maxCapacity, edge.getCapacity());
				maxCost = Math.max(maxCost, edge.getCost());
			}
		}

		this.nbEdges = nbEdges;

		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxDemand = maxDemand;
	}

	public int getNbVertices() {
		return this.nbVertices;
	}

	public double getVertexDemand(int vertexID) {
		return this.verticesDemands[vertexID];
	}

	public int getNbEdges() {
		return this.nbEdges;
	}

	public boolean hasEdgeBetween(int source, int destination) {
		return this.adjacencyList.get(source).parallelStream().anyMatch(edge -> {
			return edge.getDestination() == destination;
		});
	}

	public Edge getEdge(int source, int destination) {
		return this.adjacencyList.get(source).parallelStream().filter(edge -> {
			return edge.getDestination() == destination;
		}).findFirst().get();
	}

	public List<Edge> getOutEdges(int source) {
		return this.adjacencyList.get(source);
	}

	public double getMaxCapacity() {
		return this.maxCapacity;
	}

	public double getMaxCost() {
		return this.maxCost;
	}

	public double getMaxDemand() {
		return this.maxDemand;
	}

	private void generateRandomData() {
		for (int i = 0; i < this.adjacencyList.size(); ++i) {
			this.adjacencyList.set(i, new ArrayList<Edge>(1));
			this.adjacencyList.get(i).add(new Edge(i + 1, 1, 1));

			this.verticesDemands[i] = 0;
		}

		this.verticesDemands[0] = 1;
		this.verticesDemands[this.verticesDemands.length - 1] = 1;
	}

	// /**
	// * Generate random data set based on the network's caracteristics.
	// */
	// private void setRandomData() {
	// /* Demand vector */
	// for (int i = 0; i < this.nbVertices; i++) {
	// this.verticesDemands[i] = Math.random() * 2 * this.maxDemand -
	// this.maxDemand;
	// }

	// /* Capacity and cost matrices */
	// for (int i = 0; i < nbEdges; i++) {
	// int[] Coordinates = getEmptyCapacityMatrixCell();

	// this.capacityMatrix[Coordinates[0]][Coordinates[1]] = Math.random() *
	// maxCapacity;
	// this.costMatrix[Coordinates[0]][Coordinates[1]] = Math.random() * 2 * maxCost
	// - maxCost;
	// }
	// }

	// /**
	// * @return A cell from the capacity matrix that's empty.
	// */
	// private int[] getEmptyCapacityMatrixCell() {
	// int x, y;
	// do {
	// x = (int) Math.round(Math.random() * (nbVertices - 1));
	// y = (int) Math.round(Math.random() * (nbVertices - 1));
	// } while (this.capacityMatrix[x][y] != 0.0);
	// return new int[] { x, y };
	// }

	/**
	 * @param solution The solution to check
	 * @return true if the solution is realisable, else false.
	 */
	public boolean isSolutionValid(Solution solution) {
		if (this.nbVertices != solution.getNbVertices()) {
			System.out.println("Unvalid solution: Instance and solution must have the same number of vertices.\n");
			return false;
		}

		boolean shouldReturn = false;
		for (int source = 0; source < this.nbVertices; ++source) {
			for (Edge edge : this.getOutEdges(source)) {
				if (solution.getEdgeFlow(source, edge.getDestination()) > edge.getCapacity()) {
					System.out.println("Unvalid solution: Flow from #" + source + " to #" + edge.getDestination()
							+ " exceeds the edge's capacity.\n");
					shouldReturn = true;
				}
			}
		}

		if (shouldReturn) {
			return false;
		}

		for (int vertex = 0; vertex < this.nbVertices; ++vertex) {
			if (solution.getVertexFlowIn(vertex) - solution.getVertexFlowOut(vertex) < this.verticesDemands[vertex]) {
				System.out.println("Unvalid solution: Vertex #" + vertex + "'s demand isn't satisfied.\n");
				shouldReturn = true;
			}
		}

		if (shouldReturn) {
			return false;
		}

		return true;
	}

	/**
	 * @param solution The solution to calculate.
	 * @return The solution's cost.
	 */
	public double calculateSolutionCost(Solution solution) {
		double cost = 0;

		for (int source = 0; source < solution.getNbVertices(); ++source) {
			for (Edge edge : this.getOutEdges(source)) {
				cost += solution.getEdgeFlow(source, edge.getDestination()) * edge.getCost();
			}
		}

		return cost;
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
		for (int source = 0; source < this.nbVertices; ++source) {
			str += "[";
			int currentDestination = 0;
			for (Edge edge : this.getOutEdges(source)) {
				for (int i = currentDestination; i < edge.getDestination(); ++i) {
					str += "0.0, ";
				}

				str += String.format("%.2f", edge.getCapacity()) + ", ";
				currentDestination = edge.getDestination() + 1;
			}

			str = str.substring(0, str.length() - 2) + "]\n";
		}

		str += "\nCost matrix:\n";
		for (int source = 0; source < this.nbVertices; ++source) {
			str += "[";
			int currentDestination = 0;
			for (Edge edge : this.getOutEdges(source)) {
				for (int i = currentDestination; i < edge.getDestination(); ++i) {
					str += "0.0, ";
				}

				str += String.format("%.2f", edge.getCost()) + ", ";
				currentDestination = edge.getDestination() + 1;
			}

			str = str.substring(0, str.length() - 2) + "]\n";
		}

		str += "\nDemand vector:\n";
		str += "[";
		for (int i = 0; i < this.nbVertices; i++) {
			str += String.format("%.2f", this.verticesDemands[i]) + ", ";
		}
		str = str.substring(0, str.length() - 2) + "]\n";

		return str;
	}
}
