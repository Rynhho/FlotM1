package optim.flow.domain.data;

public class Edge {
	private int from, to;

	public Edge(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
