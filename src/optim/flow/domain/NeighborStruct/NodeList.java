package NeighborStruct;

import java.util.ArrayList;

public class NodeList {
	private final ArrayList <Node> nodes = new ArrayList <Node> ();
	// list of nodes
	
	private final int n;
	// total number of nodes in the network
	
	private int finished=0;
	// state of the class (if 0, parsing in progress ; if 1, solving in progress)
	
	public NodeList (int n) {
		this.n=n;
	}
	
	public void add(Node node) {
		if (finished==1) {
			System.out.println("trying to add nodes after parsing\n");
			throw (new RuntimeException());
		}
		nodes.add(node);
		if (node.getProduction()>0) {
			
		}
	}
	// add a node if parsing in progress
	
	public void finish () {
		if (nodes.size() != n) {
			System.out.println("trying to end parsing without entering all nodes\n");
		}
		finished=1;
	}
	// swap to parsing to solving
	
	public Node FindById(int i) {
		return nodes.get(i);
	}
	// return node of ID i
	
}
