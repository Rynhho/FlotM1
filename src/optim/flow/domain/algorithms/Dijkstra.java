package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class Dijkstra {
	Network residualNetwork;
	int nbVertices;
	List<Double> dist;
	List<Integer> predecessor;
	int start;
	int end;

	public void initialize(Network network, int start, int end) {
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
		
		Edge lightestNonFullEdge = null;
		for(Edge edge:this.residualNetwork.getEdges(v1, v2)) {
			if(edge.getCapacity() - edge.getFlow() > 0) {
				if( lightestNonFullEdge == null) {
					lightestNonFullEdge = edge;
				}
				else if(edge.getCost() < lightestNonFullEdge.getCost())
					lightestNonFullEdge = edge;
			}
		}
		
		if (this.dist.get(v2) > this.dist.get(v1) + lightestNonFullEdge.getReducedCost()) {
			this.dist.set(v2, this.dist.get(v1) + lightestNonFullEdge.getReducedCost());
			this.predecessor.set(v2, v1);
		}
	}

	private List<Integer> getShortestPath() {
		List<Integer> shortestPath = new ArrayList<Integer>();
		if(this.predecessor.isEmpty()) {
			return shortestPath;
		}
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
		if(this.predecessor.isEmpty()) {
			System.out.println("empty shortest path in Dijkstra");
			return shortestPath;
		}
		int path = this.end;
		while (this.predecessor.get(path) != -1) {
			List<Edge> edges = residualNetwork.getEdges(this.predecessor.get(path), path);
			Edge lightestNonFullEdge = null;
			for(Edge edge:edges) {
				if(edge.getCapacity() - edge.getFlow() > 0 && lightestNonFullEdge == null) {
					lightestNonFullEdge = edge;
				}
				else if(edge.getCapacity() - edge.getFlow()>0 && edge.getCost() < lightestNonFullEdge.getCost())
					lightestNonFullEdge = edge;
			}
			shortestPath.add(lightestNonFullEdge);
			path = this.predecessor.get(path);
		}
		Collections.reverse(shortestPath);
		return shortestPath;
	}

	public List<Edge> solve(Network network, int start, int end) {
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
					if(network.getClass().getName() == "optim.flow.domain.ResidualNetwork") {
						if( outEdge.getCapacity() - outEdge.getFlow() >0) {		
							updateDist(vertex, outEdge.getDestination());
						}
					}
					else
						updateDist(vertex, outEdge.getDestination());
				}
			}

		}
		return getShortestPathEdge();
	}
}
