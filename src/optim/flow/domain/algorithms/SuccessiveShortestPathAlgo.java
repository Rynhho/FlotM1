package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class SuccessiveShortestPathAlgo implements Algorithm{
    private double[] pi;
	private ResidualNetwork solution;

    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); i++) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getResidualCapacity()>0) {
					edge.updateReducedCost( pi[edge.getSource()] - pi[edge.getDestination()]);
					this.solution.getOppositeEdge(edge).updateReducedCost(-this.solution.getOppositeEdge(edge).getReducedCost());
				}
			}
    	}
    }
    
    public ResidualNetwork solve(Network network){
    	this.solution = new ResidualNetwork(addSinkAndSource(network));
    	initReducedCostResidualTo0();
//    	for (int i = 0; i < this.solution.getNbVertices(); i++) {
////    		System.out.println(i+" demands "+this.originalNet.getVertexDemand(i));
//        	for (Edge edge: this.solution.getOutEdges(i)) {
////        		if(this.solution.getFlow(edge) != 0)
////        			if( edge.getFlow()!=edge.getCapacity())
//        				System.out.println(edge+ " flow: "+this.solution.getFlow(edge));
//        	}
//        }
    	reduceCost();
    	Dijkstra dijkstra = new Dijkstra();
    	int dijkstraCount = 1;
    	
    	long timeSpentInDijkstra = System.currentTimeMillis();
        List<Edge> shortestPath = dijkstra.solve(this.solution, 0, 1);
        timeSpentInDijkstra = System.currentTimeMillis() - timeSpentInDijkstra;
        
        this.pi = dijkstra.getDistanceFromSource();
    	this.reduceCost();
        while(!shortestPath.isEmpty()) {
        	double delta = getDelta(shortestPath);
        	if(delta<=0)break;
        	for(Edge edge:shortestPath) {
        		solution.addFlow(edge, delta);
        	}
        	long tmp = System.currentTimeMillis();
        	shortestPath = dijkstra.solve(this.solution, 0, 1);
        	timeSpentInDijkstra += System.currentTimeMillis()-tmp;
        	dijkstraCount ++;
        	this.pi = dijkstra.getDistanceFromSource();
        	this.reduceCost();
        }
        System.out.println("dijkstra count: "+dijkstraCount);
        removeSourceAndSink();
		return this.solution;
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
    
    public double getDelta(List<Edge> path) {
    	// on regarde la demande des sommets du début et de la fin, avant la source et la destination
    	double delta = path.get(0).getCapacity() - path.get(0).getFlow();
    	// String capMax = ""+delta;
	    for(int j = 1; j < path.size(); j++) {
	      	delta = Math.min(delta, path.get(j).getCapacity() - path.get(j).getFlow());
	      	// capMax += " "+ (path.get(j).getCapacity()- path.get(j).getFlow());
	      }
	    // System.out.println(capMax+"\ndelta = "+delta);
//	    System.out.println("delta = "+ delta);
    	return delta;
    	
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
    	this.pi = bf.getDist();
    	return net;
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
	

}
