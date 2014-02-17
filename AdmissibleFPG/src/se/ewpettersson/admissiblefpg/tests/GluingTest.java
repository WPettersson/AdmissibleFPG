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

import se.ewpettersson.admissiblefpg.Gluing;
import se.ewpettersson.admissiblefpg.TFE;
import se.ewpettersson.admissiblefpg.TVE;

public class GluingTest {

	@Test
	public void testGluingToTVE() {
		Gluing g = new Gluing(0, 0, 1, 0, 2);
		TVE[][] tve = g.getTVEPairs();
		assertTrue(tve.length==3);
		// Should have the 3 gluings:
		// A (0,0,6)-(0,0,5)
		// B (0,2,3)-(0,1,3)
		// C (0,3,2)-(0,3,1)
		
		TVE[][] toFind = {{new TVE(0,0,6), new TVE(0,0,5)},
		                  {new TVE(0,2,3), new TVE(0,1,3)},
		                  {new TVE(0,3,2), new TVE(0,3,1)}};
		
	
		for(TVE[] pair: tve) {
			boolean found = false;
			for(TVE[] required : toFind) {
				if ((required[0].equals(pair[0]) && required[1].equals(pair[1])) ||
				    (required[0].equals(pair[1]) && required[1].equals(pair[0]))) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}
	
	@Test
	public void testGluingToTFE() {
		Gluing g = new Gluing(0, 0, 1, 0, 2);
		TFE[][] tfe = g.getTFEPairs();
		assertTrue(tfe.length==3);
		// Should have the 3 gluings:
		// A (0,1,2)-(0,2,1)
		// B (0,1,3)-(0,2,3)
		// C (0,1,6)-(0,2,5)
		
		TFE[][] toFind = {{new TFE(0,1,2), new TFE(0,2,1)},
		                  {new TFE(0,1,3), new TFE(0,2,3)},
		                  {new TFE(0,1,6), new TFE(0,2,5)}};
		

		for(TFE[] pair: tfe) {
			boolean found = false;
			for(TFE[] required : toFind) {
				if ((required[0].equals(pair[0]) && required[1].equals(pair[1])) ||
				    (required[0].equals(pair[1]) && required[1].equals(pair[0]))) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}
}
