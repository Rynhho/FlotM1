package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;

public class BellmanFord {
	Network network;
	int nbVertices;
	List<Double> dist;
//	List<Double> pi;
	List<Integer> predecessor;
	int start;
	
	public void initialize(Network network, int start) {
		this.network = network;
		this.start = start;
		this.nbVertices = network.getNbVertices();
		this.dist = new ArrayList<Double>();
//		this.pi = new ArrayList<Double>();
		this.predecessor = new ArrayList<Integer>();
		
	}
	
	public List<Integer> getPredecessors() {
		return this.predecessor;
	}
	
	public List<Double> getDist(){
		return this.dist;
	}
	
	private void initializeLists() {
		for (int i = 0; i < this.nbVertices; ++i) {
			this.dist.add(Double.MAX_VALUE);
			this.predecessor.add(-1);
//			this.pi.add(null);
		}
		this.dist.set(this.start, 0.0);
	}
	
	public boolean solve(Network network, int start) {
		initialize(network, start);
		initializeLists();
		
		for (int i = 0; i < network.getNbVertices(); i++) {
//			System.out.println(dist.toString());
			for (int j = 0; j < network.getNbVertices(); j++) {
				for(Edge edge:network.getOutEdges(j)) {
					relaxe(edge);
					
				}
			}
		}
		
		for (int j = 0; j < network.getNbVertices(); j++) {
			for(Edge edge:network.getOutEdges(j)) {
//				System.out.println(edge);
				if(this.dist.get(edge.getDestination()) > this.dist.get(edge.getSource()) + edge.getCost())
					return false;
			}
		}
		return true;
	}

	private void relaxe(Edge edge) {
		if (this.dist.get(edge.getDestination()) > this.dist.get(edge.getSource()) + edge.getCost()) {
			this.dist.set(edge.getDestination(), this.dist.get(edge.getSource()) + edge.getCost());
			this.predecessor.set(edge.getDestination(), edge.getSource()); 
		}
	}
}
