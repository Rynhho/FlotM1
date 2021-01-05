package optim.flow.infra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import optim.flow.domain.Edge;
import optim.flow.domain.Repository;
import optim.flow.domain.ResidualNetwork;

public class SolutionFileRepository implements Repository<ResidualNetwork> {
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
    public void save(String ID, ResidualNetwork solution) {
        final int nbVertices = solution.getNbVertices();

        String str = new String();
         str += ID + " " + (nbVertices-2) + "\n";

         for (int i = 2; i < nbVertices; ++i) {
        	 List<Edge> edges = solution.getOutEdges(i);
        	 Collections.reverse(edges);
        	 for (Edge edge:edges) {
//        		 if(solution.getFlow(edge) != 0) {
        			 if(!edge.isResidual() && edge.getDestination()!=1)
        				 str += i-2 + " " + (edge.getDestination()-2) + " " + solution.getFlow(edge) + "\n";
        			 
//        		 }
        	 }
         }
//         for (int i = 0; i < nbVertices; ++i) {
//        	 for (Edge edge:solution.getOutEdges(i)) {
//        		 if(solution.getFlow(edge) != 0) {
//			         str += i + " " + edge.getDestination() + " " + solution.getFlow(edge) + "\n";
//        			 
//        		 }
//        	 }
//         }
//         for (int j = 0; j < nbVertices; ++j) {
         // Todo: We don't verify if i and j is a valid edge in the network
         // Todo: zeros are getting stored and complexity is still n^2
//         double flow = solution.getEdge(i, j).getFlow();
//         }
//         }

        try {
            FileWriter fileWriter = new FileWriter(constructFilename(ID));
            fileWriter.write(str);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResidualNetwork load(String ID) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("data/" + ID + ".sol"));

            /* Number of vertices */
            String line = bufferedReader.readLine();
            List<String> keyValues = new ArrayList<>(extractWords(line));
            // final String ID = keyValues.get(0);
            final int nbVertices = Integer.parseInt(keyValues.get(1));

            double[][] flowMatrix = new double[nbVertices][nbVertices];

            while ((line = bufferedReader.readLine()) != null) {
                List<String> words = new ArrayList<>(extractWords(line));

                int from = Integer.parseInt(words.get(0));
                int to = Integer.parseInt(words.get(1));
                flowMatrix[from][to] += Integer.parseInt(words.get(2));
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private String constructFilename(String ID) {
        return this.directory + ID + this.extension;
    }
}
