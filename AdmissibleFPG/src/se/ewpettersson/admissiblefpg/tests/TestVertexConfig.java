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
package se.ewpettersson.admissiblefpg.tests;

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
