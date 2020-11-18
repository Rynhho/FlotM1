package optim.flow.domain.algorithms;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.Solution;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CplexAlgorithm implements Algorithm {

    @Override
    public Solution solve(Network net) {
        try {
            int debug = 1;
            // if equals to 1, print flows of edge comming in/out of the vertex and it's
            // supply demand

            IloCplex cplex = new IloCplex();
            int n = net.getNbVertices();
            double[][] flowMatrix = new double[n][];
            IloNumVar[][] X = new IloNumVar[n][];
            for (int i = 0; i < n; i++) {
                X[i] = cplex.numVarArray(n, 0.0, Integer.MAX_VALUE);
                for (int j = 0; j < n; j++) {
                    X[i][j].setName("X[" + i + "][" + j + "]");
                }
            }

            double[] prod = new double[n];
            for (int i = 0; i < n; i++) {
                prod[i] = net.getVertexDemand(i);
            }

            // Objective
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int i = 0; i < n; i++) {
                for (Edge edge : net.getOutEdges(i)) {
                    obj.addTerm(edge.getCost(), X[i][edge.getDestination()]);
                }
            }
            cplex.addMinimize(obj);

            // Capacity Constraint
            for (int i = 0; i < n; i++) {
                for (Edge edge : net.getOutEdges(i)) {
                    IloLinearNumExpr ct = cplex.linearNumExpr();
                    ct.addTerm(1.0, X[i][edge.getDestination()]);
                    cplex.addLe(ct, edge.getCapacity());
                }
            }

            // Todo: complexity
            // Demand constraint
            for (int i = 0; i < n; i++) {
                IloLinearNumExpr ct = cplex.linearNumExpr();
                for (int j = 0; j < n; j++) {
                    if (net.hasEdgeBetween(i, j) && i != j) {
                        // System.out.println(" + " + net.getEdgeCapacity(i, j));
                        ct.addTerm(1.0, X[i][j]);
                    } else {
                        if (net.hasEdgeBetween(j, i) && i != j) {
                            // System.out.println(" - " + net.getEdgeCapacity(j, i));
                            ct.addTerm(-1.0, X[j][i]);
                        }
                    }
                }
                // System.out.println(" = " + (-net.getVertexDemand(i)) + "\n");
                cplex.addEq(ct, -net.getVertexDemand(i));
            }

            cplex.setParam(IloCplex.IntParam.TimeLimit, 600);

            // solve and create a Solution
            if (cplex.solve()) {

                for (int i = 0; i < n; i++) {
                    flowMatrix[i] = new double[n];
                    for (int j = 0; j < n; j++) {
                        flowMatrix[i][j] = cplex.getValue(X[i][j]);
                    }
                }

            } else {
                System.out.println("error, cannot find solution\n");
            }

            if (debug == 1) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (net.hasEdgeBetween(i, j) && i != j) {
                            System.out.println(" + " + cplex.getValue(X[i][j]));
                        } else {
                            if (net.hasEdgeBetween(j, i) && i != j) {
                                System.out.println(" - " + cplex.getValue(X[j][i]));
                            }
                        }
                    }
                    System.out.println(" = " + (-net.getVertexDemand(i)) + "\n");
                }
            }

            Solution sol = new Solution("CplexSolution", flowMatrix);

            cplex.end();

            return sol;
        } catch (IloException e) {
            System.out.println("something gone wrong with Cplex \n");
            throw (new RuntimeException());
            // return new Solution(12);
        }
    }
}
