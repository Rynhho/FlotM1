package optim.flow.ui;

import optim.flow.domain.*;
import optim.flow.domain.algorithms.*;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.SolutionFileRepository;

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
  		// double[] verticesDemand = {7, 8 };

		// double[][] optimal = { { 10, 1, 9, 0, 0 }, { 0, 0, 6, 0, 0 }, { 0, 0, 0, 15,
		// 0 }, { 0, 0, 0, 0, 8 },
		// { 0, 0, 0, 0, 0 } };
		// double optimalCost = 20;

		// List<List<Edge>> adjacenceList = new ArrayList<List<Edge>>();
		// for (int i = 0; i < 2; i++) {
		//	 adjacenceList.add(new ArrayList<Edge>());
		// }
		// adjacenceList.get(0).add(new Edge(0,1,0,0));
		// for (int j = 0; j < 5; j++) {
		// if (capacityMatrix[i][j] != 0) {
		// adjacenceList.get(i).add(new Edge(j, capacityMatrix[i][j], costMatrix[i][j]
		// ));
		// }
		// }
		// }

		// Network handNetwork = new Network(adjacenceList, verticesDemand);

		Algorithm cplex = new CplexAlgorithm();
		
		Repository<Network> networkRepository = new NetworkFileRepository();
//		Network googleNet = networkRepository.load("Google");
//		System.out.println(googleNet);
		capacityScaling CS = new capacityScaling(); 
		SuccessiveShortestPathAlgo SSP = new SuccessiveShortestPathAlgo();

		CostScaling CoS = new CostScaling();
//		return;
//				SuccessiveShortestPathAlgo();
		Repository<ResidualNetwork> solRep = new SolutionFileRepository();
//		System.out.println(Double.toString(googleNet.getEdge(4, 3).getCost()));
//		ResidualNetwork n = new ResidualNetwork(googleNet);
	
//		
//		System.out.println(ex);
//		Dijkstra d = new Dijkstra();
//		BellmanFord bellman = new BellmanFord();
//		System.out.println(CS.addSinkAndSource(ex));
//		bellman.solve(CS.addSinkAndSource(ex), 0);
//		Network exSS = CS.addSinkAndSource(ex);
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
		
//		System.out.println(sol);
//		for (int i = 0; i < sol.getNbVertices(); i++) {
//			System.out.println(sol.getVertexDemand(i));
//			for (Edge edge: sol.getOutEdges(i)) {
//				System.out.println(edge);
//				edge.updateReducedCost(bellman.getDist().get(edge.getSource()) - bellman.getDist().get(edge.getDestination()));
//				System.out.println(edge+"\n");
//				if(sol.getFlow(edge) != 0 && sol.isInOriginalNet(edge))
//					System.out.println(edge+ " flow: "+sol.getFlow(edge));
//			}
//		}
//		NetworkFileRepository solRep = new NetworkFileRepository();
		
		boolean test = true;
		if(test) {
			
//			for (int i=1;i<=22;i++){
//				Network net = networkRepository.load("A"+i);
//				ResidualNetwork sol = CS.solve(net);
//				solRep.save("A" + i +"SSP.sol", sol);
//				System.out.println("SSP" + i +" : " + sol.getCost() + " "+sol.isFeasible()+ "\n");
//	//			ResidualNetwork sol2 = solRep.load("A" + i +"CplexSol");
//	//			System.out.println(sol2.getCost());
//				
//	//			System.out.println("Cplex" + i +" : " + net.getSolutionCost(sol2) + "\n");
//			}
//	
//			Network net = networkRepository.load("Google");
//			ResidualNetwork sol = CS.solve(net);
//			sol.displayEdges();
			String expectedResult = 
					"1.3999986E9\n" + 
					"5.7752234177E10\n" + 
					"1.10803271214E11\n" + 
					"1.56789569406E11\n" + 
					"1.64661111914E11\n" + 
					"4.30427916578E11\n" + 
					"4.26441403222E11\n" + 
					"5.09793469861E11\n" + 
					"2.68388402126E11\n" + 
					"3.09955345884E11\n" + 
					"4.0323221232E11\n" + 
					"2.09872787154E11\n" + 
					"1.1805477067E11\n" + 
					"1.67052688488E11\n" + 
					"1.75177496328E11\n" + 
					"3.57754256381E11\n" + 
					"2.81924142006E11\n" + 
					"5.8962995982E10\n" + 
					"2.85381545026E11\n" + 
					"1.48493862907E11\n" + 
					"1.40049729897E11\n" + 
					"1.40672231305E11";
//			System.out.println(sol.getCost()+" "+sol.isFeasible());
			boolean runSSP = false;
			boolean runCS = false;
			boolean runCoS = true;
			long totalTime = System.currentTimeMillis();
			String[] examples = {"example", "example2", "Google"};
			String result = "";
			for (int i=1;i<=3;i++){
				String toTest = 
						examples[i-1];
//						"A"+i;
//						"B"+i;
//						"X"+i; //7.98017639E8
//						"Z"+i;
				System.out.println(toTest);
				Network net = networkRepository.load(toTest);
				Double costSSP = 0.; Double costCS = 0.; Double costCoS = 0.;
//				net = (net.reduceNetwork());
//				.displayEdges(false);
//				System.out.println("end");
//				net.displayEdges(false);
//				System.out.println("network edges\n");
//				net.displayEdges();
				
//				ResidualNetwork sol2 = cplex.solve(net);
//				solRep.save("A" + i +"CplexSol", sol2);
				if(runCS) {
					
					System.out.println("\nsolution found by capacity scaling");
					long timeStartCS = System.currentTimeMillis();
					ResidualNetwork solCS = CS.solve(net);
					System.out.println("time taken by CS:"+(System.currentTimeMillis()-timeStartCS));
		//			solCS.displayEdges();
					System.out.println(solCS.getCost()+" "+solCS.isFeasible());
					costCS = solCS.getCost();
				}
				if(runSSP) {					
					System.out.println("\nsolution found by successive shortest path");
					long timeStartSSP = System.currentTimeMillis();
					ResidualNetwork solSSP = SSP.solve(networkRepository.load(toTest));
					System.out.println("time taken by SSP:"+(System.currentTimeMillis()-timeStartSSP));
		//			solSSP.displayEdges();
					System.out.println(solSSP.getCost()+" "+solSSP.isFeasible());
					result += ""+solSSP.getCost()+"\n";
					costSSP = solSSP.getCost();
				}
				if(runCoS) {
					
					System.out.println("\nsolution found by cost scaling");
					long timeStartCoS = System.currentTimeMillis();
					ResidualNetwork solCoS = CoS.solve(net);
					System.out.println("time taken by CoS:"+(System.currentTimeMillis()-timeStartCoS));
		//			solCoS.displayEdges();
					System.out.println(solCoS.getCost()+" "+solCoS.isFeasible());
					costCoS = solCoS.getCost();
				}
				if(runCS && runSSP) {			
					if(costSSP - costCS != 0) {
						System.out.println("pb with costs SSP:"+ costSSP + " CS " + costCS+" \nSSP is better ? "+ (costSSP<costCS) );
						System.out.println();
//						return;
					}
				}
//				net.displayEdges(false);
			}			
			System.out.println("\ntotal time: "+ (System.currentTimeMillis()-totalTime));
//			System.out.println();
//			System.out.println("was expected: "+result.equals(expectedResult));
//			if(!result.contentEquals(expectedResult)) {
//				System.out.println("expected Result:\n"+expectedResult+"\nResult:\n"+result);
//			}
		}
		System.out.println("done");
	
//		System.out.println(bellman.getDist());
//		System.out.println(bellman.solve(CS.addSinkAndSource(ex), 0));
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
//		CS.solution = new ResidualNetwork(googleNet);
//		CS.solution.addFlow(CS.solution.getEdges(0, 1).get(0), 5);
//		CS.updateList(googleNet);
//		CS.solve(googleNet);
//		System.out.println(googleNet.getEdges(2, 4).get(0).getCost());
//
//		networkRepository.save("Test", googleNet);
//		 List<Integer> b = d.solve(googleNet, 1, 4);
////		 for(int i:b) {
////		 	System.out.println(i);
////		 }
//		Solution sol = CS.solve(googleNet);
		// System.out.println(googleNet.getEdge(2, 4).getCost());
		
//		System.out.println("lala");
//		CS.getFullGraph(googleNet);
	}
}
