package optim.flow.ui;

import optim.flow.domain.Network;
import optim.flow.domain.Repository;
import optim.flow.domain.Solution;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.Parser;
import optim.flow.infra.SolutionFileRepository;

public class SandBox {

	public static void main(String[] args) {
		Repository<Network> networkRepo = new NetworkFileRepository(".txt", 2);

		Network smallNetwork = new Network(10, 75, 30, 30, 10);
		System.out.println("Small network valid: " + smallNetwork.checkValidity());

		networkRepo.save(smallNetwork, "SmallNetwork");

		Network bigNetwork = new Network(100, 5000, 30, 30, 10);
		System.out.println("Big network valid: " + bigNetwork.checkValidity());

		networkRepo.save(bigNetwork, "BigNetwork");

		Parser parser = new Parser();
		parser.saveToFile(bigNetwork, "BigNetworkParser");

		Repository<Solution> solutionRepo = new SolutionFileRepository(".sol", 2);

		double[][] capacityMatrix = { { 0, 0, 0, 2 }, { 0, 0, 3, 2 }, { 4, 0, 0, 0 }, { 0, 0, 0, 0 } };
		double[][] costMatrix = { { 1, 1, 1, 1 }, { 1, 1, 1, 1 }, { 1, 1, 1, 1 }, { 1, 1, 1, 1 } };
		double[] verticesDemand = { 0, -100, 2, 3 };

		Network handNetwork = new Network(capacityMatrix, costMatrix, verticesDemand);
		networkRepo.save(handNetwork, "HandNetwork");

		double[][] flowMatrix = { { 0, 0, 0, 1 }, { 0, 0, 3, 2 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } };

		Solution handSolution = new Solution(flowMatrix);

		System.out.println(handNetwork);

		solutionRepo.save(handSolution, "HandSolution");

		System.out.println("Hand solution valid for hand network: " + handNetwork.verifySolutionValidity(handSolution));
		System.out.println("Hand solution cost: " + handNetwork.calculateSolutionCost(handSolution));

	}

}
