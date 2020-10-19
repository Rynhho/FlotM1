package optim.flow.domain.data;

public class Solution {
	private double[][] edgesFlows;
	private int nbVertex;

	public Solution(int size) {
		this.edgesFlows = new double[size][size];
		this.nbVertex = size;
	}

	public Solution(double[][] edgesFlows) {
		this.edgesFlows = edgesFlows;
		this.nbVertex = edgesFlows.length;
	}

	public double[][] getEdgesFlows() {
		return this.edgesFlows;
	}

	public int getNbVertex() {
		return this.nbVertex;
	}

	public double getEdgesFlowsAt(int x, int y) {
		if (x >= 0 && x < this.edgesFlows.length && y >= 0 && y < this.edgesFlows.length) {
			return this.edgesFlows[x][y];
		} else {
			System.out.println("error in setEdgesFlowsAt: " + x + " " + y);
			return -1;
		}

	}

	public void setEdgesFlowsAt(int x, int y, double val) {
		if (x >= 0 && x < this.edgesFlows.length && y >= 0 && y < this.edgesFlows.length) {
			this.edgesFlows[x][y] = val;
		} else {
			System.out.println("error in setEdgesFlowsAt: " + x + " " + y);
		}

	}

	public double getValueIn(int vertex) {
		if (vertex >= 0 && vertex < this.edgesFlows.length) {
			double value = 0;
			for (int i = 0; i < this.edgesFlows.length; i++) {
				value += this.edgesFlows[i][vertex];
			}
			return value;
		}
		System.out.println("error in getValueIn: " + vertex);
		return -1;
	}

	public double getValueOut(int vertex) {
		if (vertex >= 0 && vertex < this.edgesFlows.length) {
			double value = 0;
			for (int i = 0; i < this.edgesFlows.length; i++) {
				value += this.edgesFlows[vertex][i];
			}
			return value;
		}
		System.out.println("error in getValueOut: " + vertex);
		return -1;
	}
}
