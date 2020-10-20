package optim.flow.ui;

import optim.flow.domain.Network;
import optim.flow.domain.Repository;
import optim.flow.domain.Solution;
import optim.flow.infra.NetworkFileRepository;
import optim.flow.infra.SolutionFileRepository;

public class CLI {
    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage();
            return;
        } else {
            String mode = args[0];
            if (mode == "-c") {
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

                if (network.verifySolutionValidity(solution)) {
                    System.out.println("Solution valid.\n");
                } else {
                    System.out.println("Solution unvalid.\n");
                }
            } else if (mode == "-r") {
                String algorithm = args[1];

                String inputFilename = args[2];

                String outputFilename = args[3];

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
        System.out.println("Check Readme for instruction on how to use the application.\n");
    }
}
