package optim.flow.domain;

public class Edge {
	private final int destination;

	private final double cost;
	private final double capacity;

	// Todo: this is inverted compared to file structure
	public Edge(int destination, double cost, double capacity) {
		this.destination = destination;

		this.cost = cost;
		this.capacity = capacity;
	}

	public int getDestination() {
		return this.destination;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getCost() {
		return cost;
	}
}
