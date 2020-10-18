package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parser {
	public Network loadFromFile(String filename) {
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      int nbVertex = parseVertexNb(myReader);
		      int nbEdges = parseEdgesNb(myReader);
		      double[][] capacityMatrix = parseCapacityMatrix(myReader, nbVertex);
		      double[][] costMatrix = parseCostMatrix(myReader, nbVertex);
		      double[] consumptionVertex = parseConsumptionVertex(myReader, nbVertex);
		      myReader.close();
		      return new Network(capacityMatrix, costMatrix, consumptionVertex);
		    }	catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		return null;
	}

	private int parseVertexNb(Scanner myReader) {
		if(myReader.hasNextLine()) {
			Pattern r = Pattern.compile("number of vertex: (\\d*)");
			String data = myReader.nextLine();
			Matcher m = r.matcher(data);
			if (m.find()) {
		         return Integer.parseInt(m.group(1));
		      } else {
		         System.out.println("NO MATCH vertex number");
		         return 0;
		      }
		}
		else
			return 0;
	}

	private int parseEdgesNb(Scanner myReader) {
		if(myReader.hasNextLine()) {
			Pattern r = Pattern.compile("number of edges: (\\d*)");
			String data = myReader.nextLine();
			Matcher m = r.matcher(data);
			if (m.find()) {
		         return Integer.parseInt(m.group(1));
		      } else {
		         System.out.println("NO MATCH edges number");
		         return 0;
		      }
		}
		return 0;
	}

	private double[][] parseCapacityMatrix(Scanner myReader, int size) {
		double [][] CapacityMatrix = new double[size][size];
		if (myReader.hasNextLine())
			myReader.nextLine(); // Should be "capacity matrix"
		for (int i = 0; i < size; i++) {
			CapacityMatrix[i] = parseVector(myReader, size);
		}
		return CapacityMatrix;
	}

	private double[][] parseCostMatrix(Scanner myReader, int size) {
		double [][] CostMatrix = new double[size][size];
		if (myReader.hasNextLine())
			myReader.nextLine(); // Should be empty
		if (myReader.hasNextLine())
			myReader.nextLine(); // Should be "cost matrix"
		for (int i = 0; i < size; i++) {
			CostMatrix[i] = parseVector(myReader, size);
		}
		return CostMatrix;
	}

	private double[] parseConsumptionVertex(Scanner myReader, int size) {
		
		if (myReader.hasNextLine())
			myReader.nextLine(); // Should be empty
		if (myReader.hasNextLine())
			myReader.nextLine(); // Should be "Consumption Vertex"
		return parseVector(myReader, size);
	}
	
	private double[] parseVector(Scanner myReader, int size) {
		double [] Vector = new double[size];
		if(myReader.hasNextLine()) {
			Pattern r = Pattern.compile("(-?\\d*\\.?\\d+)");
			String data = myReader.nextLine();
			Matcher m = r.matcher(data);
				for (int j = 0; j < size; j++) {
					if (m.find()) {
						Vector[j] = Double.parseDouble(m.group(0));
					} else {
						System.out.println("NO MATCH parseConsumptionVertex" + data);
						return null;
					}
				}
		}
		return Vector;
	}
}
