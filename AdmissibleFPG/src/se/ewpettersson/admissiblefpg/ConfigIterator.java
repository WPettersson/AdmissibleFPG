package se.ewpettersson.admissiblefpg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.ewpettersson.admissiblefpg.fpg.Arc;
import se.ewpettersson.admissiblefpg.fpg.Vertex;

public class ConfigIterator implements Iterator<Config> {

	
	public class SymClass {
		int sym;
		int f1;
		int f2;
		public SymClass() {
			sym=0;
			f1=0;
			f2=0;
		}
		
		public String toString() {
			return "[" + sym + ":" + f1 + ":" + f2 + "]";
		}
	}
	
	Vertex v;
	Config n;
	
	List<ConfigIterator> children;
	List<Config> configs;
	List<Config> configsHere;
	List<Arc> arcsAdded;
	Config[] stackConfigs;
//	int[] syms;
	List<SymClass> syms;
	boolean nothing;
	boolean configsFound;
	int configsIndex;
	int changed;
	int count;
	boolean foundNext;
	boolean first;
	
	public ConfigIterator(Vertex v) {
		this.v=v;
		n=null;
		children = new ArrayList<ConfigIterator>(v.getNumChildren());
		configs = new ArrayList<Config>(v.getNumChildren());
		configsHere = new ArrayList<Config>();
		arcsAdded = new ArrayList<Arc>(v.getArcsAdded());
		stackConfigs = new Config[v.getArcsAdded().size()+1];
		nothing=false;
		configsFound=false;
		configsIndex=0;
		count=-1;
		for(Vertex child: v.children()) {
			ConfigIterator it = new ConfigIterator(child);
			children.add(it);
			if(!it.hasNext()) { // One of the children has no possible configurations, no point proceeding?
				nothing=true;
			} else {
				configs.add(it.next());
			}
		}
		
		syms= new ArrayList<SymClass>();
		for(int i=0; i < arcsAdded.size(); i++) {
			syms.add(new SymClass());
		}
		changed=0;
		resetSyms();
		if (nothing) {
			n=null;
			foundNext = true;
		} else {
			first=true;
			foundNext = false;
		}
	}

	@Override
	public boolean hasNext() {
		if(configsFound) {
			return(configsIndex < configsHere.size());
		}
		if (!foundNext) {
			n = recurse();
			configsHere.add(n);
			foundNext = true;
		}
		return (n!=null);
	}

	private void resetSyms() {
		for(SymClass sim: syms) {
			sim.sym=0;
			sim.f1=0;
			sim.f2=0;
		}
	}
	
	private boolean nextSym(int i) {
		if (i == arcsAdded.size()) {
			return false;
		}
		SymClass sim = syms.get(i);
		if( ! nextSym(i+1)) {
			sim.sym += 1;
			changed = i;
		}
		if (sim.sym == 6) {
			sim.sym = 0;
			sim.f1 += 1;
			if (sim.f1 == 4) {
				sim.f1 = 0;
				sim.f2 += 1;
				if (sim.f2 == 4) {
					sim.f2 = 0;
					syms.set(i,sim);
					return false;
				}
			}
		}
		syms.set(i, sim);
		return true;
	}
	
	private Config recurse() {
		Config c = null;
		// First run through we don't increment the symmetries or find the next config.
		if (first) {
			first=false;
			
			stackConfigs[0] = makeConfig();
			// Just try to make the arcs
			c = addArc(0);
			// If we're successful, return this, otherwise we continue on with
			// the rest of this method, which moves on to the next symmetry
			if (c != null) {
				return c;
			}
		}
		// Look for a new config to return
		while(c==null) {
			//System.out.println(syms.toString());
			// Try to move to the next set of symmetries. If we're totally reset, 
			// then try to move on to the next config. If we're also out of those,
			// we're done so return null.
			if (! nextSym(0)) {
				if(! nextConfig(0)) {
					return null;
				}
				stackConfigs[0] = makeConfig();
			}

			// Now add the arcs. If this works, the while loop breaks and we return.
			// Otherwise the loop continues and we move on to the next symmetry/config.
			c = addArc(changed);
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
				it.reset();
				Config newConfig = it.next();
				//ConfigIterator newIt = new ConfigIterator(v.children().get(i));
				//children.set(i, newIt);
				//Config newConfig = newIt.next();
				if(newConfig == null) {
					nothing=true;
					return false;
				}
				configs.set(i, newConfig);
				return false;
			}
		} 
		return true;
	}
	
	private void reset() {
		configsFound = true;
		configsIndex = 0;
		count = getCount();
		if(children != null) { 
			children.clear();
			children = null;
		}
		if(configs != null) {
			configs.clear();
			configs = null;
		}
	}

	@Override
	public Config next() {
		if(configsFound) {
			if(configsHere.size()<=configsIndex) {
				return null;
			}
			return configsHere.get(configsIndex++);
		}
		if(nothing) {
			return null;
		}
		if(!foundNext) {
			n = recurse();
			configsHere.add(n);
		}
		foundNext = false;
		return n;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	private Config addArc(int i) {
		if (arcsAdded.size() == i) {
			// If we've already seen this config, don't find it again.
			if (configsHere.contains(stackConfigs[i]))
				return null;
			return stackConfigs[i];
		}
		Arc a = arcsAdded.get(i);
		SymClass sim = syms.get(i);
		Config copy = new Config(stackConfigs[i]);
		if (!copy.onBoundary(a.t1,sim.f1) || 
			!copy.onBoundary(a.t2,sim.f2)
			// Don't glue faces to themselves.
			 || (a.t1 == a.t2 && sim.f1 == sim.f2)
			// If attempting to glue face 0 to something, only attempt identity mapping
			 || (sim.f1 == 0 && sim.sym > 0)
			 || (sim.f2 == 0 && sim.sym > 0)
			// If face 0 is not glued, don't try other faces
			|| (sim.f1 > 0 && copy.onBoundary(a.t1, 0))
			|| (sim.f2 > 1 && a.t1 != a.t2 && copy.onBoundary(a.t2, 0))
				) {
//			if (sim.f2 > 1 && a.t1 != a.t2 && copy.onBoundary(a.t2, 0)) {
//				System.out.println("Skipping on t1,t2="+a.t1+","+a.t2 + " and sim.f2="+sim.f2);
//				System.out.println(copy.ec.toString());
//			}
			for(SymClass sym: syms.subList(i+1, syms.size())) {
				sym.sym = 5;
				sym.f1 = 3;
				sym.f2 = 3;
			}
			return null;
		}


		Gluing g = new Gluing(sim.sym,a.t1,sim.f1,a.t2,sim.f2);

//		System.out.println("Gluing "+g);
		if (copy.addGluing(g) ) {
			copy.addDescription(g.toString());
//			System.out.println(copy.getDescriptions());

			stackConfigs[i+1] = copy;
			Config good = addArc(i+1);
			if (good != null) {
				return good;
			}
		} else {
			for(SymClass sym: syms.subList(i+1, syms.size())) {
				sym.sym = 5;
				sym.f1 = 3;
				sym.f2 = 3;
			}
		}
		return null;
	}
	
	public int getCount() {
		if(count > 0) {
			return count;
		}
		int num = configsHere.size();
		for( ConfigIterator childIterator: children) {
			int childrenSize = childIterator.getCount();
			if ( childrenSize > num) {
				num = childrenSize;
			}
		}
		return num;
	}
	
}
