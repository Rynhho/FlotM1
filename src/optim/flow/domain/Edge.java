package optim.flow.domain;

import java.util.ArrayList;

public class Edge {

	private final double cost;
	
	private double capacity;
	
	public Edge(double cost,double capacity) {
		this.cost=cost;
		this.capacity=capacity;
	}

	public double getCapacity() {
		return capacity;
	}
	
	public double getCost() {
		return cost;
	}
}
