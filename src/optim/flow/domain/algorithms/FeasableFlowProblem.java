package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class FeasableFlowProblem implements Algorithm{
    private ResidualNetwork solution;
    private double[] originalDemands;
    private double delta;
    private double U; // U is an upper bound on the largest supply/demand and largest capacity in the network
    private Dijkstra dijkstra;
    private List<Edge> validPath;
    
    @Override
	public ResidualNetwork solve(Network network) {
        this.solution = new ResidualNetwork(addSinkAndSource(network));
        
    	U = Double.max(this.solution.getMaxDemand(), this.solution.getMaxCapacity());
		delta = Math.pow(2, Math.floor(Math.log(U)/Math.log(2)));
		this.dijkstra = new Dijkstra();
        int dijkstraCount = 0;
        dijkstra.setDelta(this.delta-1);
        //this.validPath = 
    	
		while(delta>=1) {
            dijkstra.setDelta(this.delta-1);
            this.validPath = dijkstra.solve(solution, 0, 1);
            dijkstraCount++;

            //System.out.println("delta= "+delta+"!");
			while(!validPath.isEmpty()) {
                //System.out.println(validPath);
                double minFlow = validPath.get(0).getResidualCapacity();
                for (Edge edge : validPath) {
                    if (edge.getResidualCapacity()<minFlow) {
                        minFlow = edge.getResidualCapacity();
                    }
                }
                for (Edge edge : validPath) {
                    solution.addFlow(edge, minFlow);
                }
                dijkstra.setDelta(this.delta-1);
                validPath = dijkstra.solve(solution, 0, 1);
                dijkstraCount++;
			}
            this.delta = this.delta/2.0;
			
		}
		//System.out.println("dijkstra used: " + dijkstraCount);
        
        removeSourceAndSink();
		return this.solution;
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
//	
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
}
