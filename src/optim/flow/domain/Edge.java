package optim.flow.domain;

public class Edge {
	private final double cost;
	private final double capacity;

	public Edge(double cost, double capacity) {
		this.cost = cost;
		this.capacity = capacity;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getCost() {
		return cost;
	}
}
