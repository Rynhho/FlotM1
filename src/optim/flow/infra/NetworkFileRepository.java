package optim.flow.infra;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Network;
import optim.flow.domain.Repository;

public class NetworkFileRepository implements Repository<Network> {
    @Override
    public String save(Network network) {
        final Date date = new Date();
        final String ID = Long.toString(date.getTime()) + ".txt";

        save(network, ID);

        return ID;
    }

    @Override
    public void save(Network network, String ID) {
        final int nbVertices = network.getNbVertices();

        String str = new String();
        str += nbVertices +" "+network.getNbEdges()+" "+ network.getMaxCapacity() +" "+network.getMaxCost()+" "+network.getMaxDemand() + "\n";
        for (int i = 0; i < nbVertices; ++i) {
            str += network.getVertexDemand(i) + "\n";
        }

        str += network.getNbEdges() + "\n";
        for (int i = 0; i < nbVertices; ++i) {
            for (int j = 0; j < nbVertices; ++j) {
                /* Don't save zero-capacity edges */
                if (network.getEdgeCapacity(i, j) != 0) {
                    str += i + " " + j + " " + network.getEdgeCapacity(i, j) + " " + network.getEdgeCost(i, j) + "\n";
                }
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(ID);
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
            BufferedReader reader = new BufferedReader(new FileReader(ID));

            String line = reader.readLine();
            List<String> keyValues = new ArrayList<>(extractWords(line));
            final int nbVertices = Integer.parseInt(keyValues.get(0));
            final int nbEdges= Integer.parseInt(keyValues.get(1));
            // final int maxCapacity = Integer.parseInt(keyValues.get(2));
            // final int maxCost = Integer.parseInt(keyValues.get(3));
            // final int maxDemand = Integer.parseInt(keyValues.get(4));
            
            List<ArrayList<Edge>> edges = new ArrayList<ArrayList<Edge>>();
            final double[] verticesDemand = new double[nbVertices];
            for (int vertexID = 0; vertexID < nbVertices; ++vertexID) {
                line = reader.readLine();
                verticesDemand[vertexID] = Integer.parseInt(line);
                edges.add(new ArrayList<Edge>());
            }
            for (int edgeID = 0; edgeID < nbEdges; ++edgeID) {
                line = reader.readLine();
                List<String> words = new ArrayList<>(extractWords(line));

                int from = Integer.parseInt(words.get(0));
                int to = Integer.parseInt(words.get(1));
                double capacity = Integer.parseInt(words.get(2));
                double cost = Integer.parseInt(words.get(2));
                Edge edge = new Edge(from, to, cost, capacity);
                edges.get(from).add(edge);
            }

            network = new Network(verticesDemand, edges);

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
    private static List<String> extractWords(String s) {
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
}
