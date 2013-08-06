package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.ewpettersson.admissiblefpg.Config;
import se.ewpettersson.admissiblefpg.ConfigIterator;
import se.ewpettersson.admissiblefpg.Gluing;

public class Vertex implements Iterable<Config> {
	List<Integer> contents;
	List<Vertex> children;
	List<Config> possibleConfigs;
	
	List<List<Config>> childrenConfigs;
	
	Set<Integer> below;
	List<Arc> arcsAdded;
	
	Map<Integer,Integer> usedFaces;
	Map<Integer,Integer> used;
	List<Integer> toAdd;
	Integer id;
	Integer deg;
	boolean isRoot;
	TreeDecomp decomp;
	int maxConfigs;
	
	public int getMaxConfigs() {
		return maxConfigs;
	}

	public Vertex(List<Integer> contents, Integer id, TreeDecomp d) {
		this.id = id;
		this.contents = contents;
		this.isRoot = false;
		deg = null;
		decomp = d;
		children = new ArrayList<Vertex>();
		usedFaces = new HashMap<Integer,Integer>();
		used = null;
	}
	
	public Map<Integer, Integer> getUsedFaces() {
		
		return usedFaces;
	}

	public List<Integer> getContents() {
		return contents;
	}

	public void setContents(List<Integer> contents) {
		this.contents = contents;
	}

	public int degree(List<Edge> edges) {
		if (deg == null) {
			int count = 0;
			for(Edge e: edges) {
				if (e.touches(this)) {
					count+=1;
				}
			}
			deg = count;
		}
		return deg;
	}
	
	public void setToAdd(List<Integer> l) {
		toAdd = l;
	}

	public void addChild(Vertex child) {
		children.add(child);
	}
	
	public List<Config> getConfigs() {
		if (possibleConfigs == null) {
			possibleConfigs = new LinkedList<Config>();
			findConfigs();
		}
		return possibleConfigs;
	}
	
	public void setRoot() {
		this.isRoot = true;
	}
	
	private void findConfigs() {
		childrenConfigs = new ArrayList<List<Config>>();
		maxConfigs=0;
		for( Vertex child: children) {
			childrenConfigs.add(child.getConfigs());
			usedFaces.putAll(child.getFinalUsedFaces());
			if (child.getMaxConfigs() > maxConfigs)
				maxConfigs = child.getMaxConfigs();
			child.forget();
		}
		
		List<Config> configsToTry = toTry();
		for(Config c: configsToTry) {
			for(Integer tetToAdd: toAdd) {
				c.addTetrahedra(tetToAdd);
				usedFaces.put(tetToAdd, 0);
			}
			addArc(c);
		}
		if (possibleConfigs.size() > maxConfigs) {
			maxConfigs = possibleConfigs.size();
		}
	}
	
	private void forget() {
		possibleConfigs = null;
	}

	public Map<Integer,Integer> getFinalUsedFaces() {
		if (used == null) {
			used = new HashMap<Integer,Integer>();
			for(Vertex v: children) {
				used.putAll(v.getFinalUsedFaces());
			}
			for(Integer tetToAdd: toAdd) {
				used.put(tetToAdd, 0);
			}
			for( Arc a: arcsAdded) {
				a.setF1(used.get(a.t1));
				used.put(a.t1, used.get(a.t1)+1);
				a.setF2(used.get(a.t2));
				used.put(a.t2, used.get(a.t2)+1);
			}
		}
		return used;
	}

	private void addArc(Config c) {
		if (isRoot && possibleConfigs.size() > 0) {
			return;
		}
		if (arcsAdded.size() == 0) {
			if (!possibleConfigs.contains(c)) {
				possibleConfigs.add(c);
			}
			return;
		}
		Arc a = arcsAdded.get(0);
		arcsAdded.remove(a);
		int f1 = usedFaces.get(a.t1);
		usedFaces.put(a.t1,f1+1);
		int f2 = usedFaces.get(a.t2);
		usedFaces.put(a.t2,f2+1);
		for(int i=0;i<6;i++) {
			Gluing g = new Gluing(i,a.t1,f1,a.t2,f2);
			Config copy = new Config(c);
			//System.out.println("Gluing "+g);
			if (copy.addGluing(g) ) {
//				String desc = "Glued "+g;
//				copy.addDescription(desc);
				//System.out.println("Glued "+g);
				addArc(copy);
			}
		}
		// Undo the changes we did.
		usedFaces.put(a.t2,f2);
		usedFaces.put(a.t1,f1);
		arcsAdded.add(0, a);
	}
	
	private Set<Integer> seenTetrahedraBelow() {
		if (below == null ) {
			below = new HashSet<Integer>(toAdd);
			for( Vertex child: children) {
				below.addAll(child.seenTetrahedraBelow());
				child.assignEdges();
			}
		}
		return below;
	}
	
	void assignEdges() {
		arcsAdded = new LinkedList<Arc>();
		for(Arc e: decomp.getFpg().getArcsBetween(seenTetrahedraBelow(), toAdd)) {
			arcsAdded.add(e);
		}
	}
	
	private List<Config> toTry() {
		// If this node has no children, there is only one (empty) config to try.
		if ( children.size() == 0) {
			List<Config> l = new ArrayList<Config>();
			l.add(new Config());
			return l;
		}
		// Else use recursion to find all possible ways of choosing one element
		// from each list in childrenConfig.  If there's only one such list,
		// return that list.
		List<Config> current = childrenConfigs.get(0);
		if ( childrenConfigs.size() == 1) {
			return current;
		}
		// If there's more than one list, merge each config in the first list
		// with every other possible config.
		childrenConfigs.remove(0);
		List<Config> newList = new ArrayList<Config>();
		List<Config> other = toTry();
		for (Config c : current) {
			for (Config d : other) {
				Config newConfig = new Config(c);
				newList.add(newConfig);
				newConfig.mergeWith(d);
			}
		}
		return newList;
	}
	
	public String toString() {
		String s = "[Vertex: id="+id+", content="+contents+"]";
		return s;
	}

	@Override
	public Iterator<Config> iterator() {
		return new ConfigIterator(this);
	}

	public int getNumChildren() {
		return children.size();
	}

	public  List<Vertex> children() {

		return children;
	}

	public boolean hasConfig() {
		getFinalUsedFaces();
		Iterator<Config> it = new ConfigIterator(this);
		if(it.hasNext()) {
			maxConfigs = ((ConfigIterator) it).getCount();
			return true;
		}
		return false;
	}

	public List<Arc> getArcsAdded() {
		return arcsAdded;
	}

	public  List<Integer> getToAdd() {
		return toAdd;
	}

	public String vertexOrder() {
		String s = new String();
		for(Vertex child: children) {
			s += child.vertexOrder() + " ";
		}
		Iterator<Integer> it = toAdd.iterator();
		while(it.hasNext()) {
			s += it.next() + " ";
		}
		if (s.endsWith(" "))
			s = s.substring(0, s.length()-1);
		return s;
	}
}
