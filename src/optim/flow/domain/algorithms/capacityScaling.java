package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class capacityScaling implements Algorithm {
	private double[] pi;
    private ResidualNetwork solution;
//    private Network deltaNetwork;
    private double[] originalDemands;
    private double delta;
    private double U; // U is an upper bound on the largest supply/demand and largest capacity in the network
    private List<Integer> ProviderS;
    private List<Integer> DemanderT;
    private Dijkstra dijkstra;

	private int dijkstraCount;
    
    @Override
	public ResidualNetwork solve(Network network) {
    	boolean withSink = false;
    	if(withSink)
    		this.solution = new ResidualNetwork(addSinkAndSource(network));
    	else
    		this.solution = new ResidualNetwork(network);
    	initialize();
    	int dijkstraCount = 0;
//    	this.deltaNetwork = generateDeltaNetwork();
    	
		while(delta>=1) {
			saturateNegativeCostEdges();
			initializeProviderDemanderSets();
			while(!this.ProviderS.isEmpty() && ! this.DemanderT.isEmpty()) {
				// if no path is available from k to i, we check for every i in T, and if no path exists, we remove k from S
				dijkstraCount++;
				addDeltaFlowAlong(getValidPath());
				updateProviderDemanderSets();
			}
			this.delta = this.delta/2;
//			updateDetlaNetwork();
//			updateDetlaGraph();
			
		}
		this.dijkstraCount = dijkstraCount;
		if(withSink)
			removeSourceAndSink();
		return this.solution;
	}

	public int getDijkstraCount(){
		return dijkstraCount;
	}
    
    private void updatePi() {
    	double[] distances = dijkstra.getDistanceFromSource();
    	for (int j = 0; j < this.pi.length; j++) {
			this.pi[j] = this.pi[j] -distances[j];
		}
    }
    
    private void saturateNegativeCostEdges() {
    	for (int i = 0; i < this.solution.getNbVertices(); i++) {
			for(Edge edge:this.solution.getOutEdges(i)) {
				if(edge.getResidualCapacity() >= delta && edge.getReducedCost()<0) {
					this.solution.addFlow(edge, edge.getResidualCapacity());
				}
			}
		}
    }
    
    private void addDeltaFlowAlong(List<Edge> Path) {
    	if(!Path.isEmpty()) {
	    	for(Edge edge:Path) {
	    		solution.addFlow(edge, delta);
			}
    		updatePi();
			reduceCost();
    	}
    }
    
    private List<Edge> getValidPath(){
    	int k = this.ProviderS.get(0);
		int i = this.DemanderT.get(0);
		
		dijkstra.setDelta(this.delta - 1);
    	List<Edge> validPath = dijkstra.solve(solution, k,i);
		if(validPath.isEmpty()) {
			boolean noAvailablePathFromK = true;
			for (int j = 1; j < this.DemanderT.size(); j++) {
				validPath = dijkstra.getShortestPathEdgeTo(j);
				if(!validPath.isEmpty()) {
					noAvailablePathFromK = false;
					break;
				}
			}
			if(noAvailablePathFromK)
				this.ProviderS.remove((Object)k);
		}
		return validPath;
    }
    
    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {					
					edge.updateReducedCost2( -pi[edge.getSource()] + pi[edge.getDestination()]);
					if(edge.getReducedCost() < 0 && edge.getResidualCapacity() > delta) {
						throw new IllegalArgumentException("residual capacity should not be negative.");
					}
			}
    	}
    }
    
    private void initializeProviderDemanderSets() {
    	this.ProviderS.clear();
    	this.DemanderT.clear();
    	for (int i = 0; i < this.solution.getNbVertices(); i++) {
    		double imbalance = this.solution.getNodeImbalance(i);
    		if(imbalance >= delta)
    			this.ProviderS.add(i);
    		else if(imbalance <= -delta)
    			this.DemanderT.add(i);
    	}
    }
    
    private void updateProviderDemanderSets() {
    	for(int i = this.ProviderS.size()-1; i>=0; --i) {
    		if(this.solution.getNodeImbalance(this.ProviderS.get(i)) < delta)
    			this.ProviderS.remove(i);
    	}
    	for(int i = this.DemanderT.size()-1; i>=0; --i) {
    		if(this.solution.getNodeImbalance(this.DemanderT.get(i)) > -delta)
    			this.DemanderT.remove(i);
    	}    		
    }
    
	private void initialize() { 
		// i think we should put min instead of max
		U = Double.max(this.solution.getMaxDemand(), this.solution.getMaxCapacity());
		delta = Math.pow(2, Math.floor(Math.log(U)/Math.log(2)));
		this.dijkstra = new Dijkstra();
		this.pi = new double[solution.getNbVertices()]; 
		for (int i = 0; i < solution.getNbVertices(); i++) {
    		this.pi[i] = 0;
		}
		this.ProviderS = new ArrayList<Integer>();
    	this.DemanderT = new ArrayList<Integer>();
//		this.deltaNetwork = generateDeltaNetwork();
		
	}
	public Network addSinkAndSource(Network network) {
    	// new 0 is the source, new 1 is the destination 
		this.originalDemands = new double[network.getNbVertices()];
    	List<List<Edge>> Edges = new ArrayList<List<Edge>>();
    	Edges.add(new ArrayList<Edge>());
    	Edges.add(new ArrayList<Edge>());
    	double[] verticesDemands = new double[network.getNbVertices()+2];
    	
    	for (int i = 0; i < network.getNbVertices(); i++) {
    		Edges.add(new ArrayList<Edge>());
    		this.originalDemands[i] = network.getVertexDemand(i);
    		if(network.getVertexDemand(i) < 0) {
    			verticesDemands[0] += network.getVertexDemand(i);
    		}else
    			verticesDemands[1] += network.getVertexDemand(i);
    		//production = demands
    		verticesDemands[0] = Math.min(verticesDemands[0], -verticesDemands[1]);
			if(network.getVertexDemand(i) < 0) {
				Edges.get(0).add(new Edge(0, i+2, -network.getVertexDemand(i), 0));
			}else if(network.getVertexDemand(i) > 0) {
				Edges.get(i+2).add(new Edge(i+2, 1, network.getVertexDemand(i), 0));
			}
			for(Edge edge:network.getOutEdges(i)) {
				Edges.get(i+2).add(new Edge(edge.getSource()+2, edge.getDestination()+2, edge.getCapacity(), edge.getCost(), edge.getReducedCost()));
			}
			
		}
    	return new Network(Edges, verticesDemands);
    }
	private void removeSourceAndSink() {
    	List<List<Edge>> adjacencyList = new ArrayList<>();
    	for (int i = 2; i < this.solution.getNbVertices(); i++) {
    		adjacencyList.add(new ArrayList<Edge>());
			for(Edge edge:this.solution.getOutEdges(i)) {
				if(!edge.isResidual() && !(edge.getDestination()<2)) {					
					adjacencyList.get(i-2).add(new Edge(edge.getSource()-2, edge.getDestination()-2, edge.getCapacity(), edge.getCost(),edge.getReducedCost(), edge.getFlow()));
				}
			}
		}
    	this.solution = new ResidualNetwork(new Network(adjacencyList, this.originalDemands)); 
    }
	
	public Network generateDeltaNetwork() {
    	boolean isEmpty = true;
    	double[] verticesDemands = new double[this.solution.getNbVertices()];
    	List<List<Edge>> adjacencyList = new ArrayList<>();
    	for(int i=0; i<solution.getNbVertices(); i++) {
    		adjacencyList.add(new ArrayList<Edge>());
    		for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getCapacity() >= delta && edge.getCapacity() < 2*delta) {
					isEmpty = false;
					adjacencyList.get(i).add(edge);
				}
			}
    	}
    	if(isEmpty)
    		return null;
    	return new Network(adjacencyList, verticesDemands);
    }
	
	private void updateDetlaNetwork() {
		for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getCapacity() >= delta && edge.getCapacity() < 2*delta) {
					this.solution.addEdge(edge, true);
				}
			}
    	}
	}
	
}

