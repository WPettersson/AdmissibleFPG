package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import se.ewpettersson.admissiblefpg.TVE;
import se.ewpettersson.admissiblefpg.VertexConfig;

public class TestVertexConfig {

	@Test
	public void testSamePunctureGoodOrientationNeighbours() {
		VertexConfig vc = new VertexConfig();
		vc.addTetrahedra(0);
		TVE[] pair = new TVE[2];
		pair[0] = new TVE(0,3,1);
		pair[1] = new TVE(0,3,2);
		assertTrue(vc.addGluing(pair));
		TVE[] link = new TVE[1];
		link[0] = new TVE(0,3,4);
		assertTrue(vc.containsLink(link));
	}

	@Test
	public void testSamePunctureBadOrientation() {
		VertexConfig vc = new VertexConfig();
		vc.addTetrahedra(0);
		TVE[] pair = new TVE[2];
		pair[0] = new TVE(0,3,1);
		pair[1] = new TVE(0,3,-2);
		assertFalse(vc.addGluing(pair));
	}
	
	@Test
	public void testDiffVertex() {
		VertexConfig vc = new VertexConfig();
		vc.addTetrahedra(0);
		TVE[] pair = new TVE[2];
		pair[0] = new TVE(0,2,3);
		pair[1] = new TVE(0,1,3);
		assertTrue(vc.addGluing(pair));
		TVE[] link = new TVE[4];
		link[0] = new TVE(0,1,2);
		link[1] = new TVE(0,1,6);
		link[2] = new TVE(0,2,-5);
		link[3] = new TVE(0,2,-1);
		assertTrue(vc.containsLink(link));
	}
	
}
