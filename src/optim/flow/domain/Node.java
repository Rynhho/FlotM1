package domain;


public class Node {
	private int prod;
	// actual production or demand of the node 
	
	private final int id;
	private final int key;
	// a random key to make sure no user can modify the structure without going first by the network
	
	public Node(int id,int prod,int key) {
		this.prod=prod;
		this.id=id;
		this.key=key;
	}
	
	public int getProduction() {
		return prod;
	}
	
	public int getId() {
		return id;
	}
	
	public void adjustProd(int adjustment,int key) {
		if (this.key != key) {
			System.out.println("you might always go throught aggregate\n");
			throw (new RuntimeException());
		}
		if (prod + adjustment < 0) {
			System.out.printf("Node %d getting more out flow than flow in + production \n",id);
			throw (new RuntimeException());
		}
		prod = prod + adjustment;
	}
	// adjust the production or demand of the node after a modification of the flow, can only be made by the structure itself
}
