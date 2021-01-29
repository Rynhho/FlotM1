package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class CostScaling implements Algorithm {

	private double[] pi;
    private ResidualNetwork solution;
    private double eps;



    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getResidualCapacity()>0) {
					edge.updateReducedCost( this.pi[edge.getSource()] - this.pi[edge.getDestination()]);
					this.solution.getOppositeEdge(edge).updateReducedCost(-this.solution.getOppositeEdge(edge).getReducedCost());
				}
			}
    	}
    }


    public ResidualNetwork solve(Network network) {
        this.solution = new ResidualNetwork(network);
        initReducedCostResidualTo0();

        this.pi = new double[this.solution.getNbVertices()];
        for (int i = 0; i < this.solution.getNbVertices(); i++) {
            this.pi[i] = 0.;
        }
        this.eps = solution.getMaxCost();
        
        reduceCost();
        FeasableFlowProblem feasableFlowProblem = new FeasableFlowProblem();
        this.solution = feasableFlowProblem.solve(network);
        
        
        //System.out.println(network);
        //System.out.println(this.solution);
        //network.displayEdges(false);
        //this.solution.displayEdges(true);
        if (!this.solution.isFeasible()){
            System.out.println("Error Solution is not feasible!");
        }

        System.out.println("Eps= "+eps+"!");

        while (this.eps >= 1.0/network.getNbVertices()) {
            improveApproximation();
            this.eps = this.eps/2;
        }
        removeSourceAndSink();
        return this.solution;
    }

    private void improveApproximation() {
        for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getReducedCost()>0) {
                    this.solution.addFlow(edge, -edge.getFlow());
                    //updatePi();
			        //reduceCost();
                }
                else if(edge.getReducedCost()<0) {
                    this.solution.addFlow(edge, edge.getCapacity()-edge.getFlow());
                    //updatePi();
			        //reduceCost();
				}
			}
        }
        
        boolean hasActiveNodes = true;
        while (hasActiveNodes) {
            hasActiveNodes = false;
            for (int i = 0; i < this.solution.getNbVertices(); i++) {
                Double e = this.solution.getNodeImbalance(i);
                if (e>0){
                    hasActiveNodes = true;
                    pushRelabel(i);
                }
            }
        }
    }

    private void pushRelabel(int i) {
        List<Edge> arcs = this.solution.getOutEdges(i);
        boolean containsAdmissibleArc = false;
        
        for (Edge edge : arcs) {
            if(-this.eps/2.0 <= edge.getReducedCost() && edge.getReducedCost() < 0 ){
                System.out.println("here!");
                containsAdmissibleArc = true;
                double delta = Math.min(this.solution.getNodeImbalance(i), edge.getResidualCapacity());
                this.solution.addFlow(edge, delta);
                break;
            }
        }
        if (!containsAdmissibleArc){
            this.pi[i] =  this.pi[i] + this.eps/2;
            reduceCost();
        }
    }

    private void initReducedCostResidualTo0() {
    	for (int i = 1; i < this.solution.getNbVertices(); i++) {
			for(Edge edge:this.solution.getOutEdges(i)) {
				if(edge.isResidual()) {
					edge.setReducedCost(0);
				}
			}
		}
    }

    public Network addSinkAndSource(Network network) {
    	// new 0 is the source, new 1 is the destination 
    	List<List<Edge>> Edges = new ArrayList<List<Edge>>();
    	Edges.add(new ArrayList<Edge>());
    	Edges.add(new ArrayList<Edge>());
    	double[] verticesDemands = new double[network.getNbVertices()+2];
    	for (int i = 0; i < network.getNbVertices(); i++) {
    		Edges.add(new ArrayList<Edge>());
    		
    		verticesDemands[i+2] = network.getVertexDemand(i);
			if(network.getVertexDemand(i) < 0) {
				Edges.get(0).add(new Edge(0, i+2, -network.getVertexDemand(i), 0));
			}else if(network.getVertexDemand(i) > 0) {
				Edges.get(i+2).add(new Edge(i+2, 1, network.getVertexDemand(i), 0));
			}
			for(Edge edge:network.getOutEdges(i)) {
				Edges.get(i+2).add(new Edge(edge.getSource()+2, edge.getDestination()+2, edge.getCapacity(), edge.getCost(), edge.getReducedCost()));
			}
			
		}
    	BellmanFord bf = new BellmanFord();
    	Network net =new Network(Edges, verticesDemands);
    	bf.solve(net, 0);
//    	this.pi = bf.getDist();
    	return net;
    }

    private void removeSourceAndSink() {
    	double[] verticesDemands = new double[this.solution.getNbVertices()-2];
    	List<List<Edge>> adjacencyList = new ArrayList<>();
    	for (int i = 2; i < this.solution.getNbVertices(); i++) {
    		verticesDemands[i-2] = this.solution.getVertexDemand(i);
    		adjacencyList.add(new ArrayList<Edge>());
			for(Edge edge:this.solution.getOutEdges(i)) {
				if(!edge.isResidual() && !(edge.getDestination()<2)) {					
					adjacencyList.get(i-2).add(new Edge(edge.getSource()-2, edge.getDestination()-2, edge.getCapacity(), edge.getCost(),edge.getReducedCost(), edge.getFlow()));
				}
			}
		}
    	this.solution = new ResidualNetwork(new Network(adjacencyList, verticesDemands));
    }
}