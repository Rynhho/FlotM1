package optim

public class Network {
	
	double [][] capacity_matrix;
	double [][] cost_matrix;
	double[] consumption_vertex;
	int vertex_nb, edges_nb;
	double capacity_max, cost_max, consumption_max;

	public Network(double [][] capacity_matrix, double [][] cost_matrix, double [] consumption_vertex) {
		this.capacity_matrix = capacity_matrix;
		this.cost_matrix = cost_matrix;
		this.consumption_vertex = consumption_vertex;
		VerifyIntegrity();
		this.vertex_nb = capacity_matrix.length;
		this.edges_nb = getEdgesFromCapacity();
		AutoSetMaxValues();
		
	}
	
	private void AutoSetMaxValues() {
		this.capacity_max = this.capacity_matrix[0][0];
		this.cost_max = this.cost_matrix[0][0];
		this.consumption_max = this.consumption_vertex[0];
		for (int i = 0; i < this.vertex_nb; i++) {
			
			if(this.consumption_vertex[i] > this.consumption_max)
				this.consumption_max = this.consumption_vertex[i];
			
			for (int j = 0; j < this.vertex_nb; j++) {
				
				if(this.capacity_matrix[i][j] > this.capacity_max)
					this.capacity_max = this.capacity_matrix[i][j];
				
				if(this.cost_matrix[i][j] > this.cost_max)
					this.cost_max = this.cost_matrix[i][j];
			}
		}
	}

	public Network(int vertex_nb, int edges_nb, double capacity_max, double cost_max, double consumption_max) {
		this.vertex_nb = vertex_nb;
		this.edges_nb = edges_nb;
		this.capacity_max = capacity_max;
		this.cost_max = cost_max;
		this.consumption_max = consumption_max;
		
		this.consumption_vertex = new double[vertex_nb];
		for (int i = 0; i < this.vertex_nb; i++) {
			this.consumption_vertex[i] = Math.random()*2*consumption_max - consumption_max;
		}

		this.capacity_matrix = new double[vertex_nb][vertex_nb];
		this.cost_matrix = new double[vertex_nb][vertex_nb];
		int x,y;
		for (int i = 0; i < edges_nb; i++) {
			x = (int)Math.round(Math.random()*(vertex_nb-1));
			y = (int)Math.round(Math.random()*(vertex_nb-1));
			while(capacity_matrix[x][y] != 0.0) {
				x = (int)Math.round(Math.random()*(vertex_nb-1));
				y = (int)Math.round(Math.random()*(vertex_nb-1));	
			}
			this.capacity_matrix[x][y] = Math.random()*capacity_max;
			this.cost_matrix[x][y] = Math.random()*2*cost_max - cost_max;
			
		}
		
	}
	
	public String toString() {
		String network = "capacity_matrix | cost_matrix\n";
		for (int i = 0; i < this.vertex_nb; i++) {
			network += "[";
			for (int j = 0; j < this.vertex_nb; j++) {
				network += String.format("%.2f",this.capacity_matrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]   [";
			for (int j = 0; j < this.vertex_nb; j++) {
				network += String.format("%.2f",this.cost_matrix[i][j]) + ", ";
			}
			network = network.substring(0, network.length()-2) + "]\n";
		}
		network += "\nconsumption vector\n[";
		for (int i = 0; i < this.vertex_nb; i++) {
			network += String.format("%.2f",this.consumption_vertex[i]) + ", "; 
		}
		return network.substring(0, network.length()-2) + "]\n";
	}
	
	private int getEdgesFromCapacity() {
		int edges_nb = 0;
		for (int i = 0; i < this.vertex_nb; i++) {
			for (int j = 0; j < this.vertex_nb; j++) {
				if(capacity_matrix[i][j] != 0)
					edges_nb+=1;
			}
		}
		return edges_nb;
	}
	
	public boolean VerifyIntegrity() {
		// turn it into throw error but idk how to do it
		return VerifySize() && VerifyCost();		
	}
	private boolean VerifyCost() {
		// Edge with no capacity can't have a cost. 
		for (int i = 0; i < this.cost_matrix.length; i++) {
			for (int j = 0; j < this.cost_matrix[i].length; j++) {
				if( this.capacity_matrix[i][j] == 0 &&  this.cost_matrix[i][j] != 0) {
					return false;					
				}
			}
		}
		return true;
	}

	private boolean VerifySize() {
		int size = this.capacity_matrix.length;
		if(size != this.cost_matrix.length ||
				size != this.consumption_vertex.length){
			return false;
		}
		for (int i = 0; i < this.capacity_matrix.length; i++) {
			if(size != this.capacity_matrix[i].length || 
					size != this.cost_matrix[i].length){
				return false;
			}
		}
		return true;
	}
	
	
}

