package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class Dijkstra {
	private Network network;
	private List<Integer> VerticesTemporarlyLabeled;
	private Integer[] predecessor;
	private double[] distances;
	private double delta = 0;
	private int start;
	private int end;

	public void initialize(Network network, int start, int end) {
		this.network = network;
		this.start = start;
		this.end = end;
		
		this.predecessor = new Integer[network.getNbVertices()];
		this.distances = new double[network.getNbVertices()];
		
		for (int i = 0; i < network.getNbVertices(); ++i) {
			this.distances[i] = Double.MAX_VALUE;
			this.predecessor[i] = -1;
		}
		this.distances[this.start] = 0.0;
	}


	private int findMin(List<Integer> VerticesAvailable) {
		double min = this.distances[VerticesAvailable.get(0)];
		int closestVertex = VerticesAvailable.get(0);
		
		for(int vertex : VerticesAvailable) {
			if(this.distances[vertex] < min) {
				min = this.distances[vertex];
				closestVertex = vertex;
			}
		}
		return closestVertex;
	}
	
	public double[] getDistanceFromSource(){
		// note that distances of temporarly labeled vertices are set to the distance of the end vertex.
		return this.distances;
	}

	private void updateDist(int v1, int v2) {
		Edge lightestNonFullEdge = null;
		
		for(Edge edge:this.network.getEdges(v1, v2)) {
			if(edge.getResidualCapacity() > this.delta) {
				if( lightestNonFullEdge == null)
					lightestNonFullEdge = edge;
				else if(edge.getReducedCost() < lightestNonFullEdge.getReducedCost())
					lightestNonFullEdge = edge;
			}
		}
		
		if (this.distances[v2] > this.distances[v1] + lightestNonFullEdge.getReducedCost()) {
			
			this.distances[v2] = this.distances[v1] + lightestNonFullEdge.getReducedCost();
			this.predecessor[v2] = v1;
		}
	}

	
	private List<Edge> getShortestPathEdge() {
		List<Edge> shortestPath = new ArrayList<Edge>();
		
		int edgeDestination = this.end;
		while (this.predecessor[edgeDestination] != -1) {
			List<Edge> edges = network.getEdges(this.predecessor[edgeDestination], edgeDestination);
			Edge lightestNonFullEdge = null;
			
			for(Edge edge:edges) {
				
				if(edge.getResidualCapacity() > this.delta && lightestNonFullEdge == null) {
					lightestNonFullEdge = edge;
				}
				else if(edge.getResidualCapacity() > this.delta && edge.getReducedCost() < lightestNonFullEdge.getReducedCost())
					lightestNonFullEdge = edge;
			}
			
			shortestPath.add(lightestNonFullEdge);
			edgeDestination = this.predecessor[edgeDestination];
		}
		
		return shortestPath;
	}

	public List<Edge> solve(Network network, int start, int end) {
		initialize(network, start, end);
		List<Integer> VerticesAvailable = IntStream.rangeClosed(0, network.getNbVertices() - 1).boxed().collect(Collectors.toList());
		
		while (!VerticesAvailable.isEmpty()) {
			
			int vertex = findMin(VerticesAvailable);
			VerticesAvailable.remove((Object) vertex);
			
			if(vertex == end) { // We found the shortest path to end.
				setVerticesTemporarlyLabeled(VerticesAvailable);
				return getShortestPathEdge();
			} 
			
			if (network.getOutEdges(vertex) != null) {
				for (Edge outEdge : network.getOutEdges(vertex)) {
					if(network.getClass().getName() == "optim.flow.domain.ResidualNetwork") {
						if( outEdge.getResidualCapacity() > this.delta) {		
							updateDist(vertex, outEdge.getDestination());
						}
					}
					else
						updateDist(vertex, outEdge.getDestination());
				}
			}
		}
		throw new IllegalArgumentException("Vertex "+end+"should have been covered previously.\n");
	}
	
	private void setVerticesTemporarlyLabeled(List<Integer> verticesAvailable) {
		VerticesTemporarlyLabeled = verticesAvailable;
		// We set distances of all temporarly labeled vertices to the distance of the end (see book, page 323)
		for(Integer i:VerticesTemporarlyLabeled) {
			this.distances[i] = this.distances[this.end];
		}
	}


	// used in capacityScaling algorithm.
//	public List<Edge> solveWithDelta(Network network, int start, int end, double delta) {
//		this.delta = delta-1;
//		List<Edge> shortestPath = this.solve(network, start, end);
//		this.delta = 0;
//		return shortestPath;
//	}
	
	
	List<Edge> getShortestPathEdgeTo(int to){
		int tmp = this.end;
		this.end = to;
		List<Edge> PathTo = getShortestPathEdge();
		this.end = tmp;
		return PathTo;
	}
//  delta is a parameter such as only the edges of the graph with residual capacity > delta will be consider. initially = 0
//	carefull: > not >=.
	public void setDelta(double delta) {
		this.delta = delta;
	}

//	public List<Edge> getShortestPathEdgeToDelta(int j, double delta) {
//		this.delta = delta;
//		List<Edge> PathTo = getShortestPathEdgeTo(j);
//		this.delta = 1;
//		return PathTo;
//	}
}