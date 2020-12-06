package optim.flow.domain.algorithms;

import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

public interface Algorithm {
    ResidualNetwork solve(Network Network);
}
