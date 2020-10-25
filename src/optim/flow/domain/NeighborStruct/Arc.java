package domain;

public class Arc {
	private final int cost;
	//cost per unit of the arc
	
	private int capacity;
	//capacity left until total fulfillment of the arc
	
	private int residualCapacity=0;
	// residual capacity, equals the flow going through the arc at the moment
	
	private final Node nodeA;
	//origin node
	
	private final Node nodeB;
	//destination node
	
	private final int key;
	// a random key to make sure no user can modify the structure without going first by the network
	
	public Arc(Node nodeA,Node nodeB,int cost,int capacity,int key) {
		this.cost=cost;
		this.capacity=capacity;
		this.nodeA=nodeA;
		this.nodeB=nodeB;
		this.key=key;
	}
	
	public void adjustFlow (int adjustment,int key) {
		if (this.key==key) {
			if (adjustment>capacity) {
				System.out.println("increasing flow over capacity\n");
				throw (new RuntimeException());
			}
			if (adjustment<(-residualCapacity)) {
				System.out.println("decreasing flow below 0\n");
				throw (new RuntimeException());
			}
			capacity=capacity-adjustment;
			residualCapacity=residualCapacity+adjustment;
			nodeA.adjustProd(-adjustment, key);
			nodeB.adjustProd(adjustment, key);
		}else {
			System.out.println("trying to set might go throught aggregate\n");
		}
	}
	//modification of the flow going through the arc, can only be made by the structure itself
	
	public int getInitialCapacity() {
		return capacity+residualCapacity;
	}
	
	public int getCapacityLeft() {
		return capacity;
	}
	
	public int getResidualCapacity() {
		return residualCapacity;
	}
	
	public int getCost() {
		return cost;
	}
	
	public Node[] getNodes(){
		Node[] nodes = new Node[2];	
		nodes[0]=nodeA;
		nodes[1]=nodeB;
		return nodes;
	}
	// return the nodes of the arc [origin,destination]
	
	public int getTotalCost() {
		return cost*residualCapacity;
	}
}
