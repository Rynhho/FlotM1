// TODO: Remove this file after CPlex solver is working

package optim.flow.ui;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Test {
    public static void main(String[] args) {
        try {
            // create cplex environment
            IloCplex cplex = new IloCplex();

            // create decision variables
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            // create constraint
            IloLinearNumExpr expr = cplex.linearNumExpr();
            expr.addTerm(1, x);
            expr.addTerm(1, y);
            cplex.addLe(expr, 1);

            // add objective
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            objExpr.addTerm(2, x);
            objExpr.addTerm(3, y);
            cplex.addMaximize(objExpr);

            // solve and retrieve optimal solution
            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));
            }
        } catch (IloException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
