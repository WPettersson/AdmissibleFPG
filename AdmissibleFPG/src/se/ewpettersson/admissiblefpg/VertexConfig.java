package se.ewpettersson.admissiblefpg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.ewpettersson.admissiblefpg.util.CircularListNode;

public class VertexConfig {
	
	// Maps a tet+vertex to a list of other pairs which have been squeezed together.
	// Note that the entries in the set are formed using the "combine" function.
	Map<Integer, Set<Integer>> equiv; 
	
	public Map<Integer, Set<Integer>> getEquiv() {
		return equiv;
	}

	public Map<Integer, Integer> getOnBoundary() {
		return onBoundary;
	}

	public Map<Integer, Map<Integer, Map<Integer, CircularListNode>>> getLinks() {
		return links;
	}

	// Maps a tet+vertex pair (formed using combine()) to a number indicating how many of the edges about
	// this tet+vertex pair are on the boundary still.
	Map<Integer, Integer> onBoundary;
	Map<Integer, Map<Integer, Map<Integer, CircularListNode>>> links;
	
	public void addTetrahedra(int id) {
		
		Map<Integer, Map<Integer, CircularListNode>> tetLinks = new HashMap<Integer, Map<Integer, CircularListNode>>();
		
		Map<Integer, CircularListNode> vertLink;
		Set<Integer> vert;
		for(int i=0; i<4; i++) {
			vert = new HashSet<Integer>();
			vert.add(combine( id, i));
			equiv.put(combine(id, i),vert);

			onBoundary.put(combine(id,i), 3);
			
			vertLink = new HashMap<Integer, CircularListNode>();
			tetLinks.put(i, vertLink);
			
		}
		links.put(id, tetLinks);
		
		createTriple(new TVE(id,0,-4), new TVE(id,0,5), new TVE(id,0,-6));
		createTriple(new TVE(id,1,2), new TVE(id,1,6), new TVE(id,1,-3));
		createTriple(new TVE(id,2,-1), new TVE(id,2,3), new TVE(id,2,-5));
		createTriple(new TVE(id,3,4), new TVE(id,3,-2), new TVE(id,3,1));
		
	}
	
	private void createTriple(TVE tve, TVE tve2, TVE tve3) {

		CircularListNode a = new CircularListNode(tve);
		CircularListNode b = new CircularListNode(tve2);
		CircularListNode c = new CircularListNode(tve3);
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		a.setPrev(c);
		c.setPrev(b);
		b.setPrev(a);
		links.get(tve.tet).get(tve.vertex).put(Math.abs(tve.edge),a);
		links.get(tve2.tet).get(tve2.vertex).put(Math.abs(tve2.edge),b);
		links.get(tve3.tet).get(tve3.vertex).put(Math.abs(tve3.edge),c);
	}

	public VertexConfig() {
		equiv = new HashMap<Integer,Set<Integer>>();
		links = new HashMap<Integer,Map<Integer,Map<Integer,CircularListNode>>>();
		onBoundary = new HashMap<Integer, Integer>();
	}
	
	public VertexConfig(VertexConfig vc) {
		equiv = new HashMap<Integer,Set<Integer>>();
		links = new HashMap<Integer,Map<Integer,Map<Integer,CircularListNode>>>();
		onBoundary = new HashMap<Integer, Integer>();
		
		// Deep cloning the annoying way.
		for( Integer key: vc.getEquiv().keySet()) {
			Set<Integer> cloneList = vc.getEquiv().get(key);
			Set<Integer> newList = new HashSet<Integer>();
			for(Integer elt: cloneList) {
				Integer newInt = new Integer(elt);
				newList.add(newInt);
			}
			equiv.put(key, newList);
		}
		
		for( Integer key: vc.getOnBoundary().keySet() ) {
			Integer onBdry = new Integer(vc.getOnBoundary().get(key));
			onBoundary.put(key, onBdry);
		}
		
		for( Integer key: vc.getLinks().keySet() ) {
			Map<Integer,Map<Integer,CircularListNode>> cloneMap = vc.getLinks().get(key);
			Map<Integer,Map<Integer,CircularListNode>> newMap = new HashMap<Integer,Map<Integer,CircularListNode>>();
			for( Integer key2: cloneMap.keySet()) {
				Map<Integer,CircularListNode> cloneMap2 = cloneMap.get(key2);
				Map<Integer,CircularListNode> newMap2 = new HashMap<Integer,CircularListNode>();
				for( Integer key3: cloneMap2.keySet()) {
					CircularListNode node = new CircularListNode(cloneMap2.get(key3));
					newMap2.put(key3, node);
				}
				newMap.put(key2, newMap2);
			}
			links.put(key, newMap);
		}
	}

	public boolean addGluing(TVE [] gluing) {
		CircularListNode linkA = links.get(gluing[0].tet).get(gluing[0].vertex).get(Math.abs(gluing[0].edge));
		CircularListNode linkB = links.get(gluing[1].tet).get(gluing[1].vertex).get(Math.abs(gluing[1].edge));
		
		// Check to see if the two link-arcs are on the same puncture.
		boolean samePuncture = false;
		CircularListNode temp = linkA.getNext();
		boolean reverseOrient = (gluing[0].edge * gluing[1].edge < 0);
		while ( !temp.equals(linkA) ) {
			if ( temp.equals(linkB) ) {
				samePuncture = true;
				if ( reverseOrient ) {
					return false;
				}
			}
			temp = temp.getNext();
			System.out.println(linkA);
			System.out.println(linkB);
		}
		
		if (! samePuncture) {
			// The link-arcs must be on distinct punctures. Check if they're on the same vertex.
			if ( equiv.get(combine(gluing[0].tet,gluing[0].vertex)).contains( combine( gluing[1].tet,gluing[1].vertex) ) ) {
				// Link arcs on distinct punctures, but same vertex.  Bad!
				return false;
			}
		}
		
		if (reverseOrient) {
			temp = linkB.getNext();
			// Reverse all "edges" in linkB
			while (temp != linkB) {
				temp.data.edge = -temp.data.edge;
				temp = temp.getNext();
			}
			// No need to reverse linkB, linkA and linkB will disappear now.
		}
		CircularListNode prevA = linkA.getPrev();
		CircularListNode prevB = linkB.getPrev();
		CircularListNode nextA = linkA.getNext();
		CircularListNode nextB = linkB.getNext();
		
		if (prevA != linkA && prevA != linkB) {
			prevA.setNext(nextB);
		}
		if (nextB != linkA && nextB != linkB) {
			nextB.setPrev(prevA);
		}
		if (prevB != linkA && prevB != linkB) {
			prevB.setNext(nextA);
		}
		if (nextA != linkA && nextA != linkB) {
			nextA.setPrev(prevB);
		}
		
		links.get(gluing[0].tet).get(gluing[0].vertex).remove(gluing[0].edge);
		links.get(gluing[1].tet).get(gluing[1].vertex).remove(gluing[1].edge);
		
		
		Integer combined0 = combine( gluing[0].tet,gluing[0].vertex);
		Integer combined1 = combine( gluing[1].tet,gluing[1].vertex);
		equiv.get(combined0).addAll(equiv.get(combined1));
		equiv.get(combined1).addAll(equiv.get(combined0));
	
		
		// Note that each of these tetrahedra/vertex pairs have one less boundary edge.
		onBoundary.put(combine(gluing[0].tet,gluing[0].vertex),onBoundary.get(gluing[0].tet)-1);
		onBoundary.put(combine(gluing[1].tet,gluing[1].vertex),onBoundary.get(gluing[1].tet)-1);
		
		// Remove them from all equivalence classes if they are no longer on the boundary	

		if (onBoundary.get(combined0) == 0) {
			Set<Integer> s = new HashSet<Integer>(equiv.get(combined0));
			for( Integer i: s ) {
				Set<Integer> otherList = equiv.get(i);
				if (otherList != null) {
					otherList.remove(i);
				}
			}
			equiv.remove(combined0);
		}
		
		if (onBoundary.get(combined1) == 0) {
			Set<Integer> s = new HashSet<Integer>(equiv.get(combined1));
			for( Integer i: s ) {
				Set<Integer> otherList = equiv.get(i);
				if(otherList != null) {
					otherList.remove(i);
				}
			}
			equiv.remove(combined1);
		}
			
		return true;
	}
	
	Integer combine(int tet, int vertex) {
		return 4*tet+vertex;
	}
	
	public String toString() {
		String s= equiv.toString() + "\n";
		s+= "[";
		List<CircularListNode> done = new LinkedList<CircularListNode>();
		for ( Map<Integer,Map<Integer,CircularListNode>> m1 : links.values()) {
			for ( Map<Integer, CircularListNode> m2 : m1.values()) {
				for (CircularListNode n : m2.values()) {
					if (! done.contains(n)) {
						s+="[";
						CircularListNode t = n.getNext();
						s+= n.toString() + ", ";
						while ( t != n) {
							s+= t.toString() + ", ";
							done.add(t);
							t = t.getNext();
						}
						s=s.substring(0, s.length()-2)+ "], ";
						done.add(t);
					}
				}
			}
		}
		if ( s.endsWith(", ")) {
			s=s.substring(0, s.length()-2);
		}
		
		return s+"]";
	}

	public boolean containsLink(TVE[] link) {
		CircularListNode linkStart = links.get(link[0].tet).get(link[0].vertex).get(Math.abs(link[0].edge));
		int linkCounter = 0;
		if (linkStart.data.edge == link[0].edge) { // Correct direction
			CircularListNode temp = linkStart.getNext();
			while ( temp != linkStart ) {
				linkCounter+=1;
				if (!link[linkCounter].equals(temp.data)) {
					return false;
				}
				temp = temp.getNext();
			}
		} else { // Reverse direction
			CircularListNode temp = linkStart.getPrev();
			while ( temp != linkStart ) {
				linkCounter+=1;
				link[linkCounter].edge = -link[linkCounter].edge;
				if (!link[linkCounter].equals(temp.data)) {
					return false;
				}
				temp = temp.getPrev();
			}
		}
		return true;
	}
	
	public boolean equals(VertexConfig other) {
		// Only need contents of equiv to match up.
		for( Integer key: equiv.keySet()) {
			Set<Integer> s = new HashSet<Integer>(equiv.get(key));
			Set<Integer> s2 = new HashSet<Integer>(other.equiv.get(key));
			if (! s.equals(s2)) {
				return false;
			}
		}
		return true;
	}

	public void mergeWith(VertexConfig vc) {
		links.putAll(vc.getLinks());
		equiv.putAll(vc.getEquiv());
		onBoundary.putAll(vc.getOnBoundary());
		
	}
}
