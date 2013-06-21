package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.input.InputException;
import nl.uu.cs.treewidth.ngraph.ListGraph;
import nl.uu.cs.treewidth.ngraph.ListVertex;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.ngraph.NVertex;

public class FPGReader implements GraphInput {

	
	String s;
	
	public FPGReader(String s){ 
		this.s=s;
	}
	@Override
	public NGraph<InputData> get() throws InputException {
		NGraph<InputData> g = new ListGraph<InputData>();
		
		List<NVertex<InputData>> vertices = new ArrayList<NVertex<InputData>>();
		List<String> spl = new LinkedList<String>();
		for(String n: s.split(" ")) {
			spl.add(n);
		}
		int tet=0;
		int face=0;
		
		while(!spl.isEmpty()) {
			if(face == 0) {
				NVertex<InputData> v = new ListVertex<InputData>();
				vertices.add(v);
				g.addVertex(v);
			}
			int adjTet = Integer.parseInt(spl.remove(0));
			
			int adjFace = Integer.parseInt(spl.remove(0));
			
			
			// Edges show up twice in the string representation.
			// Only add them once, when we are at the second 
			// such showing.  That means either we are at a "higher"
			// tet, or same tet but "higher" face.
			if (tet > adjTet) {
				NVertex<InputData> v1 = vertices.get(tet);
				NVertex<InputData> v2 = vertices.get(adjTet);
				g.addEdge(v1,v2);
			} else if ( tet == adjTet ) {
				if (face > adjFace) {
					NVertex<InputData> v1 = vertices.get(tet);
					NVertex<InputData> v2 = vertices.get(adjTet);
					g.addEdge(v1,v2);
				}
			}
			face+=1;
			if (face == 4) {
				tet+=1;
				face=0;
			}
			
		}
		
		NVertex<InputData> v1 = new ListVertex<InputData>();
		NVertex<InputData> v2 = new ListVertex<InputData>();
		g.addEdge(v1, v2);
		
		return null;
	}

}
