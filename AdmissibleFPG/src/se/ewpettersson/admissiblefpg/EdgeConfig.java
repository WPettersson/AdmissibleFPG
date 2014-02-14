package se.ewpettersson.admissiblefpg;

import java.util.LinkedList;
import java.util.List;

public class EdgeConfig {
	
	public class Pair {
		TFE[] pair;
		int degree;
		public Pair(TFE a, TFE b) {
			super();
			pair = new TFE[2];
			pair[0] = a;
			pair[1] = b;
			degree = 0;
		}

		public Pair(TFE a, TFE b, int d) {
			super();
			pair = new TFE[2];
			pair[0] = a;
			pair[1] = b;
			degree = d;
		}
		
		public Pair clone() {
			Pair p = new Pair(pair[0],pair[1],degree);
			return p;
		}
		
		public TFE[] getPairs() {
			return pair;

		}

		public void increaseDegree() {
			degree++;
			
		}

		public boolean degreeOk() {
			return degree >= 3;
		}
		
	}
	
	
	List<Pair> pairs;
	
	public EdgeConfig() {
		super();
		pairs = new LinkedList<Pair>();
		

	}

	public EdgeConfig(EdgeConfig ec) {
		pairs = new LinkedList<Pair>();
		for(Pair entry: ec.getPairs()) {
			Pair newEntry = (Pair) entry.clone();
			pairs.add(newEntry);
		}
	}

	public List<Pair> getPairs() {
		return pairs;
	}

	public void addTetrahedra(int i) {
		TFE[] pair = new TFE[2];

		pair[0] = new TFE(i,2,1);
		pair[1] = new TFE(i,3,1);
		pairs.add(new Pair(pair[0],pair[1]));
		
		pair = new TFE[2];
		pair[0] = new TFE(i,1,2);
		pair[1] = new TFE(i,3,2);
		pairs.add(new Pair(pair[0],pair[1]));
		
		pair = new TFE[2];
		pair[0] = new TFE(i,1,3);
		pair[1] = new TFE(i,2,3);
		pairs.add(new Pair(pair[0],pair[1]));
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,4);
		pair[1] = new TFE(i,3,4);
		pairs.add(new Pair(pair[0],pair[1]));
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,5);
		pair[1] = new TFE(i,2,5);
		pairs.add(new Pair(pair[0],pair[1]));
		
		pair = new TFE[2];
		pair[0] = new TFE(i,0,6);
		pair[1] = new TFE(i,1,6);
		pairs.add(new Pair(pair[0],pair[1]));
		
	}
	

	
	public int numPairs() {
		return pairs.size();
	}
	
	public boolean addGluing(TFE [] gluing) {
		TFE pairOne = null;
		TFE removePair[] = null;
		for(Pair pair : pairs) {
			TFE[] p = pair.getPairs();
			int o = p[0].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = p[1].orientTo(gluing[1]);
				if (o2 == o) {
					// MINIMAL
					if (!pair.degreeOk()) {
						return false;
					}
					// Can remove this pair as it's being closed up.
					pairs.remove(p);
					return true;
				}
				if (o2 == -o) {
					// Gluing edge to itself in reverse
					return false;
				}
				if (pairOne == null) {
					pairOne = p[1];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = p;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					p[0] = pairOne;
					pair.increaseDegree();
					pairs.remove(removePair);
					return true;
				}
			}
			o = p[1].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = p[0].orientTo(gluing[1]);
				if (o2 == o) {
					// MINIMAL
					if (!pair.degreeOk()) {
						return false;
					}
					// Can remove this pair as it's being closed up.
					pairs.remove(p);
					return true;
				}
				if (o2 == -o) {
					// Gluing edge to itself in reverse
					return false;
				}
				if (pairOne == null) {
					pairOne = p[0];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = p;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					p[1] = pairOne;
					pair.increaseDegree();
					pairs.remove(removePair);
					return true;
				}
			}
			o = p[0].orientTo(gluing[1]);
			if (o != 0) {
				if (pairOne == null) {
					pairOne = p[1];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = p;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					p[0] = pairOne;
					pair.increaseDegree();
					pairs.remove(removePair);
					return true;
				}
			}
			o = p[1].orientTo(gluing[1]);
			if (o != 0) {
				if (pairOne == null) {
					pairOne = p[0];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = p;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					p[1] = pairOne;
					pair.increaseDegree();
					pairs.remove(removePair);
					return true;
				}
			}

			 
		}
		return false;
	}
	
	public String toString() {
		String s= "[";
		for(Pair pair : pairs) {
			TFE[] p = pair.getPairs();
			s+= "["+ p[0].toString() + " to " + p[1].toString() + "], ";
		}
		if (s.endsWith(", ")) {
			s = s.substring(0,s.length()-2);
		}
		return s + "]";
	}

	public boolean pairs(TFE p1, TFE p2) {
		for(Pair pair : pairs) {
			TFE[] p = pair.getPairs();
			if ((p[0] == p1 && p[1] == p2) || (p[0] == p2 && p[1] == p1)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(EdgeConfig other) {
		for(Pair pair : pairs) {
			TFE[] p = pair.getPairs();
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
