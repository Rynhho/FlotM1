package optim.flow.domain.algorithms;

import optim.flow.domain.Network;
import optim.flow.domain.Solution;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CplexAlgorithm implements Algorithm{


    public Solution solve(Network net){
        try{
            //int debug = 0;
            // if equals to 1, print flows of edge comming in/out of the vertex and it's supply demand
            

            IloCplex cplex = new IloCplex();
            int n = net.getNbVertices();
            double[][] flowMatrix = new double[n][];
            IloNumVar[][] X = new IloNumVar[n][];
            for (int i=0;i<n;i++){
                X[i] = cplex.numVarArray(net.getOutEdges(i).size(),0.0,Integer.MAX_VALUE);
                for (int j=0;j<net.getOutEdges(i).size();j++){
                    X[i][j].setName("X["+i+"]["+j+"]");
                }
            }

            double[] prod = new double[n];
            for (int i=0;i<n;i++){
                prod[i] = net.getVertexDemand(i);
            }


            //Objective
            IloLinearNumExpr obj = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
                for (int j = 0;j < net.getOutEdges(i).size(); j++){
                    //System.out.println(i + " and " + j +" on " + n + "\n");
                    obj.addTerm(net.getEdge(i, net.getOutEdges(i).get(j).getDestination()).getCost(), X[i][j]);
                }
            }
            cplex.addMinimize(obj);



            //Capacity Constraint
            for (int i=0; i<n; i++) {
				for (int j=0; j<net.getOutEdges(i).size(); j++) {
                    IloLinearNumExpr ct = cplex.linearNumExpr();
                    ct.addTerm(1.0, X[i][j]);
                    cplex.addLe(ct, net.getEdge(i, net.getOutEdges(i).get(j).getDestination() ).getCapacity());
                }
            }
            

            //Demand constraint
            for (int i=0; i<n; i++){
                IloLinearNumExpr ct = cplex.linearNumExpr();
                for (int j=0;j<net.getOutEdges(i).size();j++){
                    if (i!=net.getOutEdges(i).get(j).getDestination()){
                        System.out.println(" + " + net.getEdge(i, net.getOutEdges(i).get(j).getDestination()).getCapacity());
                        ct.addTerm(1.0,X[i][j]);
                    }
                }
                for(int j=0;j<n;j++){
                    if (net.hasEdgeBetween(j,i) && i!=j){
                        for (int k=0;k<net.getOutEdges(j).size();k++){
                            if (net.getOutEdges(j).get(k).getDestination()==i){
                                System.out.println(" - " + net.getEdge(j, i).getCapacity());
                                ct.addTerm(-1.0,X[j][k]);
                            }
                        }
                    }
                }
                System.out.println(" = " + (-net.getVertexDemand(i)) + "\n");
                cplex.addEq(ct, - net.getVertexDemand(i));
            }
            // ps : oui c'est dégeulasse désolé
            

            cplex.setParam(IloCplex.IntParam.TimeLimit, 600);

            //solve and create a Solution
            if (cplex.solve()) {
                
                for (int i=0;i<n;i++){
                    System.out.println("i = "+ i);
                    flowMatrix[i] = new double [n];
                    for (int j=0;j<n;j++){
                        flowMatrix[i][j]=0;
                    }
                    for (int j=0;j<net.getOutEdges(i).size();j++){
                        flowMatrix[i][net.getOutEdges(i).get(j).getDestination()]=cplex.getValue(X[i][j]);
                    }
                }
                
			}else {
				System.out.println("error, cannot find solution\n");
            }

            //print the values of variables of Cplex, if needed ask me to update (still in matricial form)
            // if (debug == 1 ){
            //     for (int i=0; i<n; i++){
            //         for (int j=0;j<n;j++){
            //             if (net.hasEdgeBetween(i,j) && i!=j){
            //                 System.out.println(" + " + cplex.getValue(X[i][j]));
            //             }else{
            //                 if (net.hasEdgeBetween(j,i) && i!=j){
            //                     System.out.println(" - " + cplex.getValue(X[j][i]));
            //                 }
            //             }
            //         }
            //         System.out.println(" = " + (-net.getVertexDemand(i)) + "\n");
            //     }
            // }


            Solution sol = new Solution("CplexSolution",flowMatrix);

			cplex.end();

            return sol;
        }
        catch (IloException e){
            System.out.println("something gone wrong with Cplex \n");
            throw (new RuntimeException());
            //return new Solution(12);
        }
    }
}
