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
package se.ewpettersson.admissiblefpg;

public class TVE {
	public TVE(int tet, int vertex, int edge) {
		super();
		this.tet = tet;
		this.vertex = vertex;
		this.edge = edge;
	}



	public TVE(TVE data) {
		this.tet = data.tet;
		this.vertex = data.vertex;
		this.edge = data.edge;
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
