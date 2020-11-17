package optim.flow.infra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import optim.flow.domain.Repository;
import optim.flow.domain.Solution;

public class SolutionFileRepository implements Repository<Solution> {
    @Override
    public void save(Solution solution) {
        final int nbVertices = solution.getNbVertices();

        String str = new String();
        str += solution.getNetworkID() + " " + nbVertices + "\n";

        for (int i = 0; i < nbVertices; ++i) {
            for (int j = 0; j < nbVertices; ++j) {
                // Todo: We don't verify if i and j is a valid edge in the network
                double flow = solution.getEdgeFlow(i, j);
                str += i + " " + j + " " + flow + "\n";
            }
        }

        try {
            FileWriter fileWriter = new FileWriter("data/" + solution.getID() + ".sol");
            fileWriter.write(str);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Solution load(String ID) {
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
}
