package optim.flow.domain.algorithms;

import optim.flow.domain.data.Network;
import optim.flow.domain.data.Solution;

/**
 * Class to be implemented by network flow solving algorithms.
 */
public interface Algorithm {
    /**
     * Implementation of the specific algorithm.
     * 
     * @param instance The network flow instance to solve.
     * 
     * @return A realisable solution.
     */
    Solution solve(Network Network);
}
