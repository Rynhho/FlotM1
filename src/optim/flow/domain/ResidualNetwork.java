package optim.flow.domain;

import java.util.Map;
import java.util.HashMap;

public class ResidualNetwork extends Network {
	private final Network network;
	private final int nbEdges;

//	private final Map<Edge, Edge> oppositeEdgesMap;

	private final double[] verticesFlowIn;
	private final double[] verticesFlowOut;

	private int solutionCost;

	public ResidualNetwork(Network network) {
		super(network, true);
		this.network = network;
		this.nbEdges = 2 * network.nbEdges;

//		this.oppositeEdgesMap = new HashMap<Edge, Edge>();

//		for (int source = 0; source < network.nbVertices; ++source) {
//			final int s = source;
//			network.getOutEdges(source).stream().forEach(edge -> {
//				Edge oppositeEdge = new Edge(edge.getDestination(), s, 0, 0, 0, true);
//
//				this.adjacencyList.get(edge.getDestination()).add(oppositeEdge);
//
//				this.oppositeEdgesMap.put(edge, oppositeEdge);
//				this.oppositeEdgesMap.put(oppositeEdge, edge);
//			});
//		}

		this.verticesFlowIn = new double[this.nbVertices];
		this.verticesFlowOut = new double[this.nbVertices];

		this.solutionCost = 0;
		updateFlowInOut();
	}
	
	public Edge getOppositeEdge(Edge edge) {
		return edge.getOppositeEdge();
	}
	
	public boolean isInOriginalNet(Edge edge) {
		return !edge.isResidual();
	}

	public double getFlow(Edge edge) {
		return edge.getFlow();
	}

	public Network getNetwork() {
		return this.network;
	}
	
	public double getCost() {
		double cost=0;
		for (int i = 0; i < this.getNbVertices(); i++) {
			for (Edge edge: this.getOutEdges(i)) {
				if(!edge.isResidual())
						cost+= edge.getCost()*edge.getFlow();
			}
		}
		return cost;
	}
	
	public void updateFlowInOut() {
		for (int i = 0; i < this.getNbVertices(); i++) {
			double flow = 0;
			for(Edge edge:this.getInEdges(i) ){
				if(!edge.isResidual())
					flow+=edge.getFlow();
			}
			this.verticesFlowIn[i] = flow;
			flow = 0;
			for(Edge edge:this.getOutEdges(i)) {
				if(!edge.isResidual())
					flow-=edge.getFlow();
			}
			this.verticesFlowOut[i] = flow;
		}
	}
	
	public boolean isFeasible() {
		boolean isFeasible = true;
		for (int i = 0; i < this.getNbVertices(); i++) {
			double flowRemaing = -this.getVertexDemand(i);
			for(Edge edge:this.getInEdges(i) ){
				if(edge.getFlow()>edge.getCapacity()) {					
					System.out.println("flow of edge ("+edge.getSource()+","+edge.getDestination()+") = "+edge.getFlow()+" > capacity = "+edge.getCapacity());
					isFeasible = false;
				}
				if(!edge.isResidual())
					flowRemaing+=edge.getFlow();
			}
			for(Edge edge:this.getOutEdges(i)) {
				if(!edge.isResidual())
					flowRemaing-=edge.getFlow();
			}
			if(flowRemaing < 0) {
				System.out.println(flowRemaing +" remaining in vertex "+i+" should be 0.");
				isFeasible = false;
			}
		}
		return isFeasible;
	}
	
	public double getNodeImbalance(int vertex) {
		double flowRemaing = -this.getVertexDemand(vertex)+this.verticesFlowIn[vertex]-this.verticesFlowOut[vertex];
		return flowRemaing;	
	}
	
	public void addFlow(Edge edge, double flow) {
		edge.addFlow(flow);
		this.verticesFlowOut[edge.getSource()] += flow;
		this.verticesFlowIn[edge.getDestination()] += flow;
	}

	public double getVertexFlowIn(int vertex) {
		return this.verticesFlowIn[vertex];
	}

	public double getVertexFlowOut(int vertex) {
		return this.verticesFlowOut[vertex];
	}
	
	@Override
	public String toString() {
		String str = new String();

		str += "Number of vertices: " + this.nbVertices + "\n";

		str += "Flow matrix:\n";
		for (int source = 0; source < this.nbVertices; ++source) {
			str += "[";
			int currentDestination = 0;
			for (Edge edge : this.getOutEdges(source)) {
				for (int i = currentDestination; i < edge.getDestination(); ++i) {
					str += "0.0, ";
				}

				str += String.format("%.2f", this.getFlow(edge)) + ", ";
			}

			for (int i = currentDestination; i < this.nbVertices; ++i) {
				str += "0.00, ";
			}

			str = str.substring(0, str.length() - 2) + "]\n";
		}

		return str;
	}
}
