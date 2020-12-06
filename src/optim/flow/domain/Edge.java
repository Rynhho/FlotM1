package optim.flow.domain;

public class Edge {
	private int source;
	private int destination;

	private double capacity;
	private double cost;

	public Edge(int source, int destination, double capacity, double cost) {
		this.source = source;
		this.destination = destination;

		this.capacity = capacity;
		this.cost = cost;
	}

	public int getSource() {
		return this.source;
	}

	public int getDestination() {
		return this.destination;
	}

	public double getCapacity() {
		return this.capacity;
	}

	public double getCost() {
		return this.cost;
	}

	public boolean equals(Edge e) {
		boolean equals = true;
		equals = equals && (this.source == e.source);
		equals = equals && (this.destination == e.destination);
		equals = equals && (this.capacity == e.capacity);
		equals = equals && (this.cost == e.cost);

		return equals;
	}
}
