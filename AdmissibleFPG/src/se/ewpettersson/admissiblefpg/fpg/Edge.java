package se.ewpettersson.admissiblefpg.fpg;

import java.util.List;

public class Edge {
	List<Vertex> endPoints;

	public List<Vertex> getEndPoints() {
		return endPoints;
	}

	public Vertex get(int i) {
		return endPoints.get(i);
	}

}
