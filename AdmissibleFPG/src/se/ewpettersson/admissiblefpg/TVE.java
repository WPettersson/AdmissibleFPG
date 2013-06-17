package se.ewpettersson.admissiblefpg;

public class TVE {
	public TVE(int tet, int vertex, int edge) {
		super();
		this.tet = tet;
		this.vertex = vertex;
		this.edge = edge;
	}



	public int tet;
	public int vertex;
	public int edge;
	
	
	public String toString() {
		String s="[" + tet + ", "+vertex+", "+edge+"]";
		return s;
	}

	public boolean equals(TVE other) {
		return (tet == other.tet) && (vertex == other.vertex) && (edge == other.edge);  
	}
}
