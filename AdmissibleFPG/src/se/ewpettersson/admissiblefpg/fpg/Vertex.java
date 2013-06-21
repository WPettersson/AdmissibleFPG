package se.ewpettersson.admissiblefpg.fpg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.ewpettersson.admissiblefpg.Config;
import se.ewpettersson.admissiblefpg.Gluing;

public class Vertex {
	List<Integer> contents;
	List<Vertex> children;
	List<Config> possibleConfigs;
	
	List<List<Config>> childrenConfigs;
	
	Set<Integer> below;
	List<Arc> arcsAdded;
	
	Map<Integer,Integer> usedFaces;
	List<Integer> toAdd;
	Integer id;
		
	TreeDecomp decomp;
	
	public Vertex(List<Integer> contents, Integer id) {
		this.id = id;
		this.contents = contents;
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

	public int degree() {
		// TODO
		return 0;
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
	
	private void findConfigs() {
		childrenConfigs = new ArrayList<List<Config>>();
		for( Vertex child: children) {
			childrenConfigs.add(child.getConfigs());
			usedFaces.putAll(child.getUsedFaces());
		}
		
		List<Config> configsToTry = toTry();
		for(Config c: configsToTry) {
			for(Integer tetToAdd: toAdd) {
				c.addTetrahedra(tetToAdd);
				usedFaces.put(tetToAdd, 0);
			}
			addArc(c);
		}
	}
	
	private void addArc(Config c) {
		if (arcsAdded.size() == 0) {
			if (!possibleConfigs.contains(c)) {
				possibleConfigs.add(c);
			}
		}
		Arc a = arcsAdded.get(0);
		arcsAdded.remove(a);
		int f1 = usedFaces.get(a.t1);
		int f2 = usedFaces.get(a.t2);
		usedFaces.put(a.t1,f1-1);
		usedFaces.put(a.t2,f2-1);
		for(int i=0;i<6;i++) {
			Gluing g = new Gluing(i,a.t1,f1,a.t2,f2);
			Config copy = new Config(c);
			if (copy.addGluing(g) ) {
				addArc(copy);
			}
		}
		// Undo the changes we did.
		usedFaces.put(a.t1,f1);
		usedFaces.put(a.t2,f2);
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
}
