package NeighborStruct;

import java.util.ArrayList;
import java.util.Random;

public class Neighbors {
	private final ArrayList <Arc>[] neighbors;
	// array of lists of arcs, the array is n long and neighbors[i] is the list of arcs which have the node of id i for origin
	
	private int cost;
	// total cost of the network
	
	private final int key;
	// a random key to make sure no user can modify the structure without going first by the network
	
	private final NodeList nl;
	// list of nodes
	
	private int finished=0;
	// state of the class (equals 0 if parsing in progress ; equals 1 if solving in progress)
	
	public Neighbors(int n) {
		Random rand = new Random();
		key = rand.nextInt(1000000);
		this.neighbors = new ArrayList[n] ;
		for (int i=0;i<n;i++) {
			neighbors[i] = new ArrayList <Arc>();
		}
		nl = new NodeList(n);
		cost=0;
	}
	
	public ArrayList<Arc> getNeighbors(int NodeID){
		return neighbors[NodeID];
	}
	
	public void addNeighbor(int nodeAId,int nodeBId,int capacity,int cost) {
		if (finished==1) {
			System.out.println("You may not add arcs after parsing is done\n");
			throw (new RuntimeException());
		}
		neighbors[nodeAId].add(new Arc(FindNodeById(nodeAId),FindNodeById(nodeBId),capacity,cost,key));
	}
	// add an arc going from the node of id nodeAId to the node of id nodeBId with a capacity and cost of initial capacity and cost
	
	public void addNode(int id,int prod) {
		if (finished==1) {
			System.out.println("You may not add arcs after parsing is done\n");
			throw (new RuntimeException());
		}
		Node node = new Node(id,prod,key);
		nl.add(node);
	}
	// add a node to the network
	
	public Node FindNodeById (int id) {
		return nl.FindById(id);
	}
	// return the node of id id
	
	public void flowAdjustment(int nodeAId,int nodeBId,int adjustment) {
		if (finished==0) {
			System.out.println("You may not modify flows before ending parsing\n");
			throw (new RuntimeException());
		}
		ArrayList<Arc> nb = neighbors[nodeAId];
		int trash=0;
		int cost;
		for (int i=0;i<nb.size();i++) {
			if (nb.get(i).getNodes()[1].getId()==nodeBId) {
				trash++;
				cost = nb.get(i).getTotalCost();
				nb.get(i).adjustFlow(adjustment, key);
				this.cost = this.cost + nb.get(i).getTotalCost() - cost;
			}
		}
		if (trash != 1) {
			System.out.printf("node %d having %d arcs with node %d when 1 is required \n", nodeAId,trash,nodeBId);
			throw (new RuntimeException());
		}
	}
	// increasing or lowering the flow into the arc going from the node of id nodeAId to the node of id nodeBId by adjustment
	
	public void finish() {
		finished=1;
		nl.finish();
	}
	// Swapping from parsing to solving
	
	public int getNetworkCost() {
		return cost;
	}
}
