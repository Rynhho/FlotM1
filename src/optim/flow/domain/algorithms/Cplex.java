package optim.flow.domain.algorithms;

import optim.flow.domain.Network;
import optim.flow.domain.Solution;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Cplex implements Algorithm{


    public Solution solve(Network net){
        try{
            IloCplex cplex = new IloCplex();
            int n = net.getNbVertices();
            Solution sol = new Solution(n);
            IloNumVar[][] X = new IloNumVar[n][];
            for (int i=0;i<n;i++){
                X[i] = cplex.numVarArray(n,0.0,Integer.MAX_VALUE);
                for (int j=0;j<n;j++){
                    X[i][j].setName("X["+i+"]["+j+"]");
                }
            }

            double[] prod = new double[n];
            for (int i=0;i<n;i++){
                prod[i] = net.getVertexDemand(i);
            }

            IloLinearNumExpr obj = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
                for (int j = 0;j < n; j++){
                    //System.out.println(i + " and " + j +" on " + n + "\n");
                    //if (net.hasEdgeBetween(i,j)){
                    obj.addTerm(net.getEdgeCost(i, j), X[i][j]);
                    //}
                }
            }
            cplex.addMinimize(obj);

            for (int i=0; i<n; i++) {
				for (int j=0; j<n; j++) {
                    //if (net.hasEdgeBetween(i,j)){
                        IloLinearNumExpr ct = cplex.linearNumExpr();
                        ct.addTerm(1.0, X[i][j]);
                        cplex.addLe(ct, net.getEdgeCapacity(i,j));
                    //}
				}
            }
            
            for (int i=0; i<n; i++){
                IloLinearNumExpr ct = cplex.linearNumExpr();
                for (int j=0;j<n;j++){
                    ct.addTerm(-1.0,X[i][j]);
                    ct.addTerm(1.0,X[j][i]);
                }
                cplex.addEq(ct, -net.getVertexDemand(i));
            }

            cplex.setParam(IloCplex.IntParam.TimeLimit, 600);
            if (cplex.solve()) {
                double[][] flowMatrix = new double[n][];
                for (int i=0;i<n;i++){
                    flowMatrix[i] = new double [n];
                    for (int j=0;j<n;j++){
                        flowMatrix[i][j]=cplex.getValue(X[i][j]);
                    }
                }

                sol = new Solution(flowMatrix);

                System.out.println("non d'un gigawatt ca compile\n");
                
			}else {
				System.out.println("error, cannot find solution\n");
			}
			cplex.end();

            return sol;
        }
        catch (IloException e){
            System.out.println("something gone wrong with Cplex \n");
            throw (new RuntimeException());
        }
    }
}