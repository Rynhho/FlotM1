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
import optim.flow.ui.HeapNode;
import optim.flow.ui.HeapTree;

public class Dijkstra {
	private Network network;
	private List<Integer> VerticesAvailable;
//	private HeapTree VerticesAvailable;
	private List<Edge> shortestPath;
	private Integer[] predecessor;
	private double[] distances;
	private double delta = 0;
	private int start;
	private int end;
	public Dijkstra() {
		this.shortestPath = new ArrayList<Edge>();
	}
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
//		this.VerticesAvailable = new HeapTree(distances);
	}


//		private int findMin() {
//		System.out.println(this.VerticesAvailable.get0()+distances[this.VerticesAvailable.get0()]);
//		return this.VerticesAvailable.poll();
	private int findMin() {
		double min = this.distances[VerticesAvailable.get(0)];
		int closestVertex = VerticesAvailable.get(0);
		
		for(int vertex : VerticesAvailable) {
			if(this.distances[vertex] < min) {
				min = this.distances[vertex];
				closestVertex = vertex;
			}
		}
		VerticesAvailable.remove((Object) closestVertex);
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
//			System.out.println(v2+" dist up");
//			System.out.print("vertex "+v2+" dist "+ this.distances[v2]+" updated to ");
			this.distances[v2] = this.distances[v1] + lightestNonFullEdge.getReducedCost();
//			this.VerticesAvailable.updateDist(v2, this.distances[v2]);
			this.predecessor[v2] = v1;
//			System.out.println(this.distances[v2] + " with vertex "+v1);
		}
	}

	
	private List<Edge> getShortestPathEdge() {
		this.shortestPath.clear();
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
//		System.out.println(shortestPath);
		return this.shortestPath;
	}

	public List<Edge> solve(Network network, int start, int end) {
//		System.out.println("dijkstra begin:");
//		network.displayEdges(false);
		initialize(network, start, end);
//		this.VerticesAvailable.reset(distances);
//		while(VerticesAvailable.get0() != -1) {
		
		this.VerticesAvailable = IntStream.rangeClosed(0, network.getNbVertices() - 1).boxed().collect(Collectors.toList());
		while (!VerticesAvailable.isEmpty()) {
			int vertex = findMin();
//			System.out.println("while dijk");
//			System.out.println(vertex + " "+distances[vertex]);
//			for (int i = 0; i < distances.length; i++) {
//				System.out.print(distances[i]+" ");
//			}
//			System.out.println("\n");
			if(vertex == end) { // We found the shortest path to end.
//				setVerticesTemporarlyLabeled(VerticesAvailable.getRoot());
//				for (int i = 0; i < distances.length; i++) {
//					System.out.print(distances[i]+" ");
//				}
				setVerticesTemporarlyLabeled();
//				System.out.println("shortest path found: "+shortestPath);
				return getShortestPathEdge();
			} 
			
			if (network.getOutEdges(vertex) != null) {
				for (Edge outEdge : network.getOutEdges(vertex)) {
//					if(network.getClass().getName() == "optim.flow.domain.ResidualNetwork") {
						if( outEdge.getResidualCapacity() > this.delta) {
							updateDist(vertex, outEdge.getDestination());
						}
//					}
//					else
//						updateDist(vertex, outEdge.getDestination());
				}
			}
		}
		for (int i = 0; i < distances.length; i++) {
			System.out.print(distances[i]+" ");
		}
		throw new IllegalArgumentException("Vertex "+end+"should have been covered previously.\n");
	}
	
	private void setVerticesTemporarlyLabeled() {
//		 We set distances of all temporarly labeled vertices to the distance of the end (see book, page 323)
		for(Integer i:this.VerticesAvailable) {
			this.distances[i] = this.distances[this.end];
		}
	}
		private void setVerticesTemporarlyLabeled(HeapNode node) {
		if(node == null)
			return;
		this.distances[node.getValue()] = this.distances[this.end];
		for(HeapNode son:node.getSons()) {
			if(son == node || this.distances[node.getValue()] != this.distances[this.end])
				throw new IllegalArgumentException("son == node boloss");
//				System.out.println("pb");
			else
				setVerticesTemporarlyLabeled(son);
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