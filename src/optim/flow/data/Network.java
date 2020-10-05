package data;

import java.util.ArrayList;
import java.util.List;
public class Network {
	
	private double [][] capacityMatrix;
	private double [][] costMatrix;
	private double[] consumptionVertex;
	private int nbVertex, nbEdges;
	private double capacityMax, costMax, consumptionMax;
	private List<EdgeIndex> arcIndexes;
	
	public double[][] getCapacityMatrix() {
		return capacityMatrix;
	}
	public double[][] getCostMatrix() {
		return costMatrix;
	}
	public double[] getConsumptionVertex() {
		return consumptionVertex;
	}
	public int getNbVertex() {
		return nbVertex;
	}
	public int getNbEdges() {
		return nbEdges;
	}
	public double getCapacityMax() {
		return capacityMax;
	}
	public double getCostMax() {
		return costMax;
	}
	public double getConsumptionMax() {
		return consumptionMax;
	}
	public List<EdgeIndex> getArcIndexes() {
		return arcIndexes;
	}
	public Network(double [][] capacityMatrix, double [][] costMatrix, double [] consumptionVertex) {
		this.capacityMatrix = capacityMatrix;
		this.costMatrix = costMatrix;
		this.consumptionVertex = consumptionVertex;
		VerifyIntegrity();
		
		this.nbVertex = capacityMatrix.length;
		this.nbEdges = getEdgesFromCapacity();
		getArcIndexes();
		AutoSetMaxValues();
		
	}
	private List<EdgeIndex> setArcIndexes(){
		List<EdgeIndex> arcIndexes = new ArrayList<EdgeIndex>();
		for (int i = 0; i < this.capacityMatrix.length; i++) {
			for (int j = 0; j < this.capacityMatrix[i].length; j++) {
				if(this.capacityMatrix[i][j] != 0) {
					arcIndexes.add(new EdgeIndex(i, j));
				}
			}
		}
		
	}
	private void AutoSetMaxValues() {
		this.capacityMax = this.capacityMatrix[0][0];
		this.costMax = this.costMatrix[0][0];
		this.consumptionMax = this.consumptionVertex[0];
		for (int i = 0; i < this.nbVertex; i++) {
			
			if(this.consumptionVertex[i] > this.consumptionMax)
				this.consumptionMax = this.consumptionVertex[i];
			
			for (int j = 0; j < this.nbVertex; j++) {
				
				if(this.capacityMatrix[i][j] > this.capacityMax)
					this.capacityMax = this.capacityMatrix[i][j];
				
				if(this.costMatrix[i][j] > this.costMax)
					this.costMax = this.costMatrix[i][j];
			}
		}
	}

	public Network(int nbVertex, int nbEdges, double capacityMax, double costMax, double consumptionMax) {
		this.nbVertex = nbVertex;
		this.nbEdges = nbEdges;
		this.capacityMax = capacityMax;
		this.costMax = costMax;
		this.consumptionMax = consumptionMax;
		
		setRandomConsumptionVertex();
		setRandomCapacityAndCostMatrix();
		setArcIndexes();
	}
	
	private double[] setRandomConsumptionVertex() {
		this.consumptionVertex = new double[nbVertex];
		for (int i = 0; i < this.nbVertex; i++) {
			this.consumptionVertex[i] = Math.random()*2*consumptionMax - consumptionMax;
		}
	}
	
	private double[] setRandomCapacityAndCostMatrix() {
		this.capacityMatrix = new double[nbVertex][nbVertex];
		this.costMatrix = new double[nbVertex][nbVertex];
		int x,y;
		
		for (int i = 0; i < nbEdges; i++) {
			do{
				x = (int)Math.round(Math.random()*(nbVertex-1));
				y = (int)Math.round(Math.random()*(nbVertex-1));	
			}while(capacityMatrix[x][y] != 0.0 && noLoopArc(x,y));
			
			this.capacityMatrix[x][y] = Math.random()*capacityMax;
			this.costMatrix[x][y] = Math.random()*2*costMax - costMax;
			
		}
	}
	
	private boolean noLoopArc(int x,int y) {
			return x!=y;
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
		return VerifySize() && VerifyCost();		
	}
	private boolean VerifyCost() {
		// Edge with no capacity can't have a cost. 
		for (int i = 0; i < this.costMatrix.length; i++) {
			for (int j = 0; j < this.costMatrix[i].length; j++) {
				if( this.capacityMatrix[i][j] == 0 &&  this.costMatrix[i][j] != 0) {
					return false;					
				}
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
	
	
}

