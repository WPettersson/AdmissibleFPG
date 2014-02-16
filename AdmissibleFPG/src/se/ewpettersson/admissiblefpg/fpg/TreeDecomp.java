package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import se.ewpettersson.admissiblefpg.Config;
import se.ewpettersson.admissiblefpg.Gluing;

import nl.uu.cs.treewidth.algorithm.GreedyFillIn;
import nl.uu.cs.treewidth.algorithm.MaximumMinimumDegreePlusLeastC;
import nl.uu.cs.treewidth.algorithm.PermutationToTreeDecomposition;
import nl.uu.cs.treewidth.algorithm.QuickBB;
import nl.uu.cs.treewidth.algorithm.TreewidthDP;
import nl.uu.cs.treewidth.input.GraphInput.InputData;
import nl.uu.cs.treewidth.input.InputException;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.ngraph.NTDBag;
import nl.uu.cs.treewidth.ngraph.NVertex;
import nl.uu.cs.treewidth.ngraph.NVertexOrder;

public class TreeDecomp {
	List<Edge> edges;

	List<Vertex> vertices;
	public List<Integer> seen;
	Vertex root;

	int nTets;
	int tw;
	
	FacePairingGraph fpg;

	private Config oneConfig;

	
	public FacePairingGraph getFpg() {
		return fpg;
	}

	public TreeDecomp(FacePairingGraph f) throws InputException {
		edges = new ArrayList<Edge>();
		vertices = new ArrayList<Vertex>();
		seen = new ArrayList<Integer>();
		fpg = f;
		getTreeDecomp();
		createRootedTree();
	}
	
	private void createRootedTree() {
//		root = null;
//		for(Vertex v : vertices) {
//			if (v.degree(edges) == 1) {
//				root = v;
//				break;
//			}
//		}
//		if(vertices.size() ==1) {
//			root = vertices.get(0);
//		}
		root = vertices.get(0);
		root.setRoot();
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
	
	
	private void getTreeDecomp() throws InputException {
		FPGReader f = new FPGReader(fpg);
		NGraph<InputData> g = f.get();
		
		MaximumMinimumDegreePlusLeastC<InputData> lbAlgo = new MaximumMinimumDegreePlusLeastC<InputData>();
		lbAlgo.setInput( g );
		lbAlgo.run();
		int lowerbound = lbAlgo.getLowerBound();

		GreedyFillIn<InputData> ubAlgo = new GreedyFillIn<InputData>();
		ubAlgo.setInput( g );
		ubAlgo.run();
		int upperbound = ubAlgo.getUpperBound();
		
		NVertexOrder<InputData> permutation = null;

		if( lowerbound == upperbound ) {
			permutation = ubAlgo.getPermutation();
		} else {
//			TreewidthDP<InputData> dpAlgo = new TreewidthDP<InputData>();
//			dpAlgo.setInput( g );
//			dpAlgo.run();
//			permutation = dpAlgo.
			QuickBB<InputData> qbbAlgo = new QuickBB<InputData>();
			qbbAlgo.setInput( g );
			qbbAlgo.run();
			permutation = qbbAlgo.getPermutation();
			upperbound = qbbAlgo.getUpperBound();
		}
		
		tw = upperbound+1;
		
		PermutationToTreeDecomposition<InputData> convertor = new PermutationToTreeDecomposition<InputData>( permutation );
		convertor.setInput( g );
		convertor.run();
		NGraph<NTDBag<InputData>> decomposition = convertor.getDecomposition();
		Iterator<NVertex<NTDBag<InputData>>> it = decomposition.getVertices();
		HashMap<NVertex<NTDBag<InputData>>,Integer> map = new HashMap<NVertex<NTDBag<InputData>>,Integer>();
		HashMap<Integer,Vertex> vMap = new HashMap<Integer,Vertex>();
		int ind=0;
		while( it.hasNext() ) {
			NVertex<NTDBag<InputData>> bag = it.next();
			map.put(bag, ind);

			List<Integer> contents = new ArrayList<Integer>();
			for( NVertex<InputData> v : bag.data.vertices ) {
				contents.add(v.data.id);
				
			}
			Iterator<NVertex<NTDBag<InputData>>> i = bag.getNeighbors();
			Vertex v = new Vertex(contents, ind, this);
			vMap.put(ind, v);
			while( i.hasNext() ) {
				NVertex<NTDBag<InputData>> n = i.next();
				if (map.containsKey(n)) {
					Edge e = new Edge( v, vMap.get(map.get(n)));
					edges.add(e);
				}
			}
			vertices.add(v);

			ind++;
		}

	}
	
	
	
	public boolean isAdmissible() {
		return root.hasConfig();
		//return (root.getConfigs().size() > 0);
	}
	
	public List<Edge> getEdges() {
		return edges;
	}

	public void makeConfig(int numTet) {
		this.oneConfig = new Config();
		for(int i=0;i<numTet;i++)
			oneConfig.addTetrahedra(i);
	}
	
	public boolean glue(int t1, int f1, int t2, int f2, int s) {
		Gluing g = new Gluing(s, t1, f1, t2, f2);
		System.out.println("Gluing "+g);
		return oneConfig.addGluing(g);
		
//		System.out.println(oneConfig.getVC().getLinks().get(3).get(1).get(6));
//		System.out.println(oneConfig.getVC().getLinks().get(0).get(1).get(6));
//		
//		System.out.println(oneConfig.getVC().getLinks().get(2).get(0).get(5));
//		System.out.println(oneConfig.getVC().getLinks().get(2).get(1).get(6));
		
	}

	public int getTW() {
		if (tw<0) 
			tw=1;
		return tw;
	}
	
	public String toString() {
		String s="[";
		for(Vertex v : vertices) {
			s+= v.toString() + ", ";
		}
		if(s.endsWith(", "))
			s = s.substring(0,s.length()-2);
		s+="]\n[";
		for(Edge e: edges) {
			s+= e.toString() + ", ";
		}
		if(s.endsWith(", "))
			s = s.substring(0,s.length()-2);
		s+="]\n";
		return s;
	}

	public int getMaxConfigs() {
		return root.getMaxConfigs();
	}

	public String vertexOrder() {
		String s = new String();
		s+= root.vertexOrder();
		return s;
	}


}
