package se.ewpettersson.admissiblefpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.ewpettersson.admissiblefpg.fpg.Arc;
import se.ewpettersson.admissiblefpg.fpg.Vertex;

public class ConfigIterator implements Iterator<Config> {

	Vertex v;
	Config n;
	
	List<ConfigIterator> children;
	List<Config> configs;
	List<Arc> arcsAdded;
	HashMap<Integer, Integer> usedFaces;
	
	public ConfigIterator(Vertex v) {
		this.v=v;
		n=null;
		children = new ArrayList<ConfigIterator>(v.getNumChildren());
		configs = new ArrayList<Config>(v.getNumChildren());
		arcsAdded = new ArrayList<Arc>(v.getArcsAdded());
		usedFaces = new HashMap<Integer,Integer>();
		for(Vertex child: v.children()) {
			children.add(new ConfigIterator(child));
			configs.add(new Config());
			usedFaces.putAll(child.getFinalUsedFaces());
		}
		
		for(Integer tetToAdd: v.getToAdd()) {
			usedFaces.put(tetToAdd, 0);
		}
		
		
		if (children.size() > 0)
			n = recurse(0);
		else
			n = new Config();
	}
	@Override
	public boolean hasNext() {
		return (n==null);
	}

	
	
	private Config recurse(int i) {
//		Config c = null;
		
		if(i == children.size()) {
			return new Config();
//			c = configs.get(i);
//			if (c == null) {
//				configs.set(i, new Config());
//			} else {
//				configs.set(i, null);
//			}
//			return c;
		}
		ConfigIterator it = children.get(i);
		
		if ( i < (children.size()-1)) {
			if (!children.get(i+1).hasNext()) {
				children.set(i+1, new ConfigIterator(v.children().get(i+1)));
				configs.set(i+1, new Config(children.get(i+1).next()));
				configs.set(i, it.next());
			}
		} else {
			configs.set(i,  it.next());
		}
		
		
		Config childConfig = configs.get(i); 
		if (childConfig != null) { 
			Config nextChild = recurse(i+1);
			if (nextChild == null) {
				return null;
			}
			childConfig.mergeWith(nextChild);
			for(Integer tetToAdd: v.getToAdd()) {
				childConfig.addTetrahedra(tetToAdd);
			}
			
			while((childConfig != null) && (!addArc(childConfig))) {
				if ( i < (children.size()-1)) {
					if (!children.get(i+1).hasNext()) {
						children.set(i+1, new ConfigIterator(v.children().get(i+1)));
						configs.set(i+1, new Config(children.get(i+1).next()));
						configs.set(i, it.next());
					}
				} else {
					configs.set(i,  it.next());
				}
				childConfig = configs.get(i);
				if (childConfig != null) { 
					childConfig.mergeWith(recurse(i+1));
					for(Integer tetToAdd: v.getToAdd()) {
						childConfig.addTetrahedra(tetToAdd);
					}
				}
			}
		}
		
		
		return childConfig;
		
	}
	
	@Override
	public Config next() {
		Config next = n;
		if (children.size() > 0) {
			n = recurse(0);
		} else {
			if (n==null) {
				n = new Config();
			} else {
				n = null;
			}
		}
		return next;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	private boolean addArc(Config c) {
		if (arcsAdded.size() == 0) {
			return true;
		}
		Arc a = arcsAdded.get(0);
		arcsAdded.remove(a);
		Map<Integer,Integer> uf = v.getUsedFaces();
		int f1 = usedFaces.get(a.t1);
		usedFaces.put(a.t1,f1+1);
		int f2 = usedFaces.get(a.t2);
		usedFaces.put(a.t2,f2+1);
		for(int i=0;i<6;i++) {
			Gluing g = new Gluing(i,a.t1,f1,a.t2,f2);
			Config copy = new Config(c);
			//System.out.println("Gluing "+g);
			if (copy.addGluing(g) ) {
				String desc = "Glued "+g;
				copy.addDescription(desc);
				//System.out.println("Glued "+g);
				if (addArc(copy)) {
					return true;
				}
			}
		}
		// Undo the changes we did.
		usedFaces.put(a.t2,f2);
		usedFaces.put(a.t1,f1);
		arcsAdded.add(0, a);
		return false;
	}
	
	
}
