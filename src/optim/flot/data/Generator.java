public class Generator {

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
