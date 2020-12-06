package optim.flow.infra;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.Repository;

public class NetworkFileRepository implements Repository<Network> {
    private final String directory = "data/";
    private final String extension = ".txt";

    @Override
    public boolean exists(String ID) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void save(String ID, Network network) {
        String str = new String();
        str += network.getNbVertices() + " " + network.getNbEdges() + " " + network.getMaxCapacity() + " "
                + network.getMaxCost() + " " + network.getMaxDemand() + "\n";

        for (int vertex = 0; vertex < network.getNbVertices(); ++vertex) {
            str += network.getVertexDemand(vertex) + "\n";
        }

        for (int source = 0; source < network.getNbVertices(); ++source) {
            for (Edge edge : network.getOutEdges(source)) {
                str += source + " " + edge.getDestination() + " " + edge.getCapacity() + " " + edge.getCost() + "\n";
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

            String line = reader.readLine();

            List<String> keyValues = new ArrayList<>(extractWords(line));
            final int nbVertices = Integer.parseInt(keyValues.get(0));
            final int nbEdges = Integer.parseInt(keyValues.get(1));

            List<List<Edge>> adjacencyList = new ArrayList<List<Edge>>();

            final double[] verticesDemand = new double[nbVertices];
            for (int vertex = 0; vertex < nbVertices; ++vertex) {
                line = reader.readLine();
                verticesDemand[vertex] = Double.parseDouble(line);

                adjacencyList.add(new ArrayList<Edge>());
            }

            for (int i = 0; i < nbEdges; ++i) {
                line = reader.readLine();
                List<String> words = new ArrayList<>(extractWords(line));

                int from = Integer.parseInt(words.get(0));
                int to = Integer.parseInt(words.get(1));

                double capacity = Double.parseDouble(words.get(2));
                double cost = Double.parseDouble(words.get(3));

                Edge edge = new Edge(from, to, capacity, cost);
                adjacencyList.get(from).add(edge);
            }

            network = new Network(adjacencyList, verticesDemand);

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return network;
    }

    /**
     * Function taken from previous project.
     * 
     * Separates a line into a list of words.
     * 
     * @param s String to divide into words.
     * @return List containing s's words.
     */
    private List<String> extractWords(String s) {
        List<String> words = new ArrayList<>();

        BreakIterator it = BreakIterator.getWordInstance();
        it.setText(s);

        int start = it.first();
        int end = it.next();
        while (end != BreakIterator.DONE) {
            String word = s.substring(start, end);
            if (Character.isLetterOrDigit(word.charAt(0))) {
                words.add(word);
            }
            start = end;
            end = it.next();
        }

        return words;
    }

    private String constructFilename(String ID) {
        return this.directory + ID + this.extension;
    }
}
