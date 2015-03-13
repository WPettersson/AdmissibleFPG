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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import se.ewpettersson.admissiblefpg.Config;
import se.ewpettersson.admissiblefpg.ConfigIterator;
import se.ewpettersson.admissiblefpg.Gluing;

public class Vertex implements Iterable<Config> {
	List<Integer> contents;
	List<Vertex> children;
	Vertex parent;
	List<Config> possibleConfigs;
	
	List<List<Config>> childrenConfigs;
	
	Set<Integer> below;
	List<Arc> arcsAdded;
	
//	Map<Integer,List<Boolean>> usedFaces;
//	Map<Integer, List<Boolean>> used;
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
		parent = null;
//		usedFaces = new HashMap<Integer,List<Boolean>>();
//		used = null;
	}
	
//	public Map<Integer, List<Boolean>> getUsedFaces() {
//		
//		return usedFaces;
//	}

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
		child.parent = this;
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
//			usedFaces.putAll(child.getFinalUsedFaces());
			if (child.getMaxConfigs() > maxConfigs)
				maxConfigs = child.getMaxConfigs();
			child.forget();
		}
		
		List<Config> configsToTry = toTry();
		for(Config c: configsToTry) {
			for(Integer tetToAdd: toAdd) {
				c.addTetrahedra(tetToAdd);
//				List<Boolean> usedFacesOnNew = new LinkedList<Boolean>();
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				usedFaces.put(tetToAdd, usedFacesOnNew);
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

//	public Map<Integer,List<Boolean>> getFinalUsedFaces() {
//		if (used == null) {
//			used = new HashMap<Integer,List<Boolean>>();
//			for(Vertex v: children) {
//				used.putAll(v.getFinalUsedFaces());
//			}
//			for(Integer tetToAdd: toAdd) {
//				List<Boolean> usedFacesOnNew = new LinkedList<Boolean>();
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				usedFacesOnNew.add(false);
//				used.put(tetToAdd, usedFacesOnNew);
//			}
//			for( Arc a: arcsAdded) {
//				a.setF1(used.get(a.t1));
//				used.put(a.t1, used.get(a.t1)+1);
//				a.setF2(used.get(a.t2));
//				used.put(a.t2, used.get(a.t2)+1);
//			}
//		}
//		return used;
//	}

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
		for(int f1 = 0; f1 < 4; f1++) {
			if (!c.onBoundary(a.t1, f1)) {
				continue;
			}
			for(int f2 = 0; f2 < 4; f2++) {
				if (!c.onBoundary(a.t2,f2)) {
					continue;
				}
				for(int i=0;i<6;i++) {
					Gluing g = new Gluing(i,a.t1,f1,a.t2,f2);
					Config copy = new Config(c);
					System.out.println("Gluing gggg "+g);
					if (copy.addGluing(g) ) {
						//String desc = "Glued "+g;
		//				copy.addDescription(desc);
						//System.out.println("Glued "+g);
						addArc(copy);
					}
				}
			}
		}
		// Undo the changes we did.
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
		String s = "[Vertex: id="+id+", content="+contents+", arcsAdded="+arcsAdded.toString()+"]";
		return s;
	}

	@Override
	public Iterator<Config> iterator() {
		return new ConfigIterator(this, false);  // false as we don't need to store the configs we find.
	}

	public int getNumChildren() {
		return children.size();
	}

	public  List<Vertex> children() {

		return children;
	}

	public Vertex parent() {
		return parent;
	}

	public boolean hasConfig() {
//		getFinalUsedFaces();
		Iterator<Config> it = new ConfigIterator(this, false); // false as we don't need to store the configs we find.
		if(it.hasNext()) {
			maxConfigs = ((ConfigIterator) it).getCount();
//			System.out.println(it.next().toString());
//			System.out.println(it.next().getDescriptions());
			return true;
		}
		maxConfigs = ((ConfigIterator) it).getCount();
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
