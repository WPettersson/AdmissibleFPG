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
		for(Arc a: f.getArcs()) {
			if (!a.isLoop()) {
				NVertex<InputData> v1 = vertices.get(a.t1);
				NVertex<InputData> v2 = vertices.get(a.t2);
				g.addEdge(v1,v2);
			}
		}		
		return g;
	}

}
