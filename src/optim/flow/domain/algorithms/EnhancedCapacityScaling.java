package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class EnhancedCapacityScaling implements Algorithm {
	private double[] pi;
    private ResidualNetwork solution;
//    private double[] originalDemands;
    HashMap<Edge, Boolean> isAbundant;
    private double[] imbalanceE;
    private double delta;
    
    private Dijkstra dijkstra;
    
	@Override
	public ResidualNetwork solve(Network network) {
		initialize(network);
		
		int maxImbalanceIndex = getMaxImbalanceIndex();
		while(this.imbalanceE[maxImbalanceIndex] > 0) {
			if(this.imbalanceE[maxImbalanceIndex] <= this.delta/(8*this.solution.getNbVertices()))
				this.delta = imbalanceE[maxImbalanceIndex];			
			for(Edge edge:this.solution.getOutEdges(maxImbalanceIndex)) {
				if(edge.getFlow() >= 8*this.solution.getNbVertices()) {	
					this.isAbundant.replace(edge, true);
				}
//				update abundantComponents and reinstate the imbalance property?
			}
			while(getMaxImbalanceIndex() >= (this.solution.getNbVertices()-1)*this.delta/this.solution.getNbVertices()) {
				addDeltaFlowAlong(getValidPath());
			}
			this.delta = delta/2;
		}
		
		return this.solution;
	}
	
	private List<Edge> getNonAbundantEdgesFrom(int from) {
		// TODO Auto-generated method stub
		return null;
	}

	private int getMaxImbalanceIndex() {
		int max = 0;
		for(int i = 1; i < this.imbalanceE.length; ++i) {
			if(this.imbalanceE[i] > this.imbalanceE[max]) {
				max = i;
			}
		}
		return max;
	}
	
	public void initialize(Network network) {
		this.isAbundant = new HashMap<Edge, Boolean>();
		this.solution = new ResidualNetwork(network);
		this.dijkstra = new Dijkstra();
		this.delta = 0;
		this.pi = new double[solution.getNbVertices()];
		this.imbalanceE = new double[solution.getNbVertices()];
		for (int i = 0; i < solution.getNbVertices(); i++) {
    		this.pi[i] = 0;
    		this.imbalanceE[i] = this.solution.getNodeImbalance(i);
    		this.delta = Math.max(Math.abs(this.imbalanceE[i]), delta);
    		for(Edge edge:solution.getOutEdges(i)) {
    			this.isAbundant.put(edge, false);
    		}
		}
	}
	
	private Integer[] selectKI() {
		int k = -1;
		int i= -1;
		// first criterion
		for (int j = 0; j < this.solution.getNbVertices(); ++j) {
			if(k == -1 && this.imbalanceE[j] >= (this.solution.getNbVertices()-1)*this.delta/this.solution.getNbVertices()) {
				k = j;
			}
			else if(i == -1 && this.imbalanceE[j] < -this.delta/this.solution.getNbVertices() ) {
				i = j;
			}
		}
		// second criterion
		if(k == -1 || i == -1) {
			k = -1;
			i = -1;
			for (int j = 0; j < this.solution.getNbVertices(); ++j) {
				if(k == -1 && this.imbalanceE[j] >= this.delta/this.solution.getNbVertices()) {
					k = j;
				}
				else if(i == -1 && this.imbalanceE[j] < -(this.solution.getNbVertices()-1)*this.delta/this.solution.getNbVertices() ) {
					i = j;
				}
			}
		}
		Integer[] KI = {k,i};
		return KI;
	}
	
	private List<Edge> getValidPath(){
		Integer[] KI = selectKI();
    	int k = KI[0];
		int i = KI[1];
		
		dijkstra.setDelta(this.delta - 1);
    	List<Edge> validPath = dijkstra.solve(solution, k,i);
		return validPath;
    }
	
	private void addDeltaFlowAlong(List<Edge> Path) {
    	for(Edge edge:Path) {
    		solution.addFlow(edge, delta);
		}
    	if(!Path.isEmpty()) {
    		updatePi();
			reduceCost();
    	}
    }
	
	 private void updatePi() {
	    	double[] distances = dijkstra.getDistanceFromSource();
	    	for (int i = 0; i < this.pi.length; ++i) {
				this.pi[i] =  - distances[i];
			}
	    }
	    
	
	private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); ++i) {
			for(Edge edge: solution.getOutEdges(i)) {
					edge.updateReducedCost( -pi[edge.getSource()] + pi[edge.getDestination()]);
			}
    	}
    }
}
