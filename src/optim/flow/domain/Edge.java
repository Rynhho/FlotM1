package optim.flow.domain;

public class Edge {
	private final int source;
	private final int destination;

	private final double cost;
	private final double capacity;

	public Edge(int source, int destination, double cost, double capacity) {
		this.source = source;
		this.destination = destination;

		this.cost = cost;
		this.capacity = capacity;
	}

	public int getSource() {
		return this.source;
	}

	public int getDestionation() {
		return this.destination;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getCost() {
		return cost;
	}
}
