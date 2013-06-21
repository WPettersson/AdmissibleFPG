package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.List;

public class Edge {
	List<Vertex> endPoints;
	boolean used;

	public Edge(Vertex v, Vertex v2) {
		endPoints = new ArrayList<Vertex>();
		endPoints.add(v);
		endPoints.add(v2);
	}

	public List<Vertex> getEndPoints() {
		return endPoints;
	}

	public Vertex get(int i) {
		return endPoints.get(i);
	}
	
	public boolean touches(Vertex v) {
		return endPoints.contains(v);
	}

	public Vertex getOther(Vertex v) {
		if (endPoints.get(0) == v) {
			return endPoints.get(1);
		}
		return endPoints.get(0);
	}

	public boolean isUsed() {
		return used;
	}
	
	public void use() {
		used = true;
	}
}
