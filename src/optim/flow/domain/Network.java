package optim.flow.domain;

import java.util.List;
import java.util.ArrayList;

public class Network {
	protected final int nbVertices;
	protected final int nbEdges;

	protected final double maxCapacity;
	protected final double maxCost;
	protected final double maxDemand;

	protected final List<List<Edge>> adjacencyList;
	protected final double[] verticesDemands;

	public Network(int nbVertices, int nbEdges, double maxCapacity, double maxCost, double maxDemand, double pSource,
			double pSink) {
		
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

	public Network(List<List<Edge>> adjacencyList, double[] verticesDemands) {
		if (adjacencyList == null || verticesDemands == null) {
			throw new IllegalArgumentException("Network parameters must not be null.\n");
		}

		if (adjacencyList.size() != verticesDemands.length) {
			throw new IllegalArgumentException(
					"Network adjacency list and vertices demands array must have the same number of vertices.\n");
		}

		this.nbVertices = adjacencyList.size();

		// This kind of copy is enough as Edges are Value Objects
		this.verticesDemands = new double[this.nbVertices];
		this.adjacencyList = new ArrayList<>();
		for (int source = 0; source < this.nbVertices; ++source) {
			this.verticesDemands[source] = verticesDemands[source];

			this.adjacencyList.add(new ArrayList<>());
			for (Edge edge : adjacencyList.get(source)) {
				this.adjacencyList.get(source).add(edge);
			}
		}

		double maxCapacity = this.adjacencyList.get(0).get(0).getCapacity();
		double maxCost = this.adjacencyList.get(0).get(0).getCost();
		double maxDemand = this.verticesDemands[0];

		int nbEdges = 0;
		for (int source = 0; source < this.nbVertices; ++source) {
			maxDemand = Math.max(maxDemand, this.verticesDemands[source]);

			for (Edge edge : this.adjacencyList.get(source)) {
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

	public Network(Network network) {
		this.nbVertices = network.nbVertices;
		this.nbEdges = network.nbEdges;

		this.maxCapacity = network.maxCapacity;
		this.maxCost = network.maxCost;
		this.maxDemand = network.maxDemand;

		this.verticesDemands = new double[this.nbVertices];

		// This kind of copy is enough as Edges are Value Objects
		this.adjacencyList = new ArrayList<>();
		for (int source = 0; source < this.nbVertices; ++source) {
			this.verticesDemands[source] = network.verticesDemands[source];

			this.adjacencyList.add(new ArrayList<>());
			for (Edge edge : network.adjacencyList.get(source)) {
				this.adjacencyList.get(source).add(edge);
			}
		}
	}

	public int getNbVertices() {
		return this.nbVertices;
	}

	public int getNbEdges() {
		return this.nbEdges;
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

	public double getVertexDemand(int vertexID) {
		return this.verticesDemands[vertexID];
	}

	public boolean hasEdgeBetween(int source, int destination) {
		return this.adjacencyList.get(source).parallelStream().anyMatch(edge -> {
			return edge.getDestination() == destination;
		});
	}

	public List<Edge> getEdges(int source, int destination) {
		final List<Edge> edges = new ArrayList<>();

		this.adjacencyList.get(source).parallelStream().forEach(edge -> {
			if (edge.getDestination() == destination) {
				synchronized (edges) {
					edges.add(edge);
				}
			}
		});

		return edges;
	}

	public List<Edge> getInEdges(int destination) {
		final List<Edge> inEdges = new ArrayList<>();

		for (int source = 0; source < this.adjacencyList.size(); ++source) {
			this.adjacencyList.get(source).parallelStream().forEach(edge -> {
				if (edge.getDestination() == destination) {
					synchronized (inEdges) {
						inEdges.add(edge);
					}
				}
			});
		}

		return inEdges;
	}

	public List<Edge> getOutEdges(int source) {
		return this.adjacencyList.get(source);
	}

	private void generateRandomData() {
		for (int i = 0; i < this.adjacencyList.size(); ++i) {
			this.adjacencyList.set(i, new ArrayList<Edge>());
			this.adjacencyList.get(i).add(new Edge(i, i + 1, 1, 1));

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

	public boolean isSolutionValid(ResidualNetwork solution) {
		if (this.nbVertices != solution.getNbVertices()) {
			System.out.println("Unvalid solution: Instance and solution must have the same number of vertices.\n");
			return false;
		}

		boolean shouldReturn = false;
		for (int source = 0; source < this.nbVertices; ++source) {
			for (Edge edge : this.getOutEdges(source)) {
				if (solution.getFlow(edge) > edge.getCapacity()) {
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

	public double getSolutionCost(ResidualNetwork solution) {
		double cost = 0;

		for (int source = 0; source < solution.getNbVertices(); source++) {
			for (Edge edge : this.getOutEdges(source)) {
				cost += solution.getFlow(edge) * edge.getCost();
			}
		}

		return cost;
	}

	@Override
	public String toString() {
		String str = "Number of vertices: " + this.nbVertices + "\n";
		str += "Number of edges: " + this.nbEdges + "\n";

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

			for (int i = currentDestination; i < this.nbVertices; ++i) {
				str += "0.00, ";
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

			for (int i = currentDestination; i < this.nbVertices; ++i) {
				str += "0.00, ";
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
