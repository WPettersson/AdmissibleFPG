package se.ewpettersson.admissiblefpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import se.ewpettersson.admissiblefpg.fpg.Arc;
import se.ewpettersson.admissiblefpg.fpg.Vertex;

public class ConfigIterator implements Iterator<Config> {

	Vertex v;
	Config n;
	
	List<ConfigIterator> children;
	List<Config> configs;
	List<Arc> arcsAdded;
	int[] syms;
	boolean nothing;
	HashMap<Integer, Integer> usedFaces;
	
	public ConfigIterator(Vertex v) {
		this.v=v;
		n=null;
		children = new ArrayList<ConfigIterator>(v.getNumChildren());
		configs = new ArrayList<Config>(v.getNumChildren());
		arcsAdded = new ArrayList<Arc>(v.getArcsAdded());
		usedFaces = new HashMap<Integer,Integer>();
		nothing=false;
		for(Vertex child: v.children()) {
			ConfigIterator it = new ConfigIterator(child);
			children.add(it);
			if(!it.hasNext()) { // One of the children has no possible configurations, no point proceeding?
				nothing=true;
			} else {
				configs.add(it.next());
			}
			usedFaces.putAll(child.getFinalUsedFaces());
		}
		
		for(Integer tetToAdd: v.getToAdd()) {
			usedFaces.put(tetToAdd, 0);
		}
		
		syms= new int[arcsAdded.size()];
		resetSyms();
		if (nothing) {
			n=null;
		} else {
			n = recurse(true);
		}
	}

	@Override
	public boolean hasNext() {
		return (n!=null);
	}

	private void resetSyms() {
		for(int i=0;i<children.size();i++)
			syms[i]=0;
	}
	
	private boolean nextSym(int i) {
		if (i == arcsAdded.size()) {
			return false;
		}
		if( ! nextSym(i+1)) {
			syms[i]+=1;
		}
		if ( syms[i] == 6) {
			syms[i] = 0;
			return false;
		}

		return true;
	}
	
	private Config recurse(boolean first) {
		Config c = null;
		// First run through we don't increment the symmetries or find the next config.
		if (first) {

			// Just try to make the arcs
			c = addArc(makeConfig(),0);
			// If we're successful, return this, otherwise we continue on with
			// the rest of this method, which moves on to the next symmetry
			if (c != null) {
				return c;
			}
		}
		// Look for a new config to return
		while(c==null) {
			// Try to move to the next set of symmetries. If we're totally reset, 
			// then try to move on to the next config. If we're also out of those,
			// we're done so return null.
			if (! nextSym(0)) {
				if(! nextConfig(0)) {
					return null;
				}
			}
			// Now add the arcs. If this works, the while loop breaks and we return.
			// Otherwise the loop continues and we move on to the next symmetry/config.
			c = addArc(makeConfig(),0);
		}
		return c;
	}
	
	private Config makeConfig() {
		Config c = new Config();
		for(Config o: configs) {
			if ( o!= null) 
				c.mergeWith(o);
		}
		for(int t:v.getToAdd()) {
			c.addTetrahedra(t);
		}
		return c;
	}
	
	private Config recurse() {
		return recurse(false);
	}

	private boolean nextConfig(int i) {
		if (i == children.size()) {
			return false;
		}
		ConfigIterator it = children.get(i);

		boolean next = nextConfig(i+1);
		if (!next) {
			if (it.hasNext()) {
				configs.set(i, it.next());
				return true;
			} else {
				ConfigIterator newIt = new ConfigIterator(v.children().get(i));
				children.set(i, newIt);
				Config newConfig = newIt.next();
				if(newConfig == null) {
					return false;
				}
				configs.set(i, new Config(newConfig));
				return false;
			}
		} 
		return true;
	}
	
	@Override
	public Config next() {
		Config next = n;
		n = recurse();
		return next;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	private Config addArc(Config c, int i) {
		if (arcsAdded.size() == i) {
			return c;
		}
		Arc a = arcsAdded.get(i);
		Gluing g = new Gluing(syms[i],a.t1,a.f1,a.t2,a.f2);
		Config copy = new Config(c);
		//System.out.println("Gluing "+g);
		if (copy.addGluing(g) ) {
			String desc = "Glued "+g;
			copy.addDescription(desc);
			//System.out.println("Glued "+g);
			Config good = addArc(copy,i+1);
			if (good != null) {
				return good;
			}
		}
		return null;
	}
	
	
}
