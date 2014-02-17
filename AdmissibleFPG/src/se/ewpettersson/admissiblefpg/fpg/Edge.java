/*
 *This file is part of AdmissibleFPG.
 *
 * Copyright (C) 2014 William Pettersson <william@ewpettersson.se>
 *
 *    AdmissibleFPG is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    AdmissibleFPG is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with AdmissibleFPG.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	public String toString() {
		return "{"+endPoints.get(0).id+" "+endPoints.get(1).id+"}";
	}
}
