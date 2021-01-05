package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class SuccessiveShortestPathAlgo implements Algorithm{
    List<Integer> providerSetE;
    List<Integer> demanderSetD;
    int x = 0;
    List<Double> pi;
    ResidualNetwork solution;
    public SuccessiveShortestPathAlgo(){
    }

    private void reduceCost() {
    	for(int i=0; i<solution.getNbVertices(); ++i) {
			for(Edge edge: solution.getOutEdges(i)) {
				if(edge.getCapacity()>0) {
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
        
        
        
        
        
        
//        while(!this.providerSetE.isEmpty()){
////        	System.out.println(this.solution);
////        	System.out.println(provider);
//            int k = this.providerSetE.get(0);
//            int i = this.demanderSetD.get(0);
//            double dist = 0;
//            
//            Network remainingGraph = getRemainingGraph(network);
//            
//            List<Edge> path = dijkstra.solve(remainingGraph, k, i);
//            double delta = Math.min(-network.getVertexDemand(k), network.getVertexDemand(i));
//            for(int j = 0; j < path.size()-1; j++) {
//            	dist += path.get(j).getCost();
////            			getEdges(path.get(j+1), path.get(j)).get(0).getCost();
//            	delta = Math.min(delta, path.get(j).getCapacity() - solution.getFlow(path.get(j)));
//            }
//            for(int j = 0; j < path.size()-1; j++) {
//            	this.solution.addFlow(path.get(j), delta);
//            }
//            this.pi -= dist;
//            //update x, G(x), E, D, and the reduced costs;
//            updateList(network);
//        }
		return this.solution;

    }
    
    public double getDelta(List<Edge> path) {
    	// on regarde la demande des sommets du début et de la fin, avant la source et la destination
    	double flowOutSource = -solution.getVertexDemand(path.get(0).getDestination());
//    	- this.solution.getVertexFlowOut(path.get(0).getDestination());
    	double flowInSink = solution.getVertexDemand(path.get(path.size()-1).getSource());
//    	- this.solution.getVertexFlowOut(path.get(path.size()-1).getSource());
//    	for(Edge edge:this.solution.getInEdges(path.get(path.size()-1).getSource())) {
//    		flowOutSource -= this.solution.getFlow(edge);
//    	}
    	double delta = Math.min(flowOutSource, flowInSink);
	    for(int j = 0; j < path.size()-1; j++) {
//	    	dist += path.get(j).getCost();
	//      			getEdges(path.get(j+1), path.get(j)).get(0).getCost();
//	    	System.out.println();
	      	delta = Math.min(delta, path.get(j).getCapacity());
	      }
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
//						getEdgeFlow(i, edge.getDestination()) != network.getEdge(i, edge.getDestination()).getCapacity()) {
					remainingEdges.get(i).add(edge);
				}
			}
		}
		
		return new Network(remainingEdges, verticesDemands);
	}


	private void setPrivateVariables(){
        this.x = 0;
        
    }

    private void updateList(Network network){
        this.providerSetE = new ArrayList<>();
        this.demanderSetD = new ArrayList<>();
        for(int vertex = 0; vertex < network.getNbVertices(); ++vertex){
            double imbalance = network.getVertexDemand(vertex) - this.solution.getVertexFlowIn(vertex) + this.solution.getVertexFlowOut(vertex);
//            System.out.println("vertex: "+ vertex+ " demand: "+ network.getVertexDemand(vertex)+ " flow in: "+ this.solution.getVertexFlowIn(vertex)+ " flot out "+ this.solution.getVertexFlowOut(vertex)+ " imbalance: "+ imbalance);
            if(imbalance < 0){
                this.providerSetE.add(vertex);
            }else if(imbalance > 0){
                this.demanderSetD.add(vertex);
            }
        }
    }
}
