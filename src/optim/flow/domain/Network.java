package optim.flow.domain;

import java.util.List;
import java.util.ArrayList;

public class Network {
	protected int nbVertices;
	protected int nbEdges;

	protected double maxCapacity;
	protected double maxCost;
	protected double maxDemand;

	protected final List<List<Edge>> adjacencyList; // out edges
	protected final List<List<Edge>> reverseAdjacencyList; // in edges
	protected final List<List<List<Edge>>> fromToList; // in edges
	protected double[] verticesDemands;
	

	// deprecated, need to complete reverse adjacencylist in generate random data
//	public Network(int nbVertices, int nbEdges, double maxCapacity, double maxCost, double maxDemand, double pSource,
//			double pSink) {
//		
//		if (nbVertices <= 0 || nbEdges <= 0 || maxCapacity <= 0 || maxCost <= 0 || maxDemand <= 0) {
//			throw new IllegalArgumentException("Network parameters must be positive.\n");
//		}
//
//		if (nbEdges > 2 * nbVertices) {
//			throw new IllegalArgumentException("Condition 1 (nbEdges <= 2 * nbVertices) is not satisfied.\n");
//		}
//
//		this.nbVertices = nbVertices;
//		this.nbEdges = nbEdges;
//
//		this.maxCapacity = maxCapacity;
//		this.maxCost = maxCost;
//		this.maxDemand = maxDemand;
//
//		this.adjacencyList = new ArrayList<List<Edge>>(nbVertices);
//		this.reverseAdjacencyList = new ArrayList<List<Edge>>(nbVertices);
//		for (int i = 0; i < nbVertices; ++i) {
//			this.adjacencyList.add(null);
//			this.reverseAdjacencyList.add(null);
//		}
//
//		this.verticesDemands = new double[nbVertices];
//
//		generateRandomData();
//	}

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
		this.fromToList = new ArrayList<>();
		for (int source = 0; source < this.nbVertices; ++source) {
			this.adjacencyList.add(new ArrayList<>());
			this.fromToList.add(new ArrayList<List<Edge>>());
			for (int i = 0; i < this.nbVertices; ++i) {
				this.fromToList.get(source).add(new ArrayList<Edge>());
			}
		}
		double delta = 0;
		for (int source = 0; source < this.nbVertices; ++source) {
			
			this.verticesDemands[source] = verticesDemands[source];
			delta += verticesDemands[source];
			
			for (Edge edge : adjacencyList.get(source)) {
				this.fromToList.get(source).get(edge.getDestination()).add(edge);
				this.adjacencyList.get(source).add(edge);
			}
		}
		this.reverseAdjacencyList = new ArrayList<List<Edge>>(nbVertices);
		Edge firstEdge = null;
		for (int i = 0; i < this.adjacencyList.size(); ++i) {
			this.reverseAdjacencyList.add(new ArrayList<Edge>());
			if(!this.adjacencyList.get(i).isEmpty()) {
				firstEdge = this.adjacencyList.get(i).get(0);
//				break;
			}
			
			
		}
		if(firstEdge == null)
			throw new IllegalArgumentException("adjacencyList is empty.");
		double maxCapacity = firstEdge.getCapacity();
		double maxCost = firstEdge.getCost();
		double maxDemand = this.verticesDemands[0];

		int nbEdges = 0;
		for (int source = 0; source < this.nbVertices; ++source) {
			maxDemand = Math.max(maxDemand, this.verticesDemands[source]);

			for (Edge edge : this.adjacencyList.get(source)) {
				this.reverseAdjacencyList.get(edge.getDestination()).add(edge);
				++nbEdges;

				maxCapacity = Math.max(maxCapacity, edge.getCapacity());
				maxCost = Math.max(maxCost, edge.getCost());
			}
		}
		this.nbEdges = nbEdges;

		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxDemand = maxDemand;
		if (delta < 0) {
			this.addDumpNode(false);
		}else if(delta > 0) {
			throw new IllegalArgumentException("Demand higher than production");
		}
	}
//  combine edge with same source, destination and cost. Doesnt work 
	public Network reduceNetwork() {
		List<List<Edge>> adjacencyList = new ArrayList<List<Edge>>();
		for (int i = 0; i < this.nbVertices; ++i) {
			adjacencyList.add(new ArrayList<Edge>());
			for (int j = 0; j < this.nbVertices; j++) {
				List<Edge> edges = this.getEdges(i, j);
				while(!edges.isEmpty()) {					
					List<Edge> edgesWithSameCost = getEdgesWithCost(edges, edges.get(0).getCost());
					edges.removeAll(edgesWithSameCost);
					double totalCapacity = 0;
					for(Edge edge: edgesWithSameCost) {
						totalCapacity += edge.getCapacity();
					}
					adjacencyList.get(i).add(new Edge(i, j, totalCapacity, edgesWithSameCost.get(0).getCost()));
				}
				
			}
		}
		
		return new Network(adjacencyList, this.verticesDemands);
	}
	
//	for (int i = 0; i < this.nbVertices; ++i) {
//		for(int j = 0; j < this.nbVertices; j++) {
//			List<Edge> edges = this.getEdges(i, j);
//			List<Edge> edgesToAdd = new ArrayList<Edge>();
//			for(int k=edges.size()-1; k >=0 ; --k) {
//				Edge edgeConsidered = edges.get(k);
//				edges.remove(edgeConsidered);
//				edgesToAdd.add(edgeConsidered);
//				for(Edge edge:edges) {
//					if(edge.getCost() == edgeConsidered.getCost()) {
//						
//					}
//				}
//				if(!alreadyDone) {
//					List<Edge> edgesWithSameCost = getEdgesWithCost(edges, edges.get(k).getCost());
//					for(Edge edge:edgesWithSameCost) {
//						capacity += edge.getCapacity();
//					}
//					Edge edge = new Edge(edges.get(k).getSource(), edges.get(k).getDestination(), capacity, edges.get(k).getCost());
//					edgesToAdd.add(edge);
//					adjacencyList.get(k).addAll(edgesToAdd);
//				}
//			}
//			
//		}
//	}
	
	private List<Edge> getEdgesWithCost(List<Edge> edges, double cost) {
		List<Edge> toReturn = new ArrayList<Edge>();
		for(Edge edge:edges) {
			if(edge.getCost() == cost)
				toReturn.add(edge);
		}
		return toReturn;
	}
	
	public boolean isProdEqualsDemand() {
		double delta = 0 ;
		for (int i = 0; i < this.nbVertices; ++i) {
			delta += this.verticesDemands[i];			
		}
		return delta == 0;
	}
	
	public Network(Network network, boolean residual) {
		this.nbVertices = network.nbVertices;
		if(residual)
			this.nbEdges = 2*network.nbEdges;
		else
			this.nbEdges = network.nbEdges;
		

		this.maxCapacity = network.maxCapacity;
		this.maxCost = network.maxCost;
		this.maxDemand = network.maxDemand;

		this.verticesDemands = new double[this.nbVertices];

		// This kind of copy is enough as Edges are Value Objects
		this.adjacencyList = new ArrayList<>();
		this.reverseAdjacencyList = new ArrayList<List<Edge>>(nbVertices);
		this.fromToList = new ArrayList<List<List<Edge>>>();
		for (int source = 0; source < this.nbVertices; ++source) {
			this.adjacencyList.add(new ArrayList<>());
			this.reverseAdjacencyList.add(new ArrayList<Edge>());
			this.fromToList.add(new ArrayList<List<Edge>>());
			for (int i = 0; i < this.nbVertices; ++i) {
				this.fromToList.get(source).add(new ArrayList<Edge>());				
			}
		}
		double delta = 0;
		for (int source = 0; source < this.nbVertices; ++source) {
			this.verticesDemands[source] = network.verticesDemands[source];
			delta += this.verticesDemands[source];
			
			for (Edge edge : network.adjacencyList.get(source)) {
				addEdge(edge, residual);
			}
		}
		if (delta < 0) {
			this.addDumpNode(residual);
		}else if(delta > 0) {
			throw new IllegalArgumentException("Demand higher than production");
		}
//		for (int source = 0; source < this.nbVertices; ++source) {	
//			System.out.println(source);
//			for (Edge edge : this.adjacencyList.get(source)) {
//				System.out.println(edge);
//			}
//		}
	}
	public void setVerticesDemands(double[] verticesDemands) {
		this.verticesDemands = verticesDemands;
	}
	public Network(Network network) {
		this(network, false);
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

	public List<List<Edge>> getAdjacencyList() {
		return this.adjacencyList;
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
		return this.fromToList.get(source).get(destination);
//		final List<Edge> edges = new ArrayList<>();
//
////		this.adjacencyList.get(source).parallelStream().forEach(edge -> {
////			if (edge.getDestination() == destination) {
////				synchronized (edges) {
////					edges.add(edge);
////				}
////			}
////		});
//		for(Edge edge: this.adjacencyList.get(source)) {
//			if (edge.getDestination() == destination)
//				edges.add(edge);
//		}
//
//		return edges;
	}
	
	public void reinitFlow(){
			for (int source = 0; source < this.nbVertices; ++source) {	
				for (Edge edge : this.adjacencyList.get(0)) {
					edge.addFlow(-edge.getFlow());
				}
			}
		}
	
	public void addEdge(Edge edge, boolean withOppositeEdge) {
		this.nbEdges ++;
		this.adjacencyList.get(edge.getSource()).add(edge);
		this.reverseAdjacencyList.get(edge.getDestination()).add(edge);				
		this.fromToList.get(edge.getSource()).get(edge.getDestination()).add(edge);
		if(withOppositeEdge) {
			addEdge(edge.getOppositeEdge(), false);
		}
	}

	public void addDumpNode(boolean withResidualArcs) {
		System.out.println("		***dump node added***");
		this.nbVertices ++;
		this.adjacencyList.add(new ArrayList<Edge>());
		this.reverseAdjacencyList.add(new ArrayList<Edge>());
		this.fromToList.add(new ArrayList<List<Edge>>());
		
		double delta = 0;
		double[] copy = this.verticesDemands.clone();
		
		this.verticesDemands = new double[this.nbVertices];
		for (int i = 0; i < copy.length; i++) {
			this.fromToList.get(i).add(new ArrayList<Edge>());
			this.fromToList.get(this.nbVertices-1).add(new ArrayList<Edge>());
			delta -= copy[i];
			this.verticesDemands[i] = copy[i];
			if(this.verticesDemands[i] < 0) {
				
				Edge newEdge = new Edge(i, this.nbVertices-1, -this.verticesDemands[i], 0);
				
				this.reverseAdjacencyList.get(this.nbVertices-1).add(newEdge);
				this.adjacencyList.get(i).add(newEdge);
				this.fromToList.get(i).get(nbVertices-1).add(newEdge);
				if(withResidualArcs) {
					this.reverseAdjacencyList.get(i).add(newEdge.getOppositeEdge());
					this.adjacencyList.get(nbVertices-1).add(newEdge.getOppositeEdge());
					this.fromToList.get(nbVertices-1).get(i).add(newEdge.getOppositeEdge());
				}
				
				this.nbEdges +=1;
				this.maxCapacity = Math.max(this.maxCapacity, newEdge.getCapacity());
				
			}
		}
		this.fromToList.get(nbVertices-1).add(new ArrayList<Edge>());
		this.verticesDemands[this.verticesDemands.length-1] = delta;
		this.maxDemand = Math.max(delta, this.maxDemand);

	}
	
	public List<Edge> getInEdges(int destination) {
		return this.reverseAdjacencyList.get(destination);
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
	// for (int i = 0; i < this.nbVertices; ++i) {
	// this.verticesDemands[i] = Math.random() * 2 * this.maxDemand -
	// this.maxDemand;
	// }

	// /* Capacity and cost matrices */
	// for (int i = 0; i < nbEdges; ++i) {
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

	public void displayEdges(boolean withResidual) {
		for (int i = 0; i < this.getNbVertices(); ++i) {
			for (Edge edge: this.getOutEdges(i)) {
				if(withResidual || edge.isResidual() == false)
					System.out.println(edge+ " flow: "+edge.getFlow());
			}
		}
		System.out.println();
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
		for (int i = 0; i < this.nbVertices; ++i) {
			str += String.format("%.2f", this.verticesDemands[i]) + ", ";
		}
		str = str.substring(0, str.length() - 2) + "]\n";

		return str;
	}
}
