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

public class TFE {
	public TFE(int tet, int face, int edge) {
		super();
		this.tet = tet;
		this.face = face;
		this.edge = edge;
	}



	public int tet;
	public int edge;
	public int face;
	
	/**
	 * 
	 * @param tfe
	 * @return 0 if the two pairs do not match up
	 *         1 if they match up, and orientations agree
	 *         -1 if they match up but orientations disagree
	 */
	public int orientTo(TFE tfe) {
		if (tet == tfe.tet && face == tfe.face) {
			if (edge == tfe.edge) {
				return 1;
			}
			if (edge == -tfe.edge) {
				return -1;
			}
		}
		return 0;
	}
	
	public String toString() {
		String s="[" + tet + ", "+face+", "+edge+"]";
		return s;
	}
	
	public boolean equals(TFE other) {
		return (tet == other.tet) && (face == other.face) && (edge == other.edge);  
	}
	
}
