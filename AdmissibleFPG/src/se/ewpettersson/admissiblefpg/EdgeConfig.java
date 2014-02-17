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
			degree = 1;
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

		public void increaseDegree(int i) {
			degree+=i;
			
		}

		public boolean degreeOk() {
			return degree >= 4;
		}
		
		public String toString() {
			return "{" + pair[0].toString() + "," + pair[1].toString() + "}";
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
		Pair removePair = null;
		int degree_increase = 0;

		if (gluing[0].tet == gluing[1].tet && gluing[0].edge == gluing[1].edge) {
//			System.out.println("Loop?");
//			System.out.println(gluing[0].toString());
//			System.out.println(gluing[1].toString());
			degree_increase = 1;
		}
		for(Pair pair : pairs) {
			int o = pair.pair[0].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = pair.pair[1].orientTo(gluing[1]);
				if (o2 == o) {
					// MINIMAL
					pair.increaseDegree(degree_increase);
					if (!pair.degreeOk()) {
						//System.out.println("Low degree edge!");
						return false;
					}
					// Can remove this pair as it's being closed up.
					pairs.remove(pair);
					return true;
				}
				if (o2 == -o) {
					// Gluing edge to itself in reverse
					//System.out.println("Edge to itself!");
					return false;
				}
				if (pairOne == null) {
					pairOne = pair.pair[1];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = pair;
					degree_increase += pair.degree;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					pair.pair[0] = pairOne;
					pair.increaseDegree(degree_increase);
					
					pairs.remove(removePair);
					return true;
				}
			}
			o = pair.pair[1].orientTo(gluing[0]);
			if (o != 0) {
				int o2 = pair.pair[0].orientTo(gluing[1]);
				if (o2 == o) {
					// MINIMAL
					pair.increaseDegree(degree_increase);
					if (!pair.degreeOk()) {
						//System.out.println("Low degree edge!");
						return false;
					}
					// Can remove this pair as it's being closed up.
					pairs.remove(pair);
					return true;
				}
				if (o2 == -o) {
					// Gluing edge to itself in reverse
					//System.out.println("Edge to itself!");
					return false;
				}
				if (pairOne == null) {
					pairOne = pair.pair[0];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = pair;
					degree_increase += pair.degree;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					pair.pair[1] = pairOne;
					pair.increaseDegree(degree_increase);
					pairs.remove(removePair);
					return true;
				}
			}
			o = pair.pair[0].orientTo(gluing[1]);
			if (o != 0) {
				if (pairOne == null) {
					pairOne = pair.pair[1];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = pair;
					degree_increase += pair.degree;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					pair.pair[0] = pairOne;
					pair.increaseDegree(degree_increase);
					pairs.remove(removePair);
					return true;
				}
			}
			o = pair.pair[1].orientTo(gluing[1]);
			if (o != 0) {
				if (pairOne == null) {
					pairOne = pair.pair[0];
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					removePair = pair;
					degree_increase += pair.degree;
				} else {
					if (o==-1) {
						pairOne.edge=-pairOne.edge;
					}
					pair.pair[1] = pairOne;
					pair.increaseDegree(degree_increase);
					pairs.remove(removePair);
					return true;
				}
			}

			 
		}
		//System.out.println("Bad!");
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
	
	public boolean onBoundary(int tet, int face) {
		for (Pair p: pairs) {
			if (p.pair[0].tet == tet && p.pair[0].face == face) {
				return true;
			}
			if (p.pair[1].tet == tet && p.pair[1].face == face) {
				return true;
			}
		}
		return false;
	}

}
