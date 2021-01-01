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
    int pi = 0;
    ResidualNetwork solution;
//    Solution solution;
    public SuccessiveShortestPathAlgo(){
    }


    public ResidualNetwork solve(Network network){
    	this.solution = new ResidualNetwork(network);
    	Dijkstra dijkstra = new Dijkstra();
        setPrivateVariables();
        updateList(network);

        while(!this.providerSetE.isEmpty()){
//        	System.out.println(this.solution);
//        	System.out.println(provider);
            int k = this.providerSetE.get(0);
            int i = this.demanderSetD.get(0);
            double dist = 0;
            
            Network remainingGraph = getRemainingGraph(network);
            
            List<Edge> path = dijkstra.solve(remainingGraph, k, i);
            double delta = Math.min(-network.getVertexDemand(k), network.getVertexDemand(i));
            for(int j = 0; j < path.size()-1; j++) {
            	dist += path.get(j).getCost();
//            			getEdges(path.get(j+1), path.get(j)).get(0).getCost();
            	delta = Math.min(delta, path.get(j).getCapacity() - solution.getFlow(path.get(j)));
            }
            for(int j = 0; j < path.size()-1; j++) {
            	this.solution.addFlow(path.get(j), delta);
            }
            this.pi -= dist;
            //update x, G(x), E, D, and the reduced costs;
            updateList(network);
        }
		return this.solution;

    }
    
    public void getFullGraph(Network network) {
    	// new 0 is the source, new 1 is the destination 
    	List<List<Edge>> Edges = new ArrayList<List<Edge>>();
    	Edges.add(new ArrayList<Edge>());
    	Edges.add(new ArrayList<Edge>());
    	double[] verticesDemands = new double[network.getNbVertices()+2];
    	for (int i = 0; i < network.getNbVertices(); i++) {
    		Edges.add(new ArrayList<Edge>());
    		
    		verticesDemands[i+2] = network.getVertexDemand(i);
			if(network.getVertexDemand(i) < 0) {
				Edges.get(0).add(new Edge(0, i+2, 100, 0));
			}else if(network.getVertexDemand(i) > 0) {
				Edges.get(i+2).add(new Edge(i+2, 1, 100, 0));
			}
			
		}
    	this.solution = new ResidualNetwork(new Network(Edges, verticesDemands));
    	System.out.println(new Network(Edges, verticesDemands));
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
        this.pi = 0;
        
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
