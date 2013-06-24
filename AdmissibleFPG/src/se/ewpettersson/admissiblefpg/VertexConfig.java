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
		Map<Set<Integer>,Set<Integer>> clnSet = new HashMap<Set<Integer>,Set<Integer>>();
		for( Integer key: vc.getEquiv().keySet()) {
			Set<Integer> cloneList = vc.getEquiv().get(key);
			Set<Integer> newList;
			if(clnSet.containsKey(cloneList)) {
				newList = clnSet.get(cloneList);
			} else {
				newList = new HashSet<Integer>();
				for(Integer elt: cloneList) {
					Integer newInt = new Integer(elt);
					newList.add(newInt);
				}
				clnSet.put(cloneList, newList);
			}
			equiv.put(key, newList);
		}
		
		for( Integer key: vc.getOnBoundary().keySet() ) {
			Integer onBdry = new Integer(vc.getOnBoundary().get(key));
			onBoundary.put(key, onBdry);
		}
		
		Map<CircularListNode,CircularListNode> clnMap = new HashMap<CircularListNode,CircularListNode>();
		for( Integer key: vc.getLinks().keySet() ) {
			Map<Integer,Map<Integer,CircularListNode>> cloneMap = vc.getLinks().get(key);
			Map<Integer,Map<Integer,CircularListNode>> newMap = new HashMap<Integer,Map<Integer,CircularListNode>>();
			for( Integer key2: cloneMap.keySet()) {
				Map<Integer,CircularListNode> cloneMap2 = cloneMap.get(key2);
				Map<Integer,CircularListNode> newMap2 = new HashMap<Integer,CircularListNode>();
				for( Integer key3: cloneMap2.keySet()) {
					CircularListNode node = new CircularListNode(cloneMap2.get(key3));
					clnMap.put(cloneMap2.get(key3),	node);
					newMap2.put(key3, node);
				}
				newMap.put(key2, newMap2);
			}
			links.put(key, newMap);
		}
		for(CircularListNode oldNode: clnMap.keySet()) {
				CircularListNode newNode = clnMap.get(oldNode);
				newNode.setNext(clnMap.get(oldNode.getNext()));
				newNode.setPrev(clnMap.get(oldNode.getPrev()));
		}
	}

	public boolean addGluing(TVE [] gluing) {
		CircularListNode linkA = links.get(gluing[0].tet).get(gluing[0].vertex).get(Math.abs(gluing[0].edge));
		CircularListNode linkB = links.get(gluing[1].tet).get(gluing[1].vertex).get(Math.abs(gluing[1].edge));
		
		//System.out.println("Link A:" + linkA);
		//System.out.println("Link B:" + linkB);
		// Check to see if the two link-arcs are on the same puncture.
		boolean samePuncture = false;
		CircularListNode temp = linkA.getNext();
		boolean reverseOrient = (gluing[0].edge * gluing[1].edge < 0) != (linkA.data.edge*linkB.data.edge > 0);
		while ( !temp.equals(linkA) ) {
			if ( temp.equals(linkB) ) {
				samePuncture = true;
				if (reverseOrient) {
					return false;
				}
			}
			temp = temp.getNext();
		}
		
		if (! samePuncture) {
			// The link-arcs must be on distinct punctures. Check if they're on the same vertex.
			if ( equiv.get(combine(gluing[0].tet,gluing[0].vertex)).contains( combine( gluing[1].tet,gluing[1].vertex) ) ) {
				// Link arcs on distinct punctures, but same vertex.  Bad!
				return false;
			}
		}
		

		if (reverseOrient) {
			temp = linkB.getNext(); // Iterating "forward" along the old orientation.
			// Reverse all in linkB
			while (temp != linkB) {
				temp.data.edge = -temp.data.edge;
				CircularListNode temp2 = temp.getNext();
				temp.setNext(temp.getPrev());
				temp.setPrev(temp2);
				
				temp = temp.getPrev(); // We've swapped prev+next, so this is the right direction.
				
			}
			CircularListNode temp2 = linkB.getNext();
			linkB.setNext(linkB.getPrev());
			linkB.setPrev(temp2);
			linkB.data.edge = -temp.data.edge;
		}
	
		CircularListNode prevA = linkA.getPrev();
		CircularListNode prevB = linkB.getPrev();
		CircularListNode nextA = linkA.getNext();
		CircularListNode nextB = linkB.getNext();

		
		
		// Check to see whether either linkA or linkB represents a puncture consisting of one edge only.
		boolean linkASolo = (nextA == linkA);
		boolean linkBSolo = (nextB == linkB);
		
		if (linkASolo && linkBSolo) {
			// Do nothing if both links represent single edge punctures.
		} else if (linkASolo) {
			// Just skip the edge linkB if linkA is a single edge puncture.
			prevB.setNext(nextB);
			nextB.setPrev(prevB);
		} else if (linkBSolo) {
			// Likewise for linkA
			prevA.setNext(nextA);
			nextA.setPrev(prevA);
		} else {
			// Neither are single edge punctures. Join the two punctures into one puncture.
			prevA.setNext(nextB);
			nextB.setPrev(prevA);
			prevB.setNext(nextA);
			nextA.setPrev(prevB);
		}
		
		links.get(gluing[0].tet).get(gluing[0].vertex).remove(Math.abs(gluing[0].edge));
		links.get(gluing[1].tet).get(gluing[1].vertex).remove(Math.abs(gluing[1].edge));
		
		
		Integer combined0 = combine( gluing[0].tet,gluing[0].vertex);
		Integer combined1 = combine( gluing[1].tet,gluing[1].vertex);

		equiv.get(combined0).addAll(equiv.get(combined1));
		for(Integer i:equiv.get(combined1)) {
			equiv.put(i, equiv.get(combined0));
		}
		
	
		
		// Note that each of these tetrahedra/vertex pairs have one less boundary edge.
		onBoundary.put(combined0,onBoundary.get(combined0)-1);
		onBoundary.put(combined1,onBoundary.get(combined1)-1);
		
		// Remove them from all equivalence classes if they are no longer on the boundary	

		if (onBoundary.get(combined0) == 0) {
			Set<Integer> s = new HashSet<Integer>(equiv.get(combined0));
			for( Integer i: s ) {
				Set<Integer> otherList = equiv.get(i);
				if (otherList != null) {
					otherList.remove(combined0);
				}
			}
			equiv.remove(combined0);
		}
		
		
		// Don't try to remove a second time if combined1==combined0
		if ((combined1 != combined0) && (onBoundary.get(combined1) == 0)) {
			Set<Integer> s = new HashSet<Integer>(equiv.get(combined1));
			for( Integer i: s ) {
				Set<Integer> otherList = equiv.get(i);
				if(otherList != null) {
					otherList.remove(combined1);
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
						int i=0;
						while (t!= null && t != n && i<10) {
							s+= t.toString() + ", ";
							done.add(t);
							t = t.getNext();
							i++;
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
