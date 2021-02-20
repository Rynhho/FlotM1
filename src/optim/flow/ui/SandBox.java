package optim.flow.ui;

import optim.flow.domain.*;
import optim.flow.domain.algorithms.*;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.SolutionFileRepository;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class SandBox {
	public static void main(String[] args) {
//  		 double[] verticesDemand = {7, 8 , 2, 42, 1, 23, 9};
//  		 HeapTree hp = new HeapTree(verticesDemand);
//  		 System.out.println("ll");
////  		hp.updateDist(5, 0);
//  		hp.updateDist(2, -1);
////  		hp.updateDist(6, 3);
//  		System.out.println(hp.poll());
//  		hp.updateDist(2, -2);
//  		System.out.println(hp.poll());
//  		hp.updateDist(0, 3);
//  		System.out.println(hp.poll());
//  		hp.updateDist(5, -2);
//  		hp.updateDist(5, -3);
//  		hp.updateDist(5, -4);
//  		
//  		System.out.println(hp);
//  		System.out.println(hp.poll());
//  		System.out.println(hp.poll());
//  		System.out.println(hp);
//  		System.out.println(hp.poll());
//  		System.out.println(hp.poll());
//  		System.out.println(hp.poll());

				
		Repository<Network> networkRepository = new NetworkFileRepository();
//		Network googleNet = networkRepository.load("Google");
//		System.out.println(googleNet);
		Algorithm CPLEX = new CplexAlgorithm();
		SuccessiveShortestPath SSP = new SuccessiveShortestPath();
		capacityScaling CS = new capacityScaling();
		EnhancedCapacityScaling ECS = new EnhancedCapacityScaling();

//				SuccessiveShortestPath();
		Repository<ResidualNetwork> solRep = new SolutionFileRepository();
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
			boolean runSSP = false;
			boolean runCS = false;
			boolean runECS = false;
			boolean runAll = true;
			int nbError = 0;
			long totalTime = System.currentTimeMillis();
			String[] examples = {"example", "example2", "Google"};
			String[] Instances = {"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14", "A15", "A16", "A17", "A18", "A19", "A20", "A21", "A22", "B1", "B2", "B3", "B4", "X1", "X2", "X3", "Z1", "Z2", "Z3"};
			String result = "";
			for (int i=1;i<=3;i++){
				String toTest = 
//						"G1";
//						examples[i-1];
//						"A"+i;
//						"B"+i;
//						"X"+i; //7.98017639E8, 6.0615313E7, 2.3434351E8
						"Z"+i; //1.2736645399E10, 1.834984597E9, 2.956122454E9
				//System.out.println(toTest);				
				Double costSSP = 0.; Double costCS = 0.;
				
//				Network netw = networkRepository.load(toTest);
//				netw.displayEdges(true);
//				long tim = System.currentTimeMillis();
//				Network lala = netw.reduceNetwork();
//				System.out.println("it took "+ (System.currentTimeMillis()-tim));
//				System.out.println(netw.getNbEdges() - lala.getNbEdges() + " removed");
				
				if(runAll){
					String str = "";
					str = str + "instance"+"\t"+"nb vert"+"\t"+"nb edge"+"\t"+
					"timeSSP"+"\t"+"SSPCost"+"\t"+"SSPDijCount"+"\t"+
					"timeCS"+"\t"+"CSCost"+"\t"+"CSDijCount"+"\n"+"\n";

					for (String instance : Instances) {
						Network net = networkRepository.load(instance);

						String dataNet = instance+"\t"+net.getNbVertices()+"\t"+net.getNbEdges()+"\t";
						System.out.print(dataNet);

						long timeStartSSP= System.currentTimeMillis();
						ResidualNetwork solSSP = SSP.solve(net);
						long timeSSP = System.currentTimeMillis()-timeStartSSP;

						String dataSSP = timeSSP+"\t"+solSSP.getCost()+"\t"+SSP.getDijkstraCount()+"\t";
						System.out.print(dataSSP);

						long timeStartCS= System.currentTimeMillis();
						ResidualNetwork solCS = CS.solve(net);
						long timeCS = System.currentTimeMillis()-timeStartCS;

						String dataCS = timeCS+"\t"+solCS.getCost()+"\t"+CS.getDijkstraCount()+"\n";
						System.out.print(dataCS);

						// long timeStartCPLEX= System.currentTimeMillis();
						// ResidualNetwork solCPLEX = CPLEX.solve(net);
						// long timeCPLEX = System.currentTimeMillis()-timeStartCPLEX;
						
						str = str + dataNet+dataSSP+dataCS;

					}
					try {
						FileWriter fileWriter = new FileWriter("result.txt");
						fileWriter.write(str);
						fileWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
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
//		NetworkFileRepository solRep = new NetworkFileRepository();
//
//		for (int i=1;i<=22;i++){
//			Network net = networkRepository.load("A"+i);
////			ResidualNetwork sol2 = cplex.solve(net);
//			ResidualNetwork sol = algo1.solve(net);
//			solRep.save("A" + i +"SSP.sol", sol);
//			solRep.save("A" + i +"CplexSol", sol2);
//			System.out.println("SSP" + i +" : " + net.getSolutionCost(sol) + "\n");
//			System.out.println("Cplex" + i +" : " + net.getSolutionCost(sol2) + "\n");
//		}
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
	public String setDemandsEqualToProduction(Network net) {
		
		double[] verticesDemands = new double[net.getNbVertices()];
		SuccessiveShortestPath SSP = new SuccessiveShortestPath();
		ResidualNetwork solSSP = SSP.solve(net);
		for (int j = 0; j < net.getNbVertices(); j++) {
			if(net.getVertexDemand(j)>=0)
				verticesDemands[j] = net.getVertexDemand(j);
			else
				verticesDemands[j] = Math.max(net.getVertexDemand(j), solSSP.getVertexFlowOut(j));
//			System.out.println(net.getVertexDemand(j)+ " "+verticesDemands[j]);
		}
		net.setVerticesDemands(verticesDemands);
		return ((NetworkFileRepository)new NetworkFileRepository()).getVertexProdDemand(net);
//		save(toTest, net);
	}
}
