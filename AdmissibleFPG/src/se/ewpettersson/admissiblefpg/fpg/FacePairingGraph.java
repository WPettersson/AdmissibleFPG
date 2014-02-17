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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class FacePairingGraph {
	List<Arc> arcs;

	public List<Arc> getArcs() {
		return arcs;
	}
	
	boolean isBad;
	boolean checkedBad;
	public boolean badGraph() {
		if (checkedBad) {
			return isBad;
		}
//		int[] chainEnds = new int[tetrahedra.size()];
		for (Arc a: arcs) {
			int p = 0;
			for (Arc b: arcs) {
				if (a==b) {
					continue;
				}
				if (a.parallel(b)) {
					p+=1;
				}
			}
			if (p >= 3) {
				isBad = true;
				checkedBad = true;
				return true;
			}
		}
		isBad = false;
		checkedBad = true;
		return false;
	}

	List<Integer> tetrahedra;
	
	public FacePairingGraph(String s) {
		arcs = new LinkedList<Arc>();
		tetrahedra = new ArrayList<Integer>();
		
		List<String> spl = new LinkedList<String>();
		for(String n: s.split(" ")) {
			spl.add(n);
		}
		int tet=0;
		int face=0;
		checkedBad = false;
		while(!spl.isEmpty()) {
			if(face == 0) {
				tetrahedra.add(tet);
			}
			int adjTet = Integer.parseInt(spl.remove(0));
			int adjFace = Integer.parseInt(spl.remove(0));
			
			// Edges show up twice in the string representation.
			// Only add them once, when we are at the second 
			// such showing.  That means either we are at a "higher"
			// tet, or same tet but "higher" face.
			if (tet > adjTet) {
				Arc a = new Arc(adjTet,tet);
				arcs.add(a);
			} else if ( tet == adjTet ) {
				if (face > adjFace) {
					Arc a = new Arc(adjTet,tet);
					arcs.add(a);
				}
			}
			face+=1;
			if (face == 4) {
				tet+=1;
				face=0;
			}
			
		}
		
		
	}

	public List<Arc> getArcsBetween(Set<Integer> below,
			List<Integer> toAdd) {
		List<Arc> e = new LinkedList<Arc>();
		Set<Integer> both = new HashSet<Integer>(below);
		both.addAll(toAdd);
		for (Arc a: arcs) {
			if ((toAdd.contains(a.t1) && both.contains(a.t2))||
				 both.contains(a.t1) && (toAdd.contains(a.t2)) ) {
					e.add(a);
			}
		}
		return e;
	}

	public int numVerts() {
		return tetrahedra.size();
	}
	
	
}
