public class Generator {
    /**
     * Generates a network flow instance.
     * 
     * @param nbVertices  The desired number of vertices in the network flow
     *                    problem.
     * @param nbEdgesMin  The minimum number of edges.
     * @param nbEdgesMax  The maximum number of edges.
     * @param maxCapacity The maximum capacity of each edge.
     * @param maxCost     The maximum cost of each edge.
     * @param maxDemand   The maximum demand of each vertex.
     * 
     * @return The generated instance.
     */
    public Instance generate(int nbVertices, int nbEdgesMin, int nbEdgesMax, int maxCapacity, int maxCost,
            int maxDemand) {
        Instance instance = new Instance();

        instance.setNbVertices(nbVertices);
        instance.setNbEdgesMin(nbEdgesMin);
        instance.setNbEdgesMax(nbEdgesMax);

        instance.setMaxCapacity(maxCapacity);
        instance.setMaxCost(maxCost);
        instance.setMaxDemand(maxDemand);

        // Todo: Complete

        return instance;
    }
}
