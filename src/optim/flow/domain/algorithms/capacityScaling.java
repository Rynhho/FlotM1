package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class capacityScaling implements Algorithm {
	long timeSpendInGeneration = 0;
	private List<Double> pi;
    private ResidualNetwork solution;
    private double delta;
    private double U; // U is an upper bound on the largest supply/demand and largest capacity in the network
    private List<Integer> ProviderS;
    private List<Integer> DemanderT;
    
    @Override
	public ResidualNetwork solve(Network network) {
    	BellmanFord bf = new BellmanFord();
    	bf.solve(network, 0);
    	this.pi = bf.getDist();
    	
		this.solution = new ResidualNetwork(network);
		reduceCost();
		initialize();
		Dijkstra dijkstra = new Dijkstra();
		while(delta>=1) {
			System.out.println(delta);
			//begin delta scaling phase	
			for (int i = 0; i < this.solution.getNbVertices(); i++) {
				for(Edge edge:this.solution.getOutEdges(i)) {
					if(edge.getResidualCapacity() >= delta && edge.getReducedCost()<0) {
						edge.addFlow(edge.getResidualCapacity());
						//update x and the imbalance e
					}
				}
			}
			
			updateProviderDemanderSets();
			
			while(!this.ProviderS.isEmpty() && ! this.DemanderT.isEmpty()) {
				int k = this.ProviderS.get(0);
				int i = this.DemanderT.get(0);
				ResidualNetwork networkDelta = generateDeltaGraph();
				if(networkDelta == null)
					break;
				long time = System.currentTimeMillis();
				List<Edge> PathKtoI = dijkstra.solve(networkDelta, k, i);
				timeSpendInGeneration += System.currentTimeMillis() - time;
				if(DemanderT.get(0) == 4) {
//					return null;
				}
				for(Edge edge:PathKtoI) {					
					solution.addFlow(edge, delta);
					//update x, G(x, delta) no needed potentially
					updateProviderDemanderSets();
					
				}
//				System.out.println(!this.ProviderS.isEmpty()+" "+ ! this.DemanderT.isEmpty());
				
			}
			this.delta = this.delta/2;
			
		}
		System.out.println("time spent generating : "+timeSpendInGeneration);
		return this.solution;
	}
	
    public ResidualNetwork generateDeltaGraph() {
    	boolean isEmpty = true;
    	double[] verticesDemands = new double[this.solution.getNbVertices()];
    	List<List<Edge>> adjacencyList = new ArrayList<>();
    	for(int i=0; i<solution.getNbVertices(); i++) {
    		adjacencyList.add(new ArrayList<Edge>());
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getResidualCapacity() >= delta) {
					isEmpty = false;
					adjacencyList.get(i).add(edge);
				}
			}
    	}
    	if(isEmpty)
    		return null;
    	return new ResidualNetwork(new Network(adjacencyList, verticesDemands));
    }
    
    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getResidualCapacity() > 0) {
					edge.updateReducedCost( pi.get(edge.getSource()) - pi.get(edge.getDestination()));
					this.solution.getOppositeEdge(edge).updateReducedCost(-this.solution.getOppositeEdge(edge).getReducedCost());
				}
			}
    	}
    }
    
    private void updateProviderDemanderSets() {
    	this.ProviderS = new ArrayList<Integer>();
    	this.DemanderT = new ArrayList<Integer>();
    	for (int i = 0; i < this.solution.getNbVertices(); i++) {
    		double imbalance = this.solution.getNodeImbalance(i);
    		if(imbalance >= delta)
    			this.ProviderS.add(i);
    		else if(imbalance <= -delta)
    			this.DemanderT.add(i);
    	}
    }
    
	private void initialize() {
		this.pi = new ArrayList<Double>(this.solution.getNbVertices());
		U = Double.max(this.solution.getMaxDemand(), this.solution.getMaxCapacity());
		delta = Math.pow(2, Math.floor(Math.log(U)/Math.log(2)));
		
	}
	
	

}
