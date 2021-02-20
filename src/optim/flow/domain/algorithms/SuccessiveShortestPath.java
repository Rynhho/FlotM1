package optim.flow.domain.algorithms;

import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public class SuccessiveShortestPath implements Algorithm{
    private double[] pi;
	private ResidualNetwork solution;
	private double[] originalDemand;
	private int dijkstraCount;

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
//    	for (int i = 0; i < solution.getNbVertices(); i++) {
//			System.out.print(solution.getVertexDemand(i)+" ");
//		}
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
//        this.solution.displayEdges(false);
        this.pi = dijkstra.getDistanceFromSource();
    	this.reduceCost();
//    	this.solution.displayEdges(false);
        while(!shortestPath.isEmpty()) {
        	double delta = getDelta(shortestPath);
        	if(delta<=0)break;
        	for(Edge edge:shortestPath) {
        		solution.addFlow(edge, delta);
        	}
        	long tmp = System.currentTimeMillis();
        	shortestPath = dijkstra.solve(this.solution, 0, 1);
//        	for (int i = 0; i < solution.getNbVertices(); i++) {
//    			System.out.print(this.pi[i]+" ");
//    		}System.out.println("\n");
//    		this.solution.displayEdges(false);
        	timeSpentInDijkstra += System.currentTimeMillis()-tmp;
        	dijkstraCount ++;
        	this.pi = dijkstra.getDistanceFromSource();
//        	System.out.println(shortestPath);
        	this.reduceCost();
        }
        this.dijkstraCount = dijkstraCount;
        removeSourceAndSink();
		return this.solution;
    }

	public int getDijkstraCount(){
		return this.dijkstraCount;
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
    	this.solution = new ResidualNetwork(new Network(adjacencyList, this.originalDemand));
    }
    
    public double getDelta(List<Edge> path) {
    	// on regarde la demande des sommets du début et de la fin, avant la source et la destination
    	double delta = path.get(0).getCapacity() - path.get(0).getFlow();
    	// String capMax = ""+delta;
	    for(int j = 1; j < path.size(); j++) {
	      	delta = Math.min(delta, path.get(j).getCapacity() - path.get(j).getFlow());
	      	// capMax += " "+ (path.get(j).getCapacity()- path.get(j).getFlow());
	      }
//	    System.out.println("delta = "+ delta);
    	return delta;
    	
    }
    
    public Network addSinkAndSource(Network network) {
    	// new 0 is the source, new 1 is the destination 
    	List<List<Edge>> Edges = new ArrayList<List<Edge>>();
    	this.originalDemand = new double[network.getNbVertices()];
    	Edges.add(new ArrayList<Edge>());
    	Edges.add(new ArrayList<Edge>());
    	double[] verticesDemands = new double[network.getNbVertices()+2];
    	double prod = 0;
    	double demand = 0;
    	for (int i = 0; i < network.getNbVertices(); i++) {
    		Edges.add(new ArrayList<Edge>());
    		this.originalDemand[i] = network.getVertexDemand(i);
    		verticesDemands[i+2] = 0;
			if(network.getVertexDemand(i) < 0) {
				prod +=network.getVertexDemand(i);
				Edges.get(0).add(new Edge(0, i+2, -network.getVertexDemand(i), 0));
			}else if(network.getVertexDemand(i) > 0) {
				demand += network.getVertexDemand(i);
				Edges.get(i+2).add(new Edge(i+2, 1, network.getVertexDemand(i), 0));
			}
			for(Edge edge:network.getOutEdges(i)) {
				Edges.get(i+2).add(new Edge(edge.getSource()+2, edge.getDestination()+2, edge.getCapacity(), edge.getCost(), edge.getReducedCost()));
			}
			
			
		}
    	verticesDemands[0] = prod;
    	verticesDemands[1] = demand;
    	return initPi( new Network(Edges, verticesDemands));
    }
    
    private Network initPi(Network net) {
    	BellmanFord bf = new BellmanFord();
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
