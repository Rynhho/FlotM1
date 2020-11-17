package optim.flow.ui;

import optim.flow.domain.Network;
import optim.flow.domain.Repository;
import optim.flow.domain.Solution;
import optim.flow.domain.algorithms.Algorithm;
import optim.flow.domain.algorithms.CplexAlgorithm;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.SolutionFileRepository;

public class CLI {
    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage();
            return;
        } else {
            String mode = args[0];
            if (mode.compareTo("-c") == 0) {
                if (args.length == 3) {
                    String inputInstanceFile = args[1];
                    String inputSolutionFile = args[2];

                    Repository<Network> networkRepo = new NetworkFileRepository();
                    Network network = networkRepo.load(inputInstanceFile);
                    if (network == null) {
                        System.out.println("Error: Could not load network instance.\n");
                        return;
                    }

                    Repository<Solution> solutionRepo = new SolutionFileRepository();
                    Solution solution = solutionRepo.load(inputSolutionFile);
                    if (solution == null) {
                        System.out.println("Error: Could not load network solution file.\n");
                        return;
                    }

                    if (network.isSolutionValid(solution)) {
                        System.out.println(
                                "Solution valid. Its cost is " + network.calculateSolutionCost(solution) + ".\n");
                    } else {
                        System.out.println("Solution unvalid.\n");
                    }
                } else {
                    showUsage();
                    return;
                }
            } else if (mode.compareTo("-r") == 0) {
                String algorithmStr = args[1];

                String networkID = args[2];

                String outputSolutionID = args[3];

                Repository<Network> networkRepo = new NetworkFileRepository();
                Repository<Solution> solutionRepo = new SolutionFileRepository();

                Network network = networkRepo.load(networkID);

                Algorithm algorithm = null;

                if (algorithmStr.equals("cplex")) {
                    algorithm = new CplexAlgorithm();
                } else {
                    showUsage();
                    return;
                }

                Solution solution = algorithm.solve(network);

                solutionRepo.save(solution);
            } else {
                showUsage();
                return;
            }
        }
    }

    /**
     * Shows the usage of the command line interface.
     */
    static private void showUsage() {
        System.out.println("Check Readme for instruction on how to use the command line interface.\n");
    }
}
