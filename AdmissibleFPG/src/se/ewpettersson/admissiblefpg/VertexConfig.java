package se.ewpettersson.admissiblefpg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import se.ewpettersson.admissiblefpg.util.CircularListNode;

public class VertexConfig {
	Map<Integer, Map<Integer, List<Integer>>> equiv; 
	Map<Integer, Map<Integer, Map<Integer, CircularListNode<TVE>>>> links;
	
	public void addTetrahedra(int id) {
		
		Map<Integer, List<Integer>> tet = new HashMap<Integer,List<Integer>>();

		List<Integer> vert;
		for(int i=0; i<4; i++) {
			vert = new LinkedList<Integer>();
			vert.add(combine( id, i));
			tet.put(i, vert);
		}

		
		equiv.put(id, tet);
		
		Map<Integer, Map<Integer, CircularListNode<TVE>>> tetLinks = new HashMap<Integer, Map<Integer, CircularListNode<TVE>>>();
		
		Map<Integer, CircularListNode<TVE>> vertLink = new HashMap<Integer, CircularListNode<TVE>>();
		
		tetLinks.put(0, vertLink);
		vertLink = new HashMap<Integer, CircularListNode<TVE>>();
		tetLinks.put(1, vertLink);
		vertLink = new HashMap<Integer, CircularListNode<TVE>>();
		tetLinks.put(2, vertLink);
		vertLink = new HashMap<Integer, CircularListNode<TVE>>();
		tetLinks.put(3, vertLink);
		
		links.put(id, tetLinks);
		
		createTriple(new TVE(id,0,-4), new TVE(id,0,5), new TVE(id,0,-6));
		createTriple(new TVE(id,1,2), new TVE(id,1,6), new TVE(id,1,-3));
		createTriple(new TVE(id,2,-1), new TVE(id,2,3), new TVE(id,2,-5));
		createTriple(new TVE(id,3,4), new TVE(id,3,-2), new TVE(id,3,1));
		
		//List<TVE> equivList = new LinkedList<TVE>();
		
		
		
	}
	
	private void createTriple(TVE tve, TVE tve2, TVE tve3) {

		CircularListNode<TVE> a = new CircularListNode<TVE>(tve);
		CircularListNode<TVE> b = new CircularListNode<TVE>(tve2);
		CircularListNode<TVE> c = new CircularListNode<TVE>(tve3);
		links.get(tve.tet).get(tve.vertex).put(Math.abs(tve.edge),a);
		links.get(tve2.tet).get(tve2.vertex).put(Math.abs(tve2.edge),b);
		links.get(tve3.tet).get(tve3.vertex).put(Math.abs(tve3.edge),c);
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		a.setPrev(c);
		c.setPrev(b);
		b.setPrev(a);
	}

	public VertexConfig() {
		equiv = new HashMap<Integer,Map<Integer,List<Integer>>>();
		links = new HashMap<Integer,Map<Integer,Map<Integer,CircularListNode<TVE>>>>();
	}
	
	public boolean addGluing(TVE [] gluing) {
		CircularListNode<TVE> linkA = links.get(gluing[0].tet).get(gluing[0].vertex).get(Math.abs(gluing[0].edge));
		CircularListNode<TVE> linkB = links.get(gluing[1].tet).get(gluing[1].vertex).get(Math.abs(gluing[1].edge));
		
		// Check to see if the two link-arcs are on the same puncture.
		boolean samePuncture = false;
		CircularListNode<TVE> temp = linkA.getNext();
		boolean reverseOrient = (gluing[0].edge * gluing[1].edge < 0);
		while ( temp != linkA ) {
			if ( temp == linkB ) {
				samePuncture = true;
				if ( reverseOrient ) {
					return false;
				}
			}
			temp = temp.getNext();
		}
		
		if (! samePuncture) {
			// The link-arcs must be on distinct punctures. Check if they're on the same vertex.
			if ( equiv.get(gluing[0].tet).get(gluing[0].vertex).contains( combine( gluing[1].tet,gluing[1].vertex) ) ) {
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
		CircularListNode<TVE> prevA = linkA.getPrev();
		CircularListNode<TVE> prevB = linkB.getPrev();
		CircularListNode<TVE> nextA = linkA.getNext();
		CircularListNode<TVE> nextB = linkB.getNext();
		
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
		
		List<Integer> equiv_list = equiv.get(gluing[0].tet).get(gluing[0].vertex);
		Integer combined = combine( gluing[1].tet,gluing[1].vertex);
		if (!equiv_list.contains(combined)) {
			equiv_list.add(combined);
		}
		equiv_list = equiv.get(gluing[1].tet).get(gluing[1].vertex);
		combined = combine( gluing[0].tet,gluing[0].vertex);
		if (!equiv_list.contains(combined)) {
			equiv_list.add(combined);
		}
		

			
		return true;
	}
	
	int combine(int tet, int vertex) {
		return 4*tet+vertex;
	}
	
	public String toString() {
		String s= equiv.toString() + "\n";
		s+= "[";
		List<CircularListNode<TVE>> done = new LinkedList<CircularListNode<TVE>>();
		for ( Map<Integer,Map<Integer,CircularListNode<TVE>>> m1 : links.values()) {
			for ( Map<Integer, CircularListNode<TVE>> m2 : m1.values()) {
				for (CircularListNode<TVE> n : m2.values()) {
					if (! done.contains(n)) {
						s+="[";
						CircularListNode<TVE> t = n.getNext();
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
		CircularListNode<TVE> linkStart = links.get(link[0].tet).get(link[0].vertex).get(Math.abs(link[0].edge));
		int linkCounter = 0;
		if (linkStart.data.edge == link[0].edge) { // Correct direction
			CircularListNode<TVE> temp = linkStart.getNext();
			while ( temp != linkStart ) {
				linkCounter+=1;
				if (!link[linkCounter].equals(temp.data)) {
					return false;
				}
				temp = temp.getNext();
			}
		} else { // Reverse direction
			CircularListNode<TVE> temp = linkStart.getPrev();
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
}
