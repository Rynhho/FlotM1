package optim.flow.ui;


import optim.flow.domain.algorithms.*;

import java.util.List;
import java.util.ArrayList;

import optim.flow.domain.*;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.SolutionFileRepository;

public class SandBox {
	public static void main(String[] args) {

		double[][] capacityMatrix = { { 10, 12, 9, 0, 0 }, { 0, 0, 6, 0, 0 }, { 0, 0, 0, 15, 0 }, { 0, 0, 0, 0, 9 }, { 0, 0, 0, 0, 0 }};
		double[][] costMatrix = { { -1, 0, 1, -10, 10 }, { 10, 10, -25, -10, -10 }, { 0, 0, 0, 5, 0 }, { -10, -10, -1, 1, 3 }, { 0, 0, 0, 0, 0} };
		double[] verticesDemand = { -10, -5, 0, 7, 8 };

		double [][] optimal = {{ 10, 1, 9, 0, 0 }, { 0, 0, 6, 0, 0 }, { 0, 0, 0, 15, 0 }, { 0, 0, 0, 0, 8 }, { 0, 0, 0, 0, 0 }};
		double optimalCost = 20;

		List<List<Edge>> adjacenceList = new ArrayList<List<Edge>>();
		for (int i=0;i<5;i++){
			adjacenceList.add(new ArrayList<Edge>());
			for (int j=0;j<5;j++){
				if (capacityMatrix[i][j]!=0){
					adjacenceList.get(i).add(new Edge(i,j,costMatrix[i][j],capacityMatrix[i][j]));
				}
			}
		}

		Network handNetwork = new Network("lucas",adjacenceList, verticesDemand);

		Cplex cplex = new Cplex();
		Solution CplexSolution = cplex.solve(handNetwork);


		double flow=0;
		for (int i=0;i<5;i++){
				flow = -CplexSolution.getVertexFlowIn(i)+CplexSolution.getVertexFlowOut(i)+handNetwork.getVertexDemand(i);
				if (flow !=0){
					System.out.println("vertex " + i + " has been violated");
				}
		}


		double cost = 0;
		for (int i=0;i<5;i++){
			for (int j=0;j<5;j++){
				cost+= CplexSolution.getEdgeFlow(i, j)*costMatrix[i][j];
				System.out.println("edge " + i + " " + j + " on flow " +  CplexSolution.getEdgeFlow(i, j));
			}
		}
		System.out.println(cost);
	}
}
