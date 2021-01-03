package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class Dijkstra {
	ResidualNetwork residualNetwork;
	int nbVertices;
	List<Double> dist;
	List<Integer> predecessor;
	int start;
	int end;

	public void initialize(ResidualNetwork network, int start, int end) {
		this.residualNetwork = network;
		this.start = start;
		this.end = end;
		this.nbVertices = network.getNbVertices();
		this.dist = new ArrayList<Double>();
		this.predecessor = new ArrayList<Integer>();

	}

	private void initializeLists() {
		for (int i = 0; i < this.nbVertices; ++i) {
			this.dist.add(Double.MAX_VALUE);
			this.predecessor.add(-1);
		}
		this.dist.set(this.start, 0.0);
	}

	private int findMin(List<Integer> VerticesAvailable) {
//<<<<<<< Updated upstream
//		double min = Double.MAX_VALUE;
//		int closestVertex = -1;
//		for (int vertex : VerticesAvailable) {
//			if (this.dist.get(vertex) < min) {
//=======
		double min = this.dist.get(VerticesAvailable.get(0));
		int closestVertex = VerticesAvailable.get(0);
		for(int vertex : VerticesAvailable) {
			if(this.dist.get(vertex) < min) {
//>>>>>>> Stashed changes
				min = this.dist.get(vertex);
				closestVertex = vertex;
			}
		}
		return closestVertex;
	}

	private void updateDist(int v1, int v2) {
		if (this.dist.get(v2) > this.dist.get(v1) + this.residualNetwork.getEdges(v1, v2).get(0).getCost()) {
			this.dist.set(v2, this.dist.get(v1) + this.residualNetwork.getEdges(v1, v2).get(0).getCost());
			this.predecessor.set(v2, v1);
		}
	}

	private List<Integer> getShortestPath() {
		List<Integer> shortestPath = new ArrayList<Integer>();
		int path = this.end;
		while (this.predecessor.get(path) != -1) {
			shortestPath.add(path);
			path = this.predecessor.get(path);
		}
		shortestPath.add(path);
		return shortestPath;
	}
	
	private List<Edge> getShortestPathEdge() {
		List<Edge> shortestPath = new ArrayList<Edge>();
		int path = this.end;
		while (this.predecessor.get(path) != -1) {
			shortestPath.add(residualNetwork.getEdges(this.predecessor.get(path), path).get(0));
			path = this.predecessor.get(path);
		}
		return shortestPath;
	}

	public List<Edge> solve(ResidualNetwork network, int start, int end) {
		initialize(network, start, end);
		initializeLists();
		List<Integer> VerticesAvailable = IntStream.rangeClosed(0, nbVertices - 1).boxed().collect(Collectors.toList());
		while (!VerticesAvailable.isEmpty()) {
			int vertex = findMin(VerticesAvailable);
			if (VerticesAvailable.contains(vertex)) {
				VerticesAvailable.remove((Object) vertex);
			}
			if (network.getOutEdges(vertex) != null) {
				for (Edge outEdge : network.getOutEdges(vertex)) {
					if(network.getFlow(outEdge) != outEdge.getCapacity()) {						
						updateDist(vertex, outEdge.getDestination());
					}
				}
			}

		}
		return getShortestPathEdge();
	}
}