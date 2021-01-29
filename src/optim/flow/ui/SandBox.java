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
		SuccessiveShortestPathAlgo algo1 = new SuccessiveShortestPathAlgo();
//		System.out.println(Double.toString(googleNet.getEdge(4, 3).getCost()));
//		ResidualNetwork n = new ResidualNetwork(googleNet);
//	Z1
//
//	solution found by successive shortest path
//	dijkstra count: 12
//	time taken by SSP:1324
//	1.2736645399E10 true
//	Z2
//
//	solution found by successive shortest path
//	dijkstra count: 49
//	time taken by SSP:7477
//	1.834984597E9 true
//	Z3
//
//	solution found by successive shortest path
//	dijkstra count: 786
//	time taken by SSP:118 731
//	2.956122454E9 true
//
//	Total error: 0
//
//	total time: 127823
//	done
		boolean test = true;
		if(test) {
			boolean runSSP = true;
			boolean runCS = false;
			boolean runECS = false;
			int nbError = 0;
			long totalTime = System.currentTimeMillis();
			String[] examples = {"example", "example2", "Google"};
			String result = "";
			for (int i=1;i<=22;i++){
				String toTest = 
//						"G1";
//						examples[i-1];
						"A"+i;
//						"B"+i;
//						"X"+i; //7.98017639E8, 6.0615313E7, 2.3434351E8
//						"Z"+i; //1.2736645399E10, 1.834984597E9, 2.956122454E9
				System.out.println(toTest);				
				Double costSSP = 0.; Double costCS = 0.;
				
//				Network netw = networkRepository.load(toTest);
//				netw.displayEdges(true);
//				long tim = System.currentTimeMillis();
//				Network lala = netw.reduceNetwork();
//				System.out.println("it took "+ (System.currentTimeMillis()-tim));
//				System.out.println(netw.getNbEdges() - lala.getNbEdges() + " removed");
				
				if(runECS) {
					System.out.println("\nsolution found by successive enhanced capacity scaling");
					Network net = networkRepository.load(toTest);
					System.out.println("loaded.");
					long timeStartECS= System.currentTimeMillis();
					ResidualNetwork solECS = ECS.solve(net);
					System.out.println("time taken by SSP:"+(System.currentTimeMillis()-timeStartECS));
					//			solSSP.displayEdges();
					System.out.println(solECS.getCost()+" "+solECS.isFeasible());
					costSSP = solECS.getCost();
				}
				if(runSSP) {					
					System.out.println("\nsolution found by successive shortest path");
					Network net = networkRepository.load(toTest);
					System.out.println("loaded.");
					long timeStartSSP = System.currentTimeMillis();
					ResidualNetwork solSSP = SSP.solve(net);
					System.out.println("time taken by SSP:"+(System.currentTimeMillis()-timeStartSSP));
//								solSSP.displayEdges(false);
					System.out.println(solSSP.getCost()+" "+solSSP.isFeasible());
					result += ""+solSSP.getCost()+"\n";
					costSSP = solSSP.getCost();

				}
				if(runCS) {
					System.out.println("\nsolution found by capacity scaling");
					Network net = networkRepository.load(toTest);
					System.out.println("loaded.");
					long timeStartCS = System.currentTimeMillis();
					ResidualNetwork solCS = CS.solve(net);
					System.out.println("time taken by CS:"+(System.currentTimeMillis()-timeStartCS));
//					solCS.displayEdges(false);
					System.out.println(solCS.getCost()+" "+solCS.isFeasible());
					costCS = solCS.getCost();
				}
//				if(runCoS) {
//					
//					System.out.println("\nsolution found by cost scaling");
//					long timeStartCoS = System.currentTimeMillis();
//					ResidualNetwork solCoS = CoS.solve(net);
//					System.out.println("time taken by CoS:"+(System.currentTimeMillis()-timeStartCoS));
//		//			solCoS.displayEdges();
//					System.out.println(solCoS.getCost()+" "+solCoS.isFeasible());
//					costCoS = solCoS.getCost();
//				}
				if(runCS && runSSP) {			
					if(costSSP - costCS != 0) {
						System.out.println("pb with costs SSP:"+ costSSP + " CS " + costCS+" \nSSP is better ? "+ (costSSP<costCS) );
						System.out.println("\n");
						nbError++;
//						return;
					}
				}
//				net.displayEdges(false);
			}		
			System.out.println("\nTotal error: "+nbError);
			System.out.println("\ntotal time: "+ (System.currentTimeMillis()-totalTime));
//			System.out.println();
//			System.out.println("was expected: "+result.equals(expectedResult));
//			if(!result.contentEquals(expectedResult)) {
//				System.out.println("expected Result:\n"+expectedResult+"\nResult:\n"+result);
//			}
//		}
		NetworkFileRepository solRep = new NetworkFileRepository();

		for (int i=1;i<=22;i++){
			Network net = networkRepository.load("A"+i);
			ResidualNetwork sol2 = cplex.solve(net);
			ResidualNetwork sol = algo1.solve(net);
			solRep.save("A" + i +"SSP.sol", sol);
			solRep.save("A" + i +"CplexSol", sol2);
			System.out.println("SSP" + i +" : " + net.getSolutionCost(sol) + "\n");
			System.out.println("Cplex" + i +" : " + net.getSolutionCost(sol2) + "\n");
		}




		System.out.println("done");
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
