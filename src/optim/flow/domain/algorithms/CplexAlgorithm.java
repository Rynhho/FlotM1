package optim.flow.domain.algorithms;

import optim.flow.domain.Network;
import optim.flow.domain.ResidualNetwork;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CplexAlgorithm implements Algorithm{


    public ResidualNetwork solve(Network net){
        try{
            

            IloCplex cplex = new IloCplex();
            int n = net.getNbVertices();
            //double[][] flowMatrix = new double[n][];
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
                    obj.addTerm(net.getOutEdges(i).get(j).getCost(), X[i][j]);
                }
            }
            cplex.addMinimize(obj);



            //Capacity Constraint
            for (int i=0; i<n; i++) {
				for (int j=0; j<net.getOutEdges(i).size(); j++) {
                    IloLinearNumExpr ct = cplex.linearNumExpr();
                    ct.addTerm(1.0, X[i][j]);
                    cplex.addLe(ct, net.getOutEdges(i).get(j).getCapacity());
                }
            }
            

            //Demand constraint
            for (int i=0; i<n; i++){
                IloLinearNumExpr ct = cplex.linearNumExpr();
                for (int j=0;j<net.getOutEdges(i).size();j++){
                    if (i!=net.getOutEdges(i).get(j).getDestination()){
                        //System.out.println(" + " + net.getEdges(i, net.getOutEdges(i).get(j).getDestination()).get(0).getCapacity());
                        ct.addTerm(1.0,X[i][j]);
                    }
                }
                for(int j=0;j<n;j++){
                    if (net.hasEdgeBetween(j,i) && i!=j){
                        for (int k=0;k<net.getOutEdges(j).size();k++){
                            if (net.getOutEdges(j).get(k).getDestination()==i){
                                //System.out.println(" - " + net.getEdges(j, i).get(0).getCapacity());
                                ct.addTerm(-1.0,X[j][k]);
                            }
                        }
                    }
                }
                //System.out.println(" = " + (-net.getVertexDemand(i)) + "\n");
                cplex.addEq(ct, - net.getVertexDemand(i));
            }
            // ps : oui c'est dégeulasse désolé
            

            cplex.setParam(IloCplex.IntParam.TimeLimit, 600);

            ResidualNetwork sol = new ResidualNetwork(net);



            //solve and create a Solution
            if (cplex.solve()) {
                
                
            

            for (int i=0;i<n;i++){
                //System.out.println("i = "+ i);
                for (int j=0; j<net.getOutEdges(i).size();j++){
                    sol.addFlow(net.getOutEdges(i).get(j), cplex.getValue(X[i][j]));
                }
            }
                
			}else {
				System.out.println("error, cannot find solution\n");
            }
			cplex.end();
			cplex.close();

            return sol;
        }
        catch (IloException e){
            System.out.println("something gone wrong with Cplex \n");
            throw (new RuntimeException());
            //return new Solution(12);
        }
    }
}
