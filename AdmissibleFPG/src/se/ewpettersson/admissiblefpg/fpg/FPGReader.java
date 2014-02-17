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

import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.input.InputException;
import nl.uu.cs.treewidth.ngraph.ListGraph;
import nl.uu.cs.treewidth.ngraph.ListVertex;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.ngraph.NVertex;

public class FPGReader implements GraphInput {

	
	FacePairingGraph f;
	
	public FPGReader(FacePairingGraph f){ 
		this.f=f;
	}
	@Override
	public NGraph<InputData> get() throws InputException {
		NGraph<InputData> g = new ListGraph<InputData>();
		
		List<NVertex<InputData>> vertices = new ArrayList<NVertex<InputData>>();
		for(int i=0;i<f.numVerts();i++) {
			NVertex<InputData> v = new ListVertex<InputData>();
			v.data = new InputData();
			v.data.id = i;
			v.data.name = String.valueOf(i);
			vertices.add(v);
			g.addVertex(v);			
		}
		List<Arc> done = new ArrayList<Arc>();
		for(Arc a: f.getArcs()) {
			boolean parallel = false;
			for(Arc d: done) {
				if (d.parallel(a)) {
					parallel = true;
				}
			}
			if (!parallel && !a.isLoop()) {
				NVertex<InputData> v1 = vertices.get(a.t1);
				NVertex<InputData> v2 = vertices.get(a.t2);
				g.addEdge(v1,v2);
				done.add(a);
			}
		}		
		return g;
	}

}
