package optim.flow.infra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.Repository;

public class NetworkFileRepository implements Repository<Network> {
    private final String directory = "data/";
    private final String extension = ".txt";

    @Override
    public boolean exists(String ID) {
        File file = new File(constructFilename(ID));
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    @Override
    public void save(String ID, Network network) {
        String str = new String();

        str += "p min " + network.getNbVertices() + " " + network.getNbEdges() + "\n";

        for (int vertex = 0; vertex < network.getNbVertices(); ++vertex) {
            if (network.getVertexDemand(vertex)!=0){
                str += "n " + (vertex) + " " + network.getVertexDemand(vertex) + "\n";
            }
        }

        for (int source = 0; source < network.getNbVertices(); ++source) {
            for (Edge edge : network.getOutEdges(source)) {
                str += "a " + (source) + " " + (edge.getDestination()) + " 0 " + edge.getCapacity() + " " + edge.getCost()
                        + "\n";
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(constructFilename(ID));
            fileWriter.write(str);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Network load(String ID) {
        Network network = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/" + ID + ".txt"));

            int nbVertices;
            int nbEdges;
            boolean problemDefined = false;

            List<List<Edge>> adjacencyList = null;
            double[] verticesDemand = null;

            String line = reader.readLine();
            while (line != null) {
                List<String> words = new ArrayList<>(extractWords(line));

                String objCategory = words.get(0);
                if (objCategory.equals("p")) {
                    if (problemDefined) {
                        throw new RuntimeException("Error: Two problems in the same file in database.\n");
                    }

                    nbVertices = Integer.parseInt(words.get(2));
                    nbEdges = Integer.parseInt(words.get(3));

                    adjacencyList = new ArrayList<List<Edge>>();
                    for (int vertex = 0; vertex < nbVertices; ++vertex) {
                        adjacencyList.add(new ArrayList<Edge>());
                    }
                    verticesDemand = new double[nbVertices];

                    problemDefined = true;
                } else if (objCategory.equals("n")) {
                    if (!problemDefined) {
                        throw new RuntimeException("Error: Adding nodes to undefined problem.\n");
                    }

                    int vertex = Integer.parseInt(words.get(1));
                    verticesDemand[vertex] = Double.parseDouble(words.get(2));
                } else if (objCategory.equals("a")) {
                    if (!problemDefined) {
                        throw new RuntimeException("Error: Adding nodes to undefined problem.\n");
                    }

                    int from = Integer.parseInt(words.get(1));
                    int to = Integer.parseInt(words.get(2));

                    double capacity = Double.parseDouble(words.get(4));
                    double cost = Double.parseDouble(words.get(5));

                    Edge edge = new Edge(from, to, capacity, cost);
                    adjacencyList.get(from).add(edge);
                }

                line = reader.readLine();
            }

            reader.close();

            network = new Network(adjacencyList, verticesDemand);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return network;
    }

    private List<String> extractWords(String s) {
        String[] words = s.split(" ");
        return Arrays.asList(words);
    }

    private String constructFilename(String ID) {
        return this.directory + ID + this.extension;
    }
}
