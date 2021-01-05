package optim.flow.ui;

import optim.flow.domain.*;
import optim.flow.domain.algorithms.*;
import optim.flow.infra.NetworkFileRepository;

import java.util.List;
import java.util.ArrayList;

public class SandBox {
	public static void main(String[] args) {

		// double[][] capacityMatrix = { { 10, 12, 9, 0, 0 }, { 0, 0, 6, 0, 0 }, { 0, 0,
		// 0, 15, 0 }, { 0, 0, 0, 0, 9 },
		// { 0, 0, 0, 0, 0 } };
		// double[][] costMatrix = { { -1, 0, 1, -10, 10 }, { 10, 10, -25, -10, -10 }, {
		// 0, 0, 0, 5, 0 },
		// { -10, -10, -1, 1, 3 }, { 0, 0, 0, 0, 0 } };
		 double[] verticesDemand = {7, 8 };

		// double[][] optimal = { { 10, 1, 9, 0, 0 }, { 0, 0, 6, 0, 0 }, { 0, 0, 0, 15,
		// 0 }, { 0, 0, 0, 0, 8 },
		// { 0, 0, 0, 0, 0 } };
		// double optimalCost = 20;

		 List<List<Edge>> adjacenceList = new ArrayList<List<Edge>>();
		 for (int i = 0; i < 2; i++) {
			 adjacenceList.add(new ArrayList<Edge>());
		 }
		 adjacenceList.get(0).add(new Edge(0,1,0,0));
		// for (int j = 0; j < 5; j++) {
		// if (capacityMatrix[i][j] != 0) {
		// adjacenceList.get(i).add(new Edge(j, capacityMatrix[i][j], costMatrix[i][j]
		// ));
		// }
		// }
		// }

		// Network handNetwork = new Network(adjacenceList, verticesDemand);

		// Algorithm cplex = new CplexAlgorithm();
		// Solution CplexSolution = cplex.solve(handNetwork);

		// double flow = 0;
		// for (int i = 0; i < 5; i++) {
		// flow = -CplexSolution.getVertexFlowIn(i) + CplexSolution.getVertexFlowOut(i)
		// + handNetwork.getVertexDemand(i);
		// if (flow != 0) {
		// System.out.println("vertex " + i + " has been violated");
		// }
		// }

		// double cost = 0;
		// for (int i = 0; i < 5; i++) {
		// for (int j = 0; j < 5; j++) {
		// cost += CplexSolution.getEdgeFlow(i, j) * costMatrix[i][j];
		// System.out.println("edge " + i + " " + j + " on flow " +
		// CplexSolution.getEdgeFlow(i, j));
		// }
		// }
		// System.out.println(cost);

		Repository<Network> networkRepository = new NetworkFileRepository();
//		Network googleNet = networkRepository.load("Google");
//		System.out.println(googleNet);
		SuccessiveShortestPathAlgo algo1 = new SuccessiveShortestPathAlgo();
//		System.out.println(Double.toString(googleNet.getEdge(4, 3).getCost()));
//		ResidualNetwork n = new ResidualNetwork(googleNet);
		Network ex = networkRepository.load("Google");
		System.out.println(ex);
//		
//		System.out.println(ex);
//		Dijkstra d = new Dijkstra();
//		BellmanFord bellman = new BellmanFord();
//		System.out.println(algo1.addSinkAndSource(ex));
//		bellman.solve(algo1.addSinkAndSource(ex), 0);
//		Network exSS = algo1.addSinkAndSource(ex);
//		for (int i = 0; i < exSS.getNbVertices(); i++) {
//			for (Edge edge: exSS.getOutEdges(i)) {
////				System.out.println(edge);
//				edge.updateReducedCost(bellman.getDist().get(edge.getSource()) - bellman.getDist().get(edge.getDestination()));
////				System.out.println(edge+"\n");
//			}
//		}
//		ResidualNetwork exRN = new ResidualNetwork(exSS);
//		System.out.println(d.solve(exRN, 0, 1));
//		System.out.println(exRN);
		ResidualNetwork sol = algo1.solve(ex);
//		System.out.println(sol);
		for (int i = 0; i < sol.getNbVertices(); i++) {
//			System.out.println(sol.getVertexDemand(i));
			for (Edge edge: sol.getOutEdges(i)) {
//				System.out.println(edge);
//				edge.updateReducedCost(bellman.getDist().get(edge.getSource()) - bellman.getDist().get(edge.getDestination()));
//				System.out.println(edge+"\n");
				if(sol.getFlow(edge) != 0 && sol.isInOriginalNet(edge))
					System.out.println(edge+ " flow: "+sol.getFlow(edge));
			}
		}
//		System.out.println(bellman.getDist());
//		System.out.println(bellman.solve(algo1.addSinkAndSource(ex), 0));
//		System.out.println(bellman.getDist().toString());
//		System.out.println(d.solve(new ResidualNetwork(googleNet), 0, 4)+"lala");
//		List<Edge> edges = d.solve(new ResidualNetwork(exSS), 0, 1);
//		
//		System.out.println(edges);
//		System.out.println(-exSS.getVertexDemand(edges.get(0).getDestination())+" "+ exSS.getVertexDemand(edges.get(edges.size()-1).getSource()));
//		double delta = Math.min(-exSS.getVertexDemand(edges.get(0).getDestination()), exSS.getVertexDemand(edges.get(edges.size()-1).getSource()));
//	    for(int j = 0; j < edges.size()-1; j++) {
////	    	dist += path.get(j).getCost();
//	//      			getEdges(path.get(j+1), path.get(j)).get(0).getCost();
//	    	System.out.println(edges.get(j).getCapacity());
//	      	delta = Math.min(delta, edges.get(j).getCapacity());
//	      }
////    	
//		System.out.println(delta);
//		Network net = new Network(adjacenceList, verticesDemand);
//		List<Edge> b = d.solve(net, 0, 1);
//		System.out.println("list edges");
//		for (Edge i : b) {
//			System.out.println(i);
//		}
//		algo1.solution = new ResidualNetwork(googleNet);
//		algo1.solution.addFlow(algo1.solution.getEdges(0, 1).get(0), 5);
//		algo1.updateList(googleNet);
//		algo1.solve(googleNet);
//		System.out.println(googleNet.getEdges(2, 4).get(0).getCost());
//
//		networkRepository.save("Test", googleNet);
//		 List<Integer> b = d.solve(googleNet, 1, 4);
////		 for(int i:b) {
////		 	System.out.println(i);
////		 }
//		Solution sol = algo1.solve(googleNet);
		// System.out.println(googleNet.getEdge(2, 4).getCost());
		
//		System.out.println("lala");
//		algo1.getFullGraph(googleNet);
	}
}
