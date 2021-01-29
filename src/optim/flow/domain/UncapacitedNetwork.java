package optim.flow.domain;

import java.util.List;
import java.util.ArrayList;

public class UncapacitedNetwork extends Network {

    //le compilateur a trop pris de drogue donc il trouve pas les 3 listes qui sont dans Network...


    public UncapacitedNetwork(Network net){
        super();
        int n=net.getNbVertices();
        int m=net.getNbEdges();
        this.nbEdges = m*2;
        this.nbVertices = n+m;
        

        this.verticesDemands = new double[n+m];

        for (int i=0;i<n+m;i++){
            if (i<n){
                this.verticesDemands[i]=net.getVertexDemand(i);
            }else{
                this.verticesDemands[i]=0;
            }
        }

        for (int i=0;i<n+m;i++){
            this.adjacencyList.add(new ArrayList<Edge>());
            this.reverseAdjacencyList.add(new ArrayList<Edge>());
            this.fromToList.add(new ArrayList<List<Edge>>());
            for (int j=0;j<n+m;j++){
                this.fromToList.get(i).add(new ArrayList<Edge>());
            }
        }


        int edgecounter = 0;
        for (int i =0; i<n;i++){
            for (int j=0;j<net.getAdjacencyList().get(i).size();j++){
                Edge edge = net.getAdjacencyList().get(i).get(j);

                this.adjacencyList.get(i).add(new Edge(i,n+edgecounter,Integer.MAX_VALUE,edge.getCost()));
                this.adjacencyList.get(edge.getDestination()).add(new Edge(edge.getDestination(),n+edgecounter,Integer.MAX_VALUE,0));

                this.reverseAdjacencyList.get(n+edgecounter).add(new Edge(i,n+edgecounter,Integer.MAX_VALUE,edge.getCost()));
                this.reverseAdjacencyList.get(n+edgecounter).add(new Edge(edge.getDestination(),n+edgecounter,Integer.MAX_VALUE,0));

                this.fromToList.get(i).get(n+edgecounter).add(new Edge(i,n+edgecounter,Integer.MAX_VALUE,edge.getCost()));
                this.fromToList.get(edge.getDestination()).get(n+edgecounter).add(new Edge(edge.getDestination(),n+edgecounter,Integer.MAX_VALUE,0));

                this.verticesDemands[n+edgecounter] = -edge.getCapacity();
                this.verticesDemands[edge.getDestination()] += -edge.getCapacity();
                
                edgecounter++;
            }
        }

        this.maxCapacity=Integer.MAX_VALUE;
        this.maxCost=net.getMaxCost();
        this.maxDemand=0;
        for (int i=0;i<n;i++){
            if (verticesDemands[i]>this.maxDemand){
                this.maxDemand=verticesDemands[i];
            }
        }
    }
}