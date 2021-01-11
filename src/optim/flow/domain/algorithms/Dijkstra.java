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
	Network network;
	List<Double> dist;
	List<Integer> predecessor;
	List<Integer> VerticesTemporarlyLabeled;
	double[] distances;
	int start;
	int end;

	public void initialize(Network network, int start, int end) {
		this.network = network;
		this.start = start;
		this.end = end;
		
		this.dist = new ArrayList<Double>();
		this.predecessor = new ArrayList<Integer>();
		
		//initialize the dist and tthe predecessor array
		for (int i = 0; i < network.getNbVertices(); ++i) {
			this.dist.add(Double.MAX_VALUE);
			this.predecessor.add(-1);
		}
		this.dist.set(this.start, 0.0);
	}


	private int findMin(List<Integer> VerticesAvailable) {
		double min = this.dist.get(VerticesAvailable.get(0));
		int closestVertex = VerticesAvailable.get(0);
		
		for(int vertex : VerticesAvailable) {
			if(this.dist.get(vertex) < min) {
				min = this.dist.get(vertex);
				closestVertex = vertex;
			}
		}
		return closestVertex;
	}
	
	public List<Double> getDistanceFromSource(){
		// note that distances of temporarly labeled vertices are set to the distance of the end vertex.
		return this.dist;
	}

	private void updateDist(int v1, int v2) {
		Edge lightestNonFullEdge = null;
		
		for(Edge edge:this.network.getEdges(v1, v2)) {
			if(edge.getResidualCapacity() > 0) {
				if( lightestNonFullEdge == null)
					lightestNonFullEdge = edge;
				else if(edge.getReducedCost() < lightestNonFullEdge.getReducedCost())
					lightestNonFullEdge = edge;
			}
		}
		
		if (this.dist.get(v2) > this.dist.get(v1) + lightestNonFullEdge.getReducedCost()) {
			
			this.dist.set(v2, this.dist.get(v1) + lightestNonFullEdge.getReducedCost());
			this.predecessor.set(v2, v1);
		}
	}

	
	private List<Edge> getShortestPathEdge() {
		List<Edge> shortestPath = new ArrayList<Edge>();
		
		if(this.predecessor.isEmpty())
			throw new IllegalArgumentException("empty shortest path in Dijkstra\n");
		
		int edgeDestination = this.end;
		int cap = network.getNbVertices();
		while (this.predecessor.get(edgeDestination) != -1) {
			List<Edge> edges = network.getEdges(this.predecessor.get(edgeDestination), edgeDestination);
			Edge lightestNonFullEdge = null;
			
			for(Edge edge:edges) {
				
				if(edge.getResidualCapacity() > 0 && lightestNonFullEdge == null) {
					lightestNonFullEdge = edge;
				}
				else if(edge.getResidualCapacity() > 0 && edge.getCost() < lightestNonFullEdge.getCost())
					lightestNonFullEdge = edge;
			}
			
			shortestPath.add(lightestNonFullEdge);
			edgeDestination = this.predecessor.get(edgeDestination);
			cap -=1;
			if(cap == 0) {
//				System.out.println(predecessor);
//				network.displayEdges(true);
				System.out.println();
				System.out.println("start: "+start+" end: "+end);
				System.out.println(predecessor.get(start));
				throw new IllegalArgumentException("Dijkstra problem with shortest path: "+this.predecessor);
			}
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
				VerticesTemporarlyLabeled = VerticesAvailable;
				// We set distances of all temporarly labeled vertices to the distance of the end (see book, page 323)
				for(Integer i:VerticesTemporarlyLabeled) {
					this.dist.set(i, this.dist.get(this.end));
				}
				return getShortestPathEdge();
			}
			
			if (network.getOutEdges(vertex) != null) {
				for (Edge outEdge : network.getOutEdges(vertex)) {
					if(network.getClass().getName() == "optim.flow.domain.ResidualNetwork") {
						if( outEdge.getResidualCapacity() >0) {		
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
	
	
	
	// used in capacityScaling algorithm.
	public List<Edge> solveWithDelta(Network network, int start, int end, double Delta) {
		initialize(network, start, end);
		List<Integer> VerticesAvailable = IntStream.rangeClosed(0, this.network.getNbVertices() - 1).boxed().collect(Collectors.toList());
		
		while (!VerticesAvailable.isEmpty()) {
			int vertex = findMin(VerticesAvailable);
			VerticesAvailable.remove((Object) vertex);

			if (network.getOutEdges(vertex) != null) {
				for (Edge outEdge : network.getOutEdges(vertex)) {
//					if(network.getClass().getName() == "optim.flow.domain.ResidualNetwork") {
//						if( outEdge.getResidualCapacity() >= Delta) {		
//							updateDist(vertex, outEdge.getDestination());
//							System.out.println("couocuo");
//						}
//					}
//					else if(outEdge.getResidualCapacity() >= Delta) {
						
						updateDist(vertex, outEdge.getDestination());

//					}
//					else
//						System.out.println("lala");
						
				}
			}

		}
		return getShortestPathEdge();
	}
	
	List<Edge> getShortestPathEdgeTo(int to){
		int tmp = this.end;
		this.end = to;
		List<Edge> PathTo = getShortestPathEdge();
		this.end = tmp;
		return PathTo;
	}
}