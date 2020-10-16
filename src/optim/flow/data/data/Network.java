package data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * 1: camelCase for both variables and functions.
 * 2: List<edgesVerticesIDs>
 * 3: nb_variables instead of variable_nb. I find it clearer. same for max, min, etc
 * 4: Be careful of typos.
 * 5: ArrayList<> instead of []. It's safer and has more functionalities for the (almost) same speed.
 * 6. push après tout ça.
 */
public class Network {
	
	double [][] capacityMatrix;
	double [][] costMatrix;
	double[] consumptionVertex;
	int nbVertex, nbEdges;
	double maxCapacity, maxCost, maxConsumption;

	public Network(double [][] capacityMatrix, double [][] costMatrix, double [] consumptionVertex) {
		this.capacityMatrix = capacityMatrix;
		this.costMatrix = costMatrix;
		this.consumptionVertex = consumptionVertex;
		VerifyIntegrity();
		this.nbVertex = capacityMatrix.length;
		this.nbEdges = getEdgesFromCapacity();
		AutoSetMaxValuesFromMatrices();
		
	}
	
	private void AutoSetMaxValuesFromMatrices() {
		this.maxCapacity = this.capacityMatrix[0][0];
		this.maxCost = this.costMatrix[0][0];
		this.maxConsumption = this.consumptionVertex[0];
		
		for (int i = 0; i < this.nbVertex; i++) {
			this.maxConsumption = Math.max(this.maxConsumption, this.consumptionVertex[i]);
			
			for (int j = 0; j < this.nbVertex; j++) {
				this.maxCapacity = Math.max(this.maxCapacity, this.capacityMatrix[i][j]);
				this.maxCost = Math.max(this.maxCost, this.costMatrix[i][j]);
			}
		}
	}

	public Network(int nbVertex, int nbEdges, double maxCapacity, double maxCost, double maxConsumption) {
		this.nbVertex = nbVertex;
		this.nbEdges = nbEdges;
		this.maxCapacity = maxCapacity;
		this.maxCost = maxCost;
		this.maxConsumption = maxConsumption;
		VerifyBaseVariableIntegrity();
		
		setRandomConsumptionVertex();
		setRandomCapacityAndCostMatrix();		
	}
	
	private void VerifyBaseVariableIntegrity() {
		if(this.nbVertex <= 0)
			System.out.println("Error: Vertex Number <= 0");
		if(this.nbEdges <= 0)
			System.out.println("Error: Edges Number <= 0");
		if(this.maxCapacity <= 0)
			System.out.println("Error: Maximum Capacity <= 0");
		if(this.maxCost <= 0)
			System.out.println("Error: Maximum Cost <= 0");
		if(this.maxConsumption <= 0)
			System.out.println("Error: Maximum Consumption <= 0");
		if(this.nbEdges > this.nbVertex*this.nbVertex )
			System.out.println("Error: Edges Number > Vertex_Number^2");
		
	}

	private void setRandomConsumptionVertex(){
		this.consumptionVertex = new double[nbVertex];
		for (int i = 0; i < this.nbVertex; i++) {
			this.consumptionVertex[i] = Math.random()*2*this.maxConsumption - this.maxConsumption;
		}
	}
	
	private void setRandomCapacityAndCostMatrix(){
		this.capacityMatrix = new double[nbVertex][nbVertex];
		this.costMatrix = new double[nbVertex][nbVertex];
		for (int i = 0; i < nbEdges; i++) {
			int[] Coordinates = getEmptyCapacityMatrixCell();
			
			this.capacityMatrix[Coordinates[0]][Coordinates[1]] = Math.random()*maxCapacity;
			this.costMatrix[Coordinates[0]][Coordinates[1]] = Math.random()*2*maxCost - maxCost;
		}
	}
	
	private int[] getEmptyCapacityMatrixCell() {
		int x,y;
		do {
			x = (int)Math.round(Math.random()*(nbVertex-1));
			y = (int)Math.round(Math.random()*(nbVertex-1));	
		}while(this.capacityMatrix[x][y] != 0.0);
		return new int[]{x,y};
	}
	
	
	public String toString() {
		String network = "capacityMatrix | costMatrix\n";
		for (int i = 0; i < this.nbVertex; i++) {
			network += "[";
			for (int j = 0; j < this.nbVertex; j++) {
				network += String.format("%.2f",this.capacityMatrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]   [";
			for (int j = 0; j < this.nbVertex; j++) {
				network += String.format("%.2f",this.costMatrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]\n";
		}
		network += "\nconsumption vector\n[";
		for (int i = 0; i < this.nbVertex; i++) {
			network += String.format("%.2f",this.consumptionVertex[i]) + ", "; 
		}
		return network.substring(0, network.length()-2) + "]\n";
	}
	
	// Todo: = networkEdges.length
	private int getEdgesFromCapacity() {
		int nbEdges = 0;
		for (int i = 0; i < this.nbVertex; i++) {
			for (int j = 0; j < this.nbVertex; j++) {
				if(capacityMatrix[i][j] != 0)
					nbEdges+=1;
			}
		}
		return nbEdges;
	}
	
	public boolean VerifyIntegrity() {
		// turn it into throw error but idk how to do it
		// Todo: Non lol rpz le cours d'ao. Il vaut mieux vérifier que quelque chose est faisable avec des if que throw des erreurs. Après on se retrouve avec des try catch de partout.
		return VerifySize() && VerifyCost();		
	}
	
	private boolean VerifyCost() {
		for (int i = 0; i < this.costMatrix.length; i++) {
			for (int j = 0; j < this.costMatrix[i].length; j++) {
				if( this.capacityMatrix[i][j] == 0 &&  this.costMatrix[i][j] != 0)
					return false;					
			}
		}
		return true;
	}

	private boolean VerifySize() {
		int size = this.capacityMatrix.length;
		if(size != this.costMatrix.length ||
				size != this.consumptionVertex.length){
			return false;
		}
		for (int i = 0; i < this.capacityMatrix.length; i++) {
			if(size != this.capacityMatrix[i].length || 
					size != this.costMatrix[i].length){
				return false;
			}
		}
		return true;
	}
	
	public boolean Checker(double[][] solution) {
		// assume solution has no problem ( eg: its size )
		for (int i = 0; i < solution.length; i++) {
			for (int j = 0; j < solution.length; j++) {
				if(solution[i][j] > this.capacityMatrix[i][j])
					return false;
			}
		}
		for (int i = 0; i < solution.length; i++) {
			double in = 0;
			double out = 0;
			for (int j = 0; j < solution.length; j++) {
				in += solution[i][j];
				out += solution[j][i];
			}
			if(out>in + this.consumptionVertex[i])
				return false;
		}
		return true;
	}
	
	public double SolutionCost(double[][] solution) {
		double cost = 0;
		for (int i = 0; i < solution.length; i++) {
			for (int j = 0; j < solution.length; j++) {
				cost += solution[i][j]*this.costMatrix[i][j];
			}
		}
		return cost;
	}
	
	public void save(String filename) {
		
		try {
		File saveFile = new File(filename);
		FileWriter myWriter = new FileWriter(filename);
	    myWriter.write(this.saveString());
	    myWriter.close();
		} catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}
	
	public String saveString() {
		String network = "number of vertex: "+ Integer.toString(this.nbVertex)
		+"\nnumber of edges: " + Integer.toString(this.nbVertex) + "\ncapacityMatrix\n";
		for (int i = 0; i < this.nbVertex; i++) {
			network += "[";
			for (int j = 0; j < this.nbVertex; j++) {
				network += String.format("%.2f",this.capacityMatrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]\n";
		}
		network += "\ncostMatrix\n";
		for (int i = 0; i < this.nbVertex; i++) {
			network += "[";
			for (int j = 0; j < this.nbVertex; j++) {
				network += String.format("%.2f",this.costMatrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]\n";
		}
		network += "\nconsumption vector\n[";
		for (int i = 0; i < this.nbVertex; i++) {
			network += String.format("%.2f",this.consumptionVertex[i]) + ", "; 
		}
		return network.substring(0, network.length()-2) + "]\n";
		
	}

}
