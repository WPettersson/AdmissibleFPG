package se.ewpettersson.admissiblefpg;

import java.util.LinkedList;
import java.util.List;

public class EdgeConfig {
	public EdgeConfig() {
		super();
		pairs = new LinkedList<TFE[]>();
		

	}

	public EdgeConfig(EdgeConfig ec) {
		pairs = new LinkedList<TFE[]>();
		for(TFE[] entry: ec.getPairs()) {
			TFE[] newEntry = entry.clone();
			pairs.add(newEntry);
		}
	}

	public List<TFE[]> getPairs() {
		return pairs;
	}

	public void addTetrahedra(int i) {
		TFE[] pair = new TFE[2];

		pair[0] = new TFE(i,2,1);
		pair[1] = new TFE(i,3,1);
		pairs.add(pair);
		
		pair = new TFE[2];
		pair[0] = new TFE(i,1,2);
		pair[1] = new TFE(i,3,2);
		pairs.add(pair);
		
		pair = new TFE[2];
		pair[0] = new TFE(i,1,3);
		pair[1] = new TFE(i,2,3);
		pairs.add(pair);
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,4);
		pair[1] = new TFE(i,3,4);
		pairs.add(pair);
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,5);
		pair[1] = new TFE(i,2,5);
		pairs.add(pair);
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,6);
		pair[1] = new TFE(i,1,6);
		pairs.add(pair);
		
	}
	
	List<TFE[]> pairs;
	
	public int numPairs() {
		return pairs.size();
	}
	
	public boolean addGluing(TFE [] gluing) {
		TFE pairOne = null;
		TFE removePair[] = null;
		for(TFE p[] : pairs) {
			int o = p[0].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = p[1].orientTo(gluing[1]);
				if (o2 == 1) {
					// MINIMAL
					// Can remove this pair as it's being closed up.
					pairs.remove(p);
					return true;
				}
				if (o2 == 1) {
					// Gluing edge to itself in reverse
					return false;
				}
				if (pairOne == null) {
					pairOne = p[1];
					removePair = p;
				} else {
					p[0] = pairOne;
					pairs.remove(removePair);
					return true;
				}
			}
			o = p[1].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = p[0].orientTo(gluing[1]);
				if (o2 == 1) {
					// MINIMAL
					// Can remove this pair as it's being closed up.
					pairs.remove(p);
					return true;
				}
				if (o2 == 1) {
					// Gluing edge to itself in reverse
					return false;
				}
				if (pairOne == null) {
					pairOne = p[0];
					removePair = p;
				} else {
					p[1] = pairOne;
					pairs.remove(removePair);
					return true;
				}
			}
			if (p[0].orientTo(gluing[1]) != 0) {
				if (pairOne == null) {
					pairOne = p[1];
					removePair = p;
				} else {
					p[0] = pairOne;
					pairs.remove(removePair);
					return true;
				}
			}
			if (p[1].orientTo(gluing[1]) != 0) {
				if (pairOne == null) {
					pairOne = p[0];
					removePair = p;
				} else {
					p[1] = pairOne;
					pairs.remove(removePair);
					return true;
				}
			}

			 
		}
		return false;
	}
	
	public String toString() {
		String s= "[";
		for(TFE[] p : pairs) {
			s+= "["+ p[0].toString() + " to " + p[1].toString() + "], ";
		}
		return s.substring(0, s.length() -2) + "]";
	}

	public boolean pairs(TFE p1, TFE p2) {
		for(TFE p[] : pairs) {
			if ((p[0] == p1 && p[1] == p2) || (p[0] == p2 && p[1] == p1)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(EdgeConfig other) {
		for(TFE p[]: pairs) {
			if( ! other.pairs(p[0], p[1])) {
				return false;
			}
		}
		return true;
	}

	public void mergeWith(EdgeConfig ec) {
		pairs.addAll(ec.getPairs());
		
	}
}
