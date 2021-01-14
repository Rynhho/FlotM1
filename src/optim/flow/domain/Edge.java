package optim.flow.domain;

public class Edge {
	private int source;
	private int destination;

	private double capacity;
	private double cost;

	private double reducedCost;
	private boolean isResidual;
	private double flow;
	private Edge oppositeEdge;
	
	public Edge(int source, int destination, double capacity, double cost) {
		this.source = source;
		this.destination = destination;

		this.capacity = capacity;
		this.cost = cost;
		this.reducedCost = cost;
		this.isResidual = false;
		this.oppositeEdge = new Edge(destination, source, capacity, -cost, this);
	}
	
	private Edge(int source, int destination, double capacity, double cost, Edge oppositeEdge) {
		this.source = source;
		this.destination = destination;

		this.capacity = capacity;
		this.cost = cost;
		this.reducedCost = cost;
		this.isResidual = true;
		this.flow = capacity;
		this.oppositeEdge = oppositeEdge;
	}
	
	public Edge(int source, int destination, double capacity, double cost, double reducedcost) {
		this(source, destination, capacity, cost);
		this.reducedCost = reducedcost;
	}
	public Edge(int source, int destination, double capacity, double cost, double reducedcost, double flow) {
		this(source, destination, capacity, cost, reducedcost);
		this.addFlow(flow);
	}
	
	public Edge getOppositeEdge() {
		return this.oppositeEdge;
	}
	
	public void setReducedCost(double reducedCost) {
		this.reducedCost = reducedCost;
	}
	
	public double getFlow() {
		return this.flow;
	}
	
	public double getResidualCapacity() {
		return this.getCapacity() - this.getFlow();
	}
	public void addFlow(double toAdd) {
		this.flow += toAdd;
		this.oppositeEdge.flow -= toAdd;
		if(this.flow < 0 || this.oppositeEdge.flow < 0) {
			if(this.flow<0)
				System.out.println(this + "flow: "+this.getFlow()+ " is residual? "+ this.isResidual());
			else
			System.out.println("opposite edge "+this.getOppositeEdge() + "flow: "+this.getOppositeEdge().getFlow()+ " is residual? "+ this.isResidual());
			throw new IllegalArgumentException("Flow must be not negative.\n");
		}
	}
	
	public boolean isResidual() {
		return this.isResidual;
	}
	
	public double getReducedCost() {
		return this.reducedCost;
	}
	
	public void updateReducedCost(double toAdd) {
		this.reducedCost += toAdd;
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
	@Override
	public String toString() {
		String str = new String();
		str += this.source + "->"+this.destination+" capacity: "+this.capacity+" cost: "+this.cost+" ("+this.reducedCost+")";
		return str;
	}
}
