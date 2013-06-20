package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.List;

public class TreeDecomp {
	List<Edge> edges;

	List<Vertex> vertices;
	public List<Integer> seen;
	Vertex root;

	int nTets;
	
	FacePairingGraph fpg;

	
	public FacePairingGraph getFpg() {
		return fpg;
	}

	public TreeDecomp() {
		edges = new ArrayList<Edge>();
		vertices = new ArrayList<Vertex>();
		seen = new ArrayList<Integer>();
		
	}
	
	public void createRootedTree() {
		root = null;
		for(Vertex v : vertices) {
			if (v.degree() == 1) {
				root = v;
				break;
			}
		}
		if (root == null) {
			return;
		}
		// "ToAdd" is a list of which tetrahedra to add at this step
		// For the root bag, any tetrahedra in this bag are added in the final
		// step
		
		root.setToAdd(root.getContents());
		// Keep a track of which tetrahedra we have seen, that is which
		// tetrahedra will be added at a "higher" stage of the tree.
		seen.addAll(root.getContents());
		
		// Find the children of root, and add them.
		addChildren(root);
		
		root.assignEdges();

	}
	
	public void addChildren(Vertex v) {
		// Go through edges and find children of v. Note that the
		// e.isUsed()/e.use() test ensures that each edge is only added once.

		for(Edge e:edges) {
			if (!e.isUsed() && e.touches(v)) {
				e.use();
				// Find the actual child node
				Vertex child = e.getOther(v);
				// Get the tetrahedra at this node, and work out which are new.
				// The ones that are new are to be added at this stage.
				List<Integer> childToAdd = new ArrayList<Integer>(child.getContents());
				childToAdd.removeAll(seen);
				child.setToAdd(childToAdd);
				v.addChild(child);
				// Mark the extra tetrahedra as being seen.
				seen.addAll(childToAdd);
				// Add the children of this child.
				// Note that this is depth-first, but we need a full traversal
				// so that doesn't matter. We also know this will be correct as
				// if two children share a common tetrahedra then the parent
				// must also share that tetrahedra (and thus it must already
				// been in seen)
				addChildren(child);
			}
		}	
	}
	
	public boolean isAdmissible() {
		return (root.getConfigs().size() > 0);
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	


}
