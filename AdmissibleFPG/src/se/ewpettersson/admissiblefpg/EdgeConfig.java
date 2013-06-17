package se.ewpettersson.admissiblefpg;

import java.util.List;

public class EdgeConfig {
	public EdgeConfig(List<TFE[]> pairs) {
		super();
		this.pairs = pairs;
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
}
