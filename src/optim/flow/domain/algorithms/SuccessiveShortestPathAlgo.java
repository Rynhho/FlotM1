package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.Solution;

public class SuccessiveShortestPathAlgo implements Algorithm{
    List<Integer> providerSetE;
    List<Integer> demanderSetD;
    int x = 0;
    int pi = 0;
    Solution solution;
    public SuccessiveShortestPathAlgo(){
    }


    public Solution solve(Network network){
    	this.solution = new Solution("0", network.getNbVertices());
    	Dijkstra dijkstra = new Dijkstra();
        setPrivateVariables();
        updateList(network);

        while(!this.providerSetE.isEmpty()){
        	System.out.println(this.solution);
//        	System.out.println(provider);
            int k = this.providerSetE.get(0);
            int i = this.demanderSetD.get(0);
            double dist = 0;
            
            Network remainingGraph = getRemainingGraph(network);
            
            List<Integer> path = dijkstra.solve(remainingGraph, k, i);
            double delta = Math.min(-network.getVertexDemand(k), network.getVertexDemand(i));
            for(int j = 0; j < path.size()-1; j++) {
            	dist += network.getEdge(path.get(j+1), path.get(j)).getCost();
            	delta = Math.min(delta, network.getEdge(path.get(j+1), path.get(j)).getCapacity() - this.solution.getEdgeFlow(path.get(j+1), path.get(j)));
            }
            for(int j = 0; j < path.size()-1; j++) {
            	this.solution.setEdgeFlow(path.get(j+1), path.get(j), this.solution.getEdgeFlow(path.get(j+1), path.get(j)) + delta);
            }
            this.pi -= dist;
            //update x, G(x), E, D, and the reduced costs;
            updateList(network);
        }
		return this.solution;

    }
    
    private Network getRemainingGraph(Network network) {
		List<List<Edge>> remainingEdges = new ArrayList<List<Edge>>();
		double[] verticesDemands = new double[network.getNbVertices()];
		for(int i=0; i<network.getNbVertices(); ++i) {
			remainingEdges.add(new ArrayList<Edge>());
			for(Edge edge: network.getOutEdges(i)) {
				if(this.solution.getEdgeFlow(i, edge.getDestination()) != network.getEdge(i, edge.getDestination()).getCapacity()) {
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
        System.out.println(this.solution);
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
