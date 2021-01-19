package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;

public class BellmanFord {
	Network network;
	int nbVertices;
	double[] dist;
	List<Integer> predecessor;
	int start;
	
	public void initialize(Network network, int start) {
		this.network = network;
		this.start = start;
		this.nbVertices = network.getNbVertices();
		this.dist = new double[network.getNbVertices()];
		this.predecessor = new ArrayList<Integer>();
		
	}
	
	public List<Integer> getPredecessors() {
		return this.predecessor;
	}
	
	public double[] getDist(){
		return this.dist;
	}
	
	private void initializeLists() {
		for (int i = 0; i < this.nbVertices; ++i) {
			this.dist[i] = Double.MAX_VALUE;
			this.predecessor.add(-1);
		}
		this.dist[this.start] = 0.0;
	}
	
	public boolean solve(Network network, int start) {
		initialize(network, start);
		initializeLists();
		
		for (int i = 0; i < network.getNbVertices(); i++) {
			for (int j = 0; j < network.getNbVertices(); j++) {
				for(Edge edge:network.getOutEdges(j)) {
					relaxe(edge);
				}
			}
		}
		
		for (int j = 0; j < network.getNbVertices(); j++) {
			for(Edge edge:network.getOutEdges(j)) {
				if(this.dist[edge.getDestination()] > this.dist[edge.getSource()] + edge.getCost())
					return false;
			}
		}
		return true;
	}

	private void relaxe(Edge edge) {
		int destination = edge.getDestination();
		int source = edge.getSource();
		double distSource = this.dist[source];
		if (this.dist[destination] > distSource + edge.getCost()) {
			this.dist[edge.getDestination()] = distSource + edge.getCost();
			this.predecessor.set(destination, source); 
		}
	}
}
