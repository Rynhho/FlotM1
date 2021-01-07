package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class SuccessiveShortestPathAlgo implements Algorithm{
    List<Integer> providerSetE;
    List<Integer> demanderSetD;
    List<Double> pi;
    ResidualNetwork solution;
    public SuccessiveShortestPathAlgo(){
    }

    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); ++i) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getCapacity()!=0) {
					edge.updateReducedCost( pi.get(edge.getSource()) - pi.get(edge.getDestination()));
					this.solution.getOppositeEdge(edge).updateReducedCost(-this.solution.getOppositeEdge(edge).getReducedCost());
				}
//				System.out.println(edge);
			}
    	}
//    	System.out.println(solution);
    }
    
    public ResidualNetwork solve(Network network){
    	this.solution = new ResidualNetwork(addSinkAndSource(network));
    	reduceCost();
    	Dijkstra dijkstra = new Dijkstra();
        List<Edge> shortestPath = dijkstra.solve(this.solution, 0, 1);
        while(!shortestPath.isEmpty()) {
        	double delta = getDelta(shortestPath);
        	if(delta<=0)break;
        	for(Edge edge:shortestPath) {
        		solution.addFlow(edge, delta);
        	}
        	shortestPath = dijkstra.solve(this.solution, 0, 1);
        }
//        removeSourceAndSink();
		return this.solution;
    }
    
    private void removeSourceAndSink() {
    	double[][] flowMatrix = new double[this.solution.getNbVertices()][this.solution.getNbVertices()];
    	for (int i = 2; i < this.solution.getNbVertices(); i++) {
			for (int j = 2; j < this.solution.getNbVertices(); j++) {
				for(Edge edge:this.solution.getEdges(i, j)) {
					if(!edge.isResidual()) {						
						flowMatrix[i][j] = this.solution.getFlow(edge);
						break;
					}
						
				}
			}
		}
    	this.solution = new ResidualNetwork(this.solution.getNetwork(), flowMatrix);
    }
    
    public double getDelta(List<Edge> path) {
    	// on regarde la demande des sommets du début et de la fin, avant la source et la destination
    	double delta = path.get(0).getCapacity() - path.get(0).getFlow();
//    	String capMax = ""+delta;
	    for(int j = 1; j < path.size(); j++) {
	      	delta = Math.min(delta, path.get(j).getCapacity() - path.get(j).getFlow());
//	      	capMax += " "+ (path.get(j).getCapacity()- path.get(j).getFlow());
	      }
//	    System.out.println(capMax+"\ndelta = "+delta);
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
    	bf.solve(new Network(Edges, verticesDemands), 0);
    	this.pi = bf.getDist();
    	return new Network(Edges, verticesDemands);
    }
    
    private Network getRemainingGraph(Network network) {
		List<List<Edge>> remainingEdges = new ArrayList<List<Edge>>();
		double[] verticesDemands = new double[network.getNbVertices()];
		for(int i=0; i<network.getNbVertices(); ++i) {
			remainingEdges.add(new ArrayList<Edge>());
			for(Edge edge: network.getOutEdges(i)) {
				if(this.solution.getFlow(edge) != network.getEdges(i, edge.getDestination()).get(0).getCapacity()) {
					remainingEdges.get(i).add(edge);
				}
			}
		}
		
		return new Network(remainingEdges, verticesDemands);
	}


    private void updateList(Network network){
        this.providerSetE = new ArrayList<>();
        this.demanderSetD = new ArrayList<>();
        for(int vertex = 0; vertex < network.getNbVertices(); ++vertex){
            double imbalance = network.getVertexDemand(vertex) - this.solution.getVertexFlowIn(vertex) + this.solution.getVertexFlowOut(vertex);
            if(imbalance < 0){
                this.providerSetE.add(vertex);
            }else if(imbalance > 0){
                this.demanderSetD.add(vertex);
            }
        }
    }
}
