package optim.flow.domain;

import java.util.ArrayList;

public class Edge {

	//cost per unit of the arc
	private final double cost;
	
	// maximal capacity of the edge
	private double capacity;
	
	public Edge(double cost,double capacity) {
		this.cost=cost;
		this.capacity=capacity;
	}

	public double getCapacity() {
		return capacity;
	}


	// return the cost of the edge
	
	public double getTotalCost() {
		return cost;
	}
}
